package com.daw.CinemaDaw.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.daw.CinemaDaw.domain.cinema.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    long countByUser_Username(String username);

    List<CartItem> findByUser_UsernameOrderByCreatedAtAsc(String username);

    List<CartItem> findByScreening_Id(Long screeningId);

    List<CartItem> findByScreening_IdAndUser_Username(Long screeningId, String username);

    Optional<CartItem> findByScreening_IdAndSeat_Id(Long screeningId, Long seatId);

    Optional<CartItem> findByIdAndUser_Username(Long id, String username);

    void deleteByUser_Username(String username);
}
