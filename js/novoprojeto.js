/////////////////////////
//VARIAVEIS NECESSARIAS//
////////////////////////

const userJson = localStorage.getItem("user");
let relatorioTecnicoGlobal = "";

if (!userJson) {
    window.location.href = "login.html";
}

const user = JSON.parse(userJson);
const API_URL = "http://localhost:8080/projetos";

const urlParams = new URLSearchParams(window.location.search);
const projectId = urlParams.get("id");

// Load existing project data if editing
if (projectId) {
    // Captura as credenciais salvas no localStorage no momento do login
    const emailUsuario = localStorage.getItem("userEmail");
    const senhaUsuario = localStorage.getItem("userPassword");

    if (!emailUsuario || !senhaUsuario) {
        alert("Sessão expirada. Por favor, faça login novamente.");
        window.location.href = "login.html";
    } else {
        // Codifica no padrão Basic Auth exigido pelo Spring Security
        const credenciaisCodificadas = btoa(`${emailUsuario}:${senhaUsuario}`);

        fetch(`${API_URL}/${projectId}/usuario/${user.id}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                // 🌟 INJEÇÃO CRÍTICA: Autoriza o novoprojeto.js a descriptografar o memorial do MySQL
                "Authorization": `Basic ${credenciaisCodificadas}`
            }
        })
        .then(res => {
            if (!res.ok) throw new Error("Acesso não autorizado ou projeto inexistente no MySQL.");
            return res.json();
        })
        .then(data => {
            // Preenche as informações básicas textuais
            document.getElementById("descricao").value = data.descricao || "";
            if (document.getElementById("cb")) document.getElementById("cb").value = data.cb ?? "1.00";

            // Preenche dinamicamente as cargas estruturais vindas do MySQL
            document.getElementById("cargaPermanente").value = data.cargaPermanente ?? "15.0";
            document.getElementById("sobrecarga").value = data.sobrecarga ?? "25.0";
            document.getElementById("vento").value = data.vento ?? "45.0";
            document.getElementById("espacamento").value = data.espacamento ?? "1.50";
            document.getElementById("vao").value = data.vao ?? "6.00";
            document.getElementById("lb").value = data.lb ?? "1500.0";

            // Preenche os parâmetros geométricos
            if (data.tipoPerfil) {
                document.getElementById("tipoPerfil").value = data.tipoPerfil;
                document.getElementById("alturaAlma").value = data.alturaAlma ?? "300.0";
                document.getElementById("larguraAba").value = data.larguraAba ?? "85.0";
                document.getElementById("larguraEnrijecedor").value = data.larguraEnrijecedor ?? "25.0";
                document.getElementById("espessuraChapa").value = data.espessuraChapa ?? "4.75";
                document.getElementById("fy").value = data.fy ?? "300.0";
            }

            // Altera visualmente os títulos da tela para o modo Edição
            const sidebarTitle = document.querySelector(".sidebar h2");
            if (sidebarTitle) sidebarTitle.innerText = "Editar Projeto";
            
            const calcBtn = document.getElementById("calculate");
            if (calcBtn) calcBtn.innerHTML = `<i class="fa-solid fa-floppy-disk"></i> Atualizar Projeto`;
            
            if (typeof gerenciarCamposTela === "function") gerenciarCamposTela(); 
        })
        .catch(err => {
            console.error(err);
            alert(err.message);
            window.location.href = "index.html";
        });
    }
}


// FUNÇÃO AUXILIAR: Monta a string descritiva do perfil combinando os campos geométricos
function obterNomePerfilGerado() {
    const tipo = document.getElementById('tipoPerfil').value;
    const h = document.getElementById('alturaAlma').value;
    const b = document.getElementById('larguraAba').value;
    const d = document.getElementById('larguraEnrijecedor').value;
    const t = document.getElementById('espessuraChapa').value;

    if (tipo === 'U') {
        return `U${h}x${b}x${t}`;
    }
    return `${tipo}${h}x${b}x${d}x${t}`;
}





// Save or Update handler
document.getElementById("calculate").addEventListener("click", async (e) => {
    e.preventDefault();

    const descricao = document.getElementById("descricao").value.trim();
    const resultElement = document.getElementById("result");

    if (!descricao) {
        resultElement.style.color = "red";
        resultElement.innerText = "Por favor, informe a descrição do projeto.";
        return;
    }

    // Une as identificações do projeto com todas as propriedades da obra e geometrias
    const payloadCompleto = {
        id: projectId ? parseInt(projectId) : null, // Passa o ID se for edição, permitindo ao Hibernate atualizar em vez de criar outro
        descricao: descricao,
        tipoPerfil: document.getElementById('tipoPerfil').value,
        alturaAlma: parseFloat(document.getElementById('alturaAlma').value || 0),
        larguraAba: parseFloat(document.getElementById('larguraAba').value || 0),
        larguraEnrijecedor: document.getElementById('tipoPerfil').value === 'U' ? 0 : parseFloat(document.getElementById('larguraEnrijecedor').value || 0),
        espessuraChapa: parseFloat(document.getElementById('espessuraChapa').value || 0),
        fy: parseFloat(document.getElementById('fy').value || 300),
        fu: 400.0,
        E: 200000.0,
        G: 77000.0,
        cb: parseFloat(document.getElementById('cb').value || 1.0),
        cargaPermanente: parseFloat(document.getElementById('cargaPermanente').value || 0),
        sobrecarga: parseFloat(document.getElementById('sobrecarga').value || 0),
        vento: parseFloat(document.getElementById('vento').value || 0),
        espacamento: parseFloat(document.getElementById('espacamento').value || 0),
        vao: parseFloat(document.getElementById('vao').value || 0),
        lb: parseFloat(document.getElementById('lb').value || 0),
        usuarioId: user.id
    };

    // A rota agora bate SEMPRE no nosso endpoint inteligente de engenharia do MySQL
    const targetUrl = `${API_URL}/salvar-calculado`;

    resultElement.style.color = "#2b579a";
    resultElement.innerText = "Processando engenharia e atualizando dados no MySQL...";

    try {
        // Captura o usuário e a senha pura salvos no localStorage no momento do login
        const emailUsuario = localStorage.getItem("userEmail");
        const senhaUsuario = localStorage.getItem("userPassword");

        // Codifica no padrão Basic Auth que o Spring Security exige
        const credenciaisCodificadas = btoa(`${emailUsuario}:${senhaUsuario}`);

        const response = await fetch(targetUrl, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Basic ${credenciaisCodificadas}` 
            },
            body: JSON.stringify(payloadCompleto)
        });

        if (response.ok) {
            resultElement.style.color = "green";
            resultElement.innerText = projectId
                ? "Projeto e cálculos atualizados com sucesso!"
                : `Projeto "${descricao}" e cálculos salvos!`;

            setTimeout(() => {
                window.location.href = "index.html";
            }, 1200);
        } else {
            const errorText = await response.text();
            resultElement.style.color = "red";
            resultElement.innerText = `Erro retornado: ${errorText}`;
        }
    } catch (error) {
        console.error("Erro ao salvar:", error);
        resultElement.style.color = "red";
        resultElement.innerText = "Erro ao conectar com o servidor backend.";
    }
});





// Executa automaticamente ao carregar a página para garantir o estado inicial do enrijecedor
document.addEventListener("DOMContentLoaded", () => {
    gerenciarCamposTela();
});

// Mostra/Oculta o campo do enrijecedor caso o perfil selecionado seja o U (Simples)
function gerenciarCamposTela() {
    const tipo = document.getElementById('tipoPerfil').value;
    const grupoEnrijecedor = document.getElementById('grupoEnrijecedor');

    if (tipo === 'U') {
        grupoEnrijecedor.style.display = 'none';
    } else {
        grupoEnrijecedor.style.display = 'block';
    }
}










///////////////////
//FUNCAO CALCULAR//
///////////////////
async function calcularEVerificar() {
    const campoResultado = document.getElementById('result');

    // Captura o payload adaptado com as especificações físicas da obra
    const payload = {
        tipoPerfil: document.getElementById('tipoPerfil').value,
        alturaAlma: parseFloat(document.getElementById('alturaAlma').value || 0),
        larguraAba: parseFloat(document.getElementById('larguraAba').value || 0),
        larguraEnrijecedor: document.getElementById('tipoPerfil').value === 'U' ? 0 : parseFloat(document.getElementById('larguraEnrijecedor').value || 0),
        espessuraChapa: parseFloat(document.getElementById('espessuraChapa').value || 0),
        fy: parseFloat(document.getElementById('fy').value || 0),
        fu: parseFloat(document.getElementById('fu').value || 400.0),
        E: parseFloat(document.getElementById('E').value || 200000.0),
        G: parseFloat(document.getElementById('G').value || 77000.0),
        cb: parseFloat(document.getElementById('cb').value || 1.0),

        // Dados de cargas e espaçamento
        cargaPermanente: parseFloat(document.getElementById('cargaPermanente').value || 0),
        sobrecarga: parseFloat(document.getElementById('sobrecarga').value || 0),
        vento: parseFloat(document.getElementById('vento').value || 0),
        espacamento: parseFloat(document.getElementById('espacamento').value || 0),
        vao: parseFloat(document.getElementById('vao').value || 0),
        lb: parseFloat(document.getElementById('lb').value || 0)
    };

    campoResultado.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> Executando combinações e análises NBR...`;

    try {
        const respostaHttp = await fetch('http://localhost:8080/api/calculo/verificar-perfil', {
            method: 'POST',
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });

        if (!respostaHttp.ok) throw new Error("Erro no processamento da API.");

        const resultado = await respostaHttp.json();

        // Renderização dos dados numéricos detalhados (ELU e ELS)
        relatorioTecnicoGlobal = resultado.relatorioDiagnostico;

        // Renderiza apenas os dados numéricos limpos
        campoResultado.innerHTML = `
            <div style="margin-bottom: 15px; padding: 12px; border-radius: 4px; font-weight: bold; text-align: center; background: ${resultado.aprovadoGeral ? '#d4edda' : '#f8d7da'}; color: ${resultado.aprovadoGeral ? '#155724' : '#721c24'}">
                ${resultado.aprovadoGeral ? '✔ ESTRUTURA TOTALMENTE APROVADA' : '❌ ESTRUTURA REPROVADA'}
            </div>
            
            <div style="display: flex; flex-direction: column; gap: 8px; font-size: 14px; text-align: left; color: #333; background: #fff; padding: 10px; border-radius: 4px; border: 1px solid #ddd;">
                <h4 style="margin: 5px 0; color: #2b579a; border-bottom: 1px solid #eee; padding-bottom: 3px;">Esforços de Projeto (NBR 8681 Combinação)</h4>
                <p style="margin: 3px 0; display: flex; justify-content: space-between;"><span>Momento Atuante (Msd):</span> <strong>${resultado.msd.toFixed(2)} kN·m</strong></p>
                <p style="margin: 3px 0; display: flex; justify-content: space-between;"><span>Cortante Atuante (Vsd):</span> <strong>${resultado.vsd.toFixed(2)} kN</strong></p>
                
                <h4 style="margin: 10px 0 5px 0; color: #2b579a; border-bottom: 1px solid #eee; padding-bottom: 3px;">Verificações Mecânicas</h4>
                <p style="margin: 3px 0; display: flex; justify-content: space-between;">
                    <span>Momento Resistente (Mrd):</span> <strong>${resultado.mrd.toFixed(2)} kN·m</strong>
                    <span style="color: ${resultado.aprovadoMomento ? 'green' : 'red'}">${resultado.aprovadoMomento ? 'OK' : 'FALHOU'}</span>
                </p>
                <p style="margin: 3px 0; display: flex; justify-content: space-between;">
                    <span>Cortante Resistente (Vrd):</span> <strong>${resultado.vrd.toFixed(2)} kN</strong>
                    <span style="color: ${resultado.aprovadoCortante ? 'green' : 'red'}">${resultado.aprovadoCortante ? 'OK' : 'FALHOU'}</span>
                </p>
                <p style="margin: 3px 0; display: flex; justify-content: space-between;">
                    <span>Deformação / Flecha ELS:</span> <strong>${resultado.flechaReal.toFixed(1)} mm / ${resultado.flechaLimite.toFixed(1)} mm</strong>
                    <span style="color: ${resultado.flechaReal <= resultado.flechaLimite ? 'green' : 'red'}">${resultado.flechaReal <= resultado.flechaLimite ? 'OK' : 'FALHOU'}</span>
                </p>

                <h4 style="margin: 10px 0 5px 0; color: #2b579a; border-bottom: 1px solid #eee; padding-bottom: 3px;">Propriedades Físicas</h4>
                <p style="margin: 3px 0; display: flex; justify-content: space-between; color: #666;"><span>Área de Aço (Ag):</span> <span>${resultado.area.toFixed(1)} mm²</span></p>
                <p style="margin: 3px 0; display: flex; justify-content: space-between; color: #666;"><span>Peso Estimado:</span> <span>${resultado.pesoPorMetro.toFixed(2)} kg/m</span></p>
            </div>
        `;

        // Torna o botão de abrir o diagnóstico visível na tela
        document.getElementById('btnDiagnostico').style.display = 'block';

    } catch (erro) {
        console.error(erro);
        campoResultado.innerHTML = `<span style="color: #ef4444; font-weight: bold;">❌ Falha técnica: ${erro.message}</span>`;
    }

}


function abrirModalDiagnostico() {
    const modal = document.getElementById('modalDiagnostico');
    const textarea = document.getElementById('txtRelatorioModal');

    textarea.value = relatorioTecnicoGlobal; // Alimenta a caixa com o texto guardado
    modal.style.display = 'flex'; // Exibe o modal centralizado na tela
}

function fecharModalDiagnostico() {
    document.getElementById('modalDiagnostico').style.display = 'none';
}
