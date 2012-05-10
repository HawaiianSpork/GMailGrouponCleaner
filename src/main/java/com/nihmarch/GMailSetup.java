package com.nihmarch;

import java.io.Console;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class GMailSetup {

	public static void main(String[] args) throws IOException, GeneralSecurityException {
		Console console = System.console();
		if (console == null) {
			System.err.println("Could not get console.  Exiting...");
			System.exit(2);
		}
		GMailCleanerConfig config = new GMailCleanerConfig();
		config.setUsername(console.readLine("Email Address (example user@gmail.com):"));
		config.setPassword(console.readPassword("Password:"));
		config.store();
	}

}
