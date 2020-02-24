import React from "react";
import ElementContainer from "../Container/ElementContainer";
import { useLocation } from "react-router-dom";

function Tour(props) {
  let location = useLocation();

  let userId = location.state.user.nombre + location.state.user.apellido;

  console.log(userId);

  return (
    <div>
      <div className="centered card-center-form">
        <div className="row">
          <div className="col-md-12">
            <ElementContainer>
              <div className="container">
                <h1 className="text-center">
                  <span className="fa-stack fa-2x">
                    <i
                      className={`fas fa-check fa-stack-2x success-icon-logo`}
                    ></i>
                  </span>
                  <div className="lead">
                    <h3>USUARIO CREADO CON EXITO</h3>
                  </div>
                </h1>
                <div className="lead text-center">
                  Ingresa con sus datos para empezar a buscar sus soluciones mas
                  cercanas...
                </div>
                <hr className="my-4" />
                <div className="lead">
                  <a
                    className="btn btn-success btn-lg btn-block"
                    href={"/perfil/" + userId}
                    role="button"
                  >
                    <h2>
                      <i className="far fa-address-card mt-2"></i>
                    </h2>
                  </a>
                </div>
              </div>
            </ElementContainer>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Tour;
