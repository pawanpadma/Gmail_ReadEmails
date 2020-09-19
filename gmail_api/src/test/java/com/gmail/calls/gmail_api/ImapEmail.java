package com.gmail.calls.gmail_api;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Session;
import javax.mail.Store;

import org.testng.annotations.Test;

public class ImapEmail {

	private List<String> getResult1() {
		try {
			Properties props = new Properties();
			props.put("mail.store.protocol", "imaps");
			Session session = Session.getDefaultInstance(props, null);
			Store store = session.getStore("imaps");
			store.connect("imap.gmail.com", "emailde3@gmail.com", "myPrd1");

			// if you want mail from specified folder, just change change folder name
			// Folder inbox = store.getFolder("[Gmail]/Drafts");
			Folder inbox = store.getFolder("inbox");

			inbox.open(Folder.READ_ONLY);
			int messageCount = inbox.getMessageCount();
			System.out.println("getFolder getResult1: " + store.getDefaultFolder().list("*"));
			javax.mail.Folder[] folders = store.getDefaultFolder().list("*");

			for (javax.mail.Folder folder : folders) {
				if ((folder.getType() & javax.mail.Folder.HOLDS_MESSAGES) != 0) {
					System.out.println("getFolder getResult1: " + folder.getName());
				}
			}

			System.out.println("Mail Subject:Total Messages:-: " + messageCount);
			javax.mail.Message[] messages = inbox.getMessages();

			System.out.println("------------------------------");
			System.out.println("Mail Subject:messages: " + messages.toString());
			/*
			 * SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			 * Date date = new Date();
			 * 
			 * Date date1 = formatter.parse(date.toString());
			 * System.out.println(formatter.format(date));
			 */
			Calendar cal = Calendar.getInstance();
			// remove next line if you're always using the current time.
			//cal.setTime(currentDate);
			cal.add(Calendar.MINUTE, -10);
			
			Date oneHourBack = cal.getTime();
			System.out.println(oneHourBack);
			for (int i = 0; i < messages.length; i++) {
				if (messages[i].getSubject().contains("Welcome to Go Rest")) {
					// for (int i = 0; i < 2; i++) {
					System.out.println("Mail Subject:getResult1: " + messages[i].getSubject());
					System.out.println("Text: " + messages[i].getContent().toString());
					System.out.println("ReceiveDate: " + messages[i].getReceivedDate());
					System.out.println("ReceiveDate boolean: " + messages[i].getReceivedDate().after(oneHourBack));

				}
			}
			inbox.close(true);
			store.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Test
	public void abc() {

		getResult1();
	}
}
