package com.board.boardbackend.service;

import com.board.boardbackend.domain.User;
import com.board.boardbackend.domain.UserRoleEnum;
import com.board.boardbackend.dto.AuthRequestDto;
import com.board.boardbackend.exception.ApiException;
import com.board.boardbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ADMIN_TOKEN 설정 (나중에 application.properties로 분리하는 것이 좋음)
    private static final String ADMIN_TOKEN = "AAABnvxRVVZXg8EGMY"; // 예시 토큰

    @Transactional
    public void signup(AuthRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();

        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "중복된 사용자명입니다.");
        }

        String encodedPassword = passwordEncoder.encode(password);

        // 3. 사용자 권한 설정 (여기서는 기본 USER, 어드민 토큰으로 ADMIN 가능)
        UserRoleEnum role = UserRoleEnum.USER;
        if (requestDto.isAdmin()) { // requestDto에 isAdmin 필드를 추가했다면
            if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
                throw new ApiException(HttpStatus.FORBIDDEN, "관리자 토큰이 유효하지 않습니다.");
            }
            role = UserRoleEnum.ADMIN;
        }

        // 4. 새 사용자 저장
        User user = new User(username, encodedPassword, role);
        userRepository.save(user);
    }
}
