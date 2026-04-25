package org.lytvynovych.bettingaiverification.repository;

import org.lytvynovych.bettingaiverification.entity.VerificationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VerificationResultRepository extends JpaRepository<VerificationResult, Long> {
    List<VerificationResult> findByBetUserId(Long userId);

    @Query("SELECT vr FROM VerificationResult vr WHERE vr.bet.user.id = :userId ORDER BY vr.createdAt DESC LIMIT 1")
    Optional<VerificationResult> findLatestByUserId(@Param("userId") Long userId);

    @Query("SELECT vr FROM VerificationResult vr WHERE vr.bet.id = :betId ORDER BY vr.createdAt DESC LIMIT 1")
    Optional<VerificationResult> findLatestByBetId(@Param("betId") Long betId);
}