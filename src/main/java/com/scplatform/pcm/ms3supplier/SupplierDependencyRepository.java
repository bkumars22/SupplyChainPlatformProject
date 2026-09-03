/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SupplierDependencyRepository extends JpaRepository<SupplierDependency, Long> {
    List<SupplierDependency> findByDependentSupplierId(String dependentSupplierId);
    List<SupplierDependency> findByUpstreamSupplierId(String upstreamSupplierId);
}
