package br.edu.atitus.placa_pai.apisgasup.services;

import br.edu.atitus.placa_pai.apisgasup.entities.User;
import br.edu.atitus.placa_pai.apisgasup.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import br.edu.atitus.placa_pai.apisgasup.entities.UserType;

@Service
public class UserService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com este e-mail"));
    }

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    public User save(User newUser) throws Exception {
        if (newUser == null)
            throw new Exception("Objeto Nulo!");

        if (newUser.getName() == null || newUser.getName().isBlank())
            throw new Exception("Nome informado inválido!");
        newUser.setName(newUser.getName().trim());

        if (newUser.getEmail() == null || newUser.getEmail().isBlank())
            throw new Exception("E-mail informado inválido!");
        newUser.setEmail(newUser.getEmail().trim().toLowerCase());
        String regexEmail =
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        if (!newUser.getEmail().matches(regexEmail)) {
            throw new Exception(
                    "E-mail inválido. Exemplo válido: usuario@gmail.com"
            );
        }

        if (repository.existsByEmail(newUser.getEmail()))
            throw new Exception("Já existe usuário cadastrado com este e-mail!");

        if (newUser.getPassword() == null || newUser.getPassword().length() < 8)
            throw new Exception("Password informado inválido!");
        String regexSenha =
                "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";

        if (!newUser.getPassword().matches(regexSenha)) {
            throw new Exception(
                    "A senha deve conter no mínimo 8 caracteres, uma letra maiúscula, uma minúscula e um número."
            );
        }

        newUser.setPassword(encoder.encode(newUser.getPassword()));

        if (newUser.getType() == null)
            throw new Exception("Tipo de usuário informado inválido!");
        return repository.save(newUser);
    }

    public Optional<User> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return repository.findByEmail(email.trim().toLowerCase());
    }

    public User createGoogleUser(String name, String email) throws Exception {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setType(UserType.Common);
        user.setPassword(generateGoogleTechnicalPassword());
        return save(user);
    }

    private String generateGoogleTechnicalPassword() {
        return "Gg1!" + UUID.randomUUID().toString().replace("-", "A");
    }

}
