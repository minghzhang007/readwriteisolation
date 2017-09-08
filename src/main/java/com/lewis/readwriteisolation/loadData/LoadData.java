package com.lewis.readwriteisolation.loadData;

import com.lewis.readwriteisolation.loadData.domain.DatasourceAttribute;
import com.lewis.readwriteisolation.loadData.service.MigrationService;

import java.sql.*;

public class LoadData {



    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        String url = "jdbc:mysql://10.164.97.50:6000/library-test";
        String username = "lib-test";
        String password = "lib-test";
        DatasourceAttribute source = new DatasourceAttribute(url,username,password);
        DatasourceAttribute target = new DatasourceAttribute("jdbc:mysql://localhost:3306/snail_reader","root","root");
        MigrationService migrationService = new MigrationService();
        migrationService.migrationData(source,target);
    }


}
