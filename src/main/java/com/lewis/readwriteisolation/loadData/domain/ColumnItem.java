package com.lewis.readwriteisolation.loadData.domain;

public class ColumnItem {

    private String columnName;

    private String type;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ColumnItem(String columnName, String type) {
        this.columnName = columnName;
        this.type = type;
    }

    @Override
    public String toString() {
        return "ColumnItem{" +
                "columnName='" + columnName + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
