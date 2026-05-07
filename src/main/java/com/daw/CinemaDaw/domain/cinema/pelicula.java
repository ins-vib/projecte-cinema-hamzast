package com.daw.CinemaDaw.domain.cinema;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

import java.util.HashSet;
import java.util.Set;

@Entity
public class pelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String Titol;
    @Column
    private int Durada;
    @Column
    private String sinopsi;
    @Column
    private String estrena;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "pelicula_genere",
        joinColumns = @JoinColumn(name = "pelicula_id"),
        inverseJoinColumns = @JoinColumn(name = "genere_id")
    )
    private Set<Genere> generes = new HashSet<>();

    public pelicula() {}

    public pelicula(int Durada, String Titol, String estrena, Long id, String sinopsi) {
        this.Durada = Durada;
        this.Titol = Titol;
        this.estrena = estrena;
        this.id = id;
        this.sinopsi = sinopsi;
    }

    public Long getId() { return id; }
    public String getTitol() { return Titol; }
    public int getDurada() { return Durada; }
    public String getSinopsi() { return sinopsi; }
    public String getEstrena() { return estrena; }
    public Set<Genere> getGeneres() { return generes; }

    public void setId(Long id) { this.id = id; }
    public void setTitol(String Titol) { this.Titol = Titol; }
    public void setDurada(int Durada) { this.Durada = Durada; }
    public void setSinopsi(String sinopsi) { this.sinopsi = sinopsi; }
    public void setEstrena(String estrena) { this.estrena = estrena; }
    public void setGeneres(Set<Genere> generes) { this.generes = generes; }

    @Override
    public String toString() {
        return "pelicula [Titol=" + Titol + ", Durada=" + Durada + ", sinopsi=" + sinopsi
                + ", estrena=" + estrena + ", generes=" + generes + "]";
    }
}
