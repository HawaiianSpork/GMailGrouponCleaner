package com.nihmarch;

import java.util.TimerTask;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GMailCleanTask extends TimerTask {

	private static final Logger logger = LoggerFactory.getLogger(GMailCleaner.class);

	private final String username;
	private final char[] password;
	
	public GMailCleanTask(String username, char[] password) {
		this.username = username;
		this.password = password;
	}
	
	@Override
	public void run() {

		Store store = null;
		try {
			store = GMailUtils.createSession().getStore("imaps");
			logger.info("Connecting ");
			store.connect("imap.gmail.com", username, new String(password));
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

}
