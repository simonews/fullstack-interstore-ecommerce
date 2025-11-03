package com.interstore.interstore_backend.service;

import com.interstore.interstore_backend.dto.AuthRequest;
import com.interstore.interstore_backend.dto.AuthResponse;
import com.interstore.interstore_backend.entity.Cart;
import com.interstore.interstore_backend.entity.User;
import com.interstore.interstore_backend.repository.CartRepository;
import com.interstore.interstore_backend.repository.UserRepository;
import com.interstore.interstore_backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;

    // login
    public AuthResponse login(AuthRequest request) {
        //autentico l’utente
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        //recuper0 l’utente dal DB
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        //genero il token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        //ritorno il token
        return new AuthResponse(token, user.getRole(), user.getId());
    }

    // register
    public String register(AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return "Username già esistente!";
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(com.interstore.interstore_backend.model.Role.USER);
        User savedUser = userRepository.save(user);

        //creo il carrello
        Cart cart = new Cart();
        cart.setUser(savedUser);
        cartRepository.save(cart);

        return "Registrazione avvenuta con successo!";
    }

}

