async function buscarVeiculos() {
    const response = await fetch('/api/veiculos');
    const veiculos = await response.json();
    const lista = document.getElementById('lista-veiculos');
    lista.innerHTML = '';
    veiculos.forEach(veiculo => {
        const li = document.createElement('li');
        li.textContent = `${veiculo.modelo} - ${veiculo.fabricante} (${veiculo.ano})`;
        lista.appendChild(li);
    });
}
