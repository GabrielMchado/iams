package com.gabrielmachado.iams.service;

import com.gabrielmachado.iams.dto.LoginRequest;
import com.gabrielmachado.iams.dto.LoginResponse;
import com.gabrielmachado.iams.dto.RegisterRequest;
import com.gabrielmachado.iams.model.UserModel;
import com.gabrielmachado.iams.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void register(RegisterRequest request) throws Exception {
        if(userRepository.existsUserModelByEmail(request.email())){
            throw new Exception("Email já cadstrado...");
        }

        UserModel userModel = new UserModel();

        userModel.setEmail(request.email());
        userModel.setName(request.name());
        userModel.setPasswd(passwordEncoder.encode(request.passwd()));
        userRepository.save(userModel);
    }

    public LoginResponse login(LoginRequest request){
        UserModel userModel = userRepository.findByEmail(request.email()).orElseThrow(() -> new RuntimeException("Senha ou usuario invalido"));

        if(!passwordEncoder.matches(request.passwd(), userModel.getPasswd())){
            throw new RuntimeException("Senha ou usuario invalido");
        }

        String token = jwtService.generateToken(userModel);
        return new LoginResponse(token);
    }

}
