package com.example.portfolio.service.impl;

import com.example.portfolio.model.dto.LoginRequest;
import com.example.portfolio.model.dto.LoginResponse;
import com.example.portfolio.model.dto.UserDTO;
import com.example.portfolio.model.entity.Role;
import com.example.portfolio.model.entity.User;
import com.example.portfolio.repository.UserRepository;
import com.example.portfolio.service.AuthService;
import com.example.portfolio.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.expiration:86400000}")
    private long tokenExpiration;

    @Override
    public LoginResponse login(LoginRequest request, String ipAddress) {
        User user = userRepository.findByUsernameWithRolesAndPermissions(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new RuntimeException("账号已被禁用");
        }

        user.setLastLoginAt(LocalDateTime.now());
        user.setLastLoginIp(ipAddress);
        userRepository.save(user);

        String roles = user.getRoles().stream()
                .map(Role::getCode)
                .collect(Collectors.joining(","));

        String accessToken = jwtUtil.generateToken(user.getId(), user.getUsername(), roles);
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername());

        return new LoginResponse(
                accessToken,
                refreshToken,
                "Bearer",
                tokenExpiration / 1000,
                convertToDTO(user)
        );
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            throw new RuntimeException("无效的刷新令牌");
        }

        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        User user = userRepository.findByUsernameWithRolesAndPermissions(
                jwtUtil.getUsernameFromToken(refreshToken))
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        String roles = user.getRoles().stream()
                .map(Role::getCode)
                .collect(Collectors.joining(","));

        String newAccessToken = jwtUtil.generateToken(user.getId(), user.getUsername(), roles);
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername());

        return new LoginResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                tokenExpiration / 1000,
                convertToDTO(user)
        );
    }

    @Override
    public void logout(Long userId) {
    }

    @Override
    public UserDTO getCurrentUser(String username) {
        User user = userRepository.findByUsernameWithRolesAndPermissions(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return convertToDTO(user);
    }

    @Override
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setDisplayName(user.getDisplayName());
        dto.setAvatar(user.getAvatar());
        dto.setStatus(user.getStatus());
        dto.setLastLoginAt(user.getLastLoginAt());
        dto.setLastLoginIp(user.getLastLoginIp());
        dto.setCreatedAt(user.getCreatedAt());
        
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getCode)
                .collect(Collectors.toList());
        dto.setRoles(roleNames);
        
        List<String> permissionCodes = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> permission.getCode())
                .distinct()
                .collect(Collectors.toList());
        dto.setPermissions(permissionCodes);
        
        return dto;
    }
}