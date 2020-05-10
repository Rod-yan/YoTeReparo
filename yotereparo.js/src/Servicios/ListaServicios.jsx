import React from "react";
import SingleServicio from "./SingleServicio";
import Loading from "../Loading/Loading";
import ResourceNotFound from "../Errors/ResourceNotFound";
import Masonry from "react-masonry-css";

const ListaServicios = (props) => {
  if (props.data.loading) {
    return (
      <Loading loadingMessage="Cargando la lista de servicios, por favor espera..."></Loading>
    );
  }
  return (
    <>
      {props.data.services === undefined ? (
        <ResourceNotFound errorMessage="No hay resultados."></ResourceNotFound>
      ) : (
        <div>
          <Masonry
            breakpointCols={2}
            className="yotereparoGrid"
            columnClassName="yotereparoGrid_column"
          >
            {props.data.services.map((item, i) => (
              <div className="text-center" key={i}>
                <SingleServicio data={item}></SingleServicio>
              </div>
            ))}
          </Masonry>
        </div>
      )}
    </>
  );
};

export default ListaServicios;
