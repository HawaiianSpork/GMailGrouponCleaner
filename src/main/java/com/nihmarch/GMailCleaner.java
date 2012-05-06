package com.nihmarch;

import java.util.Date;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.FromTerm;
import javax.mail.search.SentDateTerm;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GMailCleaner implements Daemon {

	private static final Logger logger = LoggerFactory.getLogger(GMailCleaner.class);
	
	public static void main(String[] args) {
		
		if (args.length != 2) {
			System.err.println("Usage: GMailCleaner <username> <password>");
			System.exit(2);
		}
		
		String username = args[0];
		String password = args[1];
		
		Store store = null;
		try {
			store = GMailUtils.createSession().getStore("imaps");
			logger.info("Connecting ");
			store.connect("imap.gmail.com", username, password);
			logger.info("Connected");
			
			Folder inbox = store.getFolder("INBOX");
			Folder trash = GMailUtils.getTrashFolder(store);
			if (trash == null) {
				logger.error("Cannot find GMail trash folder.");
				return;
			}

			inbox.open(Folder.READ_WRITE);
			GMailUtils.deleteFromSender( inbox, trash, "mail@e.groupon.com");
			GMailUtils.deleteFromSender( inbox, trash, "LocalDeals@amazon.com");
			GMailUtils.deleteFromSender( inbox, trash, "google-offers@offers.google.com");

		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (store != null) {
				try {
					store.close();
					logger.info("Connection closed");
				} catch (MessagingException e) {
					logger.warn("Error closing connection", e);
				}
			}
		}	
	}


	@Override
	public void init(DaemonContext context) throws DaemonInitException,
			Exception {
		// Nothing to do
	}


	@Override
	public void start() throws Exception {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void destroy() {
		// Nothing to do
	}
}
