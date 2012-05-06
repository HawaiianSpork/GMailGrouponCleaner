package com.nihmarch;

import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

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

public class GMailCleaner {

	private static final Logger logger = Logger.getLogger("com.nihmarch.GMailCleaner");
	
	public static void main(String[] args) {
		
		if (args.length != 2) {
			System.err.println("Usage: GMailCleaner <username> <password>");
			System.exit(2);
		}
		
		String username = args[0];
		String password = args[1];
		
		Store store = null;
		try {
			store = createSession().getStore("imaps");
			logger.info("Connecting ");
			store.connect("imap.gmail.com", username, password);
			logger.info("Connected");
			
			Folder inbox = store.getFolder("INBOX");
			Folder trash = getTrashFolder(store);
			if (trash == null) {
				System.err.println("Cannot find GMail trash folder.");
				return;
			}

			inbox.open(Folder.READ_WRITE);
			deleteFromSender( inbox, trash, "mail@e.groupon.com");
			deleteFromSender( inbox, trash, "LocalDeals@amazon.com");
			deleteFromSender( inbox, trash, "google-offers@offers.google.com");

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
					logger.log(Level.WARNING, "Error closing connection", e);
				}
			}
		}	
	}


	private static Session createSession() {
		Properties props = new Properties();
		props.setProperty("main.store.protocol", "imaps");
		
		Session session = Session.getDefaultInstance(props);
		return session;
	}
	
	
	private static Date yesterday() {
		Date date = new Date();
		return new Date( date.getTime() - 24 * 60 * 60 * 1000);
	}
	
	private static void deleteFromSender(Folder inbox, Folder trash, String sender) throws AddressException, MessagingException {
		logger.info("Getting messages from " + sender);
		Message messages[] = inbox.search(new AndTerm(
				new FromTerm(new InternetAddress(sender)),
				new SentDateTerm( ComparisonTerm.LT, yesterday())));
		
		if (logger.isLoggable(Level.FINER)) {
			for(Message message:messages) {
				logger.finer("Message " + message);
			}
		}
		
		logger.info("Deleting " + messages.length + " messages.");
		
		if (messages.length > 0) {
			// Delete 
			inbox.copyMessages(messages, trash);
			// To Archive
			//inbox.setFlags(messages, new Flags(Flag.DELETED), true);
		}
	}
	
	private static Folder getTrashFolder(Store store) throws MessagingException {
		javax.mail.Folder[] folders = store.getDefaultFolder().list("*");
        for (Folder folder : folders) {
            if (((folder.getType() & javax.mail.Folder.HOLDS_MESSAGES) != 0) &&
                ("[Gmail]/Trash".equals(folder.getFullName()))) {
                return folder;
            }
        }
		return null;
	}
}
