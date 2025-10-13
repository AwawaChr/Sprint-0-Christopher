const URL_API = "http://10.97.245.133:8080/mediciones";

async function cargarMediciones() {
    try {
        const response = await fetch(URL_API);
        if (!response.ok) throw new Error("Error al obtener datos: " + response.status);

        const mediciones = await response.json();
        actualizarTabla(mediciones);
    } catch (err) {
        console.error(err);
    }
}

function actualizarTabla(mediciones) {
    const tbody = document.querySelector("#tabla-mediciones tbody");
    tbody.innerHTML = "";

    mediciones.forEach(med => {
        const tr = document.createElement("tr");

        tr.innerHTML = `
            <td>${med.id}</td>
            <td>${med.mac}</td>
            <td>${med.major}</td>
            <td>${med.minor}</td>
            <td>${med.txPower}</td>
            <td>${new Date(med.timestamp).toLocaleString()}</td>
        `;
        tbody.appendChild(tr);
    });
}

// Cargar mediciones al iniciar y refrescar cada 5 segundos
cargarMediciones();
setInterval(cargarMediciones, 5000);
