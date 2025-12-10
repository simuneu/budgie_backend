package com.budgie.server.entity;

import com.budgie.server.enums.IsVerified;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name="EmailVerification")
public class EmailVerificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="email_id", nullable = false)
    private long emailId;

    @ManyToOne
    @JoinColumn(name="user_id")
    private UserEntity userId;

    @Column(name="email", nullable = false, length = 255, unique = true)
    private String email;

    @Column(name="valid_code", nullable = false, length = 50)
    private String validCode;

    @Column(name="expiration_time")
    private Instant expirationTime;

    @Enumerated(EnumType.STRING)
    private IsVerified isVerified;

    @CreationTimestamp
    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;
}
