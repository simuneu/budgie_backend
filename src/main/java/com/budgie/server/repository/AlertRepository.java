package com.budgie.server.repository;

import com.budgie.server.entity.AlertEntity;
import com.budgie.server.enums.AlertType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    int countByUserIdAndIsReadFalse(Long userId);

    @Query("""
    SELECT COUNT(a)
    FROM AlertEntity a
    WHERE a.userId = :userId
      AND a.type = :type
      AND a.createdAt BETWEEN :start AND :end
    """)
    Long countMonthlyAlert(Long userId,
                           AlertType type,
                           LocalDateTime start,
                           LocalDateTime end);

    void deleteByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM AlertEntity a WHERE a.createdAt < :threshold")
    int deleteOldAlerts(@Param("threshold") LocalDateTime threshold);
}
