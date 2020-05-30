import React from "react";
import { FormText, Input, FormGroup, Button } from "reactstrap";

function MensajesForm(props) {
  return (
    <>
      <div className="row">
        <div className="col-12">
          <FormGroup>
            <Input
              type="textarea"
              name="text"
              disabled={props.disableForm}
              value={props.mensaje}
              placeholder="¿Te gustaría preguntarle algo al proveedor?"
              id="exampleText"
              rows="2"
              style={{ resize: "none" }}
              onChange={props.onChange}
              maxLength={200}
            />
          </FormGroup>
          <div className="row">
            <div className="col-6">
              <Button
                type="button"
                size="sm"
                onClick={props.onSubmit}
                color="danger"
              >
                Enviar Mensaje
              </Button>
            </div>
            <div className="col-6">
              <FormText color="muted" className="float-right">
                {props.numberCharLeft} / 200
              </FormText>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}

export default MensajesForm;
