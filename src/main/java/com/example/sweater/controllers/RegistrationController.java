package com.example.sweater.controllers;

import com.example.sweater.domain.User;
import com.example.sweater.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Model model) {

        String username = user.getUsername().trim();
        String password = user.getPassword().trim();
        String email = user.getEmail().trim();

        //Проверяем все ли поля заполнены
        if (username != null && !username.equals("")
                && password != null && !password.equals("")
                && email != null && !email.equals("")) {

            //Если такой пользователь уже существует то выдем ошибку
            if (!userService.addUser(user)) {
                model.addAttribute("message", "User exists!");
                return "registration";
            }
        } else {
            model.addAttribute("message", "Не заполнены все неообходимые поля для регистрации!");
        }

        return "redirect:/login";
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code) {

        boolean isActivated = userService.activateUser(code);

        if (isActivated) {
            model.addAttribute("message", "User successfully activated");
        } else {
            model.addAttribute("message", "Activation code is not found");
        }

        return "activate";
    }
}
