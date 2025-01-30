package br.com.veiculos.sergipetec.repository;


import br.com.veiculos.sergipetec.config.DatabaseConfig;
import br.com.veiculos.sergipetec.model.Combustivel;
import br.com.veiculos.sergipetec.model.Veiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class VeiculoRepository {

    // metodos para listar todos
    public List<Veiculo> getAllVeiculos() {
        List<Veiculo> veiculos = new ArrayList<>();
        String sql = "SELECT id, modelo, fabricante, ano, preco, cor, tipo FROM veiculos";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Veiculo veiculo = new Veiculo(
                        rs.getInt("id"),
                        rs.getString("modelo"),
                        rs.getString("fabricante"),
                        rs.getInt("ano"),
                        rs.getDouble("preco"),
                        rs.getString("cor"),
                        rs.getString("tipo").toUpperCase()
                );
                if ("carro".equalsIgnoreCase(veiculo.getTipo().toString())) {
                    addCarroDetails(conn, veiculo);
                } else if ("moto".equalsIgnoreCase(veiculo.getTipo().toString())) {
                    addMotoDetails(conn, veiculo);
                }

                veiculos.add(veiculo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return veiculos;
    }

    // metodos para adicionar veiculo
    public boolean addVeiculo(Veiculo veiculo) {
        String sqlVeiculo = "INSERT INTO veiculos (modelo, fabricante, ano, preco, cor, tipo) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmtVeiculo = conn.prepareStatement(sqlVeiculo)) {

            // Inserindo na tabela veiculos
            stmtVeiculo.setString(1, veiculo.getModelo());
            stmtVeiculo.setString(2, veiculo.getFabricante());
            stmtVeiculo.setInt(3, veiculo.getAno());
            stmtVeiculo.setDouble(4, veiculo.getPreco());
            stmtVeiculo.setString(5, veiculo.getCor());
            stmtVeiculo.setString(6, veiculo.getTipo().toString().toLowerCase());

            try (ResultSet rs = stmtVeiculo.executeQuery()) {
                if (rs.next()) {
                    int idVeiculo = rs.getInt("id");

                    if ("carro".equalsIgnoreCase(veiculo.getTipo().toString())) {
                        return addCarro(idVeiculo, veiculo);
                    } else if ("moto".equalsIgnoreCase(veiculo.getTipo().toString())) {
                        return addMoto(idVeiculo, veiculo);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // metodo para buscar por id
    public Veiculo getVeiculoById(int id) {
        String sql = "SELECT * FROM veiculos WHERE id = ?";
        Veiculo veiculo = null;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    veiculo = new Veiculo(
                            rs.getInt("id"),
                            rs.getString("modelo"),
                            rs.getString("fabricante"),
                            rs.getInt("ano"),
                            rs.getDouble("preco"),
                            rs.getString("cor"),
                            rs.getString("tipo").toUpperCase()
                    );

                    if ("carro".equalsIgnoreCase(veiculo.getTipo().toString())) {
                        addCarroDetails(conn, veiculo);
                    } else if ("moto".equalsIgnoreCase(veiculo.getTipo().toString())) {
                        addMotoDetails(conn, veiculo);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return veiculo;
    }

    //buscar por filtros
    public List<Veiculo> getVeiculosComFiltro(String tipo, String cor, String modelo, Integer ano) {
        List<Veiculo> veiculos = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM veiculos WHERE 1=1");

        if (tipo != null && !tipo.isEmpty()) {
            sql.append(" AND tipo = ?");
        }
        if (cor != null && !cor.isEmpty()) {
            sql.append(" AND cor = ?");
        }
        if (modelo != null && !modelo.isEmpty()) {
            sql.append(" AND modelo ILIKE ?");
        }
        if (ano != null) {
            sql.append(" AND ano = ?");
        }

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int index = 1;
            if (tipo != null && !tipo.isEmpty()) {
                stmt.setString(index++, tipo);
            }
            if (cor != null && !cor.isEmpty()) {
                stmt.setString(index++, cor);
            }
            if (modelo != null && !modelo.isEmpty()) {
                stmt.setString(index++, "%" + modelo + "%");
            }
            if (ano != null) {
                stmt.setInt(index++, ano);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Veiculo veiculo = new Veiculo(
                            rs.getInt("id"),
                            rs.getString("modelo"),
                            rs.getString("fabricante"),
                            rs.getInt("ano"),
                            rs.getDouble("preco"),
                            rs.getString("cor"),
                            rs.getString("tipo").toUpperCase()
                    );

                    // Adicionar detalhes específicos
                    if ("carro".equalsIgnoreCase(veiculo.getTipo().toString())) {
                        addCarroDetails(conn, veiculo);
                    } else if ("moto".equalsIgnoreCase(veiculo.getTipo().toString())) {
                        addMotoDetails(conn, veiculo);
                    }

                    veiculos.add(veiculo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return veiculos;
    }


    private void addCarroDetails(Connection conn, Veiculo veiculo) {
        String sql = "SELECT quantidade_portas, tipo_combustivel FROM carros WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, veiculo.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    veiculo.setQuantidadePortas(rs.getInt("quantidade_portas"));
                    veiculo.setTipoCombustivel(Combustivel.valueOf(rs.getString("tipo_combustivel").toUpperCase()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addMotoDetails(Connection conn, Veiculo veiculo) {
        String sql = "SELECT cilindrada FROM motos WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, veiculo.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    veiculo.setCilindrada(rs.getInt("cilindrada"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean addCarro(int id, Veiculo veiculo) {
        String sqlCarro = "INSERT INTO carros (id, quantidade_portas, tipo_combustivel) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmtCarro = conn.prepareStatement(sqlCarro)) {

            stmtCarro.setInt(1, id);
            stmtCarro.setInt(2, veiculo.getQuantidadePortas());
            stmtCarro.setString(3, veiculo.getTipoCombustivel().toString().toLowerCase());

            return stmtCarro.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean addMoto(int id, Veiculo veiculo) {
        String sqlMoto = "INSERT INTO motos (id, cilindrada) VALUES (?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmtMoto = conn.prepareStatement(sqlMoto)) {

            stmtMoto.setInt(1, id);
            stmtMoto.setInt(2, veiculo.getCilindrada());

            return stmtMoto.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}

