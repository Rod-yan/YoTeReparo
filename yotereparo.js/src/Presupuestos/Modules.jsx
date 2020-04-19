import React from "react";
import { Jumbotron, CardSubtitle, CardTitle, CardText } from "reactstrap";
import { useContext } from "react";
import { PresupuestoContext } from "./Presupuestos";
import { SessionContext } from "../Utils/SessionManage";
import StatusFooter from "./StatusFooter";
import ElementContainer from "../Container/ElementContainer";
import SummaryPresupuesto from "./SummaryPresupuesto";
import AdditionalNotes from "./AdditionalNotes";
import { useState } from "react";

export function Introduction(props) {
  const { presupuestosContextGet, presupuestosContextUpdate } = useContext(
    PresupuestoContext
  );

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
        <StatusFooter {...props} />
      </Jumbotron>
    </>
  );
}

export function Discussion(props) {
  const { presupuestosContextGet, presupuestosContextUpdate } = useContext(
    PresupuestoContext
  );

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
        <StatusFooter {...props} />
      </Jumbotron>
    </>
  );
}

export function Acceptance(props) {
  const { presupuestosContextGet, presupuestosContextUpdate } = useContext(
    PresupuestoContext
  );

  const { session } = useContext(SessionContext);

  const [additionalNotes, setAdditionalNotes] = useState({});
  const [adicionalesCheckBox, setAdicionalesCheckBox] = useState(false);
  const [insumosCheckBox, setInsumosCheckBox] = useState(false);

  const handleChanges = (event) => {
    console.log(event.target.name);
    console.log(event.target.value);
    additionalNotes[event.target.name] = event.target.value;

    setAdditionalNotes({
      ...additionalNotes,
      [event.target.name]: event.target.value,
    });
  };

  const handleSubmit = () => {
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

    //TODO: Submit Presupuesto
    //TODO: Presupuestos Listados por Usuarios Finales
    //TODO: Servicios Listados por Usuarios Prestadores
    console.log(requestObject);
  };

  return (
    <>
      <Jumbotron>
        <div className="display-4 mb-4">Datos Adicionales</div>
        <AdditionalNotes
          onHandleChange={handleChanges}
          adicionales={adicionalesCheckBox}
          setAdicionales={setAdicionalesCheckBox}
          insumos={insumosCheckBox}
          setInsumos={setInsumosCheckBox}
        />
        <StatusFooter {...props} onSubmit={handleSubmit} />
      </Jumbotron>
    </>
  );
}
