package ru.amir.springcourse.controllers;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.amir.springcourse.security.PersonDetails;

@Controller
@RequestMapping("/")
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "admin";
    }

    @GetMapping("/show-user-info")
    @ResponseBody
    public String showUserInfo() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        PersonDetails person = (PersonDetails)securityContext.getAuthentication().getPrincipal();
        return person.getUsername();
    }

    @GetMapping("/accessDenied")
    public String accessDenied() {
        return "accessDenied";
    }
}
