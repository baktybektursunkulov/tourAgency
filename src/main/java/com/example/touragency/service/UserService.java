package com.example.touragency.service;



import com.example.touragency.model.User;

import java.util.List;



public interface UserService {

    User register(User user);

    List<User> getAll();

    User findByUsername(String username);

    User findByEmail(String email);


    User findById(Long id);

    void delete(Long id);

    void updatePassword(Long user, String password);
}
