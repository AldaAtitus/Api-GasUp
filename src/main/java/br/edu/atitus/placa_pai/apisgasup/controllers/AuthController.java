package br.edu.atitus.placa_pai.apisgasup.controllers;

import br.edu.atitus.placa_pai.apisgasup.components.JwtUtil;
import br.edu.atitus.placa_pai.apisgasup.dtos.*;
import br.edu.atitus.placa_pai.apisgasup.entities.*;
import br.edu.atitus.placa_pai.apisgasup.services.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import javax.security.sasl.AuthenticationException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationConfiguration auth;

    public AuthController(UserService userService, AuthenticationConfiguration auth) {
        this.userService = userService;
        this.auth = auth;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> postSignup(@RequestBody SignupDTO dto) throws Exception {
        User newUser = new User();
        BeanUtils.copyProperties(dto, newUser);
        newUser.setType(UserType.Common);
        userService.save(newUser);
        return ResponseEntity.status(201).body(newUser);
    }
    @PostMapping("/signin")
    public ResponseEntity<String> postSignin(@RequestBody SigninDTO dto) throws AuthenticationException, Exception {
        auth.getAuthenticationManager()
                .authenticate(new UsernamePasswordAuthenticationToken(dto.email(), dto.password()));
        User user = (User) this.userService.loadUserByUsername(dto.email());
        String jwt = JwtUtil.generateToken(user.getEmail(), user.getId(), user.getType());
        return ResponseEntity.ok(jwt);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        User userAuth = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userAuth.setPassword(null);
        return ResponseEntity.ok(userAuth);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> excpetionHandler(Exception ex){
        String message = ex.getMessage().replace("\r\n", "");
        return ResponseEntity.badRequest().body(message);
    }
}

