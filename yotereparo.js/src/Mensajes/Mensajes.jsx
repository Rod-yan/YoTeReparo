import React from "react";
import ElementContainer from "../Container/ElementContainer";
import { useState } from "react";

function Mensajes(props) {
  console.log(props);
  return (
    <>
      <div className="mt-4">
        <ElementContainer>
          <div className="lead text-center font-weight-bold">Mensajes</div>
          <div className="row">
            {!(props.content.length > 0) ? (
              <div className="col-12">
                <div className="lead text-center">
                  No hay mensajes actualmente para este servicio
                </div>
              </div>
            ) : (
              <>
                <div className="col-12 lead">
                  <div>
                    {props.content.map((x) => {
                      return <div>{x}</div>;
                    })}
                  </div>
                </div>
              </>
            )}

            <div className="col-4 lead"></div>
          </div>
        </ElementContainer>
      </div>
    </>
  );
}

export default Mensajes;
