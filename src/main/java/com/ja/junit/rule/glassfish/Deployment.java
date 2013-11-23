package com.ja.junit.rule.glassfish;

import org.jboss.shrinkwrap.api.Archive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Deployment {

	private Logger log = LoggerFactory.getLogger(Deployment.class);

	private TestContext ctx;

	public Deployment(TestContext ctx) {
		this.ctx = ctx;
	}

	public void create(final Archive<?> archive) {
		ctx.add(new StartupCommand() {

			@Override
			public void execute() throws Exception {
				ctx.deploy(archive);
			}
		});
		ctx.add(new TeardownCommand() {

			@Override
			public void execute() throws Exception {
				delete(archive.getName());
			}
		});

	}

	public void delete(final String appName) {
		log.info("Undeploy {}", appName);
		ctx.add(new StartupCommand() {

			@Override
			public void execute() throws Exception {
				ctx.undeploy(appName);
			}
		});
	}

}
