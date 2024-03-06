package com.neo.mongocachetest.service;

import com.neo.mongocachetest.enums.TokenType;
import com.neo.mongocachetest.exceptions.authError.JwtAuthenticationException;
import com.neo.mongocachetest.model.AppUser;
import com.neo.mongocachetest.service.AppUserService;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class JwtTokenService {

  private final UserDetailsService userDetailsService;

  private final AppUserService appUserService;

  @Value("${jwt.secret}")
  private String secretAccessKey;
  @Value("${jwt.secretRefresh}")
  private String secretRefreshKey;
  @Value("${jwt.secretPasswordReset}")
  private String secretPasswordResetKey;
  @Value("${jwt.secretPasswordUpdate}")
  private String secretPasswordUpdateKey;
  @Value("${jwt.secretRegister}")
  private String secretRegisterKey;
  @Value("${jwt.header}")
  private String authorizationHeader;

  //All fields below in milliseconds
  @Value("${jwt.expiration}")
  private long accessTokenLiveTime;
  @Value("${jwt.expirationRefresh}")
  private long refreshTokenLiveTime;
  @Value("${jwt.expirationPasswordReset}")
  private long passwordResetTokenLiveTime;
  @Value("${jwt.expirationPasswordUpdate}")
  private long passwordUpdateTokenLiveTime;
  @Value("${jwt.expirationRegister}")
  private long registerTokenLiveTime;

  private static final String BEARER = "Bearer ";

  @PostConstruct
  protected void init() {
    this.secretAccessKey = Base64.getEncoder().encodeToString(this.secretAccessKey.getBytes());
    this.secretRefreshKey = Base64.getEncoder().encodeToString(this.secretRefreshKey.getBytes());
    this.secretPasswordResetKey = Base64.getEncoder().encodeToString(this.secretPasswordResetKey.getBytes());
    this.secretPasswordUpdateKey = Base64.getEncoder().encodeToString(this.secretPasswordUpdateKey.getBytes());
  }

  public String createToken(String userId, TokenType tokenType, String userMail) {
    String signKey = this.getSignKey(tokenType);
    Date now = new Date();
    Date expiry = this.getExpirationDate(tokenType);
    Claims claims = (userId == null) ? Jwts.claims().setSubject(userMail) : Jwts.claims().setSubject(userId);
    claims.put("email", userMail);

    return Jwts.builder()
      .setClaims(claims)
      .setIssuedAt(now)
      .setExpiration(expiry)
      .signWith(SignatureAlgorithm.HS512, signKey)
      .compact();
  }

  public Optional<Jws<Claims>> extractClaimsFromToken(String token, TokenType tokenType) throws JwtAuthenticationException {
    String signKey = this.getSignKey(tokenType);
    try {
      return Optional.ofNullable(Jwts.parser()
        .setSigningKey(signKey)
        .parseClaimsJws(token));
    } catch (SignatureException e) {
      log.error("Wrong signature key: " + signKey);
      throw new JwtAuthenticationException("Wrong signature key: " + signKey);
    } catch (MalformedJwtException e) {
      log.error("Token was malformed: " + token);
      throw new JwtAuthenticationException("Token was malformed: " + token);
    } catch (ExpiredJwtException e) {
      log.error(String.format("Token: %s expired", token));
      throw new JwtAuthenticationException(String.format("Token: %s expired", token));
    } catch (UnsupportedJwtException e) {
      log.error("Unsupported type for token: " + token);
      throw new JwtAuthenticationException("Unsupported type for token: " + token);
    } catch (InvalidClaimException e) {
      log.error("Invalid claim from token: " + token);
      throw new JwtAuthenticationException("Invalid claim from token: " + token);
    } catch (CompressionException e) {
      log.error("Invalid compression for token: " + token);
      throw new JwtAuthenticationException("Invalid compression for token: " + token);
    } catch (Exception e) {
      log.error("Some error" + e.toString());
    }
    return Optional.empty();
  }

  public Optional<String> extractTokenFromRequest(HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
      .filter(h -> h.startsWith(BEARER))
      .map(h -> h.substring(BEARER.length()));
  }

  public Optional<String> extractTokenFromHeader(String header) {
    return Optional.of(header.substring(BEARER.length()));
  }

  public Optional<String> extractIdFromClaims(Jws<Claims> claims) {
    try {
      return Optional.ofNullable(claims.getBody().getSubject()).map(String::valueOf);
    } catch (Exception e) {
      log.error(String.format("Claims id: %s id parsing went wrong: %s", claims.getBody().getId(), claims.getBody().getSubject()));
    }
    return Optional.empty();
  }

  public Optional<String> extractUserEmailFromClaims(Jws<Claims> claims) {
    try {
      return Optional.ofNullable((String) claims.getBody().get("email"));
    } catch (Exception e) {
      log.error(String.format("Claims id: %s username parsing went wrong: %s", claims.getBody().getId(), claims.getBody().getSubject()));
      return Optional.empty();
    }
  }

  public Optional<String> getIdFromRequest(HttpServletRequest request) {
    return this.extractTokenFromRequest(request)
      .flatMap(t -> this.extractClaimsFromToken(t, TokenType.ACCESS))
      .flatMap(this::extractIdFromClaims);
  }

  public boolean validateToken(String token, TokenType tokenType) {
    String signKey = this.getSignKey(tokenType);
    try {
      Jws<Claims> claimsJws = Jwts.parser().setSigningKey(signKey).parseClaimsJws(token);
      return !claimsJws.getBody().getExpiration().before(new Date());
    } catch (JwtException | IllegalArgumentException e) {
      log.error(String.format("JWT %s token is expired or invalid", tokenType.toString()));
      return false;
    }
  }

  public void updateRefreshToken(AppUser appUser, String refreshToken) {
    appUserService.updateRefreshTokenById(appUser.getId(), refreshToken);
  }

  public boolean checkRefreshTokenStatus(String refreshToken) {
    return appUserService.checkRefreshTokenStatus(refreshToken);
  }

  public void changeRefreshTokenStatusByUserId(String userId, boolean status) {
    appUserService.changeRefreshTokenStatusById(userId, status);
  }

  public void changeRefreshTokenStatusByTokenValue(String token, boolean status) {
    appUserService.changeTokenStatusByValue(token, status);
  }

  private String getSignKey(TokenType tokenType) {
    switch (tokenType) {
      case ACCESS -> {
        return this.secretAccessKey;
      }
      case REFRESH -> {
        return this.secretRefreshKey;
      }
      case PASSWORD_RESET -> {
        return secretPasswordResetKey;
      }
      case PASSWORD_UPDATE -> {
        return secretPasswordUpdateKey;
      }
      case REGISTER -> {
        return secretRegisterKey;
      }
      default -> {
        return "";
      }
    }
  }

  private Date getExpirationDate(TokenType tokenType) {
    Date now = new Date();
    switch (tokenType) {
      case ACCESS -> {
        return new Date(now.getTime() + accessTokenLiveTime);
      }
      case REFRESH -> {
        return new Date(now.getTime() + refreshTokenLiveTime);
      }
      case PASSWORD_RESET -> {
        return new Date(now.getTime() + passwordResetTokenLiveTime);
      }
      case PASSWORD_UPDATE -> {
        return new Date(now.getTime() + passwordUpdateTokenLiveTime);
      }
      case REGISTER -> {
        return new Date(now.getTime() + registerTokenLiveTime);
      }
      default -> {
        return now;
      }
    }
  }

  public HashMap<String, String> generateTokenPair(AppUser appUser) {
    String accessToken = this.createToken(appUser.getId(), TokenType.ACCESS, appUser.getEmail());
    String refreshToken = this.createToken(appUser.getId(), TokenType.REFRESH, appUser.getEmail());

    //Update refresh token for current user
    this.updateRefreshToken(appUser, refreshToken);
    this.changeRefreshTokenStatusByUserId(appUser.getId(), false);

    //JWT tokens for response packing
    HashMap<String, String> response = new HashMap<>();
    response.put("USER_ID", appUser.getId());
    response.put("ACCESS_TOKEN", accessToken);
    response.put("REFRESH_TOKEN", refreshToken);

    return response;
  }
}
