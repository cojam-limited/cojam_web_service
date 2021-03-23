package io.cojam.web.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class PassChange {
    @NotEmpty
    @NotBlank
    private String memberKey;
    @NotEmpty
    @NotBlank
    private String memberId;
    @NotEmpty @NotBlank @Email
    private String memberEmail;
    @NotEmpty @NotBlank @JsonIgnore
    private String memberPass;
    @NotEmpty @NotBlank @JsonIgnore
    private String memberPassConfirm;
    @NotEmpty @NotBlank @JsonIgnore
    private String fpNumber;
}
