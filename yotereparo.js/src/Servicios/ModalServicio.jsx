import React from "react";
import { Button, Modal, ModalHeader, ModalBody, ModalFooter } from "reactstrap";

const ModalServicio = (props) => {
  return (
    <Modal isOpen={props.modal} toggle={props.toggle} size="xl">
      <ModalHeader toggle={props.toggle}>{props.properties.title}</ModalHeader>
      <ModalBody>
        El servicio que esta por contratar no se relaciona directamente con
        nuestra organizacion. Te pedimos por favor, que denuncies todo usuario
        que no cumple con las normas de YoTeReparo.com para el bien tuyo y de la
        comunidad.
        <div className="table-responsive mt-4">
          <table className="table">
            <thead className="thead-dark">
              <tr>
                <th scope="col">Nombre del servicio</th>
                <th scope="col">Descripcion del servicio</th>
                <th scope="col">Proveedor</th>
                <th scope="col">Disponiblidad</th>
                <th scope="col">Tiempo Estimado</th>
                <th scope="col">Precio Promedio</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <th scope="row">{props.properties.title}</th>
                <td>{props.properties.body}</td>
                <td>{props.properties.provider}</td>
                <td>{props.properties.avaliable}</td>
                <td>{props.properties.estimateTime}</td>
                <td>{props.properties.averagePrice}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </ModalBody>
      <ModalFooter>
        <Button color="danger" onClick={props.toggle}>
          PEDIR PRESUPUESTO
        </Button>{" "}
        <Button color="info" onClick={props.toggle}>
          Cancel
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default ModalServicio;
