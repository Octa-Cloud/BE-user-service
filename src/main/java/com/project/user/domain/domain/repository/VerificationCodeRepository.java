package com.project.user.domain.domain.repository;

import java.util.Optional;

public interface VerificationCodeRepository {

    void save(String email, String code);
    Optional<String> findByEmail(String email);
    void delete(String email);

}
