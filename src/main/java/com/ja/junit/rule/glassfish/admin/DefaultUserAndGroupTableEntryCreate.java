package com.ja.junit.rule.glassfish.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import com.ja.junit.rule.glassfish.TestContext;
import com.ja.security.PasswordHash;

@Slf4j
public class DefaultUserAndGroupTableEntryCreate extends AbstractAdminObject {

	private final String username;
	private final String password;
	private final String[] roles;

	public DefaultUserAndGroupTableEntryCreate(final String username,
			final String password, final String... roles) {
		this.username = username;
		this.password = password;
		this.roles = roles;

	}

	private Connection getConnection() throws NamingException, SQLException {
		DataSource dataSource = (DataSource) new InitialContext()
				.lookup("jdbc/__default");
		return dataSource.getConnection();
	}

	@Override
	public void execute(final TestContext ctx) throws Exception {
		try (Connection con = getConnection()) {
			String hash = new PasswordHash().createHash(password);
			PreparedStatement ps = con
					.prepareStatement("insert into users values(?, ?)");
			ps.setString(1, username);
			ps.setString(2, hash);
			ps.execute();

			for (String role : roles) {
				ps = con.prepareStatement("insert into groups values(?, ?)");
				ps.setString(1, username);
				ps.setString(2, role);
				ps.execute();
			}
			con.commit();
		}
		log.info("User {} created", username);
		ctx.addTeardownCommand(new DefaultUserAndGroupTableEntryDelete(username));
	}

}
