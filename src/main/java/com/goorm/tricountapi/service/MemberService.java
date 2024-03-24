package com.goorm.tricountapi.service;

import com.goorm.tricountapi.enums.TricountApiErrorCode;
import com.goorm.tricountapi.model.Member;
import com.goorm.tricountapi.repository.MemberRepository;
import com.goorm.tricountapi.util.TricountApiConst;
import com.goorm.tricountapi.util.TricountApiException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    // 회원가입
    public Member signup(Member member) {
        return memberRepository.save(member);
    }

    // 로그인
    public Member login(String loginId, String password) {
        // id, password가 맞으면 통과, 맞지 않으면 로그인 처리를 안해줘야 함
        Member loginMember = memberRepository.findByLoginId(loginId)
                .filter(m -> m.getPassword().equals(password))
                .orElseThrow(() -> new TricountApiException("Member info is not found. Login failed", TricountApiErrorCode.ACCESS_DENIED));
        return loginMember;
    }

    // 로그아웃
    public void logout(HttpServletResponse response) {
        expireCookie(response, TricountApiConst.LOGIN_MEMBER_COOKIE);
    }

    // 회원 조회 - MemberContext에서 사용
    public Member findMemberById(Long memberId) {
        Optional<Member> loginMember = memberRepository.findById(memberId);
        if(!loginMember.isPresent()) {
            throw new TricountApiException("Member info is not found", TricountApiErrorCode.ACCESS_DENIED);
        }
        return loginMember.get();
    }

    private void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
