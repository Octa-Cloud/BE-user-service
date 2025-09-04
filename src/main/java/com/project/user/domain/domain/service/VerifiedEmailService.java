package com.project.user.domain.domain.service;

import com.project.user.domain.domain.repository.VerifiedEmailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerifiedEmailService {

    private final VerifiedEmailRepository verifiedEmailRepository;

    public void save(String email) {
        verifiedEmailRepository.save(email);
    }

    public void delete(String email) {
        verifiedEmailRepository.clearAfterSignUp(email);
    }

    public boolean isExist(String email) {
        return verifiedEmailRepository.isExist(email);
    }
}
