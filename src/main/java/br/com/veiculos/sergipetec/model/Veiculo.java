package br.com.veiculos.sergipetec.model;

public class Veiculo {
    private int id;
    private String modelo;
    private String fabricante;
    private int ano;
    private double preco;
    private String cor;
    private Tipo tipo;

    private Integer quantidadePortas;
    private Combustivel tipoCombustivel;

    private Integer cilindrada;

    public Veiculo(int id, String modelo, String fabricante, int ano, double preco, String cor, String tipo) {
        this.id = id;
        this.modelo = modelo;
        this.fabricante = fabricante;
        this.ano = ano;
        this.preco = preco;
        this.cor = cor;
        this.tipo = Tipo.valueOf(tipo);
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public Integer getQuantidadePortas() { return quantidadePortas; }
    public void setQuantidadePortas(Integer quantidadePortas) { this.quantidadePortas = quantidadePortas; }

    public Combustivel getTipoCombustivel() { return tipoCombustivel; }
    public void setTipoCombustivel(Combustivel tipoCombustivel) { this.tipoCombustivel = tipoCombustivel; }

    public Integer getCilindrada() { return cilindrada; }
    public void setCilindrada(Integer cilindrada) { this.cilindrada = cilindrada; }
}
