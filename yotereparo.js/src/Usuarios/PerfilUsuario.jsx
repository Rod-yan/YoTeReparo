import React from "react";
import { useState, useContext } from "react";
import Axios from "axios";
import { useEffect } from "react";
import { SessionContext, ProfileContext } from "../Utils/SessionManage";
import "../Usuarios/PerfilUsuario.css";
import Usuario from "./Usuario";
import { useHistory } from "react-router-dom";
import Loading from "../Loading/Loading";
import NotAuth from "../Errors/NotAuth";
import Direcciones from "../Servicios/Direcciones";
import ConfirmPassword from "./ConfirmPassword";

//TODO: Add CRUD for address and neighbours if the user is prestador
//TODO: Validate token expired

function PerfilUsuario(props) {
  const history = useHistory();
  const session = useContext(SessionContext);
  const [profile, setProfile] = useState({});
  const [password, setPassword] = useState(null);
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState(false);
  const [auth, setAuth] = useState(true);
  const [modify, activateModify] = useState(true);
  const [modal, setModal] = useState(false);
  const [address, setAddress] = useState(false);
  const [errors, setErrors] = useState(false);
  const [errorAddress, setAddressError] = useState(false);

  const toggle = () => {
    setModal(!modal);
    setErrors(false);
  };

  const toggleAddress = () => {
    setAddress(!address);
  };

  if (!session.security) {
    props.history.push("/ingresar");
  }

  let requestConfig = {
    headers: {
      "Access-Control-Allow-Origin": "*",
      Authorization: "Bearer " + session.security?.accessToken,
    },
  };

  const validatePassword = async () => {
    //TODO: Validate password for the user
    let result;
    try {
      await Axios.post(
        `http://localhost:8080/YoTeReparo/auth/signin`,
        {
          username: profile.id,
          password: password,
        },
        requestConfig
      )
        .then((response) => {
          console.log(response);
          result = response;
        })
        .catch((error) => {
          result = error.response;
        });
    } catch (error) {
      console.log(error.response);
    }

    if (result.status >= 400 && result.status <= 500) {
      setErrors(true);
    }

    if (typeof result == "undefined") {
      setErrors(true);
    }

    if (result.data && result.status === 200) {
      updateProfile();
      handleActivateModifications();
      toggle();
    } else {
      setErrors(true);
    }
  };

  const updateProfile = () => {
    let requestDataPrestador = {
      id: profile.id,
      nombre: profile.nombre,
      apellido: profile.apellido,
      ciudad: profile.ciudad,
      barrios: profile.barrios,
      email: profile.email,
      direcciones: profile.direcciones,
      contrasena: password,
      membresia: profile.membresia,
    };

    let requestDataUsuario = {
      id: profile.id,
      nombre: profile.nombre,
      apellido: profile.apellido,
      ciudad: profile.ciudad,
      email: profile.email,
      contrasena: password,
    };

    let requestData = profile.membresia
      ? requestDataPrestador
      : requestDataUsuario;

    setUpdating(true);

    Axios.put(
      `http://localhost:8080/YoTeReparo/users/${profile.id}`,
      requestData,
      requestConfig
    )
      .then((response) => {
        if (response.status === 400) {
          console.log(response.json);
        } else {
          setUpdating(false);
          history.push({
            pathname: `/perfil/${profile.id}`,
            state: { user: profile },
          });
          console.log("INFO: Usuario actualizado correctamente");
        }
      })
      .catch((error) => {
        throw new Error(
          "ERROR: There is a problem with the update of an User" + error
        );
      });
  };

  const handleActivateModifications = () => {
    activateModify(!modify);
  };

  useEffect(() => {
    const fetchData = async (urlToFetch) => {
      let result;

      await Axios(urlToFetch, requestConfig)
        .then((resp) => {
          result = resp;
        })
        .catch((error) => {
          return error;
        });

      if (result?.status === 404) {
        console.log("ERROR: No se encuentra el usuario");
        setAuth(false);
      } else if (
        result?.name !== "Error" ||
        result?.security.accessToken != null
      ) {
        if (session.username === result?.data.id && session.security) {
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
    return <Loading loadingMessage="Cargando, por favor espera..."></Loading>;
  } else if (auth === false) {
    return <NotAuth></NotAuth>;
  }

  return (
    <>
      {loading ? (
        <Loading loadingMessage="Cargando, por favor espera..."></Loading>
      ) : (
        <>
          <ConfirmPassword
            toggle={toggle}
            modal={modal}
            profile={profile}
            errors={errors}
            validatePassword={validatePassword}
            onChange={(event) => setPassword(event.target.value)}
          ></ConfirmPassword>
          <Direcciones
            address={address}
            toggleAddress={toggleAddress}
            errors={errorAddress}
            profile={profile}
            CreateCallback={() => console.log("TODO: Validate address")}
            ModifyOne={() => console.log("TODO: Modify Address")}
            CreateOne={() => console.log("TODO: Create Address")}
          ></Direcciones>
          {profile === undefined || auth === false ? (
            <NotAuth></NotAuth>
          ) : (
            <ProfileContext.Provider value={profile}>
              <Usuario
                modify={modify}
                updatingUser={updating}
                activateEdit={() => handleActivateModifications()}
                activateSave={() => {
                  toggle();
                }}
                modifyAddress={() => {
                  setAddress(!address);
                }}
              ></Usuario>
            </ProfileContext.Provider>
          )}
        </>
      )}
    </>
  );
}

export default PerfilUsuario;
