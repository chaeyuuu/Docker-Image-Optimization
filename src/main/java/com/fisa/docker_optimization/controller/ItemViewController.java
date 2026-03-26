package com.fisa.docker_optimization.controller;

import com.fisa.docker_optimization.dto.ItemDto;
import com.fisa.docker_optimization.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemViewController {

    private final ItemService itemService;

    @GetMapping
    public String list(Model model, @RequestParam(required = false) String search) {
        if (search != null && !search.isBlank()) {
            model.addAttribute("items", itemService.searchByName(search));
            model.addAttribute("search", search);
        } else {
            model.addAttribute("items", itemService.findAll());
        }
        model.addAttribute("totalCount", itemService.countItems());
        model.addAttribute("totalQuantity", itemService.sumQuantity());
        return "items/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("item", itemService.findById(id));
        return "items/detail";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("item", new ItemDto());
        return "items/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute("item") ItemDto dto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "items/form";
        }
        ItemDto saved = itemService.save(dto);
        redirectAttributes.addFlashAttribute("message", "상품이 등록되었습니다.");
        return "redirect:/items/" + saved.getId();
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("item", itemService.findById(id));
        return "items/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @ModelAttribute("item") ItemDto dto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "items/form";
        }
        itemService.update(id, dto);
        redirectAttributes.addFlashAttribute("message", "상품이 수정되었습니다.");
        return "redirect:/items/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        itemService.delete(id);
        redirectAttributes.addFlashAttribute("message", "상품이 삭제되었습니다.");
        return "redirect:/items";
    }
}