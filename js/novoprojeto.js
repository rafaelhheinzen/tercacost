const button = document.getElementById("calculate");

button.addEventListener("click", ()=>{

    const length = Number(document.getElementById("length").value);

    const load = Number(document.getElementById("load").value);

    if(length<=0 || load<=0){

        document.getElementById("result").innerHTML =
        "Preencha todos os campos.";

        return;

    }

    // Exemplo:
    // Momento máximo de uma viga biapoiada
    // M = qL² / 8

    const moment = (load * Math.pow(length,2))/8;

    document.getElementById("result").innerHTML =

    `<strong>Momento Máximo:</strong><br>
    ${moment.toFixed(2)} kN·m`;

});