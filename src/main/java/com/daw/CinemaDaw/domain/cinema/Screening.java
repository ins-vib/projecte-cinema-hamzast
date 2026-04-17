package com.daw.CinemaDaw.domain.cinema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

@Entity
public class Screening {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "screening_date_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime screeningDateTime;

    @Column
    private double price;

    @ManyToOne
    private pelicula movie;

    @ManyToOne
    private Room room;

    public Screening(LocalDateTime screeningDateTime, double price, pelicula movie, Room room) {
        this.screeningDateTime = screeningDateTime;
        this.price = price;
        this.movie = movie;
        this.room = room;
    }

    public Screening() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getScreeningDateTime() {
        return screeningDateTime;
    }

    public void setScreeningDateTime(LocalDateTime screeningDateTime) {
        this.screeningDateTime = screeningDateTime;
    }

    @Transient
    public LocalDate getScreeningDate() {
        return screeningDateTime != null ? screeningDateTime.toLocalDate() : null;
    }

    public void setScreeningDate(LocalDate screeningDate) {
        if (screeningDate == null) {
            screeningDateTime = null;
            return;
        }

        LocalTime time = screeningDateTime != null ? screeningDateTime.toLocalTime() : LocalTime.MIDNIGHT;
        screeningDateTime = LocalDateTime.of(screeningDate, time);
    }

    @Transient
    public LocalTime getScreeningTime() {
        return screeningDateTime != null ? screeningDateTime.toLocalTime() : null;
    }

    public void setScreeningTime(LocalTime screeningTime) {
        if (screeningTime == null) {
            screeningDateTime = null;
            return;
        }

        LocalDate date = screeningDateTime != null ? screeningDateTime.toLocalDate() : null;
        if (date == null) {
            return;
        }
        screeningDateTime = LocalDateTime.of(date, screeningTime);
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public pelicula getMovie() {
        return movie;
    }

    public void setMovie(pelicula movie) {
        this.movie = movie;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
