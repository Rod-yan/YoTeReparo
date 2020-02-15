import React from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import "../Home/Home.css";
import CardContent from "../Container/CardContent";

const descriptionServices = (
  <div>
    Si estas buscando solucionar <strong>TUS</strong> problemas...
  </div>
);
const descriptionHelp = (
  <div>
    Si estas buscando solucionar <strong>problemas de otras personas...</strong>
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
            cardButtonText="BUSCAR AYUDA"
            cardButtonStyle="danger"
          />
        </div>
        <div className="col-md-6">
          <CardContent
            cardIcon="fa-tools"
            cardDescription={descriptionServices}
            cardUrl="/registro"
            cardButtonText="ENCONTRAR TRABAJOS"
            cardButtonStyle="danger"
          />
        </div>
      </div>
    </div>
  );
}

export default Home;
