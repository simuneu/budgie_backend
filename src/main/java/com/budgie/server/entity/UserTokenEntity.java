package com.budgie.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import java.time.Instant;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name="UserToken")
@DynamicUpdate
public class UserTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="token_id", nullable = false)
    private Long tokenId;

    @OneToOne
    @JoinColumn(name="user_id", referencedColumnName = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(name="token_value", nullable = false, length = 500)
    private String tokenValue;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = true)
    private LocalDateTime createdAt;

    public void updateToken(String newTokenValue, Instant newExpiryDate){
        this.tokenValue = newTokenValue;
        this.expiryDate = newExpiryDate;
    }

}
