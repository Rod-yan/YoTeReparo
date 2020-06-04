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
import ResourceNotFound from "../Errors/ResourceNotFound";
import { useRef } from "react";

const toLower = (text) => {
  return text.toLowerCase();
};

function PerfilUsuario(props) {
  const history = useHistory();
  const { session } = useContext(SessionContext);
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
  const [modifyAddressFields, setModifyAddress] = useState(true);

  const [newAddress, setNewAddress] = useState({
    calle: 0,
    altura: 0,
    piso: 0,
    departamento: "No definido",
    descripcion: "No definido",
  });

  const onChangeNewAddress = (event) => {
    setNewAddress({ ...newAddress, [event.target.id]: event.target.value });
  };

  const toggle = () => {
    setModal(!modal);
    setErrors(false);
  };

  const toggleAddress = () => {
    setAddressError(false);
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
        `/YoTeReparo/auth/signin`,
        {
          username: toLower(profile.id),
          password: password,
        },
        requestConfig
      )
        .then((response) => {
          console.log("INFO: Usuario validado correctamente");
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
      activateModify(true);
      setModifyAddress(true);
      setAddress(false);
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
      direcciones:
        profile.direcciones.length > 0
          ? profile.direcciones
          : newAddress.altura === 0
          ? []
          : [newAddress],
      contrasena: password,
      membresia: profile.membresia,
    };

    let requestDataUsuario = {
      id: profile.id,
      nombre: profile.nombre,
      apellido: profile.apellido,
      ciudad: profile.ciudad,
      direcciones:
        profile.direcciones.length > 0
          ? profile.direcciones
          : newAddress.altura === 0
          ? []
          : [newAddress],
      email: profile.email,
      contrasena: password,
    };

    let requestData = profile.membresia
      ? requestDataPrestador
      : requestDataUsuario;

    setUpdating(true);

    Axios.put(`/YoTeReparo/users/${profile.id}`, requestData, requestConfig)
      .then((response) => {
        if (response.status === 400) {
          console.log(response.json);
        } else {
          setUpdating(false);
          setAddressError(false);
          history.push({
            pathname: `/perfil/${profile.id}`,
            state: { user: profile },
          });
          console.log("INFO: Usuario actualizado correctamente");
        }
      })
      .catch((error) => {
        setAddressError(true);
      });
  };

  const updateAddress = (event) => {
    profile.direcciones[0][event.target.id] = event.target.value;
  };

  const handleActivateModifications = () => {
    setAddressError(false);
    activateModify(!modify);
  };

  const handleCancelModifications = () => {
    activateModify(!modify);
    setUpdating(false);
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
        if (toLower(session.username) === result?.data.id && session.security) {
          setAuth(true);
          setProfile(result.data);
          console.log("OK: Ingresaste correctamente");
          setErrors(false);
        } else {
          console.log(
            "ERROR: El usuario ingresado no corresponde con la informacion de sesion"
          );
          setAuth(false);
          setErrors(true);
        }
      } else {
        setAuth(false);
        setErrors(true);
        console.log(
          "ERROR: Hay un error con la peticion al servidor y/o No estas autorizado para entrar aca"
        );
      }
    };
    try {
      fetchData(`/YoTeReparo/users/${props.match.params.userId}`).then(() => {
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
            addressModify={modifyAddressFields}
            profile={profile}
            handleChange={updateAddress}
            onChangeNewAddress={onChangeNewAddress}
            CreateCallback={() =>
              modifyAddressFields === false ? toggle() : toggleAddress()
            }
            ModifyOne={() => setModifyAddress(!modifyAddressFields)}
          ></Direcciones>
          {profile === undefined || auth === false ? (
            <NotAuth></NotAuth>
          ) : (
            <>
              {errorAddress && (
                <ResourceNotFound errorMessage="Estas intentado ingresar una direccion erronea"></ResourceNotFound>
              )}
              <ProfileContext.Provider value={profile}>
                <Usuario
                  modify={modify}
                  updatingUser={updating}
                  activateEdit={() => handleActivateModifications()}
                  activateSave={() => {
                    toggle();
                  }}
                  cancelSave={handleCancelModifications}
                  modifyAddress={() => {
                    setAddress(!address);
                  }}
                ></Usuario>
              </ProfileContext.Provider>
            </>
          )}
        </>
      )}
    </>
  );
}

export default PerfilUsuario;
