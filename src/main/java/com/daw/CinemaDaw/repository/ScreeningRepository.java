package com.daw.CinemaDaw.repository;

import com.daw.CinemaDaw.domain.cinema.Screening;
import com.daw.CinemaDaw.domain.cinema.pelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    List<Screening> findByRoom_Id(Long roomId);

    List<Screening> findByMovie(pelicula movie);

    @Query("SELECT DISTINCT s.movie FROM Screening s WHERE s.screeningDate >= CURRENT_DATE")
    List<pelicula> findPeliculesWithFutureScreenings();
}
