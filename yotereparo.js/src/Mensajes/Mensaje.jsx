import "./Mensaje.css";
import React from "react";
import PreguntaRespuesta from "./PreguntaRespuesta";
import Pregunta from "./Pregunta";

function Mensaje(props) {
  return (
    <div>
      {props.content.loading === "loading" ? (
        <>
          <div className="row">
            <div className="col-12">
              <div className="text-center">
                <div className="spinner-border" role="status">
                  <span className="sr-only">Loading...</span>
                </div>
              </div>
            </div>
          </div>
          <hr></hr>
        </>
      ) : (
        <>
          <div className="row">
            {props.content.object.respuesta === null ? (
              <Pregunta pregunta={props.content.object.consulta}></Pregunta>
            ) : (
              <PreguntaRespuesta
                pregunta={props.content.object.consulta}
                respuesta={props.content.object.respuesta}
              ></PreguntaRespuesta>
            )}
          </div>
          <div className="row">
            <div className="col-md-10">
              <div className="muted float-left props-text">
                {props.content.object.usuarioFinal}
              </div>
            </div>
            <div className="col-md-2">
              {props.esPrestador === true ? (
                <div className="float-left">
                  <button
                    type="button"
                    onClick={() => {
                      props.onReply(props.content.object.id);
                    }}
                    disabled={
                      !(
                        props.content.object.respuesta === null ||
                        props.content.loading === "loading"
                      )
                    }
                    className="btn btn-danger btn-circle"
                  >
                    <i className="fas fa-reply"></i>
                  </button>
                </div>
              ) : null}
              <div className="font-weight-light float-right props-text mr-2">
                {props.content.object.fechaConsulta.year +
                  "/" +
                  props.content.object.fechaConsulta.monthOfYear +
                  "/" +
                  props.content.object.fechaConsulta.dayOfMonth}
              </div>
            </div>
          </div>
          <hr></hr>
        </>
      )}
    </div>
  );
}

export default Mensaje;
