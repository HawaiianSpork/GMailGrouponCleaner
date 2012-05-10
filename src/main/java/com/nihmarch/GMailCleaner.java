package com.nihmarch;

import java.util.Timer;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GMailCleaner implements Daemon {

	private static final Logger logger = LoggerFactory.getLogger(GMailCleaner.class);
	private static final Timer timer = new Timer();
	private static final long twoHoursAnd17Minutes = (2*60 + 17) * 60 * 1000;
	
	public static void main(String[] args) {
		
		if (args.length != 2) {
			System.err.println("Usage: GMailCleaner <username> <password>");
			System.exit(2);
		}
		
		String username = args[0];
		String password = args[1];
		
		GMailCleanTask cleanTask = new GMailCleanTask(username, password.toCharArray());
		cleanTask.run();	
	}


	@Override
	public void init(DaemonContext context) throws DaemonInitException,
			Exception {
		// Nothing to do
	}


	@Override
	public void start() throws Exception {
		logger.info("Starting");
		GMailCleanerConfig config = GMailCleanerConfig.load();
		GMailCleanTask cleanTask = new GMailCleanTask(config.getUsername(), config.getPassword());
		cleanTask.run();
		timer.schedule(cleanTask, twoHoursAnd17Minutes);
	}


	@Override
	public void stop() throws Exception {
		logger.info("Stopping");
		timer.cancel();
	}


	@Override
	public void destroy() {
		// Nothing to do
	}
	

}
