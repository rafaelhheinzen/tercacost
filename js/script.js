const userJson = localStorage.getItem("user");
if (!userJson) {
    window.location.href = "login.html";
}
const user = JSON.parse(userJson);

const API_URL = "http://localhost:8080/projetos";

const projectGrid = document.getElementById("projectGrid");
const prev = document.getElementById("prev");
const next = document.getElementById("next");
const toggle = document.getElementById("toggleView");

const cardsPerPage = 6;
let currentPage = 0;
let showAll = false;
let projectsData = [];

// Update user name on top right header and welcome banner
if (document.getElementById("user-name")) {
    document.getElementById("user-name").innerText = user.nome;
}
const welcomeHeader = document.querySelector(".welcome h1");
if (welcomeHeader) {
    welcomeHeader.innerText = `Olá, ${user.nome}!`;
}

function logout() {
    localStorage.removeItem("user");
    window.location.href = "login.html";
}

// SINGLE fetchProjects declaration querying ONLY active user's projects
async function fetchProjects() {
    try {
        const response = await fetch(`${API_URL}/usuario/${user.id}`);
        if (!response.ok) throw new Error("Erro ao carregar projetos.");

        projectsData = await response.json();
        renderProjects();
    } catch (error) {
        console.error("Erro na requisição:", error);
        if (projectGrid) {
            projectGrid.innerHTML = `<p style="color:red;">Não foi possível carregar os projetos.</p>`;
        }
    }
}

// Render cards dynamically
function renderProjects() {
    if (!projectGrid) return;
    projectGrid.innerHTML = "";

    if (projectsData.length === 0) {
        projectGrid.innerHTML = "<p>Nenhum projeto encontrado.</p>";
        prev.style.display = "none";
        next.style.display = "none";
        return;
    }

    let displayedProjects = [];

    if (showAll) {
        displayedProjects = projectsData;
        projectGrid.classList.add("all-projects");
        prev.style.display = "none";
        next.style.display = "none";
        toggle.innerText = "Mostrar menos";
    } else {
        prev.style.display = "flex";
        next.style.display = "flex";
        toggle.innerText = "Ver todos";

        const start = currentPage * cardsPerPage;
        const end = start + cardsPerPage;
        displayedProjects = projectsData.slice(start, end);

        const totalPages = Math.ceil(projectsData.length / cardsPerPage);
        prev.disabled = currentPage === 0;
        next.disabled = currentPage >= totalPages - 1 || totalPages === 0;
    }

    // Procure por este trecho dentro da função renderProjects() no seu js/script.js:
    displayedProjects.forEach(proj => {
        const card = document.createElement("article");
        card.className = "project-card";

        // CORREÇÃO: Lê 'proj.nome' (MySQL) em vez de 'proj.descricao'
        const tituloProjeto = proj.nome || "Projeto sem título";
        const msdFormatado = proj.msd !== undefined && proj.msd !== null ? Number(proj.msd).toFixed(2) : "0.00";

        // CORREÇÃO: No MySQL, as propriedades de Lb e as dimensões ficam na tabela Coberturas.
        // Para o carrossel carregar limpo sem travar:
        card.innerHTML = `
        <div class="project-image"></div>
        <div class="project-info">
            <h3>${tituloProjeto}</h3>
            <p>Perfil: ${proj.tipoPerfil || "Disponível"} • ID: #${proj.id}</p>
            <span class="status andamento">Registrado no MySQL</span>
            
            <div style="margin-top: 10px; display: flex; gap: 8px;">
                <button onclick="abrirProjeto(${proj.id})" style="background: #007bff; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer;">
                    <i class="fa-solid fa-folder-open"></i> Abrir
                </button>
                <button onclick="deletarProjeto(${proj.id})" style="background: red; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer;">
                    <i class="fa-solid fa-trash"></i> Excluir
                </button>
            </div>
        </div>
    `;

        projectGrid.appendChild(card);
    });

}

function abrirProjeto(id) {
    window.location.href = `novoprojeto.html?id=${id}`;
}

async function deletarProjeto(id) {
    if (!confirm("Tem certeza que deseja excluir este projeto?")) return;

    try {
        const response = await fetch(`${API_URL}/${id}/usuario/${user.id}`, {
            method: "DELETE"
        });

        if (response.ok) {
            projectsData = projectsData.filter(proj => proj.id !== id);
            renderProjects();
        } else {
            alert("Você não tem permissão para excluir este projeto.");
        }
    } catch (error) {
        console.error("Erro ao deletar:", error);
    }
}

next.addEventListener("click", () => {
    const totalPages = Math.ceil(projectsData.length / cardsPerPage);
    if (currentPage < totalPages - 1) {
        currentPage++;
        renderProjects();
    }
});

prev.addEventListener("click", () => {
    if (currentPage > 0) {
        currentPage--;
        renderProjects();
    }
});

toggle.addEventListener("click", () => {
    showAll = !showAll;
    currentPage = 0;
    renderProjects();
});

function novoProjeto() {
    window.location.href = "novoprojeto.html";
}

// Initialize
fetchProjects();
