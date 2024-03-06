package com.neo.mongocachetest.exceptions.httpError;

import com.neo.mongocachetest.exceptions.AppError;

public class BadRequestException extends AppError {
  public BadRequestException(String msg) {
    super("BAD REQUEST. " + msg);
  }
}
