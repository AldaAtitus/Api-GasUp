package br.edu.atitus.placa_pai.apisgasup.services;

import br.edu.atitus.placa_pai.apisgasup.dtos.PointDTO;
import br.edu.atitus.placa_pai.apisgasup.entities.*;
import br.edu.atitus.placa_pai.apisgasup.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PointService {
    private final PointRepository repository;
    private final FavoritoRepository favoritoRepository;

    public PointService(PointRepository repository, FavoritoRepository favoritoRepository) {
        this.repository = repository;
        this.favoritoRepository = favoritoRepository;
    }

    @Transactional
    public PointEntity save(PointEntity point) throws Exception {
        if (point == null)
            throw new Exception("Objeto Nulo");

        if (point.getDescription() == null || point.getDescription().isEmpty())
            throw new Exception("Descricao Invalida");
        if (point.getLatitude() < -90 || point.getLatitude() > 90)
            throw new Exception("Latitude Invalida");
        if (point.getLongitude() < -180 || point.getLongitude() > 180)
            throw new Exception("Longitude Invalida");

        User userAuth = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        point.setUser(userAuth);
        return repository.save(point);
    }

    @Transactional
    public void deleteById(UUID id) throws Exception {
        var pointInBD = repository.findById(id)
                .orElseThrow(() -> new Exception("Ponto nao encontrado"));

        User userAuth = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Admin pode deletar qualquer ponto
        if (userAuth.getType() != UserType.Admin) {
            if (!pointInBD.getUser().getId().equals(userAuth.getId())) {
                throw new Exception("Voce nao possui permissao para essa acao");
            }
        }
        favoritoRepository.deleteByPointId(id);
        repository.deleteById(id);
    }

    public List<PointEntity> findAll() {
        return repository.findAll();
    }

    @Transactional
    public PointEntity update(UUID id, PointDTO dto) throws Exception {
        var point = repository.findById(id)
                .orElseThrow(() -> new Exception("Ponto nao encontrado"));

        User userAuth = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!point.getUser().getId().equals(userAuth.getId())) {
            throw new Exception("Voce nao possui permissao para essa acao");
        }

        point.setDescription(dto.description());
        point.setLatitude(dto.latitude());
        point.setLongitude(dto.longitude());
        return repository.save(point);
    }

    public List<PointEntity> findByUser(User user) {
        return repository.findByUser(user);
    }
}