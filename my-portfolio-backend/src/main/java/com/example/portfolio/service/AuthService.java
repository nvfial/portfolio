package com.example.portfolio.service;

import com.example.portfolio.model.dto.LoginRequest;
import com.example.portfolio.model.dto.LoginResponse;
import com.example.portfolio.model.dto.UserDTO;

public interface AuthService {
    
    LoginResponse login(LoginRequest request, String ipAddress);
    
    LoginResponse refreshToken(String refreshToken);
    
    void logout(Long userId);
    
    UserDTO getCurrentUser(String username);
    
    boolean validatePassword(String rawPassword, String encodedPassword);
    
    String hashPassword(String rawPassword);
}