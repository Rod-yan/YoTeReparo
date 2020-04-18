import React from "react";
import { useContext } from "react";
import { PresupuestoContext } from "./Presupuestos";
import StepWizard from "react-step-wizard";

function StepSystem() {
  const { presupuestosContextGet } = useContext(PresupuestoContext);

  return <StepWizard></StepWizard>;
}

export default StepSystem;
