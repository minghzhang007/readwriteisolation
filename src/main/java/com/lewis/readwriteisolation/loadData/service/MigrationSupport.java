package com.lewis.readwriteisolation.loadData.service;

import com.google.common.collect.Lists;
import com.lewis.readwriteisolation.loadData.core.DBUtil;
import com.lewis.readwriteisolation.loadData.domain.ColumnItem;
import com.lewis.readwriteisolation.loadData.domain.DatasourceAttribute;
import com.lewis.readwriteisolation.loadData.domain.Table;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MigrationSupport {

    public List<String> igoreTables = Lists.newArrayList("_Book_old","droped_Comment_1479712180","GiftCode_old20170808","DeviceKey_tmp");

    public Map<Table, String> querySqls(DatasourceAttribute source,DatasourceAttribute target) {
        try {
            List<Table> tables = getTables(source);
            clearTargetSourceData(tables,target);
            return getQuerySqls(tables);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    private void clearTargetSourceData(List<Table> tables,DatasourceAttribute target) throws SQLException {
        Connection connection = DBUtil.getConnection(target);
        StringBuilder sb = new StringBuilder();
        for (Table table : tables) {
            sb.append("delete from ").append(table.getTableName());
            PreparedStatement preparedStatement = connection.prepareStatement(sb.toString());
            preparedStatement.execute();
            sb.delete(0,sb.toString().length());
        }
        DBUtil.close(connection);
        System.out.println("清理目标数据库数据完毕");
    }

    private List<Table> getTables(DatasourceAttribute source) throws SQLException {
        List<Table> tables = new ArrayList<>();

        Connection conn = DBUtil.getConnection(source);
        DatabaseMetaData metaData = conn.getMetaData();
        String[] types = {"TABLE"};
        ResultSet tablesResultSet = metaData.getTables(null, null, "%", types);
        while (tablesResultSet.next()) {
            String tableName = tablesResultSet.getString(3);
            if(igoreTables.contains(tableName)){
                continue;
            }
            ResultSet columns = metaData.getColumns("", "%", tableName, "%");
            List<ColumnItem> columnItems = new ArrayList<>();
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String typeName = columns.getString("TYPE_NAME");
                columnItems.add(new ColumnItem(columnName, typeName));
            }
            tables.add(new Table(tableName, columnItems));
        }
        DBUtil.close(conn);
        return tables;
    }

    private Map<Table, String> getQuerySqls(List<Table> tables) {
        Map<Table, String> mapping = new HashMap<>(tables.size());
        StringBuilder stringBuilder = new StringBuilder();
        for (Table table : tables) {
            stringBuilder.append("select * from ").append(table.getTableName()).append(" limit 100");
            mapping.put(table, stringBuilder.toString());
            stringBuilder.delete(0, stringBuilder.toString().length());
        }
        return mapping;
    }
}
