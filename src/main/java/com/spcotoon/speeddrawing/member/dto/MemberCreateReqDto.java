package com.spcotoon.speeddrawing.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberCreateReqDto {
    private String email;
    private String nickname;
    private String password;
}
