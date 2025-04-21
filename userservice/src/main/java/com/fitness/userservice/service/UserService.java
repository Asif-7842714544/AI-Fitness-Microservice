package com.fitness.userservice.service;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.model.User;
import com.fitness.userservice.repository.UserRepo;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private UserRepo userRepo;

    public UserResponse getUserProfile(String userId) {
        log.info("inside getUserProfile");
      User user =userRepo.findById(userId).orElseThrow(()->new RuntimeException("User not found with id "+ userId));
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

    }

    public UserResponse register(@Valid RegisterRequest registerRequest) {
        log.info("inside register");

        if(userRepo.existsByEmail(registerRequest.getEmail())){
            throw new RuntimeException("Email already exists "+registerRequest.getEmail());
        }

        User user=new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        User savedUser = userRepo.save(user);

        return UserResponse.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .password(savedUser.getPassword())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .createdAt(savedUser.getCreatedAt())
                .updatedAt(savedUser.getUpdatedAt())
                .build();
    }

    public Boolean existByuserId(String userId) {
        log.info("inside existByuserId: {}",userId);
        return userRepo.existsById(userId);
    }
}
