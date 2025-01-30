package br.com.veiculos.sergipetec.controller;

import br.com.veiculos.sergipetec.model.Veiculo;
import br.com.veiculos.sergipetec.service.VeiculoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController {

    private final VeiculoService veiculoService = new VeiculoService();

    @GetMapping
    public List<Veiculo> getAllVeiculos() {
        return veiculoService.buscarTodosVeiculos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Veiculo> getVeiculoById(@PathVariable int id) {
       return veiculoService.getVeiculoById(id);
    }

    @GetMapping("/filtro")
    public ResponseEntity<List<Veiculo>> getByFilter(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String cor,
            @RequestParam(required = false) String modelo,
            @RequestParam(required = false) Integer ano
    ) {
        return veiculoService.getVeiculosComFiltro(tipo, cor, modelo, ano);
    }

    @PostMapping
    public ResponseEntity<String> addVeiculo(@RequestBody Veiculo veiculo) {
        return veiculoService.adicionarVeiculo(veiculo);
    }
}
