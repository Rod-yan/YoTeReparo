import React from "react";
import { Button, Modal, ModalHeader, ModalBody, ModalFooter } from "reactstrap";
import { useHistory } from "react-router-dom";

const ModalServicio = (props) => {
  const history = useHistory();

  const AskPresupuesto = () => {
    history.push({
      pathname: "/presupuestar",
      state: {
        presupuesto: props.properties,
      },
    });
  };

  return (
    <Modal isOpen={props.modal} toggle={props.toggle} size="xl">
      <ModalHeader toggle={props.toggle}>{props.properties.title}</ModalHeader>
      <ModalBody>
        Con el fin de que puedas obtener una solución adecuada y acorde a tus necesidades,
		te pedimos que revises atentamente los detalles del servicio antes de pedir un presupuesto.
		Tené en cuenta que, por el momento, deberás acordar con el prestador directamente
		para realizar el pago del servicio.
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
        <Button color="danger" onClick={AskPresupuesto}>
          PEDIR PRESUPUESTO
        </Button>{" "}
        <Button color="info" onClick={props.toggle}>
          Cancelar
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default ModalServicio;
