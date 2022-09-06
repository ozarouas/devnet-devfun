package com.imizsoft.backendsecurity.service;

import com.imizsoft.backendsecurity.model.ConfirmationToken;
import com.imizsoft.backendsecurity.reprository.ConfirmationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    public ConfirmationTokenService(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    public ConfirmationToken getConfirmationToken(String token){
        return confirmationTokenRepository.findByToken(token).orElseThrow(()-> new RuntimeException("Token not found"));
    }

    public void saveConfirmationToken(ConfirmationToken confirmationToken){
        confirmationTokenRepository.save(confirmationToken);
    }

}
