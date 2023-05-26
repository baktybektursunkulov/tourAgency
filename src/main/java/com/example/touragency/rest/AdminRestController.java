package com.example.touragency.rest;

import com.example.touragency.dto.AdminUserDto;
import com.example.touragency.model.User;
import com.example.touragency.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping(value = "/api/v1/admin/")
public class AdminRestController {

    private final UserService userService;

    @Autowired
    public AdminRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "users/{id}")
    public ResponseEntity<AdminUserDto> getUserById(@PathVariable(name = "id") Long id) {
        User user = userService.findById(id);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        AdminUserDto result = AdminUserDto.fromUser(user);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/allUsers")
    public ResponseEntity<List<AdminUserDto>> getUsers() {
        List<User> users = userService.getAll();
        List<AdminUserDto> results = new ArrayList<>();
        for (User user : users) {
            if(Objects.equals(user.getRoles().get(0).getName(), "ROLE_ADMIN"))continue;
            AdminUserDto result = AdminUserDto.fromUser(user);
            results.add(result);
        }
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @DeleteMapping(value = "delete/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable(name = "id") Long id) {
        userService.delete(id);
        return new ResponseEntity<>("Successful", HttpStatus.OK);
    }

}
