package com.fisa.docker_optimization.controller;

import com.fisa.docker_optimization.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ItemService itemService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("totalItems", itemService.countItems());
        model.addAttribute("totalQuantity", itemService.sumQuantity());
        model.addAttribute("recentItems", itemService.findAll());
        return "home";
    }
}
