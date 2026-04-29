package com.daw.CinemaDaw.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.daw.CinemaDaw.domain.cinema.PromoCode;

public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {

    Optional<PromoCode> findByCode(String code);

    boolean existsByCode(String code);
}
