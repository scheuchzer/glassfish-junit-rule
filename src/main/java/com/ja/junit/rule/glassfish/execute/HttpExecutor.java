package com.ja.junit.rule.glassfish.execute;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.glassfish.embeddable.GlassFishProperties;

import com.ja.junit.rule.glassfish.Response;

@Slf4j
public class HttpExecutor {
	private final String path;
	private final String username;
	private final String password;
	private final GlassFishProperties props;

	public HttpExecutor(final String path, final String username,
			final String password, final GlassFishProperties props) {
		this.path = path;
		this.username = username;
		this.password = password;
		this.props = props;

	}

	public Response execute() {
		try {
			HttpHost targetHost = new HttpHost("localhost",
					props.getPort("http-listener"), "http");
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(new AuthScope(
					targetHost.getHostName(), targetHost.getPort()),
					new UsernamePasswordCredentials(username, password));
			CloseableHttpClient httpclient = HttpClients
					.custom()
					.setDefaultCredentialsProvider(credsProvider)
					.setConnectionReuseStrategy(new NoConnectionReuseStrategy())
					.build();
			try {

				// Create AuthCache instance
				AuthCache authCache = new BasicAuthCache();
				// Generate BASIC scheme object and add it to the local
				// auth cache
				BasicScheme basicAuth = new BasicScheme();
				authCache.put(targetHost, basicAuth);

				// Add AuthCache to the execution context
				HttpClientContext localContext = HttpClientContext.create();
				localContext.setAuthCache(authCache);

				HttpGet httpget = new HttpGet(path);

				System.out.println("executing request: "
						+ httpget.getRequestLine());
				System.out.println("to target: " + targetHost);

				CloseableHttpResponse response = httpclient.execute(targetHost,
						httpget, localContext);
				try {
					HttpEntity entity = response.getEntity();

					System.out.println(response.getStatusLine());
					if (entity != null) {
						System.out.println("Response content length: "
								+ entity.getContentLength());
					}
					return Response.create(response.getStatusLine()
							.getStatusCode(), entity);
				} finally {
					response.close();
					authCache.clear();
					credsProvider.clear();
				}
			} finally {
				httpclient.close();

			}

		} catch (Exception e) {
			log.error("HTTP call failed.", e);
		}
		return null;

		/*
		 * log.info("Setting up username and password");
		 * Authenticator.setDefault(new Authenticator() {
		 * 
		 * @Override protected PasswordAuthentication
		 * getPasswordAuthentication() { return new
		 * PasswordAuthentication(username, password .toCharArray()); } });
		 * String url = String.format("http://localhost:%s/%s",
		 * props.getPort("http-listener"), path); log.info("Calling url={}",
		 * url); HttpURLConnection con = null; try { con = (HttpURLConnection)
		 * new URL(url).openConnection(); log.info("Response={}",
		 * con.getResponseMessage()); } catch (Exception e) {
		 * log.error("HTTP request failed.", e); fail(); } return con;
		 */
	}
}
