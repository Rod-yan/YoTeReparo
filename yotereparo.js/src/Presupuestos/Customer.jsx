import React from "react";
import ElementContainer from "../Container/ElementContainer";
import { Table } from "reactstrap";
import { renderQuoteState } from "./TablePresupuestos";
import { useEffect } from "react";
import { useState } from "react";
import { fetchData } from "../Utils/SessionHandlers";

function Customer(props) {
  const [services, setServices] = useState([]);

  useEffect(() => {
    fetchData("http://localhost:8080/YoTeReparo/services", setServices);
  }, []);

  const getPrestador = (idServicio) => {
    let service = services.filter((x) => x.id === idServicio).shift();
    return <div>{service.usuarioPrestador}</div>;
  };

  return (
    <div className="mt-4 mb-5">
      <ElementContainer>
        <div className="lead">
          <div className="row">
            <div className="col-md-6">Presupuestos Realizados</div>
            <div className="col-md-6 ">
              <i className="fas fa-arrow-right fa-2x float-right"></i>
            </div>
          </div>
        </div>
        <hr className="my-4"></hr>
        <div className="table table-striped table-responsive">
          <Table>
            <thead className="text-left thead-dark">
              <tr>
                <th className="text-center">Servicio</th>
                <th className="text-center">Prestador</th>
                <th className="text-center">Usuario Final</th>
                <th className="text-center">Descripcion</th>
                <th className="text-center">Estado</th>
                <th className="text-center">Aceptar</th>
                <th className="text-center">Rechazar</th>
              </tr>
            </thead>
            <tbody>
              {[...props.tableDataCustomer]
                .sort((a, b) => a.servicio >= b.servicio)
                .map((item, idx) => {
                  let rejectedQuote =
                    item.estado === "RECHAZADO_USUARIO_FINAL" ||
                    item.estado === "RECHAZADO_USUARIO_PRESTADOR"
                      ? true
                      : false;
                  return (
                    <tr key={idx}>
                      <td className="text-center">{item.servicio}</td>
                      <td className="text-center">
                        {getPrestador(item.servicio)}
                      </td>
                      <td className="text-center">{item.usuarioFinal}</td>
                      <td className="text-center">
                        {item.descripcionSolicitud}
                      </td>
                      <td className="text-center">
                        {renderQuoteState(item.estado)}
                      </td>
                      <td className="text-center">
                        <button
                          onClick={() => props.acceptQuote(item.id)}
                          className="btn btn-success btn-block"
                          disabled={rejectedQuote}
                        >
                          <i className="fas fa-thumbs-up fa-1x"></i>
                        </button>
                      </td>
                      <td>
                        <button
                          onClick={() => props.rejectQuote(item.id)}
                          className="btn btn-danger btn-block"
                          disabled={rejectedQuote}
                        >
                          <i className="fas fa-thumbs-down fa-1x"></i>
                        </button>
                      </td>
                    </tr>
                  );
                })}
            </tbody>
          </Table>
        </div>
      </ElementContainer>
    </div>
  );
}

export default Customer;
