import React from "react";
import ElementContainer from "../Container/ElementContainer";
import { useState, useContext } from "react";
import Axios from "axios";
import { useEffect } from "react";
import { SessionContext, ProfileContext } from "../Utils/SessionManage";
import "../Usuarios/PerfilUsuario.css";
import Usuario from "./Usuario";
import { useHistory } from "react-router-dom";

function PerfilUsuario(props) {
  const session = useContext(SessionContext);
  let history = useHistory();
  const [profile, setProfile] = useState({});
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState(false);
  const [auth, setAuth] = useState(true);
  const [modify, activateModify] = useState(true);

  const updateProfile = () => {
    console.log(profile);
    let requestHeaders = {
      "Access-Control-Allow-Origin": "*"
    };

    let requestDataPrestador = {
      id: profile.id,
      nombre: profile.nombre,
      apellido: profile.apellido,
      ciudad: profile.ciudad,
      barrios: profile.barrios,
      email: profile.email,
      contrasena: profile.contrasena,
      membresia: profile.membresia
    };

    let requestDataUsuario = {
      id: profile.id,
      nombre: profile.nombre,
      apellido: profile.apellido,
      ciudad: profile.ciudad,
      email: profile.email,
      contrasena: profile.contrasena
    };

    let requestData = profile.membresia
      ? requestDataPrestador
      : requestDataUsuario;

    setUpdating(true);

    Axios.put(
      `http://localhost:8080/YoTeReparo/users/${profile.id}`,
      requestData,
      requestHeaders
    )
      .then(response => {
        if (response.status === 400) {
          console.log(response.json);
        } else {
          setUpdating(false);
          history.push({
            pathname: `/perfil/${profile.id}`,
            state: { user: profile }
          });
          console.log("INFO: Usuario actualizado correctamente");
        }
      })
      .catch(error => {
        throw new Error("ERROR: There is a problem with the update of an User");
      });
  };

  const handleActivateModifications = () => {
    activateModify(!modify);
  };

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
      {profile === undefined || auth === false ? (
        <ElementContainer>
          <div>
            <div className="col d-flex justify-content-center">
              <div className="cover-screen">
                No existe ese nombre de usuario y/o no estas autorizado a ver
                esta pantalla.
              </div>
            </div>
          </div>
        </ElementContainer>
      ) : (
        <ProfileContext.Provider value={profile}>
          <Usuario
            modify={modify}
            updatingUser={updating}
            activateEdit={() => handleActivateModifications()}
            activateSave={() => {
              updateProfile();
              handleActivateModifications();
            }}
          ></Usuario>
        </ProfileContext.Provider>
      )}
    </>
  );
}

export default PerfilUsuario;
