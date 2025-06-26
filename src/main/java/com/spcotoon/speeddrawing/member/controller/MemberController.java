package com.spcotoon.speeddrawing.member.controller;

import com.spcotoon.speeddrawing.member.domain.Member;
import com.spcotoon.speeddrawing.member.dto.MemberCreateReqDto;
import com.spcotoon.speeddrawing.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/create")
    public ResponseEntity<?> memberCreate(@RequestBody MemberCreateReqDto memberCreateReqDto, HttpServletRequest request) {
        Member member = memberService.create(memberCreateReqDto, request);

        return new ResponseEntity<>(member.getId(), HttpStatus.CREATED);
    }
}
