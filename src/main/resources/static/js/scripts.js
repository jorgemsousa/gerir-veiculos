async function buscarVeiculoPorId() {
    const id = document.getElementById('veiculo-id').value;
    if (!id) {
        alert("Por favor, insira um ID válido.");
        return;
    }

    try {
        const response = await fetch(`/api/veiculos/${id}`);
        if (!response.ok) {
            throw new Error("Veículo não encontrado.");
        }
        const veiculo = await response.json();

        const resultado = document.getElementById('resultado-veiculo');
        resultado.innerHTML = `
            <p class="p-2 border rounded bg-gray-100">
                <strong>Modelo:</strong> ${veiculo.modelo} <br>
                <strong>Fabricante:</strong> ${veiculo.fabricante} <br>
                <strong>Ano:</strong> ${veiculo.ano} <br>
                <strong>Preço:</strong> R$ ${veiculo.preco} <br>
                <strong>Cor:</strong> ${veiculo.cor} <br>
                <strong>Tipo:</strong> ${veiculo.tipo} <br>
                ${veiculo.tipo === "carro"
                    ? `<strong>Portas:</strong> ${veiculo.quantidadePortas} <br>
                       <strong>Combustível:</strong> ${veiculo.tipoCombustivel}`
                    : `<strong>Cilindrada:</strong> ${veiculo.cilindrada}`
                }
            </p>`;
    } catch (error) {
        alert(error.message);
    }
}

async function verDetalhes(id) {
    const response = await fetch(`/api/veiculos/${id}`);
    const veiculo = await response.json();

    const modalDetalhes = document.getElementById('modal-detalhes');
    const detalhesDiv = document.getElementById('detalhes-veiculo');

    let detalhesAdicionais = '';

    if (veiculo.tipo.toLowerCase() === "carro") {
        detalhesAdicionais = `
            <p><strong>Tipo de Combustível:</strong> ${veiculo.tipoCombustivel || 'Não informado'}</p>
            <p><strong>Quantidade de Portas:</strong> ${veiculo.quantidadePortas || 'Não informado'}</p>
        `;
    }
    else if (veiculo.tipo.toLowerCase() === "moto") {
        detalhesAdicionais = `
            <p><strong>Cilindrada:</strong> ${veiculo.cilindrada || 'Não informado'}</p>
        `;
    }

    detalhesDiv.innerHTML = `
        <p><strong>ID:</strong> ${veiculo.id}</p>
        <p><strong>Modelo:</strong> ${veiculo.modelo}</p>
        <p><strong>Fabricante:</strong> ${veiculo.fabricante}</p>
        <p><strong>Ano:</strong> ${veiculo.ano}</p>
        <p><strong>Preço:</strong> ${formatarPreco(veiculo.preco)}</p>
        <p><strong>Cor:</strong> ${veiculo.cor}</p>
        <p><strong>Tipo:</strong> ${veiculo.tipo}</p>
        ${detalhesAdicionais}
    `;

    modalDetalhes.classList.add('active');
}

function fecharModalDetalhes() {
    const modalDetalhes = document.getElementById('modal-detalhes');
    modalDetalhes.classList.remove('active');
}

async function editarVeiculo(id) {
    try {

        const response = await fetch(`/api/veiculos/${id}`);
        if (!response.ok) throw new Error("Veículo não encontrado.");

        const veiculo = await response.json();


        document.getElementById('editar-id').value = veiculo.id;
        document.getElementById('editar-modelo').value = veiculo.modelo;
        document.getElementById('editar-fabricante').value = veiculo.fabricante;
        document.getElementById('editar-ano').value = formatarPreco(veiculo.ano);
        document.getElementById('editar-preco').value = veiculo.preco;
        document.getElementById('editar-cor').value = veiculo.cor;
        document.getElementById('editar-tipo').value = veiculo.tipo;

        if (veiculo.tipo.toLowerCase() === "carro") {
            document.getElementById('carro-campos').classList.remove('hidden');
            document.getElementById('moto-campos').classList.add('hidden');
            document.getElementById('editar-quantidadePortas').value = veiculo.quantidadePortas || "";
            document.getElementById('editar-tipoCombustivel').value = veiculo.tipoCombustivel || "";
        } else if (veiculo.tipo.toLowerCase() === "moto") {
            document.getElementById('moto-campos').classList.remove('hidden');
            document.getElementById('carro-campos').classList.add('hidden');
            document.getElementById('editar-cilindrada').value = veiculo.cilindrada || "";
        }

        const modal = document.getElementById('modal-editar');
        modal.classList.add('active');

    } catch (error) {
        alert(error.message);
    }
}

async function salvarEdicao() {
    const id = document.getElementById('editar-id').value;
    const modelo = document.getElementById('editar-modelo').value;
    const fabricante = document.getElementById('editar-fabricante').value;
    const ano = document.getElementById('editar-ano').value;
    const preco = document.getElementById('editar-preco').value;
    const cor = document.getElementById('editar-cor').value;
    const tipo = document.getElementById('editar-tipo').value;

    let dadosAtualizados = { modelo, fabricante, ano, preco, cor, tipo };

    if (tipo.toLowerCase() === "carro") {
        dadosAtualizados.quantidadePortas = document.getElementById('editar-quantidadePortas').value;
        dadosAtualizados.tipoCombustivel = document.getElementById('editar-tipoCombustivel').value;
    } else if (tipo.toLowerCase() === "moto") {
        dadosAtualizados.cilindrada = document.getElementById('editar-cilindrada').value;
    }

    try {
        const response = await fetch(`/api/veiculos/${id}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(dadosAtualizados)
        });
        if (!response.ok) throw new Error("Erro ao atualizar veículo.");

        alert("Veículo atualizado com sucesso!");
        fecharModalEditar();
        listaVeiculos
    } catch (error) {
        alert(error.message);
    }
}

async function deletarVeiculo(id) {
    if (!confirm("Tem certeza que deseja excluir este veículo?")) return;

    try {
        const response = await fetch(`/api/veiculos/${id}`, { method: "DELETE" });
        if (!response.ok) throw new Error("Erro ao deletar veículo.");

        listaVeiculos();
    } catch (error) {
        alert(error.message);
    }
}

async function buscarPorFiltro() {

    const filtroSelect = document.getElementById('filtroTipo').value;
    const valorInput = document.getElementById('filtroValor').value;

    let url = '/api/veiculos/filtro';


    if (filtroSelect && valorInput) {
        url += `?${filtroSelect}=${valorInput}`;
    }


    const response = await fetch(url);
    const veiculos = await response.json();


    const tbody = document.getElementById('tabela-veiculos');


    tbody.innerHTML = '';

    veiculos.forEach(veiculo => {
        const row = document.createElement('tr');

        row.innerHTML = `
            <td class="border border-gray-300 px-4 py-2 text-center">${veiculo.id}</td>
            <td class="border border-gray-300 px-4 py-2 text-center">${veiculo.modelo}</td>
            <td class="border border-gray-300 px-4 py-2 text-center">${veiculo.fabricante}</td>
            <td class="border border-gray-300 px-4 py-2 text-center">${veiculo.ano}</td>
            <td class="border border-gray-300 px-4 py-2 text-center">
                <button onclick="verDetalhes(${veiculo.id})"
                        class="bg-green-500 text-white px-2 py-1 rounded hover:bg-green-700">
                    Detalhes
                </button>
                <button onclick="editarVeiculo(${veiculo.id})"
                        class="bg-yellow-500 text-white px-2 py-1 rounded hover:bg-yellow-700">
                    Editar
                </button>
                <button onclick="deletarVeiculo(${veiculo.id})"
                        class="bg-red-500 text-white px-2 py-1 rounded hover:bg-red-700">
                    Deletar
                </button>
            </td>
        `;
        tbody.appendChild(row);
        document.getElementById('filtroTipo').value = 'todos';
        document.getElementById('filtroValor').value = '';
    });
}

function alterarCampos() {
    const tipo = document.getElementById("tipo-veiculo").value;
    const camposDinamicos = document.getElementById("campos-dinamicos");

    camposDinamicos.innerHTML = "";

    if (tipo.toUpperCase() === "CARRO") {
        let quantidadePortasField = document.createElement("div");
        quantidadePortasField.innerHTML = `
            <label class="block mt-2">Quantidade de Portas</label>
            <input type="number" id="quantidadePortas" class="border p-2 w-full" />
        `;
        camposDinamicos.appendChild(quantidadePortasField);

        let tipoCombustivelField = document.createElement("div");
        tipoCombustivelField.innerHTML = `
            <label class="block mt-2">Tipo de Combustível</label>
            <select id="tipoCombustivel" class="border p-2 w-full">
                <option value="GASOLINA">Gasolina</option>
                <option value="ETANOL">Etanol</option>
                <option value="DIESEL">Diesel</option>
                <option value="FLEX">Flex</option>
            </select>
        `;
        camposDinamicos.appendChild(tipoCombustivelField);
    } else if (tipo.toUpperCase() === "MOTO") {
        let cilindradaField = document.createElement("div");
        cilindradaField.innerHTML = `
            <label class="block mt-2">Cilindrada</label>
            <input type="number" id="cilindrada" class="border p-2 w-full" />
        `;
        camposDinamicos.appendChild(cilindradaField);
    }
}

async function adicionarVeiculo() {
    const tipo = document.getElementById("tipo-veiculo").value;
    const modelo = document.getElementById("modeloVeiculo").value;
    const fabricante = document.getElementById("fabricante").value;
    const ano = parseInt(document.getElementById("anoVeiculo").value);
    const preco = parseFloat(document.getElementById("preco").value);
    const cor = document.getElementById("corVeiculo").value;

    let veiculo = {
        modelo,
        fabricante,
        ano,
        preco,
        cor,
        tipo
    };

    if (tipo.toUpperCase() === "CARRO") {
        veiculo.quantidadePortas = parseInt(document.getElementById("quantidadePortas").value);
        veiculo.tipoCombustivel = document.getElementById("tipoCombustivel").value.toUpperCase();
    }
    else if (tipo.toUpperCase === "MOTO") {
        veiculo.cilindrada = parseInt(document.getElementById("cilindrada").value);
    }

    try {
        const response = await fetch('/api/veiculos', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(veiculo)
        });

        if (!response.ok) {
            throw new Error('Erro ao adicionar veículo');
        }

        alert('Veículo adicionado com sucesso!');
        fecharModalVeiculo();
        listaVeiculos();
    } catch (error) {
        alert(error.message);
    }
}

function abrirModalVeiculo() {
    const modal = document.getElementById('modal-veiculo');
    modal.classList.add('active');
}

function fecharModalVeiculo() {
    const modal = document.getElementById('modal-veiculo');
        modal.classList.remove('active');
}

function aplicarFiltro() {
   const tipo = document.getElementById("tipo").value;
   const cor = document.getElementById("cor").value;
   const ano = document.getElementById("ano").value;
   const modelo = document.getElementById("modelo").value;

   let params = new URLSearchParams();

   if (tipo) params.append("tipo", tipo.toLowerCase());
   if (cor) params.append("cor", cor);
   if (ano) params.append("ano", ano);
   if (modelo) params.append("modelo", modelo);

   let url = "/api/veiculos/filtro";
   if (params.toString()) {
       url += "?" + params.toString();
   }
   console.log(url)
    // Faz a requisição com os filtros aplicados
    fetch(url)
            .then(response => {
                if (!response.ok) {
                    throw new Error("Erro na resposta do servidor");
                }
                return response.json();
            })
            .then(data => {
                const tabela = document.getElementById("tabela-veiculos");
                tabela.innerHTML = ""; // Limpa a tabela antes de adicionar os dados filtrados

                if (data.length === 0) {
                    tabela.innerHTML = "<tr><td colspan='5'>Nenhum veículo encontrado</td></tr>";
                    return;
                }

                data.forEach(veiculo => {
                    const row = document.createElement("tr");
                    row.innerHTML = `
                        <td>${veiculo.id}</td>
                        <td>${veiculo.modelo}</td>
                        <td>${veiculo.fabricante}</td>
                        <td>${veiculo.ano}</td>
                        <td class="actions">
                            <i class="fa-solid fa-eye" title="Visualizar" onclick="verDetalhes(${veiculo.id})"></i>
                            <i class="fa-solid fa-pen" title="Editar" onclick="editarVeiculo(${veiculo.id})"></i>
                            <i class="fa-solid fa-trash" title="Deletar" onclick="deletarVeiculo(${veiculo.id})"></i>
                        </td>
                    `;
                    tabela.appendChild(row);
                });
            })
            .catch(error => {
                alert("Nenhum dado encontrado!!!");
                console.error("Erro ao carregar veículos:", error);
            });
}

function listaVeiculos() {
    fetch("/api/veiculos")
        .then(response => response.json())
        .then(data => {
            const tabela = document.getElementById("tabela-veiculos");
            tabela.innerHTML = "";
            data.forEach(veiculo => {
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${veiculo.id}</td>
                    <td>${veiculo.modelo}</td>
                    <td>${veiculo.fabricante}</td>
                    <td>${veiculo.ano}</td>
                    <td class="actions">
                        <i class="fa-solid fa-eye" title="Visualizar" onclick="verDetalhes(${veiculo.id})"></i>
                        <i class="fa-solid fa-pen" title="Editar" onclick="editarVeiculo(${veiculo.id})"></i>
                        <i class="fa-solid fa-trash" title="Deletar" onclick="deletarVeiculo(${veiculo.id})"></i>
                    </td>
                `;
                tabela.appendChild(row);
            });
        })
        .catch(error => console.error("Erro ao carregar veículos:", error));
}

function fecharModalEditar() {
    const modal = document.getElementById('modal-editar');
    modal.classList.remove('active');
}

function formatarPreco(preco) {
  if (!preco) return 'Preço não informado';

  const numero = parseFloat(preco.toString().replace(/[^0-9,.-]+/g, '').replace(',', '.'));
  return numero.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}