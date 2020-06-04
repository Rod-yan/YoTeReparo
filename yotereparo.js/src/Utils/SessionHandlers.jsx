import React, { useState, useEffect, useContext } from "react";
import {
  setSessionCokie,
  deleteSessionCookie,
  SessionContext,
  getSessionCookie,
} from "./SessionManage";
import Container from "../Container/Container";
import ElementContainer from "../Container/ElementContainer";
import { Button } from "reactstrap";
import Axios from "axios";

export const LoginHandler = ({ history }) => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loadingUser, setLoadingUser] = useState(false);
  const [errors, setErrors] = useState(false);
  const { setSession } = useContext(SessionContext);

  let requestConfig = {
    headers: {
      "Access-Control-Allow-Origin": "*",
    },
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    let result;
    try {
      setLoadingUser(true);
      await Axios.post(
        `/YoTeReparo/auth/signin`,
        {
          username: username,
          password: password,
        },
        requestConfig
      )
        .then((response) => {
          console.log("INFO: Usuario correctamente autorizado");
          result = response;
        })
        .catch((error) => {
          result = error.response;
        });
    } catch (error) {
      console.log(error.response);
    }

    if (result?.status >= 400 && result?.status <= 500) {
      setLoadingUser(false);
      setErrors(true);
    }

    if (typeof result == "undefined") {
      setLoadingUser(false);
      setErrors(true);
    }

    if (result?.data && result?.status === 200) {
      setLoadingUser(false);
      setErrors(false);
      setSessionCokie({ username: username, security: result.data });
      setSession(getSessionCookie());
      history.push({
        pathname: `/buscar`,
        state: {
          username: username,
          security: result.data,
        },
      });
      window.location.reload();
    } else {
      setLoadingUser(false);
      setErrors(true);
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
                  Contratá o registrá nuevos servicios para tu hogar
                  <br />
                  Rápido, seguro, y gratuito :)
                </div>
                <div className="mt-4">
                  <form onSubmit={handleSubmit}>
                    <input
                      type="string"
                      placeholder="Ingresa tu nombre de usuario"
                      value={username}
                      className="form-control btn-block"
                      onChange={(event) => setUsername(event.target.value)}
                      required
                    />
                    <input
                      placeholder="Ingresa tu contraseña"
                      value={password}
                      type="password"
                      className="form-control btn-block"
                      onChange={(event) => setPassword(event.target.value)}
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

  return <></>;
};

export const putData = async (
  urlToFetch,
  requestConfig,
  callback,
  object = {}
) => {
  await Axios.put(urlToFetch, object, requestConfig)
    .then((resp) => {
      callback(resp.data);
    })
    .catch((error) => {
      return error;
    });
};

export const fetchData = async (urlToFetch, callback) => {
  const result = await Axios(urlToFetch)
    .then((resp) => {
      return resp;
    })
    .catch((error) => {
      console.log(error);
      return error;
    });

  callback(result.data);
};
