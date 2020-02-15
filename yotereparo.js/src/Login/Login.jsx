import React from "react";
import CardContent from "../Container/CardContent";

function Login(props) {
  return (
    <>
      <div className="centered">
        <div className="row">
          <div className="col-md-6">
            <CardContent
              cardIcon="fa-users"
              cardDescription="Si estas buscando crear un nuevo usuario, puedes registrarte normalmente"
              cardUrl="/registrar-usuario"
              cardButtonText="REGISTRAR USUARIO"
              cardButtonStyle="success"
            />
          </div>
          <div className="col-md-6">
            <CardContent
              cardIcon="fa-briefcase"
              cardDescription="Si estas buscando crear un nuevo usuario, puedes registrarte normalmente"
              cardUrl="/registrar-empleador"
              cardButtonText="REGISTRARTE COMO EMPLEADOR"
              cardButtonStyle="info"
            />
          </div>
        </div>
      </div>
    </>
  );
}

export default Login;
