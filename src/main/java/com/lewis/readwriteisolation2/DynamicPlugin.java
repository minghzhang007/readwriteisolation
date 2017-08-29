package com.lewis.readwriteisolation2;


import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;

import java.sql.Connection;
import java.util.Properties;

@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = Connection.class)})
public class DynamicPlugin implements Interceptor {

    public Object intercept(Invocation invocation) throws Throwable {
        Connection conn = (Connection) invocation.getArgs()[0];
        //若采用了代理，则路由数据源
        if (conn instanceof ConnectionProxy) {
            StatementHandler statementHandler = (StatementHandler) invocation.getTarget();

            MappedStatement mappedStatement;
            if (statementHandler instanceof RoutingStatementHandler) {
                StatementHandler delegate = (StatementHandler) ReflectionUtil.getFieldValue(statementHandler, "delegate");
                mappedStatement = (MappedStatement) ReflectionUtil.getFieldValue(delegate, "mappedStatement");
            } else {
                mappedStatement = (MappedStatement) ReflectionUtil.getFieldValue(statementHandler, "mappedStatement");
            }
            String key;
            if (mappedStatement.getSqlCommandType() == SqlCommandType.SELECT) {
                key = AbstractDynamicDatasourceProxy.READ;
            } else {
                key = AbstractDynamicDatasourceProxy.WRITE;
            }
            ConnectionProxy connectionProxy = (ConnectionProxy) conn;
            connectionProxy.getTargetConnection(key);
        }
        return invocation.proceed();
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {

    }
}
