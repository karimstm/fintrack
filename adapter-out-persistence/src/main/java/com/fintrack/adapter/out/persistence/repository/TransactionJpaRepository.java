package com.fintrack.adapter.out.persistence.repository;

import com.fintrack.adapter.out.persistence.entity.TransactionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface TransactionJpaRepository extends JpaRepository<TransactionJpaEntity, UUID> {

    List<TransactionJpaEntity> findAllByAccountIdOrderByOccurredAtDesc(UUID accountId);

    @Query("""
            SELECT t FROM TransactionJpaEntity t
            WHERE t.accountId = :accountId
              AND t.occurredAt BETWEEN :from AND :to
            ORDER BY t.occurredAt DESC
            """)
    List<TransactionJpaEntity> findByAccountIdAndDateRange(
            @Param("accountId") UUID accountId,
            @Param("from") Instant from,
            @Param("to") Instant to
    );
}