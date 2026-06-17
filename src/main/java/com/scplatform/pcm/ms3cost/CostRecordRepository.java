/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3cost;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CostRecordRepository extends JpaRepository<CostRecord, Long> {

    @Query("SELECT c FROM CostRecord c JOIN c.item i WHERE " +
           "(:search IS NULL OR LOWER(i.itemNumber) LIKE LOWER(CONCAT('%',:search,'%')) OR " +
           "LOWER(c.justification) LIKE LOWER(CONCAT('%',:search,'%')))")
    Page<CostRecord> searchAll(@Param("search") String search, Pageable pageable);

    List<CostRecord> findByItemItemNumberOrderByVersionNumberDesc(String itemCode);

    List<CostRecord> findByStatusOrderBySubmittedDateAsc(CostStatus status);

    @Query("SELECT COALESCE(MAX(c.versionNumber), 0) + 1 FROM CostRecord c WHERE c.item.itemNumber = :itemCode")
    Integer getNextVersionNumber(@Param("itemCode") String itemCode);

    Optional<CostRecord> findByItemItemNumberAndStatusIn(String itemCode, List<CostStatus> statuses);

    @Query("SELECT c.status, COUNT(c) FROM CostRecord c GROUP BY c.status")
    List<Object[]> countByStatus();

    @Query("SELECT COUNT(c) FROM CostRecord c WHERE c.status = 'PENDING_APPROVAL'")
    Long countPending();
}
