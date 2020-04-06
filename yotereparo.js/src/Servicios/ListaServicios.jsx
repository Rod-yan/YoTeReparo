import React from "react";
import SingleServicio from "./SingleServicio";
import Loading from "../Loading/Loading";
import ResourceNotFound from "../Errors/ResourceNotFound";

const ListaServicios = (props) => {
  if (props.data.loading) {
    return (
      <Loading loadingMessage="Cargando la lista de servicios, por favor espera..."></Loading>
    );
  }
  return (
    <>
      {props.data.users === undefined ? (
        <ResourceNotFound errorMessage="No hay resultados."></ResourceNotFound>
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
