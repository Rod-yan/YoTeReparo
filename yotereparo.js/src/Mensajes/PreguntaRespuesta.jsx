import React from "react";

function PreguntaRespuesta(props) {
  return (
    <>
      <div className="col-8 mt-2 mb-2">
        <div className="lead body-text-pregunta">{props.pregunta}</div>
      </div>
      <div className="col-4 mt-2 mb-2">
        <div className="lead body-text-respuesta">{props.respuesta}</div>
      </div>
    </>
  );
}

export default PreguntaRespuesta;
