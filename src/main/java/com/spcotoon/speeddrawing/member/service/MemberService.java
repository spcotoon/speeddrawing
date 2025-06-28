package com.spcotoon.speeddrawing.member.service;

import com.spcotoon.speeddrawing.common.service.EnvService;
import com.spcotoon.speeddrawing.exception.custom.AlreadyExistEmailException;
import com.spcotoon.speeddrawing.exception.custom.AlreadyExistNicknameException;
import com.spcotoon.speeddrawing.exception.custom.InvalidLoginException;
import com.spcotoon.speeddrawing.member.domain.Member;
import com.spcotoon.speeddrawing.member.dto.MemberCreateReqDto;
import com.spcotoon.speeddrawing.member.dto.MemberLoginReqDto;
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

    public Member create(MemberCreateReqDto dto, HttpServletRequest request) {

        String clientIp = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");

        try {
            if (memberRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new AlreadyExistEmailException();
            }

            if (memberRepository.findByNickname(dto.getNickname()).isPresent()) {
                throw new AlreadyExistNicknameException();
            }

            Member newMember = Member.builder()
                    .email(dto.getEmail())
                    .nickname(dto.getNickname())
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .build();

            Member member = memberRepository.save(newMember);

            log.info("회원 가입 성공 | ID: {} | Email: {} | IP: {} | User-Agent: {} | Referer: {}",
                    member.getId(), member.getEmail(), clientIp, userAgent, referer);

            return member;
        } catch (Exception e) {
            if (envService.isProd()) {
                log.error("회원 가입 실패 | Email: {} | IP: {} | User-Agent: {} | 오류: {}",
                        dto.getEmail(), clientIp, userAgent, e.getMessage());
            } else {
                log.error("회원 가입 실패 | Email: {} | IP: {} | User-Agent: {} | 오류: {}",
                        dto.getEmail(), clientIp, userAgent, e.getMessage(), e);
            }

            throw e;
        }

    }

    public Member login(MemberLoginReqDto dto, HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        Member member = memberRepository.findByEmail(dto.getEmail()).orElseThrow(() -> {
            log.warn("로그인 실패 - 존재하지 않는 이메일 | Email: {} | IP: {} | UA: {}", dto.getEmail(), clientIp, userAgent);
            return new InvalidLoginException();
        });

        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            log.warn("로그인 실패 - 비밀번호 불일치 | Email: {} | IP: {} | UA: {}", dto.getEmail(), clientIp, userAgent);
            throw new InvalidLoginException();
        }

        log.info("로그인 성공 | MemberId: {} | Email: {} | IP: {} | UA: {}", member.getId(), member.getEmail(), clientIp, userAgent);
        return member;
    }
}
