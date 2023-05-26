package com.example.touragency.service;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
public class MyEmailSender extends JavaMailSenderImpl {
}
