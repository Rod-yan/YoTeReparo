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
        <Form>
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
                min={props.minPrice}
                max={props.maxPrice}
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
            <div className="row">
              <div className="col-md-6">
                <InputGroup className="mb-2">
                  <InputGroupAddon addonType="prepend">
                    <InputGroupText>Insumos</InputGroupText>
                  </InputGroupAddon>
                  <Input
                    name="incluyeInsumos"
                    type="checkbox"
                    className="form-control btn-block"
                    defaultChecked={props.dataQuote.incluyeInsumos}
                    onChange={props.onQuoteChange}
                  />
                </InputGroup>
              </div>
              <div className="col-md-6">
                <InputGroup className="mb-2">
                  <InputGroupAddon addonType="prepend">
                    <InputGroupText>Adicionales</InputGroupText>
                  </InputGroupAddon>
                  <Input
                    name="incluyeAdicionales"
                    type="checkbox"
                    className="form-control btn-block"
                    defaultChecked={props.dataQuote.incluyeAdicionales}
                    onChange={props.onQuoteChange}
                  />
                </InputGroup>
              </div>
            </div>
          </ModalBody>
          <ModalFooter>
            <Button onClick={props.sendResponseQuote} color="danger">
              Enviar
            </Button>{" "}
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
