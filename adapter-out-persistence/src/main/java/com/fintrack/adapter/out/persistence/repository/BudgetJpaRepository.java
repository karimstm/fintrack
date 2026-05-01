package com.fintrack.adapter.out.persistence.repository;

import com.fintrack.adapter.out.persistence.entity.BudgetJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetJpaRepository extends JpaRepository<BudgetJpaEntity, UUID> {

    Optional<BudgetJpaEntity> findByOwnerIdAndCategoryAndPeriodYearAndPeriodMonth(
            UUID ownerId, String category, int periodYear, int periodMonth);

    List<BudgetJpaEntity> findAllByOwnerIdAndPeriodYearAndPeriodMonth(
            UUID ownerId, int periodYear, int periodMonth);

    List<BudgetJpaEntity> findAllByOwnerId(UUID ownerId);
}