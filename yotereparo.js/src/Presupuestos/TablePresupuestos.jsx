import React, { useState } from "react";
import ElementContainer from "../Container/ElementContainer";
import { useContext } from "react";
import { SessionContext } from "../Utils/SessionManage";
import { Table } from "reactstrap";
import { useEffect } from "react";
import Axios from "axios";

function TablePresupuestos(props) {
  const { session } = useContext(SessionContext);
  const [tableData, setTableData] = useState([]);

  let requestConfig = {
    headers: {
      "Access-Control-Allow-Origin": "*",
      Authorization: "Bearer " + session.security?.accessToken,
    },
  };

  useEffect(() => {
    const fetchData = async (urlToFetch) => {
      await Axios(urlToFetch, requestConfig)
        .then((resp) => {
          setTableData([resp.data]);
        })
        .catch((error) => {
          return error;
        });
    };
    try {
      props.prestador === true
        ? fetchData(`http://localhost:8080/YoTeReparo/quotes?userRole=provider`)
        : fetchData(
            `http://localhost:8080/YoTeReparo/quotes?userRole=customer`
          );
    } catch (error) {
      console.log(error.response);
    }
  }, [session.username, props.match.params.userId]);

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
              {tableData.map((item, idx) => {
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

export default TablePresupuestos;
