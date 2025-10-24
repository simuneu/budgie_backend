package com.budgie.server.repository;

import com.budgie.server.entity.RefreshTokenEntity;
import com.budgie.server.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByTokenValue(String tokenValue);

    Optional<RefreshTokenEntity> findByUser (UserEntity user);

}
