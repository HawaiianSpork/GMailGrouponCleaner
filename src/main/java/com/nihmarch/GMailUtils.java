package com.nihmarch;

import java.util.Date;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.FromTerm;
import javax.mail.search.SentDateTerm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GMailUtils {

	private static final Logger logger = LoggerFactory.getLogger(GMailUtils.class);
	
	public static Session createSession() {
		Properties props = new Properties();
		props.setProperty("main.store.protocol", "imaps");
		
		Session session = Session.getDefaultInstance(props);
		return session;
	}
	
	
	public static Date yesterday() {
		Date date = new Date();
		return new Date( date.getTime() - 24 * 60 * 60 * 1000);
	}
	
	public static void deleteFromSender(Folder inbox, Folder trash, String sender) throws AddressException, MessagingException {
		logger.info("Getting messages from " + sender);
		Message messages[] = inbox.search(new AndTerm(
				new FromTerm(new InternetAddress(sender)),
				new SentDateTerm( ComparisonTerm.LT, yesterday())));
		
		
		if (logger.isTraceEnabled()) {
			for(Message message:messages) {
				logger.trace("Message " + message);
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
	
	public static Folder getTrashFolder(Store store) throws MessagingException {
		javax.mail.Folder[] folders = store.getDefaultFolder().list("*");
        for (Folder folder : folders) {
            if (((folder.getType() & javax.mail.Folder.HOLDS_MESSAGES) != 0) &&
                ("[Gmail]/Trash".equals(folder.getFullName()))) {
                return folder;
            }
        }
		return null;
	}
	
	
	private GMailUtils() { /* Utility, don't instantiate */ }
}
