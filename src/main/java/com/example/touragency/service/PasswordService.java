package com.example.touragency.service;


import com.example.touragency.model.Password_Reset_Token;
import com.example.touragency.model.User;
import com.example.touragency.repository.Password_Reset_TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
public class PasswordService {

    @Autowired
    private Password_Reset_TokenRepository Password_Reset_TokenRepository;

    @Autowired
    private EmailService emailService;

    private static final int EXPIRATION = 60 * 24;

    public void createPassword_Reset_Token(Long user) {
        String token = UUID.randomUUID().toString();
        Password_Reset_Token Password_Reset_Token = new Password_Reset_Token();
        Password_Reset_Token.setToken(token);
        Password_Reset_Token.setUser(user);
        LocalDateTime localDateTime=LocalDateTime.now().plusMinutes(EXPIRATION);
        Date date=Timestamp.valueOf(localDateTime);
        Timestamp timestamp=new Timestamp(date.getTime());
        Password_Reset_TokenRepository.saveToken(user,token,timestamp);
    }

    public void sendPasswordResetEmail(User user) throws MessagingException, IOException {
        Password_Reset_Token password_Reset_Token = getPassword_Reset_Token(user.getId());
        if (password_Reset_Token == null) {
            throw new IllegalStateException("Password reset token not found");
        }
        String recipientAddress = user.getEmail();
        String subject = "Password reset request";
        String message = "Please click on the following link to reset your password: "
                + "http://localhost:3000/reset-password?token=" + password_Reset_Token.getToken();
        emailService.sendEmail(recipientAddress, subject, message);
    }

    public Password_Reset_Token getPassword_Reset_Token(Long user) {
        return Password_Reset_TokenRepository.findByUserId(user);
    }

    public Password_Reset_Token getPassword_Reset_Token(String token) {
        return Password_Reset_TokenRepository.findByToken(token);
    }

    public void deletePassword_Reset_Token(Long user) {
        Password_Reset_TokenRepository.deleteByUserId(user);
    }
}