import React from "react";
import ElementContainer from "../Container/ElementContainer";
import { useState } from "react";
import Axios from "axios";
import { useEffect } from "react";

function PerfilUsuario(props) {
  const [profile, setProfile] = useState({});
  const [loading, setLoading] = useState(true);

  const fetchData = async urlToFetch => {
    const result = await Axios(urlToFetch);
    setProfile(result.data);
  };

  useEffect(() => {
    try {
      fetchData(
        `http://localhost:8080/yotereparo/users/${props.match.params.userId}`
      ).then(resp => {
        setLoading(false);
      });
    } catch (error) {
      console.log(error);
    }
  }, [loading]);

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
  }
  return (
    <>
      {profile === undefined ? (
        <div className="col d-flex justify-content-center">
          <div className="cover-screen">No existe ese nombre de usuario.</div>
        </div>
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
                                  className="card-img rounded-circle"
                                  alt="placeholder"
                                ></img>
                              </div>
                              <div className="col-md-8 text-right">
                                <div className="card-body">
                                  <div className="row">
                                    <div className="col-md-12">
                                      <h5>
                                        <span class="badge badge-pill badge-success mt-1 mb-1 mr-1 ml-1">
                                          USUARIO
                                        </span>
                                        <span class="badge badge-pill badge-danger mt-1 mb-1 mr-1 ml-1">
                                          PRESTADOR
                                        </span>
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
