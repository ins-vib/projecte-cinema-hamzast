package com.daw.CinemaDaw.config;


import java.util.List;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.daw.CinemaDaw.domain.cinema.Cinema;
import com.daw.CinemaDaw.repository.CinemaRepository;

@Component
public class Proves implements CommandLineRunner{
    private CinemaRepository cinemaRepository;
 
 
 
    public Proves(CinemaRepository cinemaRepository) {
        this.cinemaRepository = cinemaRepository;
    }
   
   
   
    @Override
    public void run (String... args) throws Exception{
        
       Cinema cinema1 = new Cinema("Ocine", "Gavarres, 46", "Tarragona", "43122");
       Cinema cinema2 = new Cinema("Oscars", "Major,15", "Tarragona", "43100");
       cinemaRepository.save(cinema1);
       cinemaRepository.save(cinema2);

       List<Cinema> llista = cinemaRepository.findAll();
       for (Cinema cinema  : llista) {
           System.out.println(cinema);
       }


       Optional<Cinema> optionalCinema =cinemaRepository.findById(4L);
       if(optionalCinema.isPresent()){
        Cinema cinema = optionalCinema.get();
        System.out.println(cinema);
        cinema.setCity("Reus");
        cinemaRepository.save(cinema);
       }
       else{
        System.out.println("No trobat");
       }

         List<Cinema> llista2 = cinemaRepository.findByCity("Tarragona");
       for (Cinema cinema  : llista2) {
           System.out.println(cinema);
       }

       cinemaRepository.delete(llista2.get(0));

       llista = cinemaRepository.findAll();
       for (Cinema cinema  : llista) {
           System.out.println(cinema);
       }

       

    
    }

   
    
}
