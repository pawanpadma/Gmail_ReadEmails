package com.gmail.calls.gmail_api;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.ArrayUtils;
import org.testng.annotations.Test;

public class ReadLinkFromEmail {

	public static int getMessageCount() {
		int messageCount = 0;
		Folder inbox;
		Properties props = new Properties();
		props.put("mail.store.protocol", "imaps");
		Session session = Session.getDefaultInstance(props, null);
		Store store;
		try {
			store = session.getStore("imaps");

			store.connect("imap.gmail.com", "ds@gmail.com", "dd@team");
			inbox = store.getFolder("inbox");

			inbox.open(Folder.READ_ONLY);

			messageCount = inbox.getMessageCount();
			inbox.close(true);
			store.close();
		} catch (Exception e) {

		}

		return messageCount;
	}

	public static boolean validateEmail(Date date, String subject) {
		boolean isPresent = false;
		try {
			Properties props = new Properties();
			props.put("mail.store.protocol", "imaps");
			Session session = Session.getDefaultInstance(props, null);
			Store store = session.getStore("imaps");
			store.connect("imap.gmail.com", "ss@gmail.com", "s@team");

			// if you want mail from specified folder, just change change folder name
			// Folder inbox = store.getFolder("[Gmail]/Drafts");
			Folder inbox = store.getFolder("inbox");

			inbox.open(Folder.READ_ONLY);
			// int messageCount = inbox.getMessageCount();

			javax.mail.Message[] messages = inbox.getMessages();

			ArrayUtils.reverse(messages);

			// for (int i = 0; i < messages.length; i++) {
			for (int i = 0; i < 3; i++) {
				System.out.println("Mail Subject:getResult1: " + messages[i].getSubject());
				System.out.println("ReceiveDate: " + messages[i].getReceivedDate());

				if (messages[i].getSubject().contains(subject)) {
					System.out.println("date sent is  " + date);
					// for (int i = 0; i < 2; i++) {
					System.out.println("Mail Subject:getResult1: " + messages[i].getSubject());
					System.out.println("Text: " + getTextFromMessage(messages[i]));
					System.out.println("ReceiveDate: " + messages[i].getReceivedDate());
					// System.out.println("ReceiveDate boolean: " +
					// messages[i].getReceivedDate().after(date));
					isPresent = messages[i].getReceivedDate().after(date);
					break;

				}
			}
			inbox.close(true);
			store.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isPresent;

	}

	public static String readBody(String subject, String userName, String password) {
		boolean isPresent = false;
		String message = null;
		try {
			Properties props = new Properties();
			props.put("mail.store.protocol", "imaps");
			Session session = Session.getDefaultInstance(props, null);
			Store store = session.getStore("imaps");
			store.connect("imap.gmail.com", userName, password);

			Folder inbox = store.getFolder("inbox");

			inbox.open(Folder.READ_ONLY);
			javax.mail.Message[] messages = inbox.getMessages();

			ArrayUtils.reverse(messages);

			for (int i = 0; i < messages.length; i++) {

				if (messages[i].getSubject().contains(subject)) {

					message = getTextFromMessage(messages[i]);
					break;

				}
			}
			inbox.close(true);
			store.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;

	}

	public static String readBodyWithSpecificText(String subject, String userName, String password, String BodyText) {
		boolean isPresent = false;
		String message = null;
		String url = null;
		try {
			Properties props = new Properties();
			props.put("mail.store.protocol", "imaps");
			Session session = Session.getDefaultInstance(props, null);
			Store store = session.getStore("imaps");
			store.connect("imap.gmail.com", userName, password);

			Folder inbox = store.getFolder("inbox");

			inbox.open(Folder.READ_ONLY);
			javax.mail.Message[] messages = inbox.getMessages();

			ArrayUtils.reverse(messages);

			for (int i = 0; i < messages.length; i++) {

				if (messages[i].getSubject().contains(subject)) {

					message = getTextFromMessage(messages[i]);
					if (getTextFromMessage(messages[i]).contains(BodyText)) {
						
						break;
					}

				}
			}
			inbox.close(true);
			store.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;

	}

	private static String getTextFromMessage(Message message) throws MessagingException, IOException {
		String result = "";
		if (message.isMimeType("text/plain")) {
			result = message.getContent().toString();
		} else if (message.isMimeType("multipart/*")) {
			MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
			System.out.println("multipart is "+mimeMultipart);
			result = getTextFromMimeMultipart(mimeMultipart);
			//System.out.println("text message is   " + result);
		}
		return result;
	}

	private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
		String result = "";
		int count = mimeMultipart.getCount();
		for (int i = 0; i < count; i++) {
			BodyPart bodyPart = mimeMultipart.getBodyPart(i);
			if (bodyPart.isMimeType("text/plain")) {
				result = result + "\n" + bodyPart.getContent();
				break; // without break same text appears twice in my tests
			} else if (bodyPart.isMimeType("text/html")) {
				String html = (String) bodyPart.getContent();
				result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
			} else if (bodyPart.getContent() instanceof MimeMultipart) {
				result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
				// System.out.println("HMTL message is "+result);
			}
		}
		return result;
	}
	
	@Test
	public void abc() {
		String s=readBodyWithSpecificText("Fwd: Proposal Clarification: ttstdyj","emaildemo83","OKTesting","Reply buyer by email");
		System.out.println("text is   "+s);
	}
}
