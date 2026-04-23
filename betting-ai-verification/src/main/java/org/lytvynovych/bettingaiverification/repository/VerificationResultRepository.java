package org.lytvynovych.bettingaiverification.repository;

import org.lytvynovych.bettingaiverification.entity.VerificationResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VerificationResultRepository extends JpaRepository<VerificationResult, Long> {
    List<VerificationResult> findByBetUserId(Long userId);
}