import React from "react";
import ElementContainer from "../Container/ElementContainer";
import { Table } from "reactstrap";

function Provider(props) {
  return (
    <>
      <ElementContainer>
        <div className="display-4">Presupuestos Recibidos</div>
        <hr className="my-4"></hr>
        <div className="table table-striped table-responsive">
          <Table>
            <thead className="text-left thead-dark">
              <tr>
                <th>Servicio</th>
                <th>Usuario Final</th>
                <th>Descripcion</th>
                <th>Estado</th>
                <th>Aceptar</th>
                <th>Rechazar</th>
              </tr>
            </thead>
            <tbody>
              {props.tableDataProvider?.map((item, idx) => {
                return (
                  <tr key={idx}>
                    <td>{item.servicio}</td>
                    <td>{item.usuarioFinal}</td>
                    <td>{item.descripcionSolicitud}</td>
                    <td>{item.estado}</td>
                    <td>
                      <a className="btn btn-success btn-block">
                        <i className="fas fa-thumbs-up fa-1x"></i>
                      </a>
                    </td>
                    <td>
                      <a className="btn btn-danger btn-block">
                        <i className="fas fa-thumbs-down fa-1x"></i>
                      </a>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </Table>
        </div>
      </ElementContainer>
    </>
  );
}

export default Provider;
