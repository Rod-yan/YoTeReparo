import React, { useState } from "react";
import ElementContainer from "../Container/ElementContainer";
import { useContext } from "react";
import { SessionContext } from "../Utils/SessionManage";
import { Table } from "reactstrap";
import { useEffect } from "react";
import Axios from "axios";

function TableServicios(props) {
  const { session } = useContext(SessionContext);
  const [tableData, setTableData] = useState([]);

  let requestConfig = {
    headers: {
      "Access-Control-Allow-Origin": "*",
      Authorization: "Bearer" + session.security?.accessToken,
    },
  };

  useEffect(() => {
    const fetchData = async (urlToFetch) => {
      await Axios(urlToFetch, requestConfig)
        .then((resp) => {
          if (resp.status === 204) {
            setTableData("No hay servicios disponibles para el usuario");
          } else {
            setTableData(resp.data);
          }
        })
        .catch((error) => {
          return error;
        });
    };
    try {
      fetchData(
        `http://localhost:8080/YoTeReparo/services?user=${session.username}`
      );
    } catch (error) {
      console.log(error.response);
    }
  }, [session.username, props.match.params.userId]);

  return (
    <>
      <ElementContainer>
        <div className="display-4">Mis servicios</div>
        <hr className="my-4"></hr>
        <div className="table table-striped table-responsive">
          <Table>
            <thead className="text-left thead-dark">
              <tr>
                <th>Servicio</th>
                <th>Descripcion</th>
                <th>Disponibilidad</th>
                <th>Presupuestos</th>
              </tr>
            </thead>
            <tbody>
              {tableData.map((item) => {
                return (
                  <tr>
                    <td>{item.titulo}</td>
                    <td>{item.descripcion}</td>
                    <td>{item.disponibilidad}</td>
                    <td>
                      <button className="btn btn-danger btn-block">
                        <i className="fas fa-chevron-right fa-1x"></i>
                      </button>
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

export default TableServicios;
