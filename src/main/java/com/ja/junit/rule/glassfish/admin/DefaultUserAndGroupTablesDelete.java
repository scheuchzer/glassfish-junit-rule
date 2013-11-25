package com.ja.junit.rule.glassfish.admin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import com.ja.junit.rule.glassfish.TestContext;

@Slf4j
public class DefaultUserAndGroupTablesDelete extends AbstractAdminObject {

	@Override
	public void execute(final TestContext ctx) throws Exception {
		DataSource dataSource = (DataSource) new InitialContext()
				.lookup("jdbc/__default");
		try (Connection con = dataSource.getConnection()) {
			try (Statement s = con.createStatement();
					ResultSet rs = s.executeQuery("SELECT * from users")) {
				log.info("Users in db={}", s.getUpdateCount());
			}
			log.info("Resetting users and groups tables.");
			try (Statement s = con.createStatement()) {
				s.execute("DROP TABLE groups");
			}
			try (Statement s = con.createStatement()) {
				s.execute("DROP TABLE users");
			}
			con.commit();
		} catch (Exception e) {
			log.warn("Drop failed.", e);
		}
	}

}
