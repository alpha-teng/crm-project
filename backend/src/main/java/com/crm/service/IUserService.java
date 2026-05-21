package com.crm.service;

import com.crm.dto.request.LoginRequest;
import com.crm.dto.response.LoginResponse;

public interface IUserService {
    
    LoginResponse login(LoginRequest request);
    
    void register(LoginRequest request, String realName, String role);
    
    LoginResponse getUserInfo(Integer userId);
}
