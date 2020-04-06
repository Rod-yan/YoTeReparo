import React from "react";
import ElementContainer from "../Container/ElementContainer";

const NotAuth = (props) => {
  return (
    <ElementContainer>
      <div>
        <div className="col d-flex justify-content-center">
          <div className="cover-screen">
            No estas autorizado para ver esta pagina
          </div>
        </div>
      </div>
    </ElementContainer>
  );
};

export default NotAuth;
