package io.thorntail.agroal;

import io.thorntail.jdbc.DriverMetaData;
import io.thorntail.TraceMode;

/**
 * Meta-data describing an agroal connection pool.
 *
 * @see DriverMetaData
 */
public class AgroalPoolMetaData {

    /**
     * Construct
     *
     * @param id The unique datasource identifier.
     */
    public AgroalPoolMetaData(String id) {
        this.id = id;
    }

    /**
     * Retrieve the unique identifier.
     *
     * @return The unique identifier.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Set the JDBC driver identifier.
     *
     * @param driver The driver identifier.
     * @return This meta-data object.
     * @see DriverMetaData#getId()
     */
    public AgroalPoolMetaData setDriver(String driver) {
        this.driver = driver;
        return this;
    }

    /**
     * Retrieve the JDBC driver identifier.
     *
     * @return The JDBC driver identifier.
     * @see DriverMetaData#getId()
     */
    public String getDriver() {
        return this.driver;
    }

    /**
     * Set the connection username, if required.
     *
     * @param username The connection username.
     * @return This meta-data object.
     */
    public AgroalPoolMetaData setUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * Retrieve the connection username.
     *
     * @return The connection username, or {@code null} if unset.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Set the connection, password if required.
     *
     * @param password The connection password.
     * @return This meta-data object.
     */
    public AgroalPoolMetaData setPassword(String password) {
        this.password = password;
        return this;
    }

    /**
     * Retrieve the connection password.
     *
     * @return The connectoin password, or {@code null} if unset.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Set the connection URL.
     *
     * @param connectionUrl The connection URL.
     * @return This meta-data object.
     */
    public AgroalPoolMetaData setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
        return this;
    }

    /**
     * Retrieve the connection URL.
     *
     * @return The connection URL.
     */
    public String getConnectionUrl() {
        return this.connectionUrl;
    }

    /**
     * Set the explicit JNDI name to which to bind this datasource.
     *
     * <p>If unset, a name will be calculated based upon {@link #getId()}.</p>
     *
     * @param jndiName The JNDI name to which to bind this datasource.
     * @return
     */
    public AgroalPoolMetaData setJNDIName(String jndiName) {
        this.jndiName = jndiName;
        return this;
    }

    /**
     * Retrieve the explicit JNDI name to which to bind this datasource.
     *
     * <p>If unset, a name will be calculated based upon {@link #getId()}.</p>
     *
     * @return The JNDI name to which to bind this datasource, or {@code null} if unset.
     */
    public String getJNDIName() {
        return this.jndiName;
    }

    /**
     * Enable tracing for this datasource.
     *
     * <p>Requires the {@code opentracing} component to be available if set to {@code true}.</p>
     *
     * @param trace Flag to determine if this datasource should be traced.
     */
    public void setTraceMode(TraceMode trace) {
        this.trace = trace;
    }

    /**
     * Determine if tracing is enabled for this datasource.
     *
     * <p>Requires the {@code opentracing} component to be available if set to {@code true}.</p>
     *
     * @return {@code true} if tracing is requested, otherwise {@code false}.
     */
    public TraceMode getTraceMode() {
        return this.trace;
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

    @Override
    public String toString() {
        return "[DS: id="+ this.id + "; driver=" + this.driver + "; jndiName=" + this.jndiName + "; trace=" + this.trace + "]";
    }

    private final String id;

    private String driver;

    private String username;

    private String password;

    private String jndiName;

    private String connectionUrl;

    private boolean jta = true;

    private boolean connectable;

    private boolean xa;


    private TraceMode trace = TraceMode.OFF;

}
