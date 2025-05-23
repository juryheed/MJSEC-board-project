package org.mjulikelion.board.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mjulikelion.board.domain.UserEntity;
import org.mjulikelion.board.security.CustomUserDetails;
import org.mjulikelion.board.util.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;



public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // request에서 Authorization 헤더를 읽음
        String authorization = request.getHeader("Authorization");

        // Authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            System.out.println("token null");
            filterChain.doFilter(request, response);

            //조건이 해당되면 메서드 종료(필수)
            return;
        }

        System.out.println("authorization now");
        //Bearer부분 제거 후 순수 토큰만 획득
        String token = authorization.split(" ")[1];

        // 토큰 만료 시간 검증
        if (jwtUtil.isExpired(token)) {
            System.out.println("token expired");
            filterChain.doFilter(request, response);

            //조건이 해당되면 메서드 종료(필수)
            return;
        }

        // 토큰에서 username과 role 획득
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        // UserEntity 생성하여 set
        UserEntity userEntity;
        userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword("temppassword");
        userEntity.setRole(role);

        // UserDetails에 회원정보 객체 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

        // 시큐리티 컨텍스트에 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}

