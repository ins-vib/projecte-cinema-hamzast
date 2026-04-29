package com.daw.CinemaDaw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.daw.CinemaDaw.domain.cinema.PromoUsage;

public interface PromoUsageRepository extends JpaRepository<PromoUsage, Long> {

    boolean existsByPromoCode_IdAndUser_Username(Long promoCodeId, String username);

    List<PromoUsage> findByPromoCode_IdOrderByUsedAtDesc(Long promoCodeId);
}
