import React from "react";
import { Jumbotron, CardSubtitle, CardTitle } from "reactstrap";
import { useContext } from "react";
import { PresupuestoContext } from "./Presupuestos";
import { SessionContext } from "../Utils/SessionManage";
import StatusFooter from "./StatusFooter";
import ElementContainer from "../Container/ElementContainer";
import SummaryPresupuesto from "./SummaryPresupuesto";
import AdditionalNotes from "./AdditionalNotes";
import { useState } from "react";
import { useEffect } from "react";
import { fetchData } from "../Utils/SessionHandlers";
import Axios from "axios";
import { useHistory } from "react-router-dom";
import Errors from "../Errors/Errors";
import { processErrors } from "../Utils/Errors";

export function Introduction(props) {
  const { presupuestosContextGet } = useContext(PresupuestoContext);

  return (
    <>
      <Jumbotron>
        <h1 className="display-4">
          Ya estas a un paso de solucionar tu {presupuestosContextGet.title}
        </h1>
        <div className="lead">
          Al continuar con el proceso, comenzara el proceso para presupuestar el
          servicio de {presupuestosContextGet.provider}
        </div>
        <hr className="my-2" />
        <p className="text-center">¿Estas seguro que deseas continuar?</p>
        <StatusFooter validateSubmit={true} {...props} />
      </Jumbotron>
    </>
  );
}

export function Discussion(props) {
  const { presupuestosContextGet } = useContext(PresupuestoContext);

  const { session } = useContext(SessionContext);

  return (
    <>
      <Jumbotron>
        <div className="display-4 mb-4">
          Presupuesto para {session.username}
        </div>
        <div className="lead">
          De la mano de {presupuestosContextGet.provider} encargado de{" "}
          {presupuestosContextGet.body}
        </div>
        <hr className="my-4" />
        <ElementContainer>
          <CardTitle className="lead">
            <strong>Resumen de Servicio</strong>
          </CardTitle>
          <CardSubtitle>{presupuestosContextGet.title}</CardSubtitle>
          <SummaryPresupuesto
            presupuesto={presupuestosContextGet}
          ></SummaryPresupuesto>
        </ElementContainer>
        <hr className="my-4" />
        <StatusFooter validateSubmit={true} {...props} />
      </Jumbotron>
    </>
  );
}

export function Acceptance(props) {
  const { presupuestosContextGet } = useContext(PresupuestoContext);

  const history = useHistory();

  const { session } = useContext(SessionContext);

  const [additionalNotes, setAdditionalNotes] = useState({});
  const [adicionalesCheckBox, setAdicionalesCheckBox] = useState(false);
  const [insumosCheckBox, setInsumosCheckBox] = useState(false);
  const [readyToSubmit, setReadyToSubmit] = useState(false);
  const [serviceFromData, setServiceFromData] = useState({});
  const [userFromData, setUserFromData] = useState({});
  const [formErrors, setErrors] = useState({ errors: [] });
  const [validateState, setValidateState] = useState(false);

  const handleChanges = (event) => {
    additionalNotes[event.target.name] = event.target.value;

    setAdditionalNotes({
      ...additionalNotes,
      [event.target.name]: event.target.value,
    });

    if (
      additionalNotes.fechaInicioEjecucionPropuesta &&
      additionalNotes.horaInicioEjecucionPropuesta
    ) {
      setReadyToSubmit(true);
      setValidateState(true);
    }
  };

  useEffect(() => {
    fetchData(
      `http://localhost:8080/YoTeReparo/services/${presupuestosContextGet.id}`,
      setServiceFromData
    );
  }, []);

  let requestConfig = {
    headers: {
      "Access-Control-Allow-Origin": "*",
      Authorization: "Bearer " + session.security?.accessToken,
    },
  };

  useEffect(() => {
    async function result(urlToFetch, requestConfig) {
      await Axios(urlToFetch, requestConfig)
        .then((resp) => {
          setUserFromData(resp.data);
        })
        .catch((error) => {
          return error;
        });
    }

    result(
      `http://localhost:8080/YoTeReparo/users/${session.username}`,
      requestConfig
    );
  }, []);

  const handleSubmit = () => {
    if (validateState === true) {
      let requestObject = {
        servicio: presupuestosContextGet.id,
        usuarioFinal: session.username,
        descripcionSolicitud: additionalNotes.descripcionSolicitud,
        incluyeInsumos: insumosCheckBox,
        incluyeAdicionales: adicionalesCheckBox,
        fechaInicioEjecucionPropuesta:
          additionalNotes.fechaInicioEjecucionPropuesta +
          "T" +
          additionalNotes.horaInicioEjecucionPropuesta,
        fechaFinEjecucionPropuesta:
          additionalNotes.fechaFinEjecucionPropuesta +
          "T" +
          additionalNotes.horaInicioEjecucionPropuesta,
        estado: "ESPERANDO_USUARIO_PRESTADOR",
      };

      console.log(requestObject);

      if (
        serviceFromData.insitu === true &&
        userFromData.direcciones !== null
      ) {
        requestObject = {
          ...requestObject,
          direccionUsuarioFinal: userFromData.direcciones[0],
        };
      }
      sendQuote(requestObject, requestConfig);
    } else {
      setReadyToSubmit(false);
    }
  };

  const sendQuote = (object, config) => {
    Axios.post("http://localhost:8080/YoTeReparo/quotes/", object, config)
      .then((response) => {
        if (response.status === 400) {
          console.log(response.data);
        } else {
          let urlTo =
            session.security.roles.length > 1
              ? "/prestador/presupuestos"
              : "/presupuestos";
          history.push(urlTo, {
            prestador: session.security.roles.length > 1 ? true : false,
          });
        }
      })
      .catch((error) => {
        console.log(error.response);
        let errors = processErrors(error.response.data);
        setErrors({ ...formErrors, errors });
      });
  };

  return (
    <>
      <Jumbotron>
        <Errors formErrors={formErrors}></Errors>
        <div className="display-4 mb-4">Datos Adicionales</div>
        <AdditionalNotes
          onHandleChange={handleChanges}
          adicionales={adicionalesCheckBox}
          setAdicionales={setAdicionalesCheckBox}
          insumos={insumosCheckBox}
          setInsumos={setInsumosCheckBox}
        />
        <StatusFooter
          {...props}
          validateSubmit={readyToSubmit}
          onSubmit={handleSubmit}
        />
      </Jumbotron>
    </>
  );
}
