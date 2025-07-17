package com.spcotoon.speeddrawing.member.controller;

import com.spcotoon.speeddrawing.common.auth.JwtTokenProvider;
import com.spcotoon.speeddrawing.member.domain.Member;
import com.spcotoon.speeddrawing.member.dto.MemberCreateReqDto;
import com.spcotoon.speeddrawing.member.dto.MemberDto;
import com.spcotoon.speeddrawing.member.dto.MemberLoginReqDto;
import com.spcotoon.speeddrawing.member.dto.MemberLoginRespDto;
import com.spcotoon.speeddrawing.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "MemberAPI", description = "유저 도메인")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(
            summary = "회원가입 post",
            description = "회원 생성",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원가입 JSON BODY 데이터",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MemberCreateReqDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Long.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "실패"
                    )
            }
    )
    @PostMapping("/create")
    public ResponseEntity<?> memberCreate(@Valid @RequestBody MemberCreateReqDto dto, HttpServletRequest request) {
        Member member = memberService.create(dto, request);

        return new ResponseEntity<>(member.getId(), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> doLogin(@RequestBody MemberLoginReqDto dto, HttpServletRequest request) {
        Member member = memberService.login(dto, request);

        String accessToken = jwtTokenProvider.createAccessToken(member.getId(), member.getEmail(), member.getNickname(), member.getRole().getKey());

        MemberLoginRespDto loginInfo
                = MemberLoginRespDto.builder()
                .refreshToken(null)
                .accessToken(accessToken)
                .user(MemberDto.from(member))
                .build();

        return new ResponseEntity<>(loginInfo, HttpStatus.OK);
    }

}
