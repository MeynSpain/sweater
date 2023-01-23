package com.example.sweater.controllers;

import com.example.sweater.domain.Message;
import com.example.sweater.domain.Role;
import com.example.sweater.domain.User;
import com.example.sweater.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/user")
public class UserController {

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public String userList(Model model) {
        model.addAttribute("users", userService.findAll());
        return "userList";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("{user}")
    public String userEditForm(@PathVariable User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "userEdit";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public String userSave(
            @RequestParam String username,
            @RequestParam Map<String, String> form,
            @RequestParam("userId") User user) {

        userService.saveUser(user, username, form);

        return "redirect:/user";
    }

    @GetMapping("profile")
    public String getProfile(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());

        return "profile";
    }

    @PostMapping("profile")
    public String updateProfile(
            @AuthenticationPrincipal User user,
            @RequestParam String password,
            @RequestParam String email
    ) {
        userService.updateProfile(user, password, email);
        return "redirect:/user/profile";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("post/{id}")
    public String editPost(
            @PathVariable(value = "id") Long id,
            Model model
    ) {

        if (!userService.existsById(id)) {
            return "redirect:/main";
        }

        Message message = userService.findById(id);

        //Добавляем сообщение в модель
        model.addAttribute("message", message);

        return "post-edit";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("post/{id}")
    public String editPostSave(
            @PathVariable(value = "id") Long id,
            @RequestParam String text,
            @RequestParam String tag,
            @RequestParam Boolean delete,
            Model model
    ) throws IOException {

        if (!userService.existsById(id)) {
            return "redirect:/main";
        }

        Message message = userService.findById(id);

        message.setText(text);
        message.setTag(tag);

        //Если есть картинка
        if (message.getFilename() != null) {
            if (delete) {

                File file = new File(uploadPath + "/" + message.getFilename());

                message.setFilename(null);

                if (file.delete()) {
                    System.out.println("Файл удален");
                } else {
                    System.out.println("Не получилось удалить файл");
                }
            }
        }
        userService.updateMessage(message);

        return "redirect:/main";
    }


}
