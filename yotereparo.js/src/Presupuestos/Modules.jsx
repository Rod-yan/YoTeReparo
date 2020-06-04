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
import Lottie from "react-lottie";
import * as doneState from "../Utils/Animations/433-checked-done.json";

const defaultOptions = {
  loop: false,
  autoplay: true,
  animationData: doneState.default,
  rendererSettings: {
    preserveAspectRatio: "xMidYMid slice",
  },
};

export function Introduction(props) {
  const { presupuestosContextGet } = useContext(PresupuestoContext);

  return (
    <>
      <Jumbotron>
        <h1 className="display-4 text-center">
          ¡Estas cada vez más cerca de solucionar tu inconveniente!
        </h1>
        <div className="lead text-center">
          Si eliges continuar con el proceso, se solicitará un presupuesto para
          el servicio:
          <br />
          <b>{presupuestosContextGet.title}</b>
          <br />
          Ofrecido por el usuario:
          <br />
          <b>{presupuestosContextGet.provider}</b>
        </div>
        <hr className="my-2" />
        <p className="text-center">
          ¿Deseas continuar con la solicitud del presupuesto?
        </p>
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
        <div className="display-4 mb-4 text-center">
          Revisá el servicio una vez más
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
  const [finalState, setFinalState] = useState(false);

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
      `/YoTeReparo/services/${presupuestosContextGet.id}`,
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

    result(`/YoTeReparo/users/${session.username}`, requestConfig);
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
        estado: "ESPERANDO_USUARIO_PRESTADOR",
      };

      if (additionalNotes.fechaFinEjecucionPropuesta !== undefined) {
        requestObject = {
          ...requestObject,
          fechaFinEjecucionPropuesta:
            additionalNotes.fechaFinEjecucionPropuesta +
            "T" +
            additionalNotes.horaInicioEjecucionPropuesta,
        };
      }

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

  const changeToFinalState = () => {
    setFinalState(true);
    setTimeout(() => {
      let urlTo =
        session.security.roles.length > 1
          ? "/prestador/presupuestos"
          : "/presupuestos";
      history.push(urlTo, {
        prestador: session.security.roles.length > 1 ? true : false,
      });
    }, 2000);
  };

  const sendQuote = (object, config) => {
    Axios.post("/YoTeReparo/quotes/", object, config)
      .then((response) => {
        if (response.status === 400) {
          console.log(response.data);
          console.log("Test");
        } else {
          changeToFinalState();
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
      {finalState ? (
        <div>
          <h2 className="display-4 mb-4 mt-2 text-center">
            Tu presupuesto ya esta listo!
          </h2>
          <hr className="my-4" />
          <Lottie options={defaultOptions} height={300} width={300} />
        </div>
      ) : (
        <Jumbotron>
          <Errors formErrors={formErrors}></Errors>
          <div className="display-4 mb-4 text-center">
            Detalle de la solicitud de presupuesto
          </div>
          <div className="lead text-center">
            Una vez confirmada la solicitud, podrás gestionar los detalles de la
            misma en la pantalla de Presupuestos.
            <br />
            Allí podrás seguir el estado de tu solicitud, así como también
            modificarla si así lo deseas.
          </div>
          <hr className="my-4" />
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
      )}
    </>
  );
}
