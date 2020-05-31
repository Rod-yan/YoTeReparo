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
  Alert,
} from "reactstrap";

function ModalRespuestaMensaje(props) {
  return (
    <div>
      <Modal isOpen={props.isReplyModalOpen} toggle={props.openReplyModal}>
        <Form>
          <ModalHeader toggle={props.openReplyModal}>
            Escriba una respuesta al usuario
          </ModalHeader>
          <ModalBody>
            {props.isErrors && (
              <Alert>Debes rellenar el campo de respuesta</Alert>
            )}
            <InputGroup className="mb-2">
              <InputGroupAddon addonType="prepend">
                <InputGroupText>Respuesta</InputGroupText>
              </InputGroupAddon>
              <Input
                name="respuesta"
                type="text"
                className="form-control btn-block"
                onChange={props.onChangeText}
                defaultValue={props.textRespuesta}
                required
              />
            </InputGroup>
          </ModalBody>
          <ModalFooter>
            <Button onClick={props.onModalReply} type="button" color="danger">
              Enviar
            </Button>{" "}
            <Button color="secondary" onClick={props.openReplyModal}>
              Cancelar
            </Button>
          </ModalFooter>
        </Form>
      </Modal>
    </div>
  );
}

export default ModalRespuestaMensaje;
