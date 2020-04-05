import { useLocation } from "react-router-dom";
import React from "react";
import ElementContainer from "../Container/ElementContainer";

export const NoMatch = () => {
  let location = useLocation();

  return (
    <>
      {" "}
      <ElementContainer>
        <div className="card-center-form d-flex align-items-center mx-auto">
          <div className="row">
            <div className="col-xs-12">
              Donde sea que estes yendo, este no es el camino.
              <br></br>
              <br></br>
              <code>
                <strong>{location.pathname}</strong>
              </code>
            </div>
          </div>
        </div>
      </ElementContainer>
    </>
  );
};
