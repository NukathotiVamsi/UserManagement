package com.example.springboot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.springboot.entity.User;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender javaMailSender;

	public void sendEmail(String emailId, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();

		message.setTo("javalucky88@gmail.com");
		message.setSubject("Mail Send");
		message.setText(text);
		System.out.println(text);
		javaMailSender.send(message);
	}

	public void sendUserDetailsEmail(User user) {
		String subject = "User Registration";
		String text = "Welcome, your account has been successfully created!\n\n" + "User Details:\n" + "First Name: "
				+ user.getFirstName() + "\n" + "Last Name: " + user.getLastName() + "\n" + "Email: " + user.getEmailId()
				+ "\n" + "Phone: " + user.getPhoneNo() + "\n";

		sendEmail(user.getEmailId(), subject, text);
	}

}
