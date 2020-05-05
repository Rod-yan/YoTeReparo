import React from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import "../Home/Home.css";
import CardContent from "../Container/CardContent";

import * as teamWork from "../Utils/21332-teamwork.json";
import * as plumber from "../Utils/14525-plumbers.json";
import * as writer from "../Utils/21333-writer.json";

const descriptionServices = (
  <div align="center">
    ¿Querés <strong>contratar o publicar servicios profesionales</strong> en tu
    zona fácilmente y sin complicaciones?
  </div>
);
const descriptionHelp = (
  <div align="center">
    ¿Necesitas encontrar una <strong>solución rápida y accesible</strong> a tus
    problemas del hogar?
  </div>
);

const defaultOptionsWriters = {
  loop: true,
  autoplay: true,
  animationData: teamWork.default,
  rendererSettings: {
    preserveAspectRatio: "xMidYMid meet",
  },
};

const defaultOptionsPlumbers = {
  loop: true,
  autoplay: true,
  animationData: plumber.default,
  rendererSettings: {
    preserveAspectRatio: "xMidYMid meet",
  },
};

const defaultOptionsWorkers = {
  loop: true,
  autoplay: true,
  animationData: writer.default,
  rendererSettings: {
    preserveAspectRatio: "xMidYMid meet",
  },
};

function Home(props) {
  return (
    <div className="centered">
      <div className="row">
        <div className="col-md-6">
          <CardContent
            animation={defaultOptionsPlumbers}
            cardIcon="fa-hands-helping"
            cardDescription={descriptionHelp}
            cardUrl="/buscar"
            cardButtonText="BUSCÁ SERVICIOS"
            cardButtonStyle="danger"
          />
        </div>
        <div className="col-md-6">
          <CardContent
            animation={defaultOptionsWorkers}
            cardIcon="fa-tools"
            cardDescription={descriptionServices}
            cardUrl="/registro"
            cardButtonText="REGISTRÁ TU USUARIO"
            cardButtonStyle="danger"
          />
        </div>
      </div>
    </div>
  );
}

export default Home;
