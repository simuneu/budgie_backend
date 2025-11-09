package com.budgie.server.entity;

import com.budgie.server.enums.BudgetType;
import com.budgie.server.enums.CategoryName;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "category",
        uniqueConstraints = @UniqueConstraint(columnNames = {"budget_type", "name"}))
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BudgetType budgetType; //income, exp

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryName name;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
