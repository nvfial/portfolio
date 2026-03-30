package com.example.portfolio.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.portfolio.model.entity.Permission;
import com.example.portfolio.model.entity.Role;
import com.example.portfolio.model.entity.User;
import com.example.portfolio.repository.PermissionRepository;
import com.example.portfolio.repository.RoleRepository;
import com.example.portfolio.repository.UserRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            PasswordEncoder passwordEncoder) {
        
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }

            Permission contentRead = new Permission();
            contentRead.setName("查看公开内容");
            contentRead.setCode("content:read");
            contentRead.setResource("content");
            contentRead.setAction("read");
            contentRead.setDescription("查看已发布的内容");
            permissionRepository.save(contentRead);

            Permission contentCreate = new Permission();
            contentCreate.setName("创建内容");
            contentCreate.setCode("content:create");
            contentCreate.setResource("content");
            contentCreate.setAction("create");
            contentCreate.setDescription("创建新内容");
            permissionRepository.save(contentCreate);

            Permission contentEditOwn = new Permission();
            contentEditOwn.setName("编辑自己的内容");
            contentEditOwn.setCode("content:edit:own");
            contentEditOwn.setResource("content");
            contentEditOwn.setAction("edit");
            contentEditOwn.setDescription("编辑自己创建的内容");
            permissionRepository.save(contentEditOwn);

            Permission contentSubmit = new Permission();
            contentSubmit.setName("提交审核");
            contentSubmit.setCode("content:submit");
            contentSubmit.setResource("content");
            contentSubmit.setAction("submit");
            contentSubmit.setDescription("提交内容审核");
            permissionRepository.save(contentSubmit);

            Permission contentReview = new Permission();
            contentReview.setName("审核内容");
            contentReview.setCode("content:review");
            contentReview.setResource("content");
            contentReview.setAction("review");
            contentReview.setDescription("审核他人提交的内容");
            permissionRepository.save(contentReview);

            Permission contentEditAll = new Permission();
            contentEditAll.setName("编辑任何内容");
            contentEditAll.setCode("content:edit:all");
            contentEditAll.setResource("content");
            contentEditAll.setAction("edit");
            contentEditAll.setDescription("编辑所有内容");
            permissionRepository.save(contentEditAll);

            Permission contentDelete = new Permission();
            contentDelete.setName("删除内容");
            contentDelete.setCode("content:delete");
            contentDelete.setResource("content");
            contentDelete.setAction("delete");
            contentDelete.setDescription("删除内容");
            permissionRepository.save(contentDelete);

            Permission userManage = new Permission();
            userManage.setName("管理用户");
            userManage.setCode("user:manage");
            userManage.setResource("user");
            userManage.setAction("manage");
            userManage.setDescription("管理系统用户");
            permissionRepository.save(userManage);

            Permission roleManage = new Permission();
            roleManage.setName("管理角色");
            roleManage.setCode("role:manage");
            roleManage.setResource("role");
            roleManage.setAction("manage");
            roleManage.setDescription("管理系统角色");
            permissionRepository.save(roleManage);

            Permission systemConfig = new Permission();
            systemConfig.setName("系统设置");
            systemConfig.setCode("system:config");
            systemConfig.setResource("system");
            systemConfig.setAction("config");
            systemConfig.setDescription("系统配置管理");
            permissionRepository.save(systemConfig);

            Role adminRole = new Role();
            adminRole.setName("管理员");
            adminRole.setCode("ADMIN");
            adminRole.setDescription("系统管理员，拥有所有权限");
            adminRole.setPermissions(new HashSet<>(permissionRepository.findAll()));
            roleRepository.save(adminRole);

            Role reviewerRole = new Role();
            reviewerRole.setName("审核员");
            reviewerRole.setCode("REVIEWER");
            reviewerRole.setDescription("内容审核员");
            Set<Permission> reviewerPermissions = new HashSet<>();
            reviewerPermissions.add(contentRead);
            reviewerPermissions.add(contentReview);
            reviewerPermissions.add(contentEditAll);
            reviewerRole.setPermissions(reviewerPermissions);
            roleRepository.save(reviewerRole);

            Role authorRole = new Role();
            authorRole.setName("作者");
            authorRole.setCode("AUTHOR");
            authorRole.setDescription("内容作者");
            Set<Permission> authorPermissions = new HashSet<>();
            authorPermissions.add(contentRead);
            authorPermissions.add(contentCreate);
            authorPermissions.add(contentEditOwn);
            authorPermissions.add(contentSubmit);
            authorRole.setPermissions(authorPermissions);
            roleRepository.save(authorRole);

            Role userRole = new Role();
            userRole.setName("普通用户");
            userRole.setCode("USER");
            userRole.setDescription("普通注册用户");
            Set<Permission> userPermissions = new HashSet<>();
            userPermissions.add(contentRead);
            userRole.setPermissions(userPermissions);
            roleRepository.save(userRole);

            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setDisplayName("系统管理员");
            admin.setStatus("ACTIVE");
            admin.setRoles(new HashSet<>(Arrays.asList(adminRole)));
            userRepository.save(admin);

            System.out.println("数据库初始化完成!");
            System.out.println("默认管理员账号: admin / admin123");
        };
    }
}