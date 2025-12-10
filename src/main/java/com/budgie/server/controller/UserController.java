package com.budgie.server.controller;

import com.budgie.server.dto.NicknameChangeRequestDto;
import com.budgie.server.dto.PasswordChangeRequestDto;
import com.budgie.server.dto.UserInfoDto;
import com.budgie.server.entity.UserEntity;
import com.budgie.server.repository.UserRepository;
import com.budgie.server.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/me")
    public UserInfoDto getMyInfo(Principal principal){
        Long userId = Long.parseLong(principal.getName());

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(()-> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        return UserInfoDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }

    //비번 변경
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(Principal principal, @RequestBody PasswordChangeRequestDto dto){
        Long userId = Long.parseLong(principal.getName());
        userService.changePassword(userId, dto);
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

    //닉네입 변경
    @PutMapping("/nickname")
    public ResponseEntity<?> changeNickname(Principal principal, @RequestBody NicknameChangeRequestDto dto){
        Long userId = Long.parseLong(principal.getName());
        userService.changeNickname(userId, dto.getNickname());

        return ResponseEntity.ok("닉네임이 변경되었습니다.");
    }
}
