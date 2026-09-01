// =====================================================================
// VARIÁVEIS GLOBAIS DE CONTROLE DO CARROSSEL E SISTEMA
// =====================================================================
const API_URL = "http://localhost:8080/projetos";
let projectsData = []; // Array global unificado que recebe os dados do MySQL
let currentPage = 0;
const cardsPerPage = 3;
let showAll = false;

// Elementos da árvore do DOM da página index.html
const projectGrid = document.getElementById("project-grid")
const prev = document.getElementById("prev-btn");
const next = document.getElementById("next-btn");
const toggle = document.getElementById("toggle-btn");

// Captura e validação inicial do usuário logado na sessão web
const userRaw = localStorage.getItem("user");
if (!userRaw) {
    window.location.href = "login.html";
}
const user = JSON.parse(userRaw);





// Adicione esta lógica dentro do bloco de carregamento inicial do seu js/script.js:
document.addEventListener("DOMContentLoaded", () => {
    const userRaw = localStorage.getItem("user");
    if (userRaw) {
        const usuarioLogado = JSON.parse(userRaw);
        
        // Substitui o texto estático "João Silva" pelo nome real cadastrado no MySQL
        const elementoNomeTopo = document.querySelector("#user-name"); // Ajuste o seletor CSS se necessário
        const elementoSaudacao = document.querySelector(".welcome-section h1, h1"); 
        
        // Alimenta dinamicamente as tags da tela com o nome vindo do banco
        if (elementoNomeTopo && usuarioLogado.nome) {
            elementoNomeTopo.innerText = usuarioLogado.nome;
        }
        if (elementoSaudacao && usuarioLogado.nome) {
            // Pega apenas o primeiro nome para a saudação amigável
            const primeiroNome = usuarioLogado.nome.split(" ")[0];
            elementoSaudacao.innerText = `Olá, ${primeiroNome}!`;
        }
    }
});







// =====================================================================
// FUNÇÃO 1: FAZ O FETCH DOS DADOS NO BACK-END (MYSQL + SPRING SECURITY)
// =====================================================================
async function carregarProjetosDoUsuario() {
    const emailUsuario = localStorage.getItem("userEmail");
    const senhaUsuario = localStorage.getItem("userPassword");

    if (!emailUsuario || !senhaUsuario) {
        window.location.href = "login.html";
        return;
    }

    // Gera o passaporte criptografado em texto Basic Auth para passar pelo SecurityConfig
    const credenciaisCodificadas = btoa(`${emailUsuario}:${senhaUsuario}`);

    try {
        if (projectGrid) {
            projectGrid.innerHTML = `<p><i class="fa-solid fa-spinner fa-spin"></i> Lendo tabelas estruturais no MySQL...</p>`;
        }

        const response = await fetch(`${API_URL}/usuario/${user.id}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Basic ${credenciaisCodificadas}`
            }
        });

        if (!response.ok) throw new Error("Não foi possível carregar os registros do banco.");

        const projetos = await response.json();
        
        // Alimenta a variável global correta que o renderizador lê
        projectsData = projetos; 
        
        // Dispara a montagem visual dos cards
        renderProjects();

    } catch (err) {
        console.error("Erro na carga do carrossel:", err);
        if (projectGrid) {
            projectGrid.innerHTML = `<p style="color: #ef4444; font-weight: bold;">❌ Erro ao conectar com o banco: ${err.message}</p>`;
        }
    }
}

// =====================================================================
// FUNÇÃO 2: RENDERIZA OS CARDS FÍSICOS NA TELA (MUSEU E PAGINAÇÃO)
// =====================================================================
function renderProjects() {
    if (!projectGrid) return;
    projectGrid.innerHTML = "";

    if (projectsData.length === 0) {
        projectGrid.innerHTML = "<p>Nenhum projeto encontrado no seu perfil.</p>";
        if (prev) prev.style.display = "none";
        if (next) next.style.display = "none";
        return;
    }

    let displayedProjects = [];

    if (showAll) {
        displayedProjects = projectsData;
        projectGrid.classList.add("all-projects");
        if (prev) prev.style.display = "none";
        if (next) next.style.display = "none";
        if (toggle) toggle.innerText = "Mostrar menos";
    } else {
        if (prev) prev.style.display = "flex";
        if (next) next.style.display = "flex";
        if (toggle) toggle.innerText = "Ver todos";

        const start = currentPage * cardsPerPage;
        const end = start + cardsPerPage;
        displayedProjects = projectsData.slice(start, end);

        const totalPages = Math.ceil(projectsData.length / cardsPerPage);
        if (prev) prev.disabled = currentPage === 0;
        if (next) next.disabled = currentPage >= totalPages - 1 || totalPages === 0;
    }

    // Varre os dados e cria as caixas HTML na tela
    displayedProjects.forEach(proj => {
        const card = document.createElement("article");
        card.className = "project-card";

        // Ajuste exato das propriedades novas do MySQL Workbench ('nome')
        const tituloProjeto = proj.nome || "Projeto de Terça sem título";

        card.innerHTML = `
            <div class="project-image" style="background-image: url('img/PlantaTerca.png'); background-size: cover; background-position: center; height: 120px; border-radius: 4px 4px 0 0;"></div>
            <div class="project-info" style="padding: 12px; text-align: left;">
                <h3 style="margin: 0 0 5px 0; color: #1e293b; font-size: 16px;">${tituloProjeto}</h3>
                <p style="margin: 0 0 10px 0; color: #64748b; font-size: 13px;">ID da Obra: #${proj.id}</p>
                <span class="status andamento" style="background: #e2e8f0; color: #334155; padding: 3px 8px; border-radius: 12px; font-size: 11px; font-weight: bold;">Ativo no MySQL</span>
                
                <div style="margin-top: 15px; display: flex; gap: 8px;">
                    <button onclick="abrirProjeto(${proj.id})" style="background: #0284c7; color: white; border: none; padding: 6px 12px; border-radius: 4px; cursor: pointer; font-weight: bold; font-size: 12px; display: flex; align-items: center; gap: 4px;">
                        <i class="fa-solid fa-folder-open"></i> Abrir
                    </button>
                    <button onclick="deletarProjeto(${proj.id})" style="background: #ef4444; color: white; border: none; padding: 6px 12px; border-radius: 4px; cursor: pointer; font-weight: bold; font-size: 12px; display: flex; align-items: center; gap: 4px;">
                        <i class="fa-solid fa-trash"></i> Excluir
                    </button>
                </div>
            </div>
        `;

        projectGrid.appendChild(card);
    });
}

// =====================================================================
// FUNÇÃO 3: ABRE O PROJETO SELECIONADO REDIRECIONANDO COM O ID NA URL
// =====================================================================
function abrirProjeto(id) {
    // Passa o ID na URL para o novoprojeto.html interceptar e ler no modo Edição
    window.location.href = `novoprojeto.html?id=${id}`;
}

// =====================================================================
// FUNÇÃO 4: EXCLUI O PROJETO EM CASCATA DE FORMA TOTALMENTE SEGURA
// =====================================================================
async function deletarProjeto(id) {
    if (!confirm("Tem certeza absoluta que deseja excluir permanentemente este projeto estrutural?")) return;

    const emailUsuario = localStorage.getItem("userEmail");
    const senhaUsuario = localStorage.getItem("userPassword");
    const credenciaisCodificadas = btoa(`${emailUsuario}:${senhaUsuario}`);

    try {
        const response = await fetch(`${API_URL}/${id}/usuario/${user.id}`, {
            method: "DELETE",
            headers: {
                "Authorization": `Basic ${credenciaisCodificadas}`
            }
        });

        if (response.ok) {
            // Remove da memória RAM local
            projectsData = projectsData.filter(proj => proj.id !== id);
            
            // Corrige paginação se deletar o último card da página final
            const totalPages = Math.ceil(projectsData.length / cardsPerPage);
            if (currentPage >= totalPages && currentPage > 0) {
                currentPage--;
            }
            
            // Atualiza o painel na hora de forma reativa
            renderProjects();
        } else {
            const erroMsg = await response.text();
            alert(`Falha ao excluir: ${erroMsg || "Verifique as permissões no MySQL."}`);
        }
    } catch (error) {
        console.error("Erro na conexão da deleção:", error);
        alert("Erro técnico ao conectar com o servidor.");
    }
}

// =====================================================================
// CONTROLES DE CLIQUES DOS BOTÕES DE NAVEGAÇÃO DO CARROSSEL
// =====================================================================
if (prev) {
    prev.addEventListener("click", () => {
        if (currentPage > 0) {
            currentPage--;
            renderProjects();
        }
    });
}

if (next) {
    next.addEventListener("click", () => {
        const totalPages = Math.ceil(projectsData.length / cardsPerPage);
        if (currentPage < totalPages - 1) {
            currentPage++;
            renderProjects();
        }
    });
}

if (toggle) {
    toggle.addEventListener("click", () => {
        showAll = !showAll;
        currentPage = 0;
        renderProjects();
    });
}

// 🌟 ADICIONE ESTA FUNÇÃO NO FINAL DO SEU SCRIPT.JS:
function novoProjeto() {
    // Redireciona o usuário de forma limpa para a tela de cadastro de terças
    window.location.href = "novoprojeto.html";
}


// Dispara o carregamento do banco assim que a página termina de abrir
document.addEventListener("DOMContentLoaded", carregarProjetosDoUsuario);



function logout() {
        localStorage.clear(); // Limpa tokens, e-mail, senha e dados do usuário
        window.location.href = "login.html";
}


document.addEventListener("DOMContentLoaded", () => {
    if (typeof carregarProjetosDoUsuario === "function") {
        carregarProjetosDoUsuario();
    }
});
