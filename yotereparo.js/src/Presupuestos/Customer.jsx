import React from "react";
import ElementContainer from "../Container/ElementContainer";
import { Table, UncontrolledTooltip } from "reactstrap";
import { renderQuoteState, getStates } from "../Utils/EstadosPresupuesto";
import { useEffect } from "react";
import { useState } from "react";
import { fetchData } from "../Utils/SessionHandlers";
import { getTitleService, getPrestador } from "../Utils/GetFromService";

function Customer(props) {
  const [services, setServices] = useState([]);

  const [tooltipOpen, setTooltipOpen] = useState(false);

  const toggle = () => setTooltipOpen(!tooltipOpen);

  useEffect(() => {
    fetchData("/YoTeReparo/services", setServices);
  }, []);

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
        <div className="table table-responsive table-radius table-hover">
          <Table>
            <thead className="text-left thead-dark">
              <tr>
                <th className="text-center">Servicio</th>
                <th className="text-center">Prestador</th>
                <th className="text-center">Descripcion</th>
                <th className="text-center">Respuesta</th>
                <th className="text-center">Presupuesto</th>
                <th className="text-center">Estado</th>
                <th className="text-center">Aceptar</th>
                <th className="text-center">Rechazar</th>
              </tr>
            </thead>
            <tbody>
              {[...props.tableDataCustomer]
                .sort((a, b) => a.servicio >= b.servicio)
                .map((item, idx) => {
                  let {
                    rejectedQuote,
                    acceptedQuote,
                    waitingForProvider,
                  } = getStates(item);
                  let existQuote = item.contrato !== null ? true : false;

                  return (
                    <tr key={idx}>
                      <td className="text-center bg-info">
                        {getTitleService(item.servicio, services)}
                      </td>
                      <td className="text-center">
                        {getPrestador(item.servicio, services)}
                      </td>
                      <td className="text-center">
                        {item.descripcionSolicitud}
                      </td>
                      <td className="text-center">
                        {item.descripcionRespuesta}
                      </td>
                      <td className="text-center">
                        {item.precioPresupuestado}
                      </td>
                      <td className="text-center">
                        <UncontrolledTooltip
                          placement="right"
                          delay={{ show: 50, hide: 0 }}
                          target={"id" + idx + "presupuesto"}
                        >
                          {item.estado}
                        </UncontrolledTooltip>
                        <div id={"id" + idx + "presupuesto"}>
                          {renderQuoteState(item.estado)}
                        </div>
                      </td>
                      {existQuote ? (
                        <>
                          <td>
                            <button
                              onClick={() =>
                                props.showContract(item.contrato, false)
                              }
                              className="btn btn-primary btn-block"
                            >
                              <i className="fas fa-angle-double-right fa-1x"></i>
                            </button>
                          </td>
                          <td>
                            <button
                              onClick={() => props.rejectQuote(item.id)}
                              className="btn btn-danger btn-block"
                              disabled={
                                rejectedQuote ||
                                acceptedQuote ||
                                waitingForProvider
                              }
                            >
                              <i className="fas fa-thumbs-down fa-1x"></i>
                            </button>
                          </td>
                        </>
                      ) : (
                        <>
                          <td className="text-center">
                            <button
                              onClick={() => props.acceptQuote(item.id)}
                              className="btn btn-success btn-block"
                              disabled={
                                rejectedQuote ||
                                acceptedQuote ||
                                waitingForProvider
                              }
                            >
                              <i className="fas fa-thumbs-up fa-1x"></i>
                            </button>
                          </td>
                          <td>
                            <button
                              onClick={() => props.rejectQuote(item.id)}
                              className="btn btn-danger btn-block"
                              disabled={
                                rejectedQuote ||
                                acceptedQuote ||
                                waitingForProvider
                              }
                            >
                              <i className="fas fa-thumbs-down fa-1x"></i>
                            </button>
                          </td>
                        </>
                      )}
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
