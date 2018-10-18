package io.thorntail.agroal.impl;

import io.agroal.api.AgroalDataSource;
import io.agroal.api.configuration.supplier.AgroalDataSourceConfigurationSupplier;
import io.agroal.api.security.NamePrincipal;
import io.agroal.api.security.SimplePassword;
import io.agroal.api.transaction.TransactionIntegration;
import io.agroal.narayana.NarayanaTransactionIntegration;
import io.thorntail.agroal.AgroalPoolMetaData;
import io.thorntail.jdbc.DriverMetaData;
import io.thorntail.jdbc.impl.JDBCDriverRegistry;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class AgroalProducer {

    private static final Logger log = Logger.getLogger(AgroalProducer.class.getName());

    private Class driver;
    private String dataSourceName;
    private String url;
    private String userName;
    private String password;
    private boolean jta = true;
    private boolean connectable;
    private boolean xa;

    private AgroalDataSource agroalDataSource;

    @Inject
    private TransactionManager transactionManager;

    @Inject
    private TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    @Inject
    private InitialContext jndi;

    @Inject
    JDBCDriverRegistry jdbcDriverRegistry;

    public AgroalDataSource deploy(AgroalPoolMetaData metaData) throws SQLException {
        this.setUrl(metaData.getConnectionUrl());

        DriverMetaData driverMetaData = jdbcDriverRegistry.get(metaData.getDriver());
        if ( driverMetaData == null) {
            AgroalMessages.MESSAGES.noRegisteredJDBCdrivers();
            return null;
        }
        String driverClassname = driverMetaData.getDriverClassName();
        try {
            this.setDriver(this.getClass().getClassLoader().loadClass(driverClassname));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.setUserName(metaData.getUsername());
        this.setPassword(metaData.getPassword());
        AgroalDataSource dataSource = getDatasource();

        try {
            jndi.bind(metaData.getJNDIName(), dataSource);
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return dataSource;
    }

    @Produces
    @ApplicationScoped
    public AgroalDataSource getDatasource() throws SQLException {
        Class<?> providerClass = driver;
        if (xa) {
            if (!XADataSource.class.isAssignableFrom(providerClass)) {
                throw new RuntimeException("Driver is not an XA datasource and xa has been configured");
            }
        } else {
            if (providerClass != null && !DataSource.class.isAssignableFrom(providerClass) && !Driver.class.isAssignableFrom(providerClass)) {
                throw new RuntimeException("Driver is an XA datasource and xa has been configured");
            }
        }
        AgroalDataSourceConfigurationSupplier dataSourceConfiguration = new AgroalDataSourceConfigurationSupplier();
        dataSourceConfiguration.connectionPoolConfiguration().connectionFactoryConfiguration().jdbcUrl(url);
        dataSourceConfiguration.connectionPoolConfiguration().connectionFactoryConfiguration().connectionProviderClass(providerClass);

        if (jta || xa) {
            TransactionIntegration txIntegration = new NarayanaTransactionIntegration(transactionManager, transactionSynchronizationRegistry, null, connectable);
            dataSourceConfiguration.connectionPoolConfiguration().transactionIntegration(txIntegration);
        }

        // use the name / password from the callbacks
        if (userName != null) {
            dataSourceConfiguration.connectionPoolConfiguration().connectionFactoryConfiguration().principal(new NamePrincipal(userName));
        }
        if (password != null) {
            dataSourceConfiguration.connectionPoolConfiguration().connectionFactoryConfiguration().credential(new SimplePassword(password));
        }

        agroalDataSource = AgroalDataSource.from(dataSourceConfiguration);
        log.log(Level.INFO, "Started data source " + url);
        return agroalDataSource;
    }

    @PreDestroy
    public void stop() {
        if (agroalDataSource != null) {
            agroalDataSource.close();
        }
    }

    public static Logger getLog() {
        return log;
    }

    public Class getDriver() {
        return driver;
    }

    public void setDriver(Class driver) {
        this.driver = driver;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isJta() {
        return jta;
    }

    public void setJta(boolean jta) {
        this.jta = jta;
    }

    public boolean isConnectable() {
        return connectable;
    }

    public void setConnectable(boolean connectable) {
        this.connectable = connectable;
    }

    public boolean isXa() {
        return xa;
    }

    public void setXa(boolean xa) {
        this.xa = xa;
    }

    public AgroalDataSource getAgroalDataSource() {
        return agroalDataSource;
    }

    public void setAgroalDataSource(AgroalDataSource agroalDataSource) {
        this.agroalDataSource = agroalDataSource;
    }

}
