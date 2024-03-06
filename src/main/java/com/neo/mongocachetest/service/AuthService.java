package com.neo.mongocachetest.service;

import com.neo.mongocachetest.dto.request.AppUserDTO;
import com.neo.mongocachetest.enums.TokenType;
import com.neo.mongocachetest.exceptions.authError.AuthErrorException;
import com.neo.mongocachetest.exceptions.authError.JwtAuthenticationException;
import com.neo.mongocachetest.exceptions.authError.UserAlreadyRegisteredException;
import com.neo.mongocachetest.model.AppUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class AuthService {

  private final JwtTokenService jwtTokenService;
  private final AuthenticationManager authenticationManager;
  private final AppUserService userService;
  private final PasswordEncoder encoder;
  private final ModelMapper mm;

  public ResponseEntity<HashMap<String, String>> makeLogin(AppUserDTO loginDTO) {
    //Auth procedure handling
    Authentication authentication = authenticationManager
      .authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    Object principal = authentication.getPrincipal();

    Optional<User> maybeAuthUser = (principal instanceof User) ? Optional.of((User) principal) : Optional.empty();
    User authUser = maybeAuthUser.orElseThrow(() -> new AuthErrorException("Something went wrong during authentication"));

    //User extraction from DB by security credentials from Authenticated User (email aka username)
    Optional<AppUser> maybeUser = userService.getUserByEmail(authUser.getUsername());
    AppUser currentAdmin = maybeUser.orElseThrow(() -> new AuthErrorException("Authenticated user not found in DB! MAGIC!"));

    return ResponseEntity.ok(jwtTokenService.generateTokenPair(currentAdmin));
  }

  public ResponseEntity<HashMap<String, String>> makeSighUp(AppUserDTO signUpDTO) {
    //Email duplicate checking
    if (userService.isEmailPresentInDB(signUpDTO.getEmail()))
      throw new UserAlreadyRegisteredException("email: " + signUpDTO.getEmail());

    //Saving new Admin to DB and getting user_id
    signUpDTO.setPassword(encoder.encode(signUpDTO.getPassword()));
    AppUser freshUser = mm.map(signUpDTO, AppUser.class);
    freshUser = userService.save(freshUser);

    return ResponseEntity.ok(jwtTokenService.generateTokenPair(freshUser));
  }

  public String makeLogOut(String userId) {
    jwtTokenService.changeRefreshTokenStatusByUserId(userId, true);
    log.info("Admin id: " + userId + " logged out");
    return "Admin with Id: " + userId + " logged out";
  }

  public ResponseEntity<HashMap<String, String>> makeRefresh(HttpServletRequest request) {
    String token = jwtTokenService.extractTokenFromRequest(request).orElseThrow(() -> new JwtAuthenticationException("Token not found!"));
    if (jwtTokenService.validateToken(token, TokenType.REFRESH) && !jwtTokenService.checkRefreshTokenStatus(token)) {
      AppUser currUser = userService.getUserByRefreshToken(token).get();
      return ResponseEntity.ok(jwtTokenService.generateTokenPair(currUser));
    } else return ResponseEntity.status(400).body(new HashMap<>());
  }
}

