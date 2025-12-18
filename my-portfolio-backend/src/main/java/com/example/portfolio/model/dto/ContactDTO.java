package com.example.portfolio.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ContactDTO {

    private Long id;

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    private String subject;

    @NotBlank(message = "消息内容不能为空")
    private String message;

    private LocalDateTime createdAt;
}






