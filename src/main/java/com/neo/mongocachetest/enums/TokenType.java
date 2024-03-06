package com.neo.mongocachetest.enums;

import lombok.Getter;

@Getter
public enum TokenType {
    ACCESS,
    REFRESH,
    PASSWORD_RESET,
    PASSWORD_UPDATE,
    REGISTER
}
