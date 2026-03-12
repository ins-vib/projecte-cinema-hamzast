

package com.daw.CinemaDaw.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.springframework.stereotype.Service;

import com.daw.CinemaDaw.domain.cinema.News;

@Service
public class NewsService {

    public ArrayList<News> getNews() throws FileNotFoundException {

        ArrayList<News> newList = new ArrayList<>();

        // Llegir un fitxer de text línia a línia
        File f = new File("News.txt");
        if (f.exists()) {
            // llegir l'arxiu
            Scanner lectorFitxer = new Scanner(f);
            String linia;
            while (lectorFitxer.hasNextLine()) {
                linia = lectorFitxer.nextLine();
                String[] camps = linia.split(":");

                if (camps.length == 2) {
                    News n = new News(camps[0], camps[1]);
                    newList.add(n);
                }

                System.out.println(linia);
            }

        } else {
            System.out.println("No existeix l'arxiu");
        }

        return newList;
    }

}
