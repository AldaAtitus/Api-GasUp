package br.edu.atitus.placa_pai.apisgasup.controllers;

import br.edu.atitus.placa_pai.apisgasup.dtos.FavoritoDTO;
import br.edu.atitus.placa_pai.apisgasup.entities.FavoritoEntity;
import br.edu.atitus.placa_pai.apisgasup.services.FavoritoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ws/favorito")
public class FavoritoController {

    private final FavoritoService service;

    public FavoritoController(FavoritoService service) {
        this.service = service;
    }

    // Adicionar favorito
    @PostMapping
    public ResponseEntity<?> adicionar(@RequestBody FavoritoDTO dto) {
        try {
            var favorito = service.salvar(dto.pointId());
            return ResponseEntity.status(HttpStatus.CREATED).body(favorito);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Remover favorito
    @DeleteMapping("/{pointId}")
    public ResponseEntity<String> remover(@PathVariable UUID pointId) {
        try {
            service.remover(pointId);
            return ResponseEntity.ok("Favorito removido com sucesso");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Listar favoritos do usuário
    @GetMapping
    public ResponseEntity<List<FavoritoEntity>> listar() {
        var lista = service.listarFavoritosDoUsuario();
        return ResponseEntity.ok(lista);
    }

    // Verificar se é favorito
    @GetMapping("/{pointId}")
    public ResponseEntity<Boolean> isFavorito(@PathVariable UUID pointId) {
        var isFavorito = service.isFavorito(pointId);
        return ResponseEntity.ok(isFavorito);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception ex) {
        String message = ex.getMessage().replace("\r\n", "");
        return ResponseEntity.badRequest().body(message);
    }
}