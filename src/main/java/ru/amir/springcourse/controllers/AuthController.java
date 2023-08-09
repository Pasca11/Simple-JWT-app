package ru.amir.springcourse.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.amir.springcourse.dto.AuthDto;
import ru.amir.springcourse.dto.PersonDTO;
import ru.amir.springcourse.models.Person;
import ru.amir.springcourse.security.JWTUtil;
import ru.amir.springcourse.services.RegistrationService;
import ru.amir.springcourse.util.PersonValidator;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final PersonValidator personValidator;
    private final RegistrationService registrationService;
    private final JWTUtil jwtUtil;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;

    public AuthController(PersonValidator personValidator, RegistrationService registrationService, JWTUtil jwtUtil, ModelMapper modelMapper, AuthenticationManager authenticationManager) {
        this.personValidator = personValidator;
        this.registrationService = registrationService;
        this.jwtUtil = jwtUtil;
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/registration")
    public String registrationPage(@ModelAttribute("person") Person person) {
        return "auth/registration";
    }

    @PostMapping("/registration")
    public Map<String, String> register(@RequestBody @Valid PersonDTO personDTO,
                           BindingResult result) {
        personValidator.validate(modelMapper.map(personDTO, Person.class), result);
        if (result.hasErrors())
            return Map.of("Error", "Oh no!");

        registrationService.register(modelMapper.map(personDTO, Person.class));

        String token = jwtUtil.getToken(personDTO.getUsername());

        return Map.of("jwt-token", token);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody AuthDto authDto) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                authDto.getUsername(),
                authDto.getPassword()
        );
        try {
            authenticationManager.authenticate(token);
        } catch (BadCredentialsException e) {
            return Map.of("error", "Bad credentials");
        }
        String token2 = jwtUtil.getToken(authDto.getUsername());
        return Map.of("jwt-token", token2);
    }

}
