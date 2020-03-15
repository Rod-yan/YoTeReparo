import { setSessionCokie, deleteSessionCookie } from "./SessionManage";
import React, { useState, useEffect } from "react";
import Container from "../Container/Container";
import ElementContainer from "../Container/ElementContainer";
import { Button } from "reactstrap";
import { useLocation } from "react-router-dom";
import Axios from "axios";

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

export const LoginHandler = ({ history }) => {
  const [username, setUsername] = useState("");
  const [loadingUser, setLoadingUser] = useState(false);
  const [errors, setErrors] = useState(false);

  const handleSubmit = async event => {
    event.preventDefault();
    let result;
    try {
      setLoadingUser(true);
      await Axios(`http://localhost:8080/YoTeReparo/users/${username}`)
        .then(response => {
          console.log(response);
          result = response;
          setLoadingUser(false);
          setErrors(false);
        })
        .catch(error => {
          result = error.response;
        });
    } catch (error) {
      console.log(error.response);
    }

    if (typeof result == "undefined") {
      setLoadingUser(false);
      setErrors(true);
    } else {
      if (username && result.status !== 404) {
        setSessionCokie({ username });
        history.push("/");
        window.location.reload();
      } else {
        setLoadingUser(false);
        setErrors(true);
      }
    }
  };

  return (
    <Container>
      <div className="centered">
        <div className="row">
          <div className="col-md-12">
            <ElementContainer>
              <div className="container">
                {errors ? (
                  <div className="alert alert-danger text-center" role="alert">
                    Los datos de sesion no son correctos
                  </div>
                ) : (
                  <></>
                )}
                <h1 className="text-center">
                  <span className="fa-stack fa-2x">
                    <i
                      className={`fas fa-user fa-stack-4x fa-1x success-icon-logo`}
                    ></i>
                  </span>
                  <div className="lead">
                    <h3>INGRESAR</h3>
                  </div>
                </h1>
                <div className="lead text-center">
                  Ingresa con sus datos para empezar a buscar sus soluciones mas
                  cercanas...
                </div>
                <div className="mt-4">
                  <form onSubmit={handleSubmit}>
                    <input
                      type="string"
                      placeholder="Ingresa tu nombre de usuario"
                      value={username}
                      className="form-control btn-block"
                      onChange={event => setUsername(event.target.value)}
                      required
                    />
                    <Button color="danger" size="lg" block className="mt-4">
                      {!loadingUser ? (
                        "INGRESAR"
                      ) : (
                        <div className="spinner-border" role="status">
                          <span className="sr-only">Creando Usuario...</span>
                        </div>
                      )}
                    </Button>
                  </form>
                </div>
              </div>
            </ElementContainer>
          </div>
        </div>
      </div>
    </Container>
  );
};

export const LogOutHandler = ({ history }) => {
  useEffect(() => {
    deleteSessionCookie("userSession");
    history.push("/ingresar");
    window.location.reload();
  }, [history]);

  return <div>Has salido de la aplicación</div>;
};
