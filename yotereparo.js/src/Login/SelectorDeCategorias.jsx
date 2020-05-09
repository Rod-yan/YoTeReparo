import React from "react";
import CardContent from "../Container/CardContent";

function SelectorDeCategorias(props) {
  return (
    <>
      <div className="centered">
        <div className="row">
          <div align="center" className="col-md-6">
            <CardContent
              animation={null}
              cardIcon="fa-users"
              cardDescription="Si estas buscando contratar servicios, puedes registrarte como cliente"
              cardUrl="/registrar-usuario"
              cardButtonText="REGISTRARSE COMO CLIENTE"
              cardButtonStyle="success"
            />
          </div>
          <div align="center" className="col-md-6">
            <CardContent
              animation={null}
              cardIcon="fa-briefcase"
              cardDescription="Si estas buscando publicar tus servicios, puedes registrarte como prestador"
              cardUrl="/registrar-empleador"
              cardButtonText="REGISTRARSE COMO PRESTADOR"
              cardButtonStyle="info"
            />
          </div>
        </div>
      </div>
    </>
  );
}

export default SelectorDeCategorias;
