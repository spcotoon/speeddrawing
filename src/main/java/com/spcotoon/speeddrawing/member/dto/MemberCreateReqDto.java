package com.spcotoon.speeddrawing.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberCreateReqDto {

    @NotBlank(message = "이메일은 필수입니다.")
    @Size(min = 4, max = 20, message = "이메일은 4자 이상 50자 이하이어야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9@._-]+$", message = "영문자, 숫자, @, ., -, _ 만 허용됩니다.")
    private String email;

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 6, message = "닉네임은 2자 이상 6자 이하이어야 합니다.")
    private String nickname;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 4, max = 12, message = "비밀번호는 4자 이상 12자 이하이어야 합니다.")
    private String password;
}
