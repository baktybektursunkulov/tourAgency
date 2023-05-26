package com.example.touragency.rest;

import com.example.touragency.model.Password_Reset_Token;
import com.example.touragency.model.User;
import com.example.touragency.service.PasswordService;
import com.example.touragency.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/password")
public class PasswordController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordService passwordService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) throws MessagingException, IOException {
        String email = request.get("email");
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("No user found with email " + email);
        }
        passwordService.createPassword_Reset_Token(user.getId());
        passwordService.sendPasswordResetEmail(user);
        return ResponseEntity.ok("Password reset email sent");
    }

    @GetMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token) {
        Password_Reset_Token Password_Reset_Token = passwordService.getPassword_Reset_Token(token);
        if (Password_Reset_Token == null || Password_Reset_Token.isExpired(new Date())) {
            return ResponseEntity.badRequest().body("Invalid or expired password reset token");
        }
        return ResponseEntity.ok("Password reset token is valid");
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String password = request.get("password");
        Password_Reset_Token Password_Reset_Token = passwordService.getPassword_Reset_Token(token);
        if (Password_Reset_Token == null || Password_Reset_Token.isExpired(new Date())) {
            return ResponseEntity.badRequest().body("Invalid or expired password reset token");
        }
        Long user = Password_Reset_Token.getUser();
        userService.updatePassword(user, password);
        passwordService.deletePassword_Reset_Token(user);
        return ResponseEntity.ok("Password reset successfully");
    }
}
