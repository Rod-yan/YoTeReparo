import React from "react";
import {
  Button,
  Modal,
  ModalHeader,
  ModalBody,
  ModalFooter,
  InputGroup,
  InputGroupAddon,
  InputGroupText,
  Input,
  Form,
} from "reactstrap";

function ModalRespuesta(props) {
  return (
    <div>
      <Modal isOpen={props.responseQuoteModal} toggle={props.openResponseModel}>
        <Form onClick={props.sendResponseQuote}>
          <ModalHeader toggle={props.openResponseModel}>
            Enviar comentarios sobre el presupuesto
          </ModalHeader>
          <ModalBody>
            <InputGroup className="mb-2">
              <InputGroupAddon addonType="prepend">
                <InputGroupText>Comentarios</InputGroupText>
              </InputGroupAddon>
              <Input
                name="descripcionRespuesta"
                type="text"
                placeholder="Descripcion del Presupuesto"
                className="form-control btn-block"
                onChange={props.onQuoteChange}
                required
              />
            </InputGroup>
            <InputGroup className="mb-2">
              <InputGroupAddon addonType="prepend">
                <InputGroupText>Precio Presupuestado</InputGroupText>
              </InputGroupAddon>
              <Input
                name="precioPresupuestado"
                type="number"
                min="0"
                step="10.0"
                placeholder="Descripcion del Presupuesto"
                className="form-control btn-block"
                onChange={props.onQuoteChange}
                required
              />
              <InputGroupAddon addonType="append">
                <InputGroupText>$</InputGroupText>
              </InputGroupAddon>
            </InputGroup>
          </ModalBody>
          <ModalFooter>
            <Button color="danger">Enviar</Button>{" "}
            <Button color="secondary" onClick={props.openResponseModel}>
              Cancelar
            </Button>
          </ModalFooter>
        </Form>
      </Modal>
    </div>
  );
}

export default ModalRespuesta;
