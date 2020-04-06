import React from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import "../Home/Home.css";
import CardContent from "../Container/CardContent";

const descriptionServices = (
  <div align="center">
    ¿Querés contratar o publicar servicios profesionales en tu zona <strong>fácilmente y sin complicaciones</strong>?
  </div>
);
const descriptionHelp = (
  <div align="center">
	¿Necesitas encontrar una solución <strong>rápida y accesible</strong> a tus problemas del hogar?
  </div>
);

function Home(props) {
  return (
    <div className="centered">
      <div className="row">
        <div className="col-md-6">
          <CardContent
            cardIcon="fa-hands-helping"
            cardDescription={descriptionHelp}
            cardUrl="/buscar"
            cardButtonText="BUSCÁ SERVICIOS EN TU ZONA"
            cardButtonStyle="danger"
          />
        </div>
        <div className="col-md-6">
          <CardContent
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
