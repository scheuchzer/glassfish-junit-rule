package com.ja.junit.rule.glassfish;

import static org.junit.Assert.fail;

import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

import org.glassfish.embeddable.GlassFishProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpExecutor {
	private final Logger log = LoggerFactory.getLogger(HttpExecutor.class);
	private final String path;
	private final String username;
	private final String password;
	private GlassFishProperties props;
	public HttpExecutor(final String path,
			final String username, final String password, final GlassFishProperties props) {
				this.path = path;
				this.username = username;
				this.password = password;
				this.props = props;
		
	}
	public HttpURLConnection execute() {
		if (username != null) {
			log.info("Setting up username and password");
			Authenticator.setDefault(new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password
							.toCharArray());
				}
			});
		}
		String url = String.format("http://localhost:%s/%s",
				props.getPort("http-listener"), path);
		log.info("Calling url={}", url);
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) new URL(url).openConnection();
			log.info("Response={}", con.getResponseMessage());
		} catch (Exception e) {
			log.error("HTTP request failed.", e);
			fail();
		} finally {
			Authenticator.setDefault(null);
		}
		return con;
	}
}
