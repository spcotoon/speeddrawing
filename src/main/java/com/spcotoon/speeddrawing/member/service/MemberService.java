package com.spcotoon.speeddrawing.member.service;

import com.spcotoon.speeddrawing.common.service.EnvService;
import com.spcotoon.speeddrawing.exception.custom.AlreadyExistException;
import com.spcotoon.speeddrawing.member.domain.Member;
import com.spcotoon.speeddrawing.member.dto.MemberCreateReqDto;
import com.spcotoon.speeddrawing.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EnvService envService;

    public Member create(MemberCreateReqDto memberCreateReqDto, HttpServletRequest request) {

        String clientIp = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");

        try {
            if (memberRepository.findByEmail(memberCreateReqDto.getEmail()).isPresent()) {
                throw new AlreadyExistException();
            }

            if (memberRepository.findByNickname(memberCreateReqDto.getNickname()).isPresent()) {
                throw new AlreadyExistException();
            }

            Member newMember = Member.builder()
                    .email(memberCreateReqDto.getEmail())
                    .nickname(memberCreateReqDto.getNickname())
                    .password(passwordEncoder.encode(memberCreateReqDto.getPassword()))
                    .build();

            Member member = memberRepository.save(newMember);

            log.info("회원 가입 성공 | ID: {} | Email: {} | IP: {} | User-Agent: {} | Referer: {}",
                    member.getId(), member.getEmail(), clientIp, userAgent, referer);

            return member;
        } catch (Exception e) {
            if (envService.isProd()) {
                log.error("회원 가입 실패 | Email: {} | IP: {} | User-Agent: {} | 오류: {}",
                        memberCreateReqDto.getEmail(), clientIp, userAgent, e.getMessage());
            } else {
                log.error("회원 가입 실패 | Email: {} | IP: {} | User-Agent: {} | 오류: {}",
                        memberCreateReqDto.getEmail(), clientIp, userAgent, e.getMessage(), e);
            }

            throw e;
        }

    }
}
