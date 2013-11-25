package com.ja.junit.rule.glassfish.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.ja.junit.rule.glassfish.TestContext;

@Slf4j
@RequiredArgsConstructor
public class DefaultUserAndGroupTableEntryDelete extends AbstractAdminObject {

	private final String username;

	private Connection getConnection() throws NamingException, SQLException {
		DataSource dataSource = (DataSource) new InitialContext()
				.lookup("jdbc/__default");
		return dataSource.getConnection();
	}

	@Override
	public void execute(final TestContext ctx) throws Exception {
		log.info("removing user {}", username);
		try (Connection con = getConnection()) {
			PreparedStatement ps = con
					.prepareStatement("delete from users where username=?");
			ps.setString(1, username);
			ps.execute();
			ps = con.prepareStatement("delete from groups where username=?");
			ps.setString(1, username);
			ps.execute();
		}
	}
}
