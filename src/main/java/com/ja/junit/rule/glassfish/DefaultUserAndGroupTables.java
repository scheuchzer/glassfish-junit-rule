package com.ja.junit.rule.glassfish;

import java.sql.Connection;
import java.sql.Statement;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultUserAndGroupTables {

	private Logger log = LoggerFactory
			.getLogger(DefaultUserAndGroupTables.class);

	private TestContext ctx;

	public DefaultUserAndGroupTables(TestContext ctx) {
		this.ctx = ctx;
	}

	public void create() {
		ctx.add(new StartupCommand() {

			@Override
			public void execute() throws Exception {

				DataSource dataSource = (DataSource) new InitialContext()
						.lookup("jdbc/__default");
				String createUserTable = "CREATE TABLE users (username varchar(255) NOT NULL, password varchar(255) DEFAULT NULL,PRIMARY KEY (username))";
				String createGroupTable = "CREATE TABLE groups (username varchar(255) DEFAULT NULL,groupname varchar(255) DEFAULT NULL)";
				try (Connection con = dataSource.getConnection()) {
					log.info("Create users table={}", createUserTable);
					con.prepareStatement(createUserTable).execute();
					log.info("Create group tabls={}", createGroupTable);
					con.prepareStatement(createGroupTable).execute();
					con.commit();
				}
			}
		});
		ctx.add(new TeardownCommand() {

			@Override
			public void execute() throws Exception {
				delete();
			}
		});
	}

	public void delete() {
		ctx.add(new StartupCommand() {

			@Override
			public void execute() throws Exception {
				DataSource dataSource = (DataSource) new InitialContext()
						.lookup("jdbc/__default");
				try (Connection con = dataSource.getConnection()) {
					Statement s = con.createStatement();
					s.execute("SELECT * from users");
					log.info("Resetting users and groups tables.");
					s.execute("DROP TABLE groups");
					s.execute("DROP TABLE users");
					con.commit();
				} catch (Exception e) {
					// ignore
				}
			}
		});
	}
}
