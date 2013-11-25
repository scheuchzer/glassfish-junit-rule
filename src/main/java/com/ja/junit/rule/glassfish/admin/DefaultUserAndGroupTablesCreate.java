package com.ja.junit.rule.glassfish.admin;

import java.sql.Connection;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import com.ja.junit.rule.glassfish.TestContext;

@Slf4j
public class DefaultUserAndGroupTablesCreate extends AbstractAdminObject {

	@Override
	public void execute(final TestContext ctx) throws Exception {

		DataSource dataSource = (DataSource) new InitialContext()
				.lookup("jdbc/__default");
		String createUserTable = "CREATE TABLE users (username varchar(255) NOT NULL, password varchar(255) DEFAULT NULL,PRIMARY KEY (username))";
		String createGroupTable = "CREATE TABLE groups (username varchar(255) DEFAULT NULL,groupname varchar(255) DEFAULT NULL)";
		try (Connection con = dataSource.getConnection()) {
			log.info("Create users table={}", createUserTable);
			con.prepareStatement(createUserTable).execute();
			log.info("Create group table={}", createGroupTable);
			con.prepareStatement(createGroupTable).execute();
			con.commit();
		}
		ctx.addTeardownCommand(new DefaultUserAndGroupTablesDelete());
	}
}
