package com.ja.junit.rule.glassfish;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.Collection;
import java.util.Stack;

import lombok.extern.slf4j.Slf4j;

import org.glassfish.embeddable.CommandResult;
import org.glassfish.embeddable.CommandRunner;
import org.glassfish.embeddable.Deployer;
import org.glassfish.embeddable.GlassFishException;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.junit.rules.TemporaryFolder;

import com.ja.junit.rule.glassfish.admin.AbstractAdminObject;

@Slf4j
public class TestContext {

	private final TemporaryFolder tmpFolder;
	private final GlassfishFuture gf;
	private final Collection<AbstractAdminObject> startupCommands;
	private final Stack<AbstractAdminObject> teardownCommands;

	public TestContext(TemporaryFolder tmpFolder,
			Collection<AbstractAdminObject> startupCommands,
			Stack<AbstractAdminObject> teardownCommands, GlassfishFuture gf) {
		this.gf = gf;
		this.startupCommands = startupCommands;
		this.teardownCommands = teardownCommands;
		this.tmpFolder = tmpFolder;
	}

	public Deployer getDeployer() {
		try {
			return gf.get().getService(Deployer.class, null);
		} catch (Exception e) {
			log.error("GlassFish not ready, yet.", e);
			fail();
		}
		return null;
	}

	public CommandRunner getCommandRunner() {
		try {
			return gf.get().getCommandRunner();
		} catch (Exception e) {
			log.error("GlassFish not ready, yet.", e);
			fail();
		}
		return null;
	}

	/**
	 * Adds a command to the list of commands that get executed at the startup
	 * time of glassfish. If glassfish is already running the command will be
	 * executed shortly.
	 * 
	 * @param startupCommand
	 */
	public void addStartCommand(AbstractAdminObject startupCommand) {
		if (startupCommand != null) {
			if (gf.get() != null) {
				try {
					startupCommand.execute(this);
				} catch (Exception e) {
					log.error("StartupCommand failed.", e);
					fail();
				}
			} else {
				startupCommands.add(startupCommand);
			}
		}
	}

	public void addTeardownCommand(AbstractAdminObject teardownCommand) {
		if (teardownCommand != null) {
			teardownCommands.add(teardownCommand);
		}
	}

	public CommandResult runCommand(String cmd, String... args) {
		return getCommandRunner().run(cmd, args);
	}

	public void undeploy(String appName) {
		try {
			getDeployer().undeploy(appName);
		} catch (GlassFishException e) {
			log.error("Undeploy of " + appName + " failed", e);
		}

	}

	public void deploy(Archive<?> archive) {
		try {
			ZipExporter exporter = archive.as(ZipExporter.class);
			File file = tmpFolder.newFile(archive.getName());
			exporter.exportTo(file, true);
			final String appName = getDeployer().deploy(file);
			log.info("Application {} deployed as {}", archive.getName(),
					appName);
		} catch (Exception e) {
			throw new RuntimeException("Deployment failed", e);
		}
	}

}
