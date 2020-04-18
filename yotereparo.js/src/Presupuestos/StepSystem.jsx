import React from "react";
import { Introduction, Discussion, Acceptance } from "./Modules";
import StepWizard from "react-step-wizard";

function StepSystem() {
  return (
    <StepWizard>
      <Introduction />
      <Discussion />
      <Acceptance />
    </StepWizard>
  );
}

export default StepSystem;
