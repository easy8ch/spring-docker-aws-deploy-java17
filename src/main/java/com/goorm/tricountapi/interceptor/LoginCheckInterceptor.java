package com.goorm.tricountapi.interceptor;

import com.goorm.tricountapi.enums.TricountApiErrorCode;
import com.goorm.tricountapi.service.MemberService;
import com.goorm.tricountapi.util.MemberContext;
import com.goorm.tricountapi.util.TricountApiConst;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Autowired
    private MemberService memberService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("로그인 인터셉터 시작");

        Cookie[] cookies = request.getCookies();
        if(!this.containsUserCookie(cookies)) {
            log.info("미인증 사용자 요청");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, TricountApiErrorCode.LOGIN_NEEDED.getMessage());
            return false;
        }

        log.info("인증된 사용자 요청");

        // TODO MemberContext에 값을 set해주고, 이를 활용할 수 있게 한다. (나중에 구현)
        for(Cookie cookie : cookies) {
            if(TricountApiConst.LOGIN_MEMBER_COOKIE.equals(cookie.getName())) {
                try {
                    // cookie에서 아이디를 꺼내고, DB에서 이 아이디에 해당하는 Member 객체를 조회해서, 이 값을 ThreadLocal에 set한다.
                    MemberContext.setCurrentMember(memberService.findMemberById(Long.parseLong(cookie.getValue())));
                } catch(Exception e) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "MEMBER INFO SET ERROR " + e.getMessage());
                }
            }
        }

        return true;
    }

    private boolean containsUserCookie(Cookie[] cookies) {
        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if(TricountApiConst.LOGIN_MEMBER_COOKIE.equals(cookie.getName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
