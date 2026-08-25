const userJson = localStorage.getItem("user");

if (!userJson) {
    window.location.href = "login.html";
}

const user = JSON.parse(userJson);
const API_URL = "http://localhost:8080/projetos";

const urlParams = new URLSearchParams(window.location.search);
const projectId = urlParams.get("id");

// Carregar dados do projeto no modo edição
if (projectId) {
    fetch(`${API_URL}/${projectId}/usuario/${user.id}`)
        .then(res => {
            if (!res.ok) throw new Error("Acesso não autorizado ou projeto inexistente.");
            return res.json();
        })
        .then(data => {
            document.getElementById("descricao").value = data.descricao || "";
            document.getElementById("nomedoPerfil").value = data.nomedoPerfil || "";
            document.getElementById("msd").value = data.msd ?? "";
            document.getElementById("vsd").value = data.vsd ?? "";
            document.getElementById("lb").value = data.lb ?? "";
            document.getElementById("cb").value = data.cb ?? "";

            document.querySelector(".sidebar h2").innerText = "Editar Projeto";
            document.getElementById("calculate").innerHTML = `<i class="fa-solid fa-floppy-disk"></i> Atualizar Projeto`;
        })
        .catch(err => {
            alert(err.message);
            window.location.href = "index.html";
        });
}

// Salvar ou atualizar inputs do projeto
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
        cb: Cb,
        usuario: { id: user.id }
    };

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
