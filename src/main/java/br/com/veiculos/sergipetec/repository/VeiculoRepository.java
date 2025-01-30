package br.com.veiculos.sergipetec.repository;


import br.com.veiculos.sergipetec.config.DatabaseConfig;
import br.com.veiculos.sergipetec.model.Veiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class VeiculoRepository {

    public List<Veiculo> getAllVeiculos() {
        List<Veiculo> veiculos = new ArrayList<>();
        String sql = "SELECT id, modelo, fabricante, ano, preco, cor, tipo FROM veiculos";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                veiculos.add(new Veiculo(
                        rs.getInt("id"),
                        rs.getString("modelo"),
                        rs.getString("fabricante"),
                        rs.getInt("ano"),
                        rs.getDouble("preco"),
                        rs.getString("cor"),
                        rs.getString("tipo")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return veiculos;
    }
}

