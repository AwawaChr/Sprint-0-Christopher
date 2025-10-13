// mainServidorREST.js
const express = require("express");
const bodyParser = require("body-parser");
const cors = require("cors");
const Logica = require("./logica/Logica");

const app = express();
const PORT = 8080;

app.use(cors());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

// Inicializar la lógica (lee config desde datos/bd_config.json)
const logica = new Logica("./datos/bd_config.json");

// Ruta de comprobación
app.get("/", (req, res) => {
  res.send("Servidor REST activo y funcionando");
});

// POST /insertar  -> recibe datos desde la app Android
app.post("/insertar", async (req, res) => {
  try {
    const { mac, major, minor, txPower, timestamp } = req.body;

    if (!mac || typeof major === "undefined" || typeof minor === "undefined" ||
        typeof txPower === "undefined" || typeof timestamp === "undefined") {
      return res.status(400).json({ ok: false, error: "Faltan parámetros" });
    }

    await logica.insertarMedicion(mac, major, minor, txPower, timestamp);
    res.json({ ok: true, mensaje: "Medición insertada correctamente" });
  } catch (error) {
    console.error("Error en /insertar:", error);
    res.status(500).json({ ok: false, error: error.message });
  }
});

// GET /mediciones -> lista todas las mediciones
app.get("/mediciones", async (req, res) => {
  try {
    const mediciones = await logica.obtenerMediciones();
    res.json(mediciones);
  } catch (error) {
    console.error("Error en /mediciones:", error);
    res.status(500).json({ ok: false, error: error.message });
  }
});

app.listen(PORT, () => {
  console.log(`Servidor REST escuchando en http://localhost:${PORT}`);
});
