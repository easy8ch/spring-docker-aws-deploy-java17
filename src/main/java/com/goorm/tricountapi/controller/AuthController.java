package com.goorm.tricountapi.controller;

import com.goorm.tricountapi.dto.LoginRequest;
import com.goorm.tricountapi.dto.SignupRequest;
import com.goorm.tricountapi.model.ApiResponse;
import com.goorm.tricountapi.model.Member;
import com.goorm.tricountapi.service.MemberService;
import com.goorm.tricountapi.util.TricountApiConst;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final MemberService memberService;

    // 회원가입
    @PostMapping("/signup")
    public ApiResponse<Member> signup(@RequestBody @Valid SignupRequest signupRequest) {
        Member member = Member.builder()
                .loginId(signupRequest.getLoginId())
                .password(signupRequest.getPassword())
                .name(signupRequest.getName())
                .build();
        return new ApiResponse<Member>().ok(memberService.signup(member));
    }

    // 로그인
    @PostMapping("/login")
    public ApiResponse login(
            @RequestBody @Valid LoginRequest loginRequest,
            HttpServletRequest request, HttpServletResponse response
    ) {
        // 멤버 조회
        Member loginMember = memberService.login(loginRequest.getLoginId(), loginRequest.getPassword());

        // 로그인 성공 처리 - 쿠키 생성
        Cookie idCookie = new Cookie(TricountApiConst.LOGIN_MEMBER_COOKIE, String.valueOf(loginMember.getId()));
        response.addCookie(idCookie);

        return new ApiResponse<>().ok();
    }

    // 로그아웃
    @PostMapping("/logout")
    public ApiResponse logout(HttpServletResponse response) {
        memberService.logout(response);
        return new ApiResponse<>().ok();
    }
}
