import React from "react";
import { useHistory } from "react-router-dom";

//Fetch from API if location.state is null

const SingleServicio = (props) => {
  let history = useHistory();

  const handleClick = (data) => {
    console.log(data);
    history.push({
      pathname: "/servicio/" + data.id,
      state: {
        id: data.id,
        title: data.titulo,
        body: data.descripcion,
        provider: data.usuarioPrestador,
        avaliable: data.disponibilidad,
        estimateTime: data.horasEstimadasEjecucion,
        averagePrice: data.precioPromedio,
        valoracionPromedio: data.valoracionPromedio,
        mensajes: data.mensajes,
      },
    });
  };

  function getRandomArbitrary(min, max) {
    return Math.round(Math.random() * (max - min) + min);
  }

  return (
    <div className="card mb-4">
      <div className="card card-element">
        <div className="row no-gutters">
          <div className="col-md-4">
            <img
              src={`http://lorempixel.com/g/${getRandomArbitrary(
                100,
                1000
              )}/${getRandomArbitrary(300, 400)}`}
              className="card-img picture"
              alt="placeholder"
            ></img>
          </div>
          <div className="col-md-8">
            <div className="card-body">
              <h3 className="card-title">{props.data.titulo}</h3>
              <div className="card-text">
                <p>{props.data.descripcion}</p>
              </div>
              <button
                href={"/servicio/" + props.data.id}
                onClick={() => {
                  handleClick(props.data);
                }}
                className="btn btn-danger btn-block"
              >
                VER SERVICIO
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SingleServicio;
