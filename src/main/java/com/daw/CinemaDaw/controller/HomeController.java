package com.daw.CinemaDaw.controller;


import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.daw.CinemaDaw.domain.cinema.News;
import com.daw.CinemaDaw.service.NewsService;

@Controller
public class HomeController {

    private NewsService newsService;

    public HomeController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/")
    public String home(Model model) {

        ArrayList<News> lista = new ArrayList<>();

        try {
            lista = newsService.getNews();
        } catch (FileNotFoundException ex) {
            System.getLogger(HomeController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }

        model.addAttribute("lista", lista);
        return "home";
    }
    

















    
}




