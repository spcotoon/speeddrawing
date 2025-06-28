package com.spcotoon.speeddrawing.common.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spcotoon.speeddrawing.member.domain.Member;
import com.spcotoon.speeddrawing.member.dto.MemberCreateReqDto;
import com.spcotoon.speeddrawing.member.dto.MemberLoginReqDto;
import com.spcotoon.speeddrawing.member.repository.MemberRepository;
import com.spcotoon.speeddrawing.member.service.MemberService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class JwtAuthFilterTest {

    @Autowired
    JwtAuthFilter jwtAuthFilter;

    @Autowired
    MemberService memberService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MockMvc mockMvc;

    private static final String TEST_EMAIL = "test@naver.com";
    private static final String TEST_PASSWORD = "1234";
    private static final String TEST_NICKNAME = "nick";
    private static final String LOGIN_URL = "/api/v1/member/login";
    private static final String TOKEN_TEST_URL = "/test/jwt-filter";
    private Member member;
    @BeforeEach
    void setUp() {
       member = createJoinedMember();
    }

    @AfterEach
    void tearDown() {
        memberRepository.deleteAll();
    }


    @DisplayName("jwtAuthFilter 통과 성공 - 서버에서 발행한 토큰으로 통과한다.")
    @Test
    void pass_success_jwtToken_filter() throws Exception {
        //given
        MemberLoginReqDto dto = MemberLoginReqDto.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        String loginRequestJson = objectMapper.writeValueAsString(dto);

        String loginResponse = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(loginResponse);
        String accessToken = jsonNode.get("accessToken").asText();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + accessToken);

        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain dummyChain = (servletRequest, servletResponse) -> {};

        //when
        //then
        assertDoesNotThrow(() -> jwtAuthFilter.doFilter(request, response, dummyChain));
     }

    @DisplayName("jwtAuthFilter 통과 성공 - 발행한 jwt 토큰으로 authenticated() url 요청 성공")
    @Test
    void valid_token_request_success() throws Exception {
        //given
        MemberLoginReqDto dto = MemberLoginReqDto.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        String loginRequestJson = objectMapper.writeValueAsString(dto);

        String loginResponse = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(loginResponse);
        String accessToken = "Bearer " + jsonNode.get("accessToken").asText();

        //when
        //then
        mockMvc.perform(get(TOKEN_TEST_URL)
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("jwtAuthFilter 통과 실패 - 유효하지 않은 토큰으로 authenticated() url 요청 실패")
    @Test
    void invalid_token_return401() throws Exception {
        //given
        MemberLoginReqDto dto = MemberLoginReqDto.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        String loginRequestJson = objectMapper.writeValueAsString(dto);

        String loginResponse = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(loginResponse);
        String accessToken = "Bearer " + "invalid" + jsonNode.get("accessToken").asText();

        //when
        //then
        mockMvc.perform(get(TOKEN_TEST_URL)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("invalid token"));
    }

    private Member createJoinedMember() {
        MemberCreateReqDto dto = MemberCreateReqDto.builder()
                .email(TEST_EMAIL)
                .nickname(TEST_NICKNAME)
                .password(TEST_PASSWORD)
                .build();

        MockHttpServletRequest request = new MockHttpServletRequest();

        return memberService.create(dto, request);
    }




}