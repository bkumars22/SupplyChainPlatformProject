/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

import com.scplatform.pcm.alert.entity.Alert;
import com.scplatform.pcm.alert.repository.AlertRepository;
import com.scplatform.pcm.auditlog.Auditable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class SupplierRatingService {

    @Autowired
    private SupplierRatingRepository ratingRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private AlertRepository alertRepository;

    public List<SupplierRating> getRatings(String supplierId) {
        return ratingRepository.findBySupplierIdOrderByRatingDateDesc(supplierId);
    }

    public List<SupplierRating> getLatestRatings(String supplierId) {
        return ratingRepository.findTop10BySupplierIdOrderByRatingDateDesc(supplierId);
    }

    @Auditable(entityType = "SupplierRating", action = "ADD_RATING")
    public SupplierRating addRating(String supplierId, RatingRequest req) {
        validateScore(req.qualityScore(), "qualityScore");
        validateScore(req.deliveryScore(), "deliveryScore");
        validateScore(req.responsiveness(), "responsiveness");

        SupplierRating rating = new SupplierRating();
        rating.setSupplierId(supplierId);
        rating.setRatedBy(req.ratedBy());
        rating.setRatingDate(LocalDate.now());
        rating.setQualityScore(req.qualityScore());
        rating.setDeliveryScore(req.deliveryScore());
        rating.setResponsiveness(req.responsiveness());
        rating.setComments(req.comments());
        rating.setCreatedAt(LocalDateTime.now());

        SupplierRating saved = ratingRepository.save(rating);

        // Overall score: average of the three 0-100 inputs, scaled to 0-5 for tier thresholds
        BigDecimal overall = req.qualityScore()
                .add(req.deliveryScore())
                .add(req.responsiveness())
                .divide(new BigDecimal("3"), 2, RoundingMode.HALF_UP);
        BigDecimal scaled = overall.divide(new BigDecimal("20"), 4, RoundingMode.HALF_UP);

        updateTierAndAlert(supplierId, scaled, overall, req.ratedBy());

        return saved;
    }

    // Runs inside the same @Transactional — rolls back if either save fails
    private void updateTierAndAlert(String supplierId, BigDecimal scaled, BigDecimal overall, String ratedBy) {
        supplierRepository.findById(supplierId).ifPresent(supplier -> {

            SupplierTier newTier;
            if (scaled.compareTo(new BigDecimal("4.5")) >= 0) {
                newTier = SupplierTier.GOLD;
            } else if (scaled.compareTo(new BigDecimal("3.5")) >= 0) {
                newTier = SupplierTier.SILVER;
            } else if (scaled.compareTo(new BigDecimal("2.5")) >= 0) {
                newTier = SupplierTier.AT_RISK;
            } else {
                newTier = SupplierTier.CRITICAL;
            }

            supplier.setTier(newTier);
            supplierRepository.save(supplier);

            if (newTier == SupplierTier.CRITICAL) {
                String formattedScore = overall.setScale(1, RoundingMode.HALF_UP).toPlainString();
                Alert alert = new Alert();
                alert.setState(Alert.ACTIVE);
                alert.setAlertId("SupplierRating-CRITICAL-" + supplierId + "-" + System.currentTimeMillis());
                alert.setAlertType("SupplierRatingCritical");
                alert.setAlertLabel("Supplier Rated CRITICAL");
                alert.setShortSummary("Supplier rated CRITICAL — score " + formattedScore);
                alert.setLongSummary("Supplier " + supplier.getSupplierName() + " (" + supplierId
                        + ") received a critical quality rating of " + formattedScore
                        + "/100. Immediate review recommended.");
                alert.setUserId(ratedBy);
                alert.setCreated(new Date());
                alert.setExpirationDate(java.sql.Date.valueOf(LocalDate.now().plusDays(30)));
                alertRepository.save(alert);
            }
        });
    }

    private void validateScore(BigDecimal score, String fieldName) {
        if (score == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
        if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException(fieldName + " must be between 0 and 100");
        }
    }

    public record RatingRequest(
            String ratedBy,
            BigDecimal qualityScore,
            BigDecimal deliveryScore,
            BigDecimal responsiveness,
            String comments
    ) {}
}
