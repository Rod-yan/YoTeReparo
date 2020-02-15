import React from "react";
import "../Home/Home.css";
import CardContent from "../Container/CardContent";

const descriptionServices = <div>Registro de usuarios</div>;

const FormRegistro = props => {
  return (
    <div className="centered">
      <div className="row">
        <div className="col-md-12">
          <CardContent
            cardIcon="fa-hands-helping"
            cardDescription={descriptionServices}
            cardUrl="/buscar"
            cardButtonText="BUSCAR AYUDA"
            cardButtonStyle="danger"
          />
        </div>
      </div>
    </div>
  );
};

export default FormRegistro;
