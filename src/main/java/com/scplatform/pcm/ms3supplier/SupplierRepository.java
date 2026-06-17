/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<SupplierProfile, String> {
    List<SupplierProfile> findByIsActiveTrueOrderBySupplierNameAsc();

    @Query("SELECT s FROM SupplierProfile s WHERE s.isActive = true AND " +
           "(LOWER(s.supplierName) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(s.supplierId) LIKE LOWER(CONCAT('%',:q,'%')))")
    List<SupplierProfile> search(@Param("q") String query);
}
