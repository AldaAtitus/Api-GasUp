package br.edu.atitus.placa_pai.apisgasup.controllers;

import br.edu.atitus.placa_pai.apisgasup.dtos.PointDTO;
import br.edu.atitus.placa_pai.apisgasup.entities.PointEntity;
import br.edu.atitus.placa_pai.apisgasup.entities.User;
import br.edu.atitus.placa_pai.apisgasup.services.PointService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ws/point")
public class PointController {
    private final PointService service;

    public PointController(PointService service) {
        super();
        this.service = service;
    }

    // Para listar todos os pontos (qualquer usuário autenticado)
    @GetMapping("/todos")
    public ResponseEntity<List<PointEntity>> findAll() {
        var lista = service.findAll();
        return ResponseEntity.ok(lista);
    }

    // Para listar apenas os pontos do usuário
    @GetMapping
    public ResponseEntity<List<PointEntity>> findMyPoints() {
        User userAuth = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var lista = service.findByUser(userAuth);
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    public ResponseEntity<PointEntity> save(@RequestBody PointDTO dto) throws Exception {
        PointEntity point =  new PointEntity();
        BeanUtils.copyProperties(dto, point);
        service.save(point);
        return ResponseEntity.status(201).body(point);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) throws Exception {
        service.deleteById(id);
        return ResponseEntity.ok("Ponto Deletado com sucesso");
    }

    @PutMapping("/{id}")
    public ResponseEntity<PointEntity> update(@PathVariable UUID id, @RequestBody PointDTO dto) throws Exception {
        var point = service.update(id, dto);
        return ResponseEntity.ok(point);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> excpetionHandler(Exception ex){
        String message = ex.getMessage().replace("\r\n", "");
        return ResponseEntity.badRequest().body(message);
    }
}
