package br.edu.atitus.placa_pai.apisgasup.controllers;

import br.edu.atitus.placa_pai.apisgasup.components.JwtUtil;
import br.edu.atitus.placa_pai.apisgasup.dtos.*;
import br.edu.atitus.placa_pai.apisgasup.entities.*;
import br.edu.atitus.placa_pai.apisgasup.services.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationConfiguration auth;

    // Diretório onde as fotos serão salvas
    private static final String UPLOAD_DIR = "uploads/photos/";

    public AuthController(UserService userService, AuthenticationConfiguration auth) {
        this.userService = userService;
        this.auth = auth;
        // Cria o diretório se não existir
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> postSignup(@RequestBody SignupDTO dto) {
        try {
            User newUser = new User();
            BeanUtils.copyProperties(dto, newUser);
            newUser.setType(UserType.Common);

            // Se a foto foi enviada como Base64, salva
            if (dto.photo() != null && !dto.photo().isEmpty()) {
                newUser.setPhoto(dto.photo());
            }

            userService.save(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Endpoint para upload de foto (opcional)
    @PostMapping("/upload-photo")
    public ResponseEntity<?> uploadPhoto(@RequestParam("file") MultipartFile file) {
        try {
            // Obtém o usuário autenticado
            User userAuth = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            // Valida o arquivo
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("Nenhum arquivo enviado.");
            }

            // Valida o tipo do arquivo
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("O arquivo deve ser uma imagem.");
            }

            // Valida o tamanho (máximo 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("A imagem deve ter no máximo 5MB.");
            }

            // Gera um nome único para o arquivo
            String fileName = userAuth.getId() + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + fileName);

            // Salva o arquivo
            Files.write(path, file.getBytes());

            // Atualiza o usuário com a URL da foto
            String photoUrl = "/uploads/photos/" + fileName;
            userAuth.setPhoto(photoUrl);
            userService.save(userAuth);

            Map<String, String> response = new HashMap<>();
            response.put("photoUrl", photoUrl);
            response.put("message", "Foto enviada com sucesso!");

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao salvar a foto: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> postSignin(@RequestBody SigninDTO dto) {
        try {
            auth.getAuthenticationManager()
                    .authenticate(new UsernamePasswordAuthenticationToken(dto.email(), dto.password()));

            User user = (User) this.userService.loadUserByUsername(dto.email());

            // Cria um objeto de resposta com os dados do usuário e o token
            Map<String, Object> response = new HashMap<>();
            response.put("token", JwtUtil.generateToken(user.getEmail(), user.getId(), user.getType()));
            response.put("user", user);

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuário ou senha incorretos. Verifique seus dados e tente novamente.");

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuário não encontrado. Verifique seu e-mail ou cadastre-se.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Erro ao fazer login. Tente novamente mais tarde.");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        User userAuth = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userAuth.setPassword(null);
        return ResponseEntity.ok(userAuth);
    }

    @PostMapping("/signup-admin")
    public ResponseEntity<?> postSignupAdmin(@RequestBody SignupDTO dto) {
        try {
            User newUser = new User();
            BeanUtils.copyProperties(dto, newUser);
            newUser.setType(UserType.Admin); // Define como Admin diretamente

            if (dto.photo() != null && !dto.photo().isEmpty()) {
                newUser.setPhoto(dto.photo());
            }

            userService.save(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception ex) {
        String message = ex.getMessage().replace("\r\n", "");
        return ResponseEntity.badRequest().body(message);
    }
}