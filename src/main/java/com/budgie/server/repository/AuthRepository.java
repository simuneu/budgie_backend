package com.budgie.server.repository;

import com.budgie.server.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Locale;
import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<UserEntity, Long> {
    //로그인 정보조회하기
    @Query("select user from UserEntity user where user.email = :email and user.deletedAt is null")
    Optional<UserEntity> findByEmail(String email);
    Boolean existsByEmail(String email); //이메일 중복 확인

    @Query("select user from UserEntity user where user.email = :email")
    Optional<UserEntity> findByEmailWithDeleted(String email);
}
