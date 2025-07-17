package com.spcotoon.speeddrawing.member.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberLoginReqDto {

    @Size(max = 30, message = "30자 까지 입력 가능.")
    private String email;
    @Size(max = 30, message = "30자 까지 입력 가능.")
    private String password;
}
