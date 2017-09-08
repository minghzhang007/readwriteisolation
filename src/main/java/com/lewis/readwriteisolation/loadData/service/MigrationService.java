package com.lewis.readwriteisolation.loadData.service;

import com.lewis.readwriteisolation.loadData.core.DBUtil;
import com.lewis.readwriteisolation.loadData.core.InsertSqlHolder;
import com.lewis.readwriteisolation.loadData.core.executor.MyExecutor;
import com.lewis.readwriteisolation.loadData.core.task.TaskCallback;
import com.lewis.readwriteisolation.loadData.domain.ColumnItem;
import com.lewis.readwriteisolation.loadData.domain.DatasourceAttribute;
import com.lewis.readwriteisolation.loadData.domain.Table;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Semaphore;

public class MigrationService {

    private InsertSqlHolder insertSqlHolder = new InsertSqlHolder();

    private static MigrationSupport migrationSupport = new MigrationSupport();


    public void migrationData(DatasourceAttribute source, DatasourceAttribute target) throws SQLException {

        produceData(source, target);

        consumeData(target);
    }

    private void produceData(DatasourceAttribute source, DatasourceAttribute target) throws SQLException {
        Map<Table, String> tableStringMap = migrationSupport.querySqls(source, target);

        List<Map<Table, String>> queryTaskParams = getTasks(tableStringMap);
        for (Map<Table, String> queryTaskParam : queryTaskParams) {
            MyExecutor.getTaskProcessor("query").asynExecuteTask(new TaskCallback() {
                @Override
                public Object doCallback() throws Exception {
                    generateInsertSql(source, queryTaskParam);
                    return null;
                }
            });
        }
    }

    private void consumeData(DatasourceAttribute target) {
        MyExecutor.getTaskProcessor("insert").asynExecuteTask(() -> {
            Semaphore semaphore = new Semaphore(10);
            while (true) {
                String insertSql = null;
                boolean success = true;
                try {
                    semaphore.acquire();
                    insertSql = insertSqlHolder.take();
                    if (insertSql != null) {
                        Connection connection = DBUtil.getConnection(target);
                        PreparedStatement preparedStatement = connection.prepareStatement(insertSql);
                        success = preparedStatement.execute();
                        DBUtil.close(connection);
                    }
                } catch (InterruptedException | SQLException e) {
                    System.out.println("出错："+success+"  "+insertSql);
                } finally {
                    semaphore.release();
                }
            }
        });
    }

    private List<Map<Table, String>> getTasks(Map<Table, String> tableStringMap) {
        List<Map<Table, String>> list = new ArrayList<>();
        tableStringMap.entrySet().stream().forEach(entry -> {
            Map<Table, String> map = new HashMap<>();
            map.put(entry.getKey(), entry.getValue());
            list.add(map);
        });
        return list;
    }

    private void generateInsertSql(DatasourceAttribute source, Map<Table, String> querySqlsMap) throws SQLException {
        Connection connection = DBUtil.getConnection(source);
        Iterator<Map.Entry<Table, String>> iterator = querySqlsMap.entrySet().iterator();
        while (iterator.hasNext()) {
            List<String> insertSqls = new ArrayList<>();
            Map.Entry<Table, String> entry = iterator.next();
            PreparedStatement preparedStatement = connection.prepareStatement(entry.getValue());
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Map<ColumnItem, String>> pairs = new ArrayList<>();
            int recordCount = 0;
            while (resultSet.next()) {
                Map<ColumnItem, String> columnValueMap = new LinkedHashMap<>(entry.getKey().getColumnItems().size());
                recordCount++;
                for (ColumnItem columnItem : entry.getKey().getColumnItems()) {
                    try {
                        String value = resultSet.getString(columnItem.getColumnName());
                        columnValueMap.put(columnItem, value);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                pairs.add(columnValueMap);
            }
            if (hasRecords(recordCount)) {
                StringBuilder insertBuilder = new StringBuilder();
                for (Map<ColumnItem, String> mapping : pairs) {
                    insertBuilder.append("insert into ").append(entry.getKey().getTableName()).append(" (");
                    Iterator<Map.Entry<ColumnItem, String>> iterator1 = mapping.entrySet().iterator();
                    while (iterator1.hasNext()) {
                        Map.Entry<ColumnItem, String> ColumnValueEntry = iterator1.next();
                        insertBuilder.append(ColumnValueEntry.getKey().getColumnName()).append(",");
                    }
                    insertBuilder.deleteCharAt(insertBuilder.toString().length() - 1);
                    insertBuilder.append(") values(");
                    iterator1 = mapping.entrySet().iterator();
                    while (iterator1.hasNext()) {
                        Map.Entry<ColumnItem, String> columnValueEntry = iterator1.next();
                        if (isStringOfColumnType(columnValueEntry)) {
                            if (StringUtils.isEmpty(columnValueEntry.getValue())) {
                                insertBuilder.append("\"默认值\"");
                            } else {
                                insertBuilder.append("\"").append(columnValueEntry.getValue()).append("\"");
                            }
                        } else {
                            insertBuilder.append(columnValueEntry.getValue());
                        }
                        insertBuilder.append(",");
                    }
                    insertBuilder.deleteCharAt(insertBuilder.toString().length() - 1);
                    insertBuilder.append(")");
                    insertSqlHolder.put(insertBuilder.toString());
                    insertSqls.add(insertBuilder.toString());
                    insertBuilder.delete(0, insertBuilder.toString().length());
                }
            } else {
                System.out.println("表" + entry.getKey().getTableName() + " 没有数据。");
            }
        }
        DBUtil.close(connection);
    }


    private boolean isStringOfColumnType(Map.Entry<ColumnItem, String> columnValueEntry) {
        String type = columnValueEntry.getKey().getType();
        return type.equalsIgnoreCase("VARCHAR")
                || type.equalsIgnoreCase("TEXT")
                || type.equalsIgnoreCase("CHAR");
    }

    private boolean hasRecords(int recordCount) {
        return recordCount > 0;
    }
}
