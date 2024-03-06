package com.neo.mongocachetest.exceptions.authError;

public class EmailNotFoundException extends AuthErrorException {
  public EmailNotFoundException(String message) {
    super("Email not found." + message);
  }
}
