package com.neo.mongocachetest.exceptions.httpError;

import com.neo.mongocachetest.exceptions.AppError;

public class UnAuthorizedException extends AppError {
  public UnAuthorizedException(String msg) {
    super("UNAUTHORIZED. " + msg);
  }
}
