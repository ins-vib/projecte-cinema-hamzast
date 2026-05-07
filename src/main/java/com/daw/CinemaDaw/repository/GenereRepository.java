package com.daw.CinemaDaw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.daw.CinemaDaw.domain.cinema.Genere;

@Repository
public interface GenereRepository extends JpaRepository<Genere, Long> {}
