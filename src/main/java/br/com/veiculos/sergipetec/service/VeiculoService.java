package br.com.veiculos.sergipetec.service;

import br.com.veiculos.sergipetec.model.Veiculo;
import br.com.veiculos.sergipetec.repository.VeiculoRepository;

import java.util.List;

public class VeiculoService {
    private final VeiculoRepository veiculoRepository = new VeiculoRepository();

    public List<Veiculo> buscarTodosVeiculos() {
        return veiculoRepository.getAllVeiculos();
    }
}
