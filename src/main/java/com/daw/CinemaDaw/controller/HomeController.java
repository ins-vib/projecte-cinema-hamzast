package com.daw.CinemaDaw.controller;


import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.daw.CinemaDaw.domain.cinema.News;
import com.daw.CinemaDaw.domain.cinema.user.Role;
import com.daw.CinemaDaw.domain.cinema.user.User;
import com.daw.CinemaDaw.repository.UserRepository;
import com.daw.CinemaDaw.service.NewsService;

@Controller

public class HomeController {

    private final NewsService newsService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public HomeController(NewsService newsService,
                          UserRepository userRepository,
                          BCryptPasswordEncoder passwordEncoder) {
        this.newsService = newsService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
    

    // Mostra la pàgina de login
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               Model model) {
        String cleanUsername = username == null ? "" : username.trim();

        if (cleanUsername.isEmpty() || password == null || password.isBlank()) {
            model.addAttribute("error", "Usuario y contrasena son obligatorios.");
            model.addAttribute("username", cleanUsername);
            return "register";
        }

        if (userRepository.findByUsername(cleanUsername).isPresent()) {
            model.addAttribute("error", "Ese nombre de usuario ya existe.");
            model.addAttribute("username", cleanUsername);
            return "register";
        }

        User user = new User();
        user.setUsername(cleanUsername);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.CLIENT);
        userRepository.save(user);

        return "redirect:/login?registered";
    }


 // Pàgina d'admin
    @GetMapping("/admin")
    public String admin() {
        return "admin/home";
    }

    // Pàgina de client
    @GetMapping("/client")
    public String client(Model model) {
       
        return "client/home";
    }













    
}



