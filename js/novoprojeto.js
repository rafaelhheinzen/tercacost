const API_URL = "http://localhost:8080/projetos";

// Verificar se existe um id na url
const urlParams = new URLSearchParams(window.location.search);
const projectId = urlParams.get("id");

// Carregar dados existentes de um projeto antigo
if (projectId) {
    carregarProjeto(projectId);
}

async function carregarProjeto(id) {
    try {
        const response = await fetch(`${API_URL}/${id}`);
        if (!response.ok) throw new Error("Projeto não encontrado");

        const proj = await response.json();

        // Carregar inputs
        document.getElementById("descricao").value = proj.descricao || "";
        document.getElementById("nomedoPerfil").value = proj.nomedoPerfil || "";
        document.getElementById("msd").value = proj.msd ?? "";
        document.getElementById("vsd").value = proj.vsd ?? "";
        document.getElementById("lb").value = proj.lb ?? "";
        document.getElementById("cb").value = proj.cb ?? "";

        // Atualizar UI do botão e título
        document.querySelector(".sidebar h2").innerText = "Editar Projeto";
        document.getElementById("calculate").innerHTML = `<i class="fa-solid fa-floppy-disk"></i> Atualizar Projeto`;
    } catch (error) {
        console.error("Erro ao carregar dados:", error);
        alert("Erro ao carregar os dados do projeto.");
    }
}

// Salvar o atualizar
document.getElementById("calculate").addEventListener("click", async () => {
    const descricao = document.getElementById("descricao").value.trim();
    const nomedoPerfil = document.getElementById("nomedoPerfil").value.trim();
    const MSD = parseFloat(document.getElementById("msd").value) || 0;
    const VSD = parseFloat(document.getElementById("vsd").value) || 0;
    const Lb = parseFloat(document.getElementById("lb").value) || 0;
    const Cb = parseFloat(document.getElementById("cb").value) || 0;

    const resultElement = document.getElementById("result");

    if (!descricao) {
        resultElement.style.color = "red";
        resultElement.innerText = "Por favor, informe a descrição do projeto.";
        return;
    }

    const projetoPayload = {
        descricao: descricao,
        nomedoPerfil: nomedoPerfil,
        msd: MSD,
        vsd: VSD,
        lb: Lb,
        cb: Cb
    };

    // Determinar se é PUT ou POST
    const isEdit = Boolean(projectId);
    const targetUrl = isEdit ? `${API_URL}/${projectId}` : API_URL;
    const httpMethod = isEdit ? "PUT" : "POST";

    try {
        const response = await fetch(targetUrl, {
            method: httpMethod,
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(projetoPayload)
        });

        if (response.ok) {
            const data = await response.json();
            resultElement.style.color = "green";
            resultElement.innerText = isEdit 
                ? "Projeto atualizado com sucesso!" 
                : `Projeto "${data.descricao}" salvo com sucesso!`;

            setTimeout(() => {
                window.location.href = "index.html";
            }, 1200);
        } else {
            const errorText = await response.text();
            resultElement.style.color = "red";
            resultElement.innerText = `Erro: ${errorText}`;
        }
    } catch (error) {
        console.error("Erro ao salvar:", error);
        resultElement.style.color = "red";
        resultElement.innerText = "Erro ao conectar com o servidor backend.";
    }
});
