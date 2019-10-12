package com.yotereparo.persistence;

import org.springframework.beans.factory.annotation.Value;

public class PostgreSQLHandler extends JDBCHandler {
	
	private static PostgreSQLHandler instance = null;
	
	@Value("${postgresql.server.host}") private String server;
	@Value("${postgresql.server.port}") private String port; 
	@Value("${postgresql.server.dbname}") private String dbname; 
	@Value("${postgresql.server.auth.user}") private String authUser;
	@Value("${postgresql.server.auth.pwd}") private String authPwd;
	
	private PostgreSQLHandler() {
		this.connectionUrl = String.format("postgres://%s:%s/%s", this.server, this.port, this.dbname);
	}
	
	public static PostgreSQLHandler getInstance() {
		if(instance == null) {
			instance = new PostgreSQLHandler();
		}
		return instance;
	}
	
	
}
