package br.edu.atitus.placa_pai.apisgasup.repositories;

import br.edu.atitus.placa_pai.apisgasup.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Boolean existsByEmail(String email);

    Boolean existsByEmailAndName(String email, String name);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.password = 'algumacoisa'")
    List<User> listaParaRelatorio();
}