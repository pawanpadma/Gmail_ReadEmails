package com.gmail.calls.gmail_api;
import java.io.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * This class is used to reply email.
 * 
 * @author w3spoint
 */
public class EmailForwordTest {
	final String username = "emaildemo1983@gmail.com";
	final String password = "MyPassword1";
	final String receiverEmail = "pawanpadma@gmail.com";
	final String emailSMTPserver = "smtp.gmail.com";
	final String emailSMTPPort = "587";
	final String mailStoreType = "pop3s";

	public EmailForwordTest(String forwardedText) {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", emailSMTPserver);
		props.put("mail.smtp.port", emailSMTPPort);

		try {
			Authenticator auth = new SMTPAuthenticator();
			Session session = Session.getInstance(props, auth);

			Store mailStore = session.getStore(mailStoreType);
			mailStore.connect(emailSMTPserver, username, password);

			Folder folder = mailStore.getFolder("inbox");
			folder.open(Folder.READ_ONLY);

			Message[] messages = folder.getMessages();
			System.out.println("Total Message - " + messages.length);

			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

			for (int i = 0; i < messages.length; i++) {
				Message emailMessage = messages[i];
				System.out.println();
				System.out.println("Email " + (i + 1) + " -");
				System.out.println("Subject - " + emailMessage.getSubject());
				System.out.println("From - " + emailMessage.getFrom()[0]);
			}

			System.out.print("Enter email number to " + "which you want to forward: ");
			String emailNo = reader.readLine();

			Message emailMessage = folder.getMessage(Integer.parseInt(emailNo) - 1);

			Message mimeMessage = new MimeMessage(session);
			mimeMessage = (MimeMessage) emailMessage.reply(false);
			mimeMessage.setFrom(new InternetAddress(username));
			mimeMessage.setSubject("Fwd: " + mimeMessage.getSubject());
			mimeMessage.setText(forwardedText);
			mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmail));

			Transport.send(mimeMessage);
			System.out.println("Email message " + "forwarded successfully.");

			folder.close(false);
			mailStore.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error in forwarding email.");
		}
	}

	private class SMTPAuthenticator extends javax.mail.Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(username, password);
		}
	}

	public static void main(String[] args) {
		new EmailForwordTest("Forwarded Email Text.");
	}
}