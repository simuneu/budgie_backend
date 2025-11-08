package com.budgie.server.security;

import com.budgie.server.entity.UserEntity;
import com.budgie.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService  implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException{
        UserEntity user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(()->new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        return new UserPrincipal(user);
    }
}
