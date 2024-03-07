package com.neo.mongocachetest.exceptions.authError;

import com.neo.mongocachetest.exceptions.httpError.BadRequestException;

public class UserAlreadyRegisteredException extends BadRequestException {
  public UserAlreadyRegisteredException(String message) {

    super("User with " + message + " already registered.");
  }
}
