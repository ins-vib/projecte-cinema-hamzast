package com.daw.CinemaDaw.domain.cinema;

import jakarta.persistence.*;

@Entity
public class Genere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    public Genere() {}
    public Genere(String nom) { this.nom = nom; }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public void setId(Long id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
}
