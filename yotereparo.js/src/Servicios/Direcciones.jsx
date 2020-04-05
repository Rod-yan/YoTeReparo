import React from "react";
import { Button, Modal, ModalHeader, ModalBody, ModalFooter } from "reactstrap";
import ElementContainer from "../Container/ElementContainer";
import ResourceNotFound from "../Errors/ResourceNotFound";

function Direcciones(props) {
  return (
    <div>
      <Modal isOpen={props.address} toggle={props.toggleAddress}>
        <ModalHeader toggle={props.toggleAddress}>
          {" "}
          Direcciones del usuario {props.profile.nombre}
        </ModalHeader>
        <ModalBody>
          {props.errors ? (
            <ResourceNotFound errorMessage="Estas intentado ingresar una direccion erronea"></ResourceNotFound>
          ) : (
            <ElementContainer>
              {props.profile.direcciones.length > 0 ? (
                props.profile.direcciones.map((item, idx) => {
                  return (
                    <div key={idx}>
                      <div className="input-group mb-3">
                        <div className="input-group-prepend">
                          <span
                            className="input-group-text"
                            id="inputGroup-sizing-default"
                          >
                            Calle
                          </span>
                        </div>
                        <input
                          className="form-control"
                          defaultValue={item.calle}
                          disabled={true}
                        />
                      </div>
                      <div className="input-group mb-3">
                        <div className="input-group-prepend">
                          <span
                            className="input-group-text"
                            id="inputGroup-sizing-default"
                          >
                            Altura
                          </span>
                        </div>
                        <input
                          className="form-control"
                          defaultValue={item.altura}
                          disabled={true}
                        />
                      </div>
                      <div className="input-group mb-3">
                        <div className="input-group-prepend">
                          <span
                            className="input-group-text"
                            id="inputGroup-sizing-default"
                          >
                            Piso
                          </span>
                        </div>
                        <input
                          className="form-control"
                          defaultValue={item.piso}
                          disabled={true}
                        />
                      </div>
                      <div className="input-group mb-3">
                        <div className="input-group-prepend">
                          <span
                            className="input-group-text"
                            id="inputGroup-sizing-default"
                          >
                            Departamento
                          </span>
                        </div>
                        <input
                          className="form-control"
                          defaultValue={item.departamento}
                          disabled={true}
                        />
                      </div>
                      <div className="input-group mb-3">
                        <div className="input-group-prepend">
                          <span
                            className="input-group-text"
                            id="inputGroup-sizing-default"
                          >
                            Descripcion
                          </span>
                        </div>
                        <input
                          className="form-control"
                          defaultValue={item.descripcion}
                          disabled={true}
                        />
                      </div>
                      <Button onClick={props.ModifyOne}>Modificar</Button>
                    </div>
                  );
                })
              ) : (
                <div className="text-center">
                  Actualmente no posee direcciones cargadas{" "}
                  <Button onClick={props.AddOne}>Agregar una!</Button>
                </div>
              )}
            </ElementContainer>
          )}
        </ModalBody>
        <ModalFooter>
          <Button color="primary" onClick={props.CreateCallback}>
            Aceptar
          </Button>{" "}
          <Button color="secondary" onClick={props.toggleAddress}>
            Cerrar
          </Button>
        </ModalFooter>
      </Modal>
    </div>
  );
}

export default Direcciones;
