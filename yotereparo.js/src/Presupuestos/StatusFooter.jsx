import React from "react";
import { Button } from "reactstrap";
import { useHistory } from "react-router-dom";

function StatusFooter(props) {
  const history = useHistory();

  return (
    <>
      <div className="lead mt-3">
        <Button
          color="danger"
          block={true}
          className="my-auto pb-4 pt-4"
          onClick={() => {
            if (props.currentStep === 3) {
              props.onSubmit();
            } else {
              props.nextStep();
            }
          }}
          disabled={!props.validateSubmit || false}
        >
          <i className="fas fa-chevron-circle-right fa-2x"></i>
        </Button>
        <Button
          color="info"
          block={true}
          className="mt-2"
          onClick={() => {
            if (props.currentStep === 1) {
              history.go(-1);
            } else {
              props.previousStep();
            }
          }}
        >
          <i className="fas fa-chevron-circle-left fa-2x"></i>
        </Button>
      </div>
    </>
  );
}

export default StatusFooter;
