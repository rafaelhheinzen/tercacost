const AUTH_URL = "http://localhost:8080/auth";

document.getElementById("login-form").addEventListener("submit", async (e) => {
    e.preventDefault();
    const email = document.getElementById("login-email").value;
    const senha = document.getElementById("login-password").value;

    try {
        const response = await fetch(`${AUTH_URL}/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, senha })
        });

        if (!response.ok) throw new Error(await response.text());

        const user = await response.json();
        localStorage.setItem("user", JSON.stringify(user));
        localStorage.setItem("userEmail", email);
        localStorage.setItem("userPassword", senha);
        window.location.href = "index.html";
    } catch (err) {
        document.getElementById("auth-message").innerText = err.message;
        document.getElementById("auth-message").style.color = "red";
    }
});

document.getElementById("register-form").addEventListener("submit", async (e) => {
    e.preventDefault();
    const nome = document.getElementById("reg-name").value;
    const email = document.getElementById("reg-email").value;
    const senha = document.getElementById("reg-password").value;

    try {
        const response = await fetch(`${AUTH_URL}/cadastrar`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ nome, email, senha })
        });

        if (!response.ok) throw new Error(await response.text());

        alert("Conta criada com sucesso! Faça login.");
        switchTab("login");
    } catch (err) {
        document.getElementById("auth-message").innerText = err.message;
        document.getElementById("auth-message").style.color = "red";
    }
});


function switchTab(tab) {
    const loginForm = document.getElementById("login-form");
    const regForm = document.getElementById("register-form");
    const tabLogin = document.getElementById("tab-login");
    const tabReg = document.getElementById("tab-register");
    const message = document.getElementById("auth-message");

    message.innerText = "";

    if (tab === "login") {
        loginForm.classList.remove("hidden");
        regForm.classList.add("hidden");
        tabLogin.classList.add("active");
        tabReg.classList.remove("active");
    } else {
        loginForm.classList.add("hidden");
        regForm.classList.remove("hidden");
        tabReg.classList.add("active");
        tabLogin.classList.remove("active");
    }
}
