// =====================================================================
// VARIÁVEIS GLOBAIS DE CONTROLE DO CARROSSEL E SISTEMA
// =====================================================================
const API_URL = "https://tercacost.onrender.com";
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

        const response = await fetch(`${API_URL}/projetos/usuario/${user.id}`, {
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
    <div class="project-image" style="background-image: url('img/PlantaTerca.png');"></div>
    <div class="project-info">
        <h3 class="project-title">${tituloProjeto}</h3>
        
        <div class="project-actions">
            <button onclick="deletarProjeto(${proj.id})" class="btn btn-delete">
                <i class="fa-solid fa-trash"></i> Excluir
            </button>
            <button onclick="abrirProjeto(${proj.id})" class="btn btn-open">
                <i class="fa-solid fa-folder-open"></i> Abrir
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
document.addEventListener("DOMContentLoaded", () => {
    // Captura os elementos do HTML pelos IDs correspondentes
    const prev = document.getElementById("prev");
    const next = document.getElementById("next");
    const toggle = document.getElementById("toggleView");

    // Vincula os eventos apenas se os botões existirem na página
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
            // Altera o texto do botão visualmente
            toggle.textContent = showAll ? "Ver carrossel" : "Ver todos"; 
            renderProjects();
        });
    }

    // Dispara o carregamento do banco apenas uma vez de forma segura
    if (typeof carregarProjetosDoUsuario === "function") {
        carregarProjetosDoUsuario();
    }
});

// =====================================================================
// FUNÇÕES AUXILIARES DE NAVEGAÇÃO E SESSÃO
// =====================================================================
function novoProjeto() {
    window.location.href = "novoprojeto.html";
}

function logout() {
    localStorage.clear(); 
    window.location.href = "login.html";
}


document.addEventListener("DOMContentLoaded", () => {
    if (typeof carregarProjetosDoUsuario === "function") {
        carregarProjetosDoUsuario();
    }
});
