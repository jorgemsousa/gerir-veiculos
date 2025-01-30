package br.com.veiculos.sergipetec.controller;

import br.com.veiculos.sergipetec.model.Veiculo;
import br.com.veiculos.sergipetec.service.VeiculoService;
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
}
