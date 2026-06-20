package br.edu.atitus.placa_pai.apisgasup.repositories;

import br.edu.atitus.placa_pai.apisgasup.entities.FavoritoEntity;
import br.edu.atitus.placa_pai.apisgasup.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FavoritoRepository extends JpaRepository<FavoritoEntity, UUID> {

    List<FavoritoEntity> findByUser(User user);

    boolean existsByUserIdAndPointId(UUID userId, UUID pointId);

    void deleteByUserIdAndPointId(UUID userId, UUID pointId);
}