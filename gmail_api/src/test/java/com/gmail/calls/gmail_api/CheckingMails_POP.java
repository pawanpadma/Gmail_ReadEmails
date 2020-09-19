package com.gmail.calls.gmail_api;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;



public class CheckingMails_POP {

    public static final String RECEIVED_HEADER_DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss Z";
public static final String RECEIVED_HEADER_REGEXP = "^[^;]+;(.+)$";


     public static void check(String host, String storeType, String user,
      String password) 
   {
      try {

      //create properties field
      Properties properties = new Properties();

      properties.put("mail.pop3.host", host);
      properties.put("mail.pop3.port", "995");
      properties.put("mail.pop3.starttls.enable", "true");
      Session emailSession = Session.getDefaultInstance(properties);

      //create the POP3 store object and connect with the pop server
      Store store = emailSession.getStore("pop3s");

      store.connect(host, user, password);

      //create the folder object and open it
      Folder emailFolder = store.getFolder("INBOX");
      emailFolder.open(Folder.READ_ONLY);

      // retrieve the messages from the folder in an array and print it
      Message[] messages = emailFolder.getMessages();
      System.out.println("messages.length---" + messages.length);

    //  for (int i = 0, n = messages.length; i < n; i++) {
    	  for (int i = 0, n = messages.length; i < 2; i++) {
         Message message = messages[i];
         getTextFromMessage(message);
         System.out.println("---------------------------------");
         System.out.println("Email Number " + (i + 1));
         System.out.println("Subject: " + message.getSubject());
         System.out.println("From: " + message.getFrom()[0]);
         System.out.println("Text: " + message.getContent().toString());
         System.out.println("ReceiveDate: " + message.getReceivedDate());


      }

      //close the store and folder objects
      emailFolder.close(false);
      store.close();

      } catch (NoSuchProviderException e) {
         e.printStackTrace();
      } catch (MessagingException e) {
         e.printStackTrace();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public static void main(String[] args) {

      String host = "pop.gmail.com";// change accordingly
      String mailStoreType = "pop3";
      String username = "emaildemo@gmail.com";// change accordingly
      String password = "abc";// change accordingly

      check(host, mailStoreType, username, password);

}

   public Date resolveReceivedDate(MimeMessage message) throws MessagingException {
    if (message.getReceivedDate() != null) {
        return message.getReceivedDate();
    }
    String[] receivedHeaders = message.getHeader("Received");
    if (receivedHeaders == null) {
        return (Calendar.getInstance().getTime());
    }
    SimpleDateFormat sdf = new SimpleDateFormat(RECEIVED_HEADER_DATE_FORMAT);
    Date finalDate = Calendar.getInstance().getTime();
    finalDate.setTime(0l);
    boolean found = false;
    for (String receivedHeader : receivedHeaders) {
        Pattern pattern = Pattern.compile(RECEIVED_HEADER_REGEXP);
        Matcher matcher = pattern.matcher(receivedHeader);
        if (matcher.matches()) {
            String regexpMatch = matcher.group(1);
            if (regexpMatch != null) {
                regexpMatch = regexpMatch.trim();
                try {
                    Date parsedDate = sdf.parse(regexpMatch);
                    //LogMF.debug(log, "Parsed received date {0}", parsedDate);
                    if (parsedDate.after(finalDate)) {
                        //finding the first date mentioned in received header
                        finalDate = parsedDate;
                        found = true;
                    }
                } catch (Exception e) {
                    //LogMF.warn(log, "Unable to parse date string {0}", regexpMatch);
                }
            } else {
                //LogMF.warn(log, "Unable to match received date in header string {0}", receivedHeader);
            }
        }
    }

    return found ? finalDate : Calendar.getInstance().getTime();
}
   
   private static String getTextFromMessage(Message message) throws MessagingException, IOException {
	    String result = "";
	    if (message.isMimeType("text/plain")) {
	        result = message.getContent().toString();
	    } else if (message.isMimeType("multipart/*")) {
	        MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
	        result = getTextFromMimeMultipart(mimeMultipart);
	       // System.out.println("text message is   "+result);
	    }
	    return result;
	}

	private static String getTextFromMimeMultipart(
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
}
