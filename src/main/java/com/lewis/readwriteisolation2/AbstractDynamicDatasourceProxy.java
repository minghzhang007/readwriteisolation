package com.lewis.readwriteisolation2;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/28.
 */
public abstract class AbstractDynamicDatasourceProxy extends AbstractDataSource implements InitializingBean {

    private List<Object> readDatasources;

    private List<DataSource> resolvedReadDatasources;

    private Object writeDatasource;

    private DataSource resolvedWriteDatasource;

    private int readDatasourcePoolPattern = 0;

    private int readDsSize;

    private boolean defaultAutoCommit = true;

    private int defaultTransactionIsolation = Connection.TRANSACTION_READ_COMMITTED;

    private static final String READ = "read";

    private static final String WRITE = "write";

    private DataSourceLookup dataSourceLookup = new JndiDataSourceLookup();

    public Connection getConnection() throws SQLException {
        return (Connection) Proxy.newProxyInstance(ConnectionProxy.class.getClassLoader(), new Class[]{ConnectionProxy.class},
                new RWConnectionInvocationHandler());
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return (Connection) Proxy.newProxyInstance(ConnectionProxy.class.getClassLoader(), new Class[]{ConnectionProxy.class},
                new RWConnectionInvocationHandler(username, password));
    }

    public void afterPropertiesSet() throws Exception {
        if (writeDatasource == null) {
            throw new IllegalArgumentException("Property 'writeDataSource' is required");
        }
        this.resolvedWriteDatasource = resolveSpecifiedDatasource(writeDatasource);

        this.resolvedReadDatasources = new ArrayList<DataSource>(readDatasources.size());
        for (Object readDatasource : readDatasources) {
            this.resolvedReadDatasources.add(resolveSpecifiedDatasource(readDatasource));
        }
        this.readDsSize = this.readDatasources.size();
    }

    protected DataSource determineTargetDatasource(String key) {
        Assert.notNull(this.resolvedReadDatasources, "DataSource router not initialized");
        if (WRITE.equals(key)) {
            return resolvedWriteDatasource;
        } else {
            return loadReadDatasource();
        }
    }

    protected abstract DataSource loadReadDatasource();

    protected DataSource resolveSpecifiedDatasource(Object datasource) throws IllegalArgumentException {
        if (datasource instanceof DataSource) {
            return DataSource.class.cast(datasource);
        } else if (datasource instanceof String) {
            return this.dataSourceLookup.getDataSource((String) datasource);
        } else {
            throw new IllegalArgumentException("Illegal data source value - only [javax.sql.DataSource] and String supported: "
                    + datasource);
        }
    }


    public int getReadDsSize() {
        return readDsSize;
    }

    public void setReadDsSize(int readDsSize) {
        this.readDsSize = readDsSize;
    }

    public List<DataSource> getResolvedReadDatasources() {
        return resolvedReadDatasources;
    }

    public List<Object> getReadDatasources() {
        return readDatasources;
    }

    public void setReadDatasources(List<Object> readDatasources) {
        this.readDatasources = readDatasources;
    }

    public void setResolvedReadDatasources(List<DataSource> resolvedReadDatasources) {
        this.resolvedReadDatasources = resolvedReadDatasources;
    }

    public Object getWriteDatasource() {
        return writeDatasource;
    }

    public void setWriteDatasource(Object writeDatasource) {
        this.writeDatasource = writeDatasource;
    }

    public DataSource getResolvedWriteDatasource() {
        return resolvedWriteDatasource;
    }

    public void setResolvedWriteDatasource(DataSource resolvedWriteDatasource) {
        this.resolvedWriteDatasource = resolvedWriteDatasource;
    }

    public int getReadDatasourcePoolPattern() {
        return readDatasourcePoolPattern;
    }

    public void setReadDatasourcePoolPattern(int readDatasourcePoolPattern) {
        this.readDatasourcePoolPattern = readDatasourcePoolPattern;
    }

    private class RWConnectionInvocationHandler implements InvocationHandler {
        private String username;

        private String password;

        private Boolean readOnly = false;
        private Integer transactionIsolation;

        private Boolean autoCommit;

        private boolean closed = false;

        private Connection target;

        public RWConnectionInvocationHandler(String username, String password) {
            this();
            this.username = username;
            this.password = password;
        }

        public RWConnectionInvocationHandler() {

        }


        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // We must avoid fetching a target Connection for "equals".
            // Only consider equal when proxies are identical.
            if (method.getName().equals("equals")) {
                return (proxy == args[0]) ? Boolean.TRUE : Boolean.FALSE;
            } else if (method.getName().equals("hashcode")) {
                // We must avoid fetching a target Connection for "hashCode",
                // and we must return the same hash code even when the target
                // Connection has been fetched: use hashCode of Connection proxy.
                return new Integer(System.identityHashCode(proxy));
            } else if (method.getName().equals("getTargetConnection")) {
                // Handle getTargetConnection method: return underlying connection.
                return getTargetConnection(method, args);
            }


            if (!hasTargetConnection()) {
                // No physical target Connection kept yet ->
                // resolve transaction demarcation methods without fetching
                // a physical JDBC Connection until absolutely necessary.
                if (method.getName().equals("toString")) {
                    return "RW Routing DataSource Proxy";
                } else if (method.getName().equals("isReadOnly")) {
                    return this.readOnly;
                } else if (method.getName().equals("setReadOnly")) {
                    this.readOnly = (Boolean) args[0];
                    return null;
                } else if (method.getName().equals("getTransactionIsolation")) {
                    if (this.transactionIsolation != null) {
                        return this.transactionIsolation;
                    }
                    return defaultTransactionIsolation;
                    // Else fetch actual Connection and check there,
                    // because we didn't have a default specified.
                } else if (method.getName().equals("setTransactionIsolation")) {
                    this.transactionIsolation = (Integer) args[0];
                    return null;
                } else if (method.getName().equals("getAutoCommit")) {
                    if (this.autoCommit != null)
                        return this.autoCommit;
                    return defaultAutoCommit;
                    // Else fetch actual Connection and check there,
                    // because we didn't have a default specified.
                } else if (method.getName().equals("setAutoCommit")) {
                    this.autoCommit = (Boolean) args[0];
                    return null;
                } else if (method.getName().equals("commit")) {
                    // Ignore: no statements created yet.
                    return null;
                } else if (method.getName().equals("rollback")) {
                    // Ignore: no statements created yet.
                    return null;
                } else if (method.getName().equals("getWarnings")) {
                    return null;
                } else if (method.getName().equals("clearWarnings")) {
                    return null;
                } else if (method.getName().equals("isClosed")) {
                    return (this.closed ? Boolean.TRUE : Boolean.FALSE);
                } else if (method.getName().equals("close")) {
                    // Ignore: no target connection yet.
                    this.closed = true;
                    return null;
                } else if (this.closed) {
                    // Connection proxy closed, without ever having fetched a
                    // physical JDBC Connection: throw corresponding SQLException.
                    throw new SQLException("Illegal operation: connection is closed");
                }
            }
            return method.invoke(target, args);
        }

        private boolean hasTargetConnection() {
            return this.target != null;
        }

        private Object getTargetConnection(Method method, Object[] args) throws SQLException {
            if (this.target == null) {
                String key = (String) args[0];

                System.out.println(("Connecting to database for operation '" + method.getName() + "'"));
                //fetch physical connection from datasource
                this.target = (this.username != null) ? determineTargetDatasource(key).getConnection(this.username, this.password) :
                        determineTargetDatasource(key).getConnection();

                if (this.readOnly.booleanValue()) {
                    this.target.setReadOnly(this.readOnly.booleanValue());
                }
                if (this.transactionIsolation != null) {
                    this.target.setTransactionIsolation(transactionIsolation.intValue());
                }

                if (this.autoCommit != null && this.autoCommit.booleanValue() != this.target.getAutoCommit()) {
                    this.target.setAutoCommit(this.autoCommit.booleanValue());
                }

            }else{
                System.out.println("Using existing database connection for operation '" + method.getName() + "'");
            }

            return this.target;
        }
    }
}
