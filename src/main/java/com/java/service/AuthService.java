package com.java.service;

import com.java.Model.LoginRequest;
import com.java.Model.RegisterRequest;
import com.java.Model.UserDTO;

public interface AuthService {
    UserDTO login(LoginRequest request);
    UserDTO register(RegisterRequest request);
    boolean validateToken(String token);
    UserDTO getUserFromToken(String token);
}
