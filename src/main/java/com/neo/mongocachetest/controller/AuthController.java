package com.neo.mongocachetest.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.neo.mongocachetest.annotation.Marker;
import com.neo.mongocachetest.dto.request.AppUserDTO;
import com.neo.mongocachetest.security.JwtUserDetails;
import com.neo.mongocachetest.service.AuthService;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Log4j2
@Validated
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(originPatterns = {"*"})
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @ApiOperation("User register")
    @Validated({Marker.New.class})
    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, String>> handleRegistration(@RequestBody @JsonView({Marker.New.class}) @Valid AppUserDTO signUpDTO) {
        return authService.makeSighUp(signUpDTO);
    }

    @ApiOperation("User login")
    @Validated({Marker.Existed.class})
    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, String>> handleLogin(@RequestBody @JsonView({Marker.Existed.class}) @Valid AppUserDTO loginDTO) {
        return authService.makeLogin(loginDTO);
    }

    @ApiOperation("User logout")
    @GetMapping(path = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> handleLogout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(authService.makeLogOut(userDetails.getId()));
    }

    @ApiOperation("Get new token pair by refresh token")
    @GetMapping(path = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, String>> handleRefresh(HttpServletRequest request) {
        return authService.makeRefresh(request);
    }
}

