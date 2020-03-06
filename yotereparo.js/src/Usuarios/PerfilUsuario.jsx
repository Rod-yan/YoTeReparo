import React from "react";
import ElementContainer from "../Container/ElementContainer";
import { useState, useContext } from "react";
import Axios from "axios";
import { useEffect } from "react";
import { SessionContext } from "../Utils/SessionManage";
import "../Usuarios/PerfilUsuario.css";

function PerfilUsuario(props) {
  const session = useContext(SessionContext);

  const [profile, setProfile] = useState({});
  const [loading, setLoading] = useState(true);
  const [auth, setAuth] = useState(true);

  useEffect(() => {
    const fetchData = async urlToFetch => {
      let result;

      await Axios(urlToFetch)
        .then(resp => {
          result = resp;
        })
        .catch(error => {
          return error;
        });

      if (result.status === 404) {
        console.log("ERROR: No se encuentra el usuario");
        setAuth(false);
      } else if (result.name !== "Error") {
        if (session.username === result.data.id) {
          setAuth(true);
          setProfile(result.data);
          console.log("OK: Ingresaste correctamente");
        } else {
          console.log(
            "ERROR: El usuario ingresado no corresponde con la informacion de sesion"
          );
          setAuth(false);
        }
      } else {
        setAuth(false);
        console.log(
          "ERROR: Hay un error con la peticion al servidor y/o No estas autorizado para entrar aca"
        );
      }
    };
    try {
      fetchData(
        `http://localhost:8080/YoTeReparo/users/${props.match.params.userId}`
      ).then(() => {
        setLoading(false);
      });
    } catch (error) {
      console.log(error.response);
    }
  }, [loading, auth, session.username, props.match.params.userId]);

  if (session.username === undefined) {
    props.history.push("/ingresar");
  }

  if (loading) {
    return (
      <ElementContainer>
        <div>
          <div className="col d-flex justify-content-center">
            <div className="cover-screen">Cargando, por favor espera...</div>
          </div>
        </div>
      </ElementContainer>
    );
  } else if (auth === false) {
    return (
      <ElementContainer>
        <>
          <div className="col d-flex justify-content-center">
            <div className="cover-screen">
              No estas autorizado para ver esta pagina
            </div>
          </div>
        </>
      </ElementContainer>
    );
  }
  return (
    <>
      {profile === undefined ? (
        <ElementContainer>
          <div>
            <div className="col d-flex justify-content-center">
              <div className="cover-screen">
                No existe ese nombre de usuario.
              </div>
            </div>
          </div>
        </ElementContainer>
      ) : (
        <ElementContainer>
          <div className="d-flex align-items-center mx-auto">
            <div className="row">
              <div className="col-xs-12">
                <div>
                  <div>
                    <div className="row">
                      <div className="col-12">
                        <div className="card mb-2">
                          <div className="card card-element">
                            <div className="row no-gutters">
                              <div className="col-md-4 my-auto">
                                <img
                                  src="https://via.placeholder.com/150/92c952"
                                  className="card-img rounded-circle on-profile-click"
                                  alt="placeholder"
                                  onClick={() => alert("Hello")}
                                ></img>
                              </div>
                              <div className="col-md-8 text-right">
                                <div className="card-body">
                                  <div className="row">
                                    <div className="col-md-12">
                                      <h5>
                                        {profile.roles.map(rol => {
                                          return (
                                            <span
                                              key={rol.id}
                                              className="badge badge-pill badge-danger mt-1 mb-1 mr-1 ml-1"
                                            >
                                              {rol.descripcion.toUpperCase()}
                                            </span>
                                          );
                                        })}
                                      </h5>
                                      <h3 className="card-title">
                                        {profile.nombre + profile.apellido}
                                      </h3>
                                      <div className="card-text">
                                        <div className="lead">
                                          <strong>{profile.email}</strong>
                                        </div>
                                        <div className="lead">
                                          {profile.nombre +
                                            " " +
                                            profile.apellido}
                                        </div>
                                        <div className="lead">
                                          {profile.ciudad}
                                        </div>
                                      </div>
                                    </div>
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </ElementContainer>
      )}
    </>
  );
}

export default PerfilUsuario;
