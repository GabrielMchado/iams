package com.gabrielmachado.iams.service;

import com.gabrielmachado.iams.model.UserModel;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    public String generateToken(UserModel userModel){
        return "OK";
    }

}
