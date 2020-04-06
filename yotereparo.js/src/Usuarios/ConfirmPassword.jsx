import React from "react";
import { Button, Modal, ModalHeader, ModalBody, ModalFooter } from "reactstrap";
import ResourceNotFound from "../Errors/ResourceNotFound";

function ConfirmPassword(props) {
  return (
    <div>
      <Modal isOpen={props.modal} fade={false}>
        <ModalHeader toggle={props.toggle}>
          {" "}
          ¿Estas seguro que deseas confirmar tus cambios?
        </ModalHeader>
        <ModalBody>
          {props.errors ? (
            <ResourceNotFound errorMessage="La contraseña que estas intentando ingresar caduco o no es                     valida"></ResourceNotFound>
          ) : (
            <input
              type="password"
              placeholder="Ingresa tu contraseña de usuario"
              className="form-control btn-block"
              onChange={props.onChange}
              required
            />
          )}
        </ModalBody>
        <ModalFooter>
          <Button color="primary" onClick={props.validatePassword}>
            Confirmar
          </Button>{" "}
          <Button color="secondary" onClick={props.toggle}>
            Cerrar
          </Button>
        </ModalFooter>
      </Modal>
    </div>
  );
}

export default ConfirmPassword;
