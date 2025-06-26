package com.spcotoon.speeddrawing.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberLoginRespDto {
    private String accessToken;
    private String refreshToken;
    private MemberDto user;
}
