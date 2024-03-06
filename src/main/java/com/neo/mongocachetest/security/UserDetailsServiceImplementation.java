package com.neo.mongocachetest.security;

import com.neo.mongocachetest.exceptions.authError.EmailNotFoundException;
import com.neo.mongocachetest.model.AppUser;
import com.neo.mongocachetest.service.AppUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class UserDetailsServiceImplementation implements UserDetailsService {

    private final AppUserService appUserService;

    public UserDetails mapper(AppUser appUser) {
        return User
                .withUsername(appUser.getEmail())
                .password(appUser.getPassword())
                .roles("USER")
                .build();
    }

    /**
     * Method returns User Details object for Spring Security authentication procedure using user email as login parameter
     *
     * @throws EmailNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String userMail) throws EmailNotFoundException {
        return this.appUserService.getUserByEmail(userMail)
                .map(this::mapper)
                .orElseThrow(() -> new EmailNotFoundException(String.format("User with email: `%s` not found", userMail)
                ));
    }
}
