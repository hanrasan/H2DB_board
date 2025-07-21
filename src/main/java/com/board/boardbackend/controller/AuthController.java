package com.board.boardbackend.controller;

import com.board.boardbackend.dto.AuthRequestDto;
import com.board.boardbackend.dto.AuthResponseDto;
import com.board.boardbackend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDto> signup(@RequestBody @Valid AuthRequestDto authRequestDto) {
        authService.signup(authRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new AuthResponseDto("회원가입 성공", HttpStatus.CREATED.value(),null)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid AuthRequestDto authRequestDto) {
        return ResponseEntity.ok(
                new AuthResponseDto("로그인 성공 (토큰 미수현)", HttpStatus.OK.value(),null)
        );
    }
}
