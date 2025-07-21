package com.board.boardbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDto {
    @NotBlank(message = "사용자명은 필수 입력 값입니다.")
    @Size(min = 3, max = 10, message = "사용자명은 3~10자여야 합니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 8, max = 15, message = "비밀번호는 8~15자여야 합니다.")
    private String password;

    private boolean admin = false; // 기본값은 일반 사용자 (false)
    private String adminToken;     // 관리자 토큰 (null일 수도 있음)
}
