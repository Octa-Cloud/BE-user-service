package com.project.user.domain.domain.repository;

public interface VerifiedEmailRepository {

    void save(String email);
    void clearAfterSignUp(String email);
    boolean isExist(String email);

}
