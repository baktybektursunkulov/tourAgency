package com.example.touragency.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "password_reset_token")
@Data
public class Password_Reset_Token {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token")
    private String token;

    @Column(name = "user")
    private Long user;

    @Column(name = "expiry_date")
    private Timestamp expiry_date;

    public boolean isExpired(Date currentTime) {
        return (expiry_date.getTime() - currentTime.getTime()) < 0;
    }
}

