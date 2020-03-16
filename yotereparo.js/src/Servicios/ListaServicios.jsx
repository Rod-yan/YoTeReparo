import React from "react";
import ElementContainer from "../Container/ElementContainer";
import SingleServicio from "./SingleServicio";

const ListaServicios = props => {
  if (props.data.loading) {
    return (
      <ElementContainer>
        <div>
          <div className="col d-flex justify-content-center">
            <div className="cover-screen">
              Cargando la lista de servicios, por favor espera...
            </div>
          </div>
        </div>
      </ElementContainer>
    );
  }
  return (
    <>
      {props.data.users === undefined ? (
        <div className="col d-flex justify-content-center">
          <div className="cover-screen">No hay resultados.</div>
        </div>
      ) : (
        <div>
          {props.data.users.map((item, i) => (
            <div key={i}>
              <SingleServicio data={item}></SingleServicio>
            </div>
          ))}
        </div>
      )}
    </>
  );
};

export default ListaServicios;
