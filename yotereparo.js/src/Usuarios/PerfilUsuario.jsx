import React from "react";
import ElementContainer from "../Container/ElementContainer";

function PerfilUsuario(props) {
  //FETCH ONLY ONE USER AND FILL THE BLANKS
  //MODIFY USER PROPERTY
  //SAVE USING PUT
  //UPLOAD A PICTURE FOR THE USER

  return (
    <ElementContainer>
      <div className="card-center-form d-flex align-items-center mx-auto">
        <div className="row">
          <div className="col-xs-12">{JSON.stringify(props.match.params)}</div>
        </div>
      </div>
    </ElementContainer>
  );
}

export default PerfilUsuario;
