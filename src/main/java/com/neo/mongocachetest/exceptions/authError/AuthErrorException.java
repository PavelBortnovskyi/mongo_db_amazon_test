package com.neo.mongocachetest.exceptions.authError;

import com.neo.mongocachetest.exceptions.httpError.UnAuthorizedException;

public class AuthErrorException extends UnAuthorizedException {
  public AuthErrorException(String message) {

    super("Authorization error. " + message);
  }
}
