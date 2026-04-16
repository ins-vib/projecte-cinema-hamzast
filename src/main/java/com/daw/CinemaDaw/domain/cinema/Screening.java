package com.daw.CinemaDaw.domain.cinema;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Screening {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDate screeningDate;

    @Column
    private LocalTime screeningTime;

    @Column
    private double price;

    @ManyToOne
    private pelicula movie;

    @ManyToOne
    private Room room;

    public Screening() {
    }

    public Screening(LocalDate screeningDate, LocalTime screeningTime, double price, pelicula movie, Room room) {
        this.screeningDate = screeningDate;
        this.screeningTime = screeningTime;
        this.price = price;
        this.movie = movie;
        this.room = room;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getScreeningDate() { return screeningDate; }
    public void setScreeningDate(LocalDate screeningDate) { this.screeningDate = screeningDate; }

    public LocalTime getScreeningTime() { return screeningTime; }
    public void setScreeningTime(LocalTime screeningTime) { this.screeningTime = screeningTime; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public pelicula getMovie() { return movie; }
    public void setMovie(pelicula movie) { this.movie = movie; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    @Override
    public String toString() {
        return "Screening [id=" + id + ", date=" + screeningDate + ", time=" + screeningTime + "]";
    }



    
}
