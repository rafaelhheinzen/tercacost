function login(){

    const email = document.getElementById("email").value;

    const senha = document.getElementById("senha").value;

    if(email === "" || senha === ""){

        alert("Preencha todos os campos.");

        return;
    }

    // Simulação de login

    localStorage.setItem("usuario","Usuário");

    window.location.href="dashboard.html";

}