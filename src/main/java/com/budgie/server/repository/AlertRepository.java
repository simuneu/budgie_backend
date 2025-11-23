package com.budgie.server.repository;

import com.budgie.server.entity.AlertEntity;
import com.budgie.server.enums.AlertType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AlertRepository extends JpaRepository<AlertEntity, Long> {

    boolean existsByUserIdAndTypeAndCreatedAtBetween(
            Long userId,
            AlertType type,
            LocalDateTime start,
            LocalDateTime end
    );
}
