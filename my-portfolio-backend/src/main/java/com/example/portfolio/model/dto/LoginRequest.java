package com.example.portfolio.model.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
    private String captcha;
    private String captchaId;
}