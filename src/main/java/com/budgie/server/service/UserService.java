package com.budgie.server.service;

import com.budgie.server.dto.PasswordChangeRequestDto;
import com.budgie.server.entity.UserEntity;
import com.budgie.server.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //패스워드
    private static final String PASSWORD_REGEX =
            "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$";

    @Transactional
    public void changePassword(Long userId, PasswordChangeRequestDto dto){
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("유저를 찾을 수 없습니다."));

        //현 비번 검증
        if(!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())){
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
        }

        //새 비번 규칙 검사
        if ((!dto.getNewPassword().matches(PASSWORD_REGEX)){
            throw new RuntimeException("비밀번호는 영문, 숫자, 특수문자 포함 8자리 이상이어야 합니다.");
        }

        //저장
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    }
}
