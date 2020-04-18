import React from "react";
import { InputGroup, Input, InputGroupText, InputGroupAddon } from "reactstrap";

function SummaryPresupuesto(props) {
  return (
    <>
      <div className="lead">{props.presupuesto.body}</div>
      <div className="mb-2 mt-2" />
      <InputGroup>
        <InputGroupAddon addonType="prepend">
          <InputGroupText>Costo Estimado en $</InputGroupText>
        </InputGroupAddon>
        <Input disabled placeholder={props.presupuesto.averagePrice} />
      </InputGroup>
      <InputGroup>
        <InputGroupAddon addonType="prepend">
          <InputGroupText>Tiempo estimado</InputGroupText>
        </InputGroupAddon>
        <Input disabled placeholder={props.presupuesto.estimateTime} />
      </InputGroup>
      <InputGroup>
        <InputGroupAddon addonType="prepend">
          <InputGroupText>Disponibilidad</InputGroupText>
        </InputGroupAddon>
        <Input disabled placeholder={props.presupuesto.avaliable} />
      </InputGroup>
    </>
  );
}

export default SummaryPresupuesto;
