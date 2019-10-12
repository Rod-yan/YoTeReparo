package com.yotereparo.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public abstract class JDBCHandler {

	protected static Connection connection = null;
	private static final Logger logger = LogManager.getLogger(JDBCHandler.class);
	
	protected String connectionUrl;
	protected String authUser;
	protected String authPwd;
	
	public Connection getConnection() {	
		if (connection == null) {
			return this.connect();
		}
		return connection;
	}
	
	private Connection connect() {
		Connection connection = null;
		try {
			logger.debug(String.format("SQL: Attempting connection to %s", this.connectionUrl));
			connection = DriverManager.getConnection(String.format("jdbc:%s", this.connectionUrl), this.authUser, this.authPwd);
		}
		catch (SQLException e) {
			logger.error(String.format("SQL Error: Connection failed - %s\n%s", e.getSQLState(), e.getMessage()));
		}
		catch (Exception e) {
            e.printStackTrace();
        }
		
		logger.info("SQL: Connection was successful");
		return connection;
	}
}
