package com.neo.mongocachetest.exceptions.authError;

import com.neo.mongocachetest.exceptions.httpError.BadRequestException;

public class UsernameIsTakenException extends BadRequestException {
  public UsernameIsTakenException(String message) {

    super("This username is already registered. Please choose another one." + message);
  }
}
