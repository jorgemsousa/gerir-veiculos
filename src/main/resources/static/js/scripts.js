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
async function buscarTodosVeiculos() {
    try {
        const response = await fetch('/api/veiculos');
        if (!response.ok) {
            throw new Error("Erro ao buscar veículos.");
        }
        const veiculos = await response.json();
        const tabela = document.getElementById('tabela-veiculos');
        tabela.innerHTML = '';

        if (veiculos.length === 0) {
            tabela.innerHTML = '<tr><td colspan="5" class="text-center p-2">Nenhum veículo encontrado.</td></tr>';
            return;
        }

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
            tabela.appendChild(row);
        });
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
        // Ajuste para usar os nomes corretos dos campos
        detalhesAdicionais = `
            <p><strong>Tipo de Combustível:</strong> ${veiculo.tipoCombustivel || 'Não informado'}</p>
            <p><strong>Quantidade de Portas:</strong> ${veiculo.quantidadePortas || 'Não informado'}</p>
        `;
    }
    else if (veiculo.tipo.toLowerCase() === "moto") {
        // Cilindrada já está correto
        detalhesAdicionais = `
            <p><strong>Cilindrada:</strong> ${veiculo.cilindrada || 'Não informado'}</p>
        `;
    }

    detalhesDiv.innerHTML = `
        <p><strong>ID:</strong> ${veiculo.id}</p>
        <p><strong>Modelo:</strong> ${veiculo.modelo}</p>
        <p><strong>Fabricante:</strong> ${veiculo.fabricante}</p>
        <p><strong>Ano:</strong> ${veiculo.ano}</p>
        <p><strong>Preço:</strong> ${veiculo.preco}</p>
        <p><strong>Cor:</strong> ${veiculo.cor}</p>
        <p><strong>Tipo:</strong> ${veiculo.tipo}</p>
        ${detalhesAdicionais}
    `;

    modalDetalhes.classList.remove('hidden');
}



function fecharModalDetalhes() {
    document.getElementById('modal-detalhes').classList.add('hidden');
}

async function editarVeiculo(id) {
    try {
        const response = await fetch(`/api/veiculos/${id}`);
        if (!response.ok) throw new Error("Veículo não encontrado.");

        const veiculo = await response.json();


        document.getElementById('editar-id').value = veiculo.id;
        document.getElementById('editar-modelo').value = veiculo.modelo;
        document.getElementById('editar-fabricante').value = veiculo.fabricante;
        document.getElementById('editar-ano').value = veiculo.ano;
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

        document.getElementById('modal-editar').classList.remove('hidden');
    } catch (error) {
        alert(error.message);
    }
}


function fecharModal() {
    document.getElementById('modal-editar').classList.add('hidden');
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

        alert(responseData.message || "Veículo atualizado com sucesso!");
        fecharModal();
        buscarTodosVeiculos();
    } catch (error) {
        alert(error.message);
    }
}

async function deletarVeiculo(id) {
    if (!confirm("Tem certeza que deseja excluir este veículo?")) return;

    try {
        const response = await fetch(`/api/veiculos/${id}`, { method: "DELETE" });
        if (!response.ok) throw new Error("Erro ao deletar veículo.");

        buscarTodosVeiculos();
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
    const tipo = document.getElementById("tipo").value;
    const camposDinamicos = document.getElementById("campos-dinamicos");

    camposDinamicos.innerHTML = ""; // Limpa os campos anteriores

    if (tipo === "CARRO") {
        camposDinamicos.innerHTML = `
            <label class="block mt-2">Quantidade de Portas</label>
            <input type="number" id="quantidadePortas" class="border p-2 w-full" />

            <label class="block mt-2">Tipo de Combustível</label>
            <input type="text" id="tipoCombustivel" class="border p-2 w-full" />
        `;
    } else if (tipo === "MOTO") {
        camposDinamicos.innerHTML = `
            <label class="block mt-2">Cilindrada</label>
            <input type="number" id="cilindrada" class="border p-2 w-full" />
        `;
    }
}

async function adicionarVeiculo() {
    const tipo = document.getElementById("tipo").value;
    const modelo = document.getElementById("modelo").value;
    const fabricante = document.getElementById("fabricante").value;
    const ano = parseInt(document.getElementById("ano").value);
    const preco = parseFloat(document.getElementById("preco").value);
    const cor = document.getElementById("cor").value;

    let veiculo = {
        modelo,
        fabricante,
        ano,
        preco,
        cor,
        tipo
    };

    if (tipo === "CARRO") {
        veiculo.quantidadePortas = parseInt(document.getElementById("quantidadePortas").value);
        veiculo.tipoCombustivel = document.getElementById("tipoCombustivel").value;
    } else if (tipo === "MOTO") {
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
        fecharModal();
        buscarPorFiltro(); // Atualiza a tabela
    } catch (error) {
        alert(error.message);
    }
}

function abrirModal() {
    document.getElementById("modal-veiculo").classList.remove("hidden");
}

function fecharModal() {
    document.getElementById("modal-veiculo").classList.add("hidden");
}

