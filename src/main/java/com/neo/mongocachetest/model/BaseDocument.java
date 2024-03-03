package com.neo.mongocachetest.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public abstract class BaseDocument {

    @Id
    public String id;
}
