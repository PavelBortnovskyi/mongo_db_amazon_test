package com.neo.mongocachetest.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Setter
@Document
@NoArgsConstructor
public class AppUser extends BaseDocument{

    private String nickName;

    private String email;

    private String password;

    private String refreshToken;

    private boolean expired = false;
}
