package com.spcotoon.speeddrawing.member.service;

import com.spcotoon.speeddrawing.IntegrationTestSupport;
import com.spcotoon.speeddrawing.exception.custom.AlreadyExistException;
import com.spcotoon.speeddrawing.exception.custom.InvalidLoginException;
import com.spcotoon.speeddrawing.member.domain.Member;
import com.spcotoon.speeddrawing.member.dto.MemberCreateReqDto;
import com.spcotoon.speeddrawing.member.dto.MemberLoginReqDto;
import com.spcotoon.speeddrawing.member.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;

class MemberServiceTest extends IntegrationTestSupport {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAll();
    }

    @DisplayName("회원가입 성공")
    @Test
    void join_success() throws Exception {
        //given
        String joinEmail = "aaa@naver.com";
        String joinNickname = "nick";
        String joinPassword = "1234";
        String clientIp = "127.0.0.1";
        String headerAgentKey = "User-Agent";
        String headerAgentValue = "";

        MemberCreateReqDto dto = MemberCreateReqDto.builder()
                .email(joinEmail)
                .nickname(joinNickname)
                .password(joinPassword)
                .build();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr(clientIp);
        request.addHeader(headerAgentKey, headerAgentValue);

        //when
        Member member = memberService.create(dto, request);

        //then
        assertThat(member.getId()).isNotNull();
        assertThat(member.getEmail()).isEqualTo(joinEmail);
        assertThat(member.getNickname()).isEqualTo(joinNickname);

        Member find = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(find.getEmail()).isEqualTo(joinEmail);
    }
    
    @DisplayName("회원가입 실패 - 이메일 중복")
    @Test
    void joinFail_emailDuplicate() throws Exception {
        //given
        String email = "aaa@naver.com";
        String existingNickname = "nick";
        String password = "1234";
        Member existingMember = Member.builder()
                .email(email)
                .nickname(existingNickname)
                .password(passwordEncoder.encode(password))
                .build();

        memberRepository.save(existingMember);

        MemberCreateReqDto dto = MemberCreateReqDto.builder()
                .email(email)
                .nickname("new" + existingNickname)
                .password(password)
                .build();

        MockHttpServletRequest request = new MockHttpServletRequest();

        //when
        //then
        assertThatThrownBy(() -> memberService.create(dto, request)).isInstanceOf(AlreadyExistException.class);
    }

    @DisplayName("회원가입 실패 - 닉네임 중복")
    @Test
    void joinFail_nicknameDuplicate() throws Exception {
        //given
        String email = "aaa@naver.com";
        String nickname = "nick";
        String password = "1234";

        Member existingMember = Member.builder()
                .email(email)
                .nickname(nickname)
                .password(passwordEncoder.encode(password))
                .build();
        memberRepository.save(existingMember);

        MemberCreateReqDto dto = MemberCreateReqDto.builder()
                .email("new" + email)
                .nickname(nickname)
                .password(password)
                .build();

        MockHttpServletRequest request = new MockHttpServletRequest();

        //when
        //then
        assertThatThrownBy(() -> memberService.create(dto, request)).isInstanceOf(AlreadyExistException.class);
    }

    private static final String TEST_EMAIL = "test@naver.com";
    private static final String TEST_PASSWORD = "1234";
    private static final String TEST_NICKNAME = "nick";
    private Member createJoinedMember() {
        MemberCreateReqDto dto = MemberCreateReqDto.builder()
                .email(TEST_EMAIL)
                .nickname(TEST_NICKNAME)
                .password(TEST_PASSWORD)
                .build();

        MockHttpServletRequest request = new MockHttpServletRequest();

        return memberService.create(dto, request);
    }

    @DisplayName("로그인 성공")
    @Test
    void login_success() throws Exception {
        //given
        createJoinedMember();

        MemberLoginReqDto loginDto = MemberLoginReqDto.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();
        MockHttpServletRequest request = new MockHttpServletRequest();

        //when
        Member member = memberService.login(loginDto, request);

        //then
        assertThat(member).isNotNull();
        assertThat(member.getEmail()).isEqualTo(TEST_EMAIL);
    }

    @DisplayName("로그인 실패 - 가입되지 않은 이메일")
    @Test
    void loginFail_email() throws Exception {
        //given
        createJoinedMember();

        MemberLoginReqDto loginDto = MemberLoginReqDto.builder()
                .email("not"+TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();
        MockHttpServletRequest request = new MockHttpServletRequest();

        //when
        //then
        assertThatThrownBy(() -> memberService.login(loginDto, request)).isInstanceOf(InvalidLoginException.class);
    }

    @DisplayName("로그인 실패 - 틀린 비밀번호")
    @Test
    void loginFail_password() throws Exception {
        //given
        createJoinedMember();

        MemberLoginReqDto loginDto = MemberLoginReqDto.builder()
                .email(TEST_EMAIL)
                .password("not"+TEST_PASSWORD)
                .build();
        MockHttpServletRequest request = new MockHttpServletRequest();

        //when
        //then
        assertThatThrownBy(() -> memberService.login(loginDto, request)).isInstanceOf(InvalidLoginException.class);
    }

    
}