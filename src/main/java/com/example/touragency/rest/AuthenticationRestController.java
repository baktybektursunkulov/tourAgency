package com.example.touragency.rest;


import com.example.touragency.dto.AuthorizationRequestDto;
import com.example.touragency.model.Password_Reset_Token;
import com.example.touragency.model.User;
import com.example.touragency.security.jwt.JwtTokenProvider;
import com.example.touragency.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping(value = "/api/v1/auth/")
public class AuthenticationRestController {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;

    @Autowired
    public AuthenticationRestController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @PostMapping("logup")
    public ResponseEntity register(@RequestBody User requestDto) {
        try {
            User user = userService.findByUsername(requestDto.getUsername());
            if (user!=null&&user.getActivatorCode()==null&& user.isExpired(new Date())){
                  userService.delete(user.getId());
            }
            userService.register(requestDto);
            return ResponseEntity.ok("USER CREATED");
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Error: Username or email is exist");
        }
    }


    @GetMapping("/activate")
    public ResponseEntity<?> resetPassword(@RequestParam("code") String code) {
        boolean isActivated = userService.activateUser(code);
        if (!isActivated) {
            return ResponseEntity.badRequest().body("Activation code is not found!");
        }
        return ResponseEntity.ok("User successfully activated");
    }

    @PostMapping("login")
    public ResponseEntity login(@RequestBody AuthorizationRequestDto requestDto) {
        try {
            String username = requestDto.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, requestDto.getPassword()));
            User user = userService.findByUsername(username);

            if (user == null) {
                throw new UsernameNotFoundException("User with username: " + username + " not found");
            }

            String token = jwtTokenProvider.createToken(username, user.getRoles());

            Map<Object, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("token", token);

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

}
