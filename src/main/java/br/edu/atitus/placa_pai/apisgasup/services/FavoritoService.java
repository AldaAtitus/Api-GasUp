package br.edu.atitus.placa_pai.apisgasup.services;

import br.edu.atitus.placa_pai.apisgasup.entities.*;
import br.edu.atitus.placa_pai.apisgasup.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final PointRepository pointRepository;

    public FavoritoService(FavoritoRepository favoritoRepository, PointRepository pointRepository) {
        this.favoritoRepository = favoritoRepository;
        this.pointRepository = pointRepository;
    }

    @Transactional
    public FavoritoEntity salvar(UUID pointId) throws Exception {
        User userAuth = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        PointEntity point = pointRepository.findById(pointId)
                .orElseThrow(() -> new Exception("Ponto não encontrado"));

        if (favoritoRepository.existsByUserIdAndPointId(userAuth.getId(), pointId)) {
            throw new Exception("Este ponto já está nos favoritos");
        }

        FavoritoEntity favorito = new FavoritoEntity();
        favorito.setUser(userAuth);
        favorito.setPoint(point);

        return favoritoRepository.save(favorito);
    }

    @Transactional
    public void remover(UUID pointId) throws Exception {
        User userAuth = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!favoritoRepository.existsByUserIdAndPointId(userAuth.getId(), pointId)) {
            throw new Exception("Ponto não está nos favoritos");
        }

        favoritoRepository.deleteByUserIdAndPointId(userAuth.getId(), pointId);
    }

    public List<FavoritoEntity> listarFavoritosDoUsuario() {
        User userAuth = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return favoritoRepository.findByUser(userAuth);
    }

    public boolean isFavorito(UUID pointId) {
        User userAuth = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return favoritoRepository.existsByUserIdAndPointId(userAuth.getId(), pointId);
    }
}