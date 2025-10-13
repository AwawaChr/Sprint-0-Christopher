// tests/test_mediciones.js
const chai = require("chai");
const expect = chai.expect;
const request = require("supertest");
const express = require("express");
const Logica = require("../servidor/logica/Logica");
const mainServidorREST = require("../servidor/mainServidorREST"); // ruta a tu mainServidorREST

describe("Test de API REST y lógica de mediciones", function () {
  let app;
  let logica;

  // Configuramos antes de todos los tests
  before(async function () {
    this.timeout(5000);

    // Instanciamos la lógica apuntando a tu archivo de configuración de BD
    logica = new Logica("./datos/bd_config.json");

    // Montamos Express para test
    app = express();
    app.use(express.json());

    // Montamos rutas de tu servidor
    app.use("/", mainServidorREST); // si mainServidorREST exporta app directamente, si no exporta las rutas y pon app.use('/api', rutas)
  });

  after(async function () {
    // Cerrar conexión si tu Logica tiene método cerrar()
    if (logica.cerrar) await logica.cerrar();
  });

  it("debería insertar una medición correctamente", async function () {
    const medicion = {
      mac: "AA:BB:CC:11:22:99",
      major: 11,
      minor: 105,
      txPower: -60,
      timestamp: Date.now(),
    };

    const res = await request(app)
      .post("/insertar")
      .send(medicion);

    expect(res.status).to.equal(200);
    expect(res.body.ok).to.be.true;
    expect(res.body.mensaje).to.include("insertada");
  });

  it("debería recuperar una lista de mediciones", async function () {
    const res = await request(app).get("/mediciones");

    expect(res.status).to.equal(200);
    expect(res.body).to.be.an("array");
    if (res.body.length > 0) {
      const m = res.body[0];
      expect(m).to.have.property("mac");
      expect(m).to.have.property("major");
      expect(m).to.have.property("minor");
      expect(m).to.have.property("txPower");
      expect(m).to.have.property("timestamp");
    }
  });
});
