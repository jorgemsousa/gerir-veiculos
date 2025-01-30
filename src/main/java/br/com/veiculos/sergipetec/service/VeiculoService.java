package br.com.veiculos.sergipetec.service;

import br.com.veiculos.sergipetec.model.Veiculo;
import br.com.veiculos.sergipetec.repository.VeiculoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public class VeiculoService {
    private final VeiculoRepository veiculoRepository = new VeiculoRepository();

    public List<Veiculo> buscarTodosVeiculos() {
        return veiculoRepository.getAllVeiculos();
    }

    public ResponseEntity<Veiculo> getVeiculoById(int id) {
        Veiculo veiculo = veiculoRepository.getVeiculoById(id);
        if (veiculo == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(veiculo);
    }

    public ResponseEntity<String> adicionarVeiculo(Veiculo veiculo) {
        if ("carro".equalsIgnoreCase(veiculo.getTipo().toString()) && (veiculo.getQuantidadePortas() == null || veiculo.getTipoCombustivel() == null)) {
            return ResponseEntity.badRequest().body("Carros precisam de quantidadePortas e tipoCombustivel.");
        }

        if ("moto".equalsIgnoreCase(veiculo.getTipo().toString()) && veiculo.getCilindrada() == null) {
            return ResponseEntity.badRequest().body("Motos precisam de cilindrada.");
        }

        boolean created = veiculoRepository.addVeiculo(veiculo);
        return created ? ResponseEntity.ok("Veículo cadastrado com sucesso!") :
                ResponseEntity.badRequest().body("Erro ao cadastrar veículo.");

    }
}
