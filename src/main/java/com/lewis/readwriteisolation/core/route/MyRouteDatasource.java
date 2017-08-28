package com.lewis.readwriteisolation.core.route;

import com.lewis.readwriteisolation.core.JdbcContextHolder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class MyRouteDatasource extends AbstractRoutingDataSource {

    protected Object determineCurrentLookupKey() {
        return JdbcContextHolder.getJdbcType();
    }
}
