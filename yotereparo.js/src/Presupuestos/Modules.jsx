import React from "react";
import { Jumbotron, CardSubtitle, CardTitle, CardText } from "reactstrap";
import { useContext } from "react";
import { PresupuestoContext } from "./Presupuestos";
import { SessionContext } from "../Utils/SessionManage";
import StatusFooter from "./StatusFooter";
import ElementContainer from "../Container/ElementContainer";
import SummaryPresupuesto from "./SummaryPresupuesto";

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
        <p className="lead">
          Al continuar con el proceso, comenzara el proceso para presupuestar el
          servicio de {presupuestosContextGet.provider}
        </p>
        <hr className="my-2" />
        <p className="text-center">¿Estas seguro que deseas continuar?</p>
      </Jumbotron>
      <StatusFooter {...props} />
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
        <h4>Presupuesto para {session.username}</h4>
        <p className="lead">
          De la mano de {presupuestosContextGet.provider} encargado de{" "}
          {presupuestosContextGet.body}
        </p>
        <hr className="my-4" />
        <ElementContainer>
          <CardTitle className="lead">
            <strong>Resumen de Servicio</strong>
          </CardTitle>
          <CardSubtitle>{presupuestosContextGet.title}</CardSubtitle>
          <CardText>
            <SummaryPresupuesto
              presupuesto={presupuestosContextGet}
            ></SummaryPresupuesto>
          </CardText>
        </ElementContainer>
        <div className="my-4" />
      </Jumbotron>
      <StatusFooter {...props} />
    </>
  );
}

export function Acceptance(props) {
  const { presupuestosContextGet, presupuestosContextUpdate } = useContext(
    PresupuestoContext
  );

  return (
    <>
      <StatusFooter {...props} />
    </>
  );
}
