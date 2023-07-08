package com.example.touragency.service.impl;

import com.example.touragency.model.Role;
import com.example.touragency.model.Status;
import com.example.touragency.model.User;
import com.example.touragency.repository.RoleRepository;
import com.example.touragency.repository.UserRepository;
import com.example.touragency.service.MailSenderService;
import com.example.touragency.service.UserService;
import liquibase.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private MailSenderService mailSenderService;


    @Override
    public User register(User user) {
        Role roleUser = roleRepository.findByName("ROLE_USER");
        List<Role> userRoles = new ArrayList<>();
        userRoles.add(roleUser);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(userRoles);
        user.setStatus(Status.ACTIVE);
        user.setCreated(ZonedDateTime.now());
        user.setUpdated(ZonedDateTime.now());
        user.setActivatorCode(UUID.randomUUID().toString());
        User registeredUser = userRepository.save(user);

        sendMessage(user);
        log.info("IN register - user: {} successfully registered", registeredUser);

        return registeredUser;
    }

    public void sendMessage(User user) {
        if (!StringUtils.isEmpty(user.getEmail())) {
            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to Sweater. Please, visit next link: localhost:3000/activate/%s",
                    user.getUsername(),
                    user.getActivatorCode()
            );

            mailSenderService.sendEmail(user.getEmail(), "Activation code", message);
        }
    }

    @Override
    public boolean activateUser(String code) {
        User user = userRepository.findByActivatorCode(code);

        if (user == null) {
            return false;
        }
        user.setActivatorCode(null);
        userRepository.save(user);
        return true;
    }

    @Override
    public List<User> getAll() {
        List<User> result = userRepository.findAllByActivatorCodeIsNull();
        log.info("IN getAll - {} users found", result.size());
        return result;
    }

    @Override
    public User findByUsername(String username) {
        User result = userRepository.findByUsername(username);
        log.info("IN findByUsername - user: {} found by username: {}", result, username);
        return result;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findById(Long id) {
        User result = userRepository.findById(id).orElse(null);

        if (result == null) {
            log.warn("IN findById - no user found by id: {}", id);
            return null;
        }

        log.info("IN findById - user: {} found by id: {}", result);
        return result;
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
        log.info("IN delete - user with id: {} successfully deleted");
    }

    @Override
    public void updatePassword(Long user, String password) {
        userRepository.updatePassword(user, passwordEncoder.encode(password));
    }
}
