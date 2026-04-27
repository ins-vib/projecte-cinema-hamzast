package com.daw.CinemaDaw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.daw.CinemaDaw.domain.cinema.TicketOrder;

public interface TicketOrderRepository extends JpaRepository<TicketOrder, Long> {

    List<TicketOrder> findByUser_UsernameOrderByPurchasedAtDesc(String username);
}
