package com.neo.mongocachetest.service;

import com.neo.mongocachetest.model.AppUser;
import com.neo.mongocachetest.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;

    private final PasswordEncoder encoder;

    public AppUser save(AppUser appUser) {
        return this.appUserRepository.save(appUser);
    }

    public Optional<AppUser> getUserById(String id) {
        return appUserRepository.findById(id);
    }

    public Optional<AppUser> getUserByEmail(String email) {
        return appUserRepository.findByEmail(email);
    }


    public Optional<AppUser> getUserByRefreshToken(String refreshToken) {
        return appUserRepository.findByRefreshToken(refreshToken);
    }

    public boolean checkRefreshTokenStatus(String refreshToken) {
        return getUserByRefreshToken(refreshToken).map(AppUser::isExpired).orElse(false);
    }

    @Transactional
    public void updateRefreshTokenById(String userId, String refreshToken) {
        AppUser appUser = this.getUserById(userId).get();
        appUser.setRefreshToken(refreshToken);
        appUserRepository.save(appUser);
    }

    @Transactional
    public void changeRefreshTokenStatusById(String userId, boolean usedStatus) {
        AppUser appUser = this.getUserById(userId).get();
        appUser.setExpired(usedStatus);
        appUserRepository.save(appUser);
    }

    @Transactional
    public void changeTokenStatusByValue(String token, boolean status) {
        getUserByRefreshToken(token).get().setExpired(status);
    }

    @Transactional
    public boolean updatePassword(String email, String oldPassword, String freshPassword) {
        return this.getUserByEmail(email)
                .filter(user -> encoder.matches(oldPassword, user.getPassword()))
                .map(user -> {
                    user.setPassword(freshPassword);
                    return true;
                })
                .orElse(false);
    }

    public boolean checkLoginPassword(String email, String password) {
        return this.getUserByEmail(email)
                .map(user -> encoder.matches(password, user.getPassword()))
                .orElse(false);
    }

    public boolean isEmailPresentInDB(String email) {
        return appUserRepository.findByEmail(email).isPresent();
    }
}
