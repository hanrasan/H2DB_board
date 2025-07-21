package com.board.boardbackend.jwt;

import com.board.boardbackend.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = jwtUtil.getTokenFromRequest(request); // 요청에서 JWT 토큰 추출

        if (token != null) { // 토큰이 존재하면
            if (!jwtUtil.validateToken(token)) { // 토큰 유효성 검증
                log.error("Token Error");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"message\": \"유효하지 않은 토큰입니다.\", \"statusCode\": 401}");
                return; // 더 이상 필터 체인 진행하지 않고 응답 반환
            }

            Claims info = jwtUtil.getClaimsFromToken(token); // 토큰에서 사용자 정보(Claims) 추출

            try {
                setAuthentication(info.getSubject()); // 추출한 사용자명으로 인증 설정
            } catch (Exception e) {
                log.error(e.getMessage());
                return; // 오류 발생 시 필터 체인 중단
            }
        }
        filterChain.doFilter(request, response); // 다음 필터로 요청 전달
    }

    // 인증 처리 (Security Context에 인증 객체 저장)
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext(); // 빈 SecurityContext 생성
        Authentication authentication = createAuthentication(username); // Authentication 객체 생성
        context.setAuthentication(authentication); // Context에 인증 객체 설정

        SecurityContextHolder.setContext(context); // SecurityContextHolder에 Context 저장
    }

    // Authentication 객체 생성
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username); // UserDetails 로드
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
