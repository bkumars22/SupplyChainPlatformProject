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
public interface DeliveryRepository extends JpaRepository<SupplierDelivery, Long> {
    List<SupplierDelivery> findBySupplierSupplierIdOrderByPromisedDateDesc(String supplierId);

    @Query("SELECT COUNT(d) FROM SupplierDelivery d WHERE d.supplier.supplierId = :sid")
    Long countBySupplierId(@Param("sid") String supplierId);

    @Query("SELECT COUNT(d) FROM SupplierDelivery d WHERE d.supplier.supplierId = :sid AND d.status = 'ON_TIME'")
    Long countOnTimeBySupplierId(@Param("sid") String supplierId);
}
