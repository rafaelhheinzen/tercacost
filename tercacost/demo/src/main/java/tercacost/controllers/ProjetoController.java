package tercacost.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tercacost.entities.Projeto;
import tercacost.services.ProjetoService;

@RestController
@RequestMapping("/projetos")
@CrossOrigin(origins = "*")
public class ProjetoController {

    @Autowired
    private ProjetoService service;

    @GetMapping
    public ResponseEntity<List<Projeto>> getProjetos() {
        return ResponseEntity.status(HttpStatus.OK).body(service.consultar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUmProjeto(@PathVariable long id) {
        try {
            Projeto projeto = service.getUm(id);
            return ResponseEntity.status(HttpStatus.OK).body(projeto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable long id) {
        service.excluir(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody Projeto projeto) {
        try {
            Projeto novoProjeto = service.salvar(projeto);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoProjeto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> alterar(@PathVariable long id, @RequestBody Projeto projeto) {
        try {
            Projeto projetoAtualizado = service.alterar(id, projeto);
            return ResponseEntity.status(HttpStatus.OK).body(projetoAtualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}