package com.neo.mongocachetest.dto.request;

import com.fasterxml.jackson.annotation.JsonView;
import com.neo.mongocachetest.annotation.Marker;
import com.neo.mongocachetest.model.BaseDocument;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AppUserDTO extends BaseDocument {

    @JsonView({Marker.New.class})
    @NotNull(groups = {Marker.New.class})
    @ApiModelProperty(value = "Nickname", example = "Bulbazaur", required = true, allowableValues = "range[2, 20]")
    @Size(max = 20, min = 2, message = "Nickname length must be in range 2..20 characters")
    private String nickName;

    @JsonView({Marker.New.class, Marker.Existed.class})
    @NotNull(groups = {Marker.New.class, Marker.Existed.class})
    @ApiModelProperty(value = "Email", example = "putin.die@example.com", required = true, allowableValues = "range[6, 50]")
    @Size(min = 6, max = 50, message = "Email length must be in range 6..50 characters")
    @Pattern(regexp = "^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$", message = "Invalid email format")
    private String email;

    @JsonView({Marker.New.class, Marker.Existed.class})
    @NotNull(groups = {Marker.New.class, Marker.Existed.class})
    @ApiModelProperty(value = "Password", example = "password123", required = true, allowableValues = "range[8, 50]")
    @Size(min = 8, max = 50, message = "Password length must be in range 8..50 characters")
    private String password;
}
