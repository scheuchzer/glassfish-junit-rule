package com.ja.junit.rule.glassfish;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ja.security.PasswordHash;

public class DefaultUserAndGroupTableEntry {

	private Logger log = LoggerFactory
			.getLogger(DefaultUserAndGroupTableEntry.class);

	private TestContext ctx;

	public DefaultUserAndGroupTableEntry(TestContext ctx) {
		this.ctx = ctx;
	}

	private Connection getConnection() throws NamingException, SQLException {
		DataSource dataSource = (DataSource) new InitialContext()
				.lookup("jdbc/__default");
		return dataSource.getConnection();
	}

	public void create(final String username, final String password,
			final String... roles) {
		ctx.add(new StartupCommand() {

			@Override
			public void execute() throws Exception {

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
			}
		});
		ctx.add(new TeardownCommand() {

			@Override
			public void execute() throws Exception {
				delete(username);
			}
		});
	}

	public void delete(final String username) {
		ctx.add(new StartupCommand() {

			@Override
			public void execute() throws Exception {
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
		});
	}
}
