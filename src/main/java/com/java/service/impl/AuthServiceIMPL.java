package com.java.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java.Model.LoginRequest;
import com.java.Model.RegisterRequest;
import com.java.Model.UserDTO;
import com.java.repository.UserRepository;
import com.java.repository.entity.UserEntity;
import com.java.service.AuthService;
import com.java.utils.JwtUtil;
import com.java.utils.PasswordUtil;

@Service
public class AuthServiceIMPL implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDTO login(LoginRequest request) {
        // Tìm user theo email
        UserEntity user = userRepository.findByEmail(request.getEmail());
        
        if (user == null) {
            throw new RuntimeException("Email hoặc mật khẩu không đúng");
        }
        
        // Kiểm tra mật khẩu
        if (!PasswordUtil.verifyPassword(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Email hoặc mật khẩu không đúng");
        }
        
        // Tạo token
        String token = JwtUtil.generateToken(user);
        
        // Chuyển sang DTO
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());
        userDTO.setSubscription(user.getSubscription());
        userDTO.setToken(token);
        
        return userDTO;
    }

    @Override
    public UserDTO register(RegisterRequest request) {
        // Kiểm tra email đã tồn tại
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng");
        }
        
        // Kiểm tra dữ liệu đầu vào
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("Tên không được để trống");
        }
        
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email không được để trống");
        }
        
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new RuntimeException("Mật khẩu phải có ít nhất 6 ký tự");
        }
        
        // Tạo user mới
        UserEntity user = new UserEntity();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(PasswordUtil.hashPassword(request.getPassword()));
        user.setRole("student"); // Mặc định là student
        user.setSubscription("free"); // Mặc định là free
        
        int userId = userRepository.createUser(user);
        
        if (userId == -1) {
            throw new RuntimeException("Đăng ký thất bại");
        }
        
        // Lấy user vừa tạo
        user = userRepository.findById(userId);
        
        // Tạo token
        String token = JwtUtil.generateToken(user);
        
        // Chuyển sang DTO
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());
        userDTO.setSubscription(user.getSubscription());
        userDTO.setToken(token);
        
        return userDTO;
    }

    @Override
    public boolean validateToken(String token) {
        return JwtUtil.validateToken(token);
    }

    @Override
    public UserDTO getUserFromToken(String token) {
        if (!JwtUtil.validateToken(token)) {
            return null;
        }
        
        String email = JwtUtil.getEmailFromToken(token);
        UserEntity user = userRepository.findByEmail(email);
        
        if (user == null) {
            return null;
        }
        
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());
        userDTO.setSubscription(user.getSubscription());
        
        return userDTO;
    }
}