import React from "react";
import ElementContainer from "../Container/ElementContainer";
import { Table } from "reactstrap";

function Customer(props) {
  return (
    <>
      <ElementContainer>
        <div className="display-4">Mis Presupuestos</div>
        <hr className="my-4"></hr>
        <div className="table table-striped table-responsive">
          <Table>
            <thead className="text-left thead-dark">
              <tr>
                <th>Servicio</th>
                <th>Usuario Final</th>
                <th>Descripcion</th>
                <th>Estado</th>
              </tr>
            </thead>
            <tbody>
              {props.tableDataCustomer?.map((item, idx) => {
                return (
                  <tr key={idx}>
                    <td>{item.servicio}</td>
                    <td>{item.usuarioFinal}</td>
                    <td>{item.descripcionSolicitud}</td>
                    <td>{item.estado}</td>
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

export default Customer;
