import React from "react";
import {
  Button,
  Modal,
  ModalHeader,
  ModalBody,
  ModalFooter,
  InputGroup,
  InputGroupAddon,
  Badge,
  Input,
  Form,
  Label,
} from "reactstrap";

function ModalValoracion(props) {
  return (
    <div>
      <Modal isOpen={props.isOpen} toggle={props.toggle}>
        <Form onSubmit={props.callbackAction}>
          <ModalHeader toggle={props.toggle}>Valorar Contrato</ModalHeader>
          <ModalBody>
            <InputGroup className="mt-2 mb-2">
              <InputGroupAddon addonType="prepend">Puntaje</InputGroupAddon>
              <Input
                type="number"
                name="valoracion"
                min="1"
                max="10"
                required
                step="1"
                defaultValue={props.valoracion.puntaje}
                onChange={props.onChange}
              />
            </InputGroup>
            <InputGroup className="mt-2 mb-2">
              <InputGroupAddon addonType="prepend">Descripcion</InputGroupAddon>
              <Input
                required
                type="text"
                name="descripcionValoracion"
                maxLength="150"
                defaultValue={props.valoracion.desc}
                onChange={props.onChange}
              />
            </InputGroup>
          </ModalBody>
          <ModalFooter>
            <Button onClick={props.toggle} color="info">
              Cancelar
            </Button>
            <Button type="submit" color="danger">
              Valorar
            </Button>
          </ModalFooter>
        </Form>
      </Modal>
    </div>
  );
}

export default ModalValoracion;
