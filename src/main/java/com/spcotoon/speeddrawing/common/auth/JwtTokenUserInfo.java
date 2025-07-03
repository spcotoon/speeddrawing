package com.spcotoon.speeddrawing.common.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtTokenUserInfo {
    private Long memberId;
    private String email;
    private String nickname;
}
