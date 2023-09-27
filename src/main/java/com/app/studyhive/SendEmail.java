package com.app.studyhive;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Properties;

public class SendEmail {
    public static void main(String[] args) {
        System.out.println("Preparing to send message");
        String message = "This is a Automated message, do not reply\n\nThe Email integration for Registration OTP is SUCCESSFUL.";
        String subject = "StudyHive: Email Integration Successful";
        String to = "kevinxaviernadar@student.sfit.ac.in";
        String from = "ajaykumar.shopify.mails@gmail.com";

        sendAttach(message, subject, to, from);
    }


    //this is responsible to send the message with attachment
    private static void sendAttach(String message, String subject, String to, String from) {

        //Variable for gmail
        String host="smtp.gmail.com";

        //get the system properties
        Properties properties = System.getProperties();
        System.out.println("PROPERTIES "+properties);

        //setting important information to properties object

        //host set
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.put("mail.smtp.ssl.ciphers", "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256"); // Use an appropriate cipher suite


        //Step 1: to get the session object..
        Session session=Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("ajaykumar.shopify.mails@gmail.com", "atgg hzpp mqfb wvtz");
            }
        });

        session.setDebug(true);

        //Step 2 : compose the message [text,multi media]
        MimeMessage m = new MimeMessage(session);

        try {
            //from email
            m.setFrom(new InternetAddress(from, "Ajaykumar Nadar"));
            //adding recipient to message
            m.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(to));
            //adding subject to message
            m.setSubject(subject);

            //attachement..

            //file path
            String path="C:\\Users\\Ajay kumar\\Desktop\\studyhive-high-resolution-color-logo.png";

            MimeMultipart mimeMultipart = new MimeMultipart();
            //text
            //file
            MimeBodyPart textMime = new MimeBodyPart();
            MimeBodyPart fileMime = new MimeBodyPart();

            try {
                textMime.setText(message);
                File file=new File(path);
                fileMime.attachFile(file);
                mimeMultipart.addBodyPart(textMime);
                mimeMultipart.addBodyPart(fileMime);
            } catch (Exception e) {
                e.printStackTrace();
            }
            m.setContent(mimeMultipart);

            //send
            //Step 3 : send the message using Transport class
            Transport.send(m);

        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Sent success...................");
    }


    //    This method is responsible to send email.
    private static void sendText(String message, String subject, String to, String from) {

//        Variable for gmail host
        String host = "smtp.gmail.com";

//        get the system properties
        Properties properties = System.getProperties();
        System.out.println("PROPERTIES " + properties);

//        setting important information to properties object
//        setting host
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.put("mail.smtp.ssl.ciphers", "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256"); // Use an appropriate cipher suite


//        properties.put("mail.smtp.EnableSSL.enable","true");

//        Step1: to get the session object
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("ajaykumar.shopify.mails@gmail.com", "atgg hzpp mqfb wvtz");
            }
        });
        session.setDebug(true);

//        Step 2: compose the message [text, multimedia]
        MimeMessage m = new MimeMessage(session);

        try {
            // from email id
            m.setFrom(new InternetAddress(from, "Ajaykumar"));


//            Adding recipient
            m.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(to));

//            Adding subject to message
            m.setSubject(subject);

//            adding text to message
            m.setText(message);

//           send
//            Step 3: sent the message using Transport class
            Transport.send(m);
            System.out.println("Sent Successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}