package com.gmail.calls.gmail_api;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;

import org.testng.annotations.Test;

public class GetEmails_WithSubject {
	private List<String> getResult1() {
		try {
			Properties props = new Properties();
			props.put("mail.store.protocol", "imaps");
			Session session = Session.getDefaultInstance(props, null);
			Store store = session.getStore("imaps");
			store.connect("imap.gmail.com", "abc@gmail.com", "MyPasrd1");

			// if you want mail from specified folder, just change change folder name
			// Folder inbox = store.getFolder("[Gmail]/Drafts");
			Folder inbox = store.getFolder("inbox");

			inbox.open(Folder.READ_ONLY);
			int messageCount = inbox.getMessageCount();
			System.out.println("getFolder getResult1: " + store.getDefaultFolder().list("*"));
			

			System.out.println("Mail Subject:Total Messages:-: " + messageCount);
			javax.mail.Message[] messages = inbox.getMessages();

			System.out.println("------------------------------");
			System.out.println("Mail Subject:messages: " + messages.toString());
			
			Calendar cal = Calendar.getInstance();			
			cal.add(Calendar.MINUTE, -10);
			
			Date tenMinutesBack = cal.getTime();
			System.out.println(tenMinutesBack);
			for (int i = 0; i < messages.length; i++) {
				if (messages[i].getSubject().contains("Are you happy with our service?")) {
					// for (int i = 0; i < 2; i++) {
					System.out.println("Mail Subject:getResult1: " + messages[i].getSubject());
					System.out.println("Text: " + getTextFromMessage(messages[i]));
					System.out.println("ReceiveDate: " + messages[i].getReceivedDate());
					System.out.println("ReceiveDate boolean: " + messages[i].getReceivedDate().after(tenMinutesBack));

				}
			}
			inbox.close(true);
			store.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
	private  String getTextFromMessage(Message message) throws MessagingException, IOException {
	    String result = "";
	    if (message.isMimeType("text/plain")) {
	        result = message.getContent().toString();
	    } else if (message.isMimeType("multipart/*")) {
	        MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
	        result = getTextFromMimeMultipart(mimeMultipart);
	        System.out.println("text message is   "+result);
	    }
	    return result;
	}

	private  String getTextFromMimeMultipart(
	        MimeMultipart mimeMultipart)  throws MessagingException, IOException{
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
	        } else if (bodyPart.getContent() instanceof MimeMultipart){
	            result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
	          //  System.out.println("HMTL message is   "+result);
	        }
	    }
	    return result;
	}
	@Test
	public void abc() {

		getResult1();
	}
}
