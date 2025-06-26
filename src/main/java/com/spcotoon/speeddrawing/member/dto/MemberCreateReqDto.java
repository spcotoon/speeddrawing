package com.spcotoon.speeddrawing.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberCreateReqDto {
    private String email;
    private String nickname;
    private String password;
}
