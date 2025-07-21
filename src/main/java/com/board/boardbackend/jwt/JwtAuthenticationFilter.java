package com.board.boardbackend.jwt;

import com.board.boardbackend.domain.UserRoleEnum;
import com.board.boardbackend.dto.AuthRequestDto;
import com.board.boardbackend.security.UserDetailsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        logger.info("로그인 시도");
        try {
            AuthRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), AuthRequestDto.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                        requestDto.getUsername(),
                        requestDto.getPassword(),
                        null
                    )
            );
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    // 인증 성공 시 호출되는 메서드
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        logger.info("로그인 성공 및 JWT 생성");
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername(); // 인증된 사용자명
        UserRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole(); // 사용자 역할

        // JWT 토큰 생성
        String token = jwtUtil.createToken(username, role.getAuthority());
        // JWT 토큰을 응답 헤더에 추가하여 클라이언트에게 전달
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);
        // 로그인 성공 메시지 반환 (Optional)
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\": \"로그인 성공\", \"statusCode\": 200}");
    }

    // 인증 실패 시 호출되는 메서드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        logger.info("로그인 실패");
        // 로그인 실패 시 401 Unauthorized 응답 반환
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\": \"로그인 실패: " + failed.getMessage() + "\", \"statusCode\": 401}");
    }
}
