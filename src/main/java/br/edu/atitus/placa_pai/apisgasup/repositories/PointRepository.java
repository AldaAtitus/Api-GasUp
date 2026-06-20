package br.edu.atitus.placa_pai.apisgasup.repositories;

import br.edu.atitus.placa_pai.apisgasup.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface PointRepository extends JpaRepository<PointEntity, UUID> {

    List<PointEntity> findByUser(User user);
}
