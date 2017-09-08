package com.lewis.readwriteisolation.loadData.domain;

import java.util.List;

public class Table {

    private String datasource;

    private String tableName;

    private List<ColumnItem> columnItems;

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ColumnItem> getColumnItems() {
        return columnItems;
    }

    public void setColumnItems(List<ColumnItem> columnItems) {
        this.columnItems = columnItems;
    }

    public Table() {
    }

    public Table(String tableName, List<ColumnItem> columnItems) {
        this.tableName = tableName;
        this.columnItems = columnItems;
    }

    public Table(String datasource, String tableName, List<ColumnItem> columnItems) {
        this.datasource = datasource;
        this.tableName = tableName;
        this.columnItems = columnItems;
    }

    @Override
    public String toString() {
        return "Table{" +
                "datasource='" + datasource + '\'' +
                ", tableName='" + tableName + '\'' +
                ", columnItems=" + columnItems +
                '}';
    }
}
