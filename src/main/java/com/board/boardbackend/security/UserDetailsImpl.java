package com.board.boardbackend.security;

import com.board.boardbackend.domain.User;
import com.board.boardbackend.domain.UserRoleEnum;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
public class UserDetailsImpl implements UserDetails {
    private final User user;
    private final String username;
    private final String password;

    public UserDetailsImpl(User user, String username, String password) {
        this.user = user;
        this.username = username;
        this.password = password;
    }

    // 사용자 권한 반환 (예: ROLE_USER, ROLE_ADMIN)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        UserRoleEnum role = user.getRole(); // User 객체에서 역할 가져오기
        String authority = role.getAuthority(); // 역할 enum에서 권한 문자열 가져오기

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority); // 권한 목록에 추가

        return authorities;
    }

    // 다음 4가지 메서드는 계정의 상태를 나타냄 (여기서는 모두 true로 설정)
    @Override
    public boolean isAccountNonExpired() { return true; } // 계정 만료 여부
    @Override
    public boolean isAccountNonLocked() { return true; } // 계정 잠금 여부
    @Override
    public boolean isCredentialsNonExpired() { return true; } // 비밀번호 만료 여부
    @Override
    public boolean isEnabled() { return true; } // 계정 활성화 여부
}
