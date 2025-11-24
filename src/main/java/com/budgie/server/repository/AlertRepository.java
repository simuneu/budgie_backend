package com.budgie.server.repository;

import com.budgie.server.entity.AlertEntity;
import com.budgie.server.enums.AlertType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<AlertEntity, Long> {

    boolean existsByUserIdAndTypeAndCreatedAtBetween(
            Long userId,
            AlertType type,
            LocalDateTime start,
            LocalDateTime end
    );

    List<AlertEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

    //중복 알림 방지
    @Query("""
        SELECT COUNT(a) > 0
        FROM AlertEntity a
        WHERE a.user.userId = :userId
          AND a.type = :type
          AND MONTH(a.createdAt) = MONTH(CURRENT_DATE)
          AND YEAR(a.createdAt) = YEAR(CURRENT_DATE)
    """)
    boolean existsMonthlyAlert(Long userId, AlertType type);

}
