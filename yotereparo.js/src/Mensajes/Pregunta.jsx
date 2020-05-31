import React from "react";

function Pregunta(props) {
  return (
    <div className="col-12 mt-2 mb-2">
      <div className="lead body-text-pregunta">{props.pregunta}</div>
    </div>
  );
}

export default Pregunta;
