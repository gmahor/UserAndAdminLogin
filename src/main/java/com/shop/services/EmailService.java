package com.shop.services;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.dao.UserRepo;
import com.shop.entities.User;
import com.sun.mail.handlers.text_html;

@Service
public class EmailService {

	@Autowired
	private UserRepo userRepo;

	public boolean sendEmail(String to, String subject, String message) {

		boolean f = false;

		String from = "gouravmahor60@gmail.com";

		String host = "smtp.gmail.com";

		Properties properties = System.getProperties();

		User user = userRepo.getUserByEmail(to);

		// if user is not in the database

		System.out.println("SMTP Is Running....");

		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		Session session = Session.getInstance(properties, new Authenticator() {

			String username = "gouravmahor60@gmail.com";
			String password = "*******";

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {

				return new PasswordAuthentication(username, password);
			}

		});

//			session.setDebug(true);

		MimeMessage mimeMessage = new MimeMessage(session);

		try {

			mimeMessage.setFrom(from);

			mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			mimeMessage.setSubject(subject);

//				mimeMessage.setText(message);

			mimeMessage.setContent(message, "text/html");

			Transport.send(mimeMessage);

			System.out.println("Email Send Successfully...");

			f = true;

		} catch (Exception e) {

			e.printStackTrace();
		}

		return f;

	}

}
