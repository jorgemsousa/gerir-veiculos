package br.com.veiculos.sergipetec.repository;


import br.com.veiculos.sergipetec.config.DatabaseConfig;
import br.com.veiculos.sergipetec.model.Combustivel;
import br.com.veiculos.sergipetec.model.Veiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        System.out.println(veiculo);
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
            System.err.println("Erro ao buscar veículos: " + e.getMessage());
            e.printStackTrace();
        }

        // Verifica se nenhum veículo foi encontrado e adiciona uma mensagem fictícia
        if (veiculos.isEmpty()) {
            System.out.println("Nenhum veículo encontrado para os filtros aplicados.");
            return new ArrayList<>(); // Retorna uma lista vazia
        }

        return veiculos;
    }


    //metodo de atualização
    public boolean atualizarVeiculo(Veiculo veiculo) {
        String sql = "UPDATE veiculos SET modelo = ?, fabricante = ?, ano = ?, preco = ?, cor = ?, tipo = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, veiculo.getModelo());
            stmt.setString(2, veiculo.getFabricante());
            stmt.setInt(3, veiculo.getAno());
            stmt.setDouble(4, veiculo.getPreco());
            stmt.setString(5, veiculo.getCor());
            stmt.setString(6, veiculo.getTipo().toString().toLowerCase());
            stmt.setInt(7, veiculo.getId());

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                if ("carro".equalsIgnoreCase(veiculo.getTipo().toString())) {
                    return atualizarCarro(conn, veiculo);
                } else if ("moto".equalsIgnoreCase(veiculo.getTipo().toString())) {
                    return atualizarMoto(conn, veiculo);
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao atualizar: " + e);
            e.printStackTrace();
        }
        return false;
    }

    //metodo para deltar
    public boolean deletarVeiculo(int id) {
        String selectTipoSql = "SELECT tipo FROM veiculos WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectTipoSql)) {

            selectStmt.setInt(1, id);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                String tipo = rs.getString("tipo");

                if ("carro".equalsIgnoreCase(tipo)) {
                    deletarCarro(conn, id);
                } else if ("moto".equalsIgnoreCase(tipo)) {
                    deletarMoto(conn, id);
                }

                String deleteVeiculoSql = "DELETE FROM veiculos WHERE id = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteVeiculoSql)) {
                    deleteStmt.setInt(1, id);
                    return deleteStmt.executeUpdate() > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private void deletarCarro(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM carros WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private void deletarMoto(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM motos WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private boolean atualizarCarro(Connection conn, Veiculo veiculo) throws SQLException {
        String sql = "UPDATE carros SET quantidade_portas = ?, tipo_combustivel = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, veiculo.getQuantidadePortas());
            stmt.setString(2, veiculo.getTipoCombustivel().toString().toLowerCase());
            stmt.setInt(3, veiculo.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    private boolean atualizarMoto(Connection conn, Veiculo veiculo) throws SQLException {
        String sql = "UPDATE motos SET cilindrada = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, veiculo.getCilindrada());
            stmt.setInt(2, veiculo.getId());
            return stmt.executeUpdate() > 0;
        }
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
        System.out.println(veiculo);
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

