import React, { useState } from "react";
import ElementContainer from "../Container/ElementContainer";
import { useContext } from "react";
import { SessionContext } from "../Utils/SessionManage";
import { Table } from "reactstrap";
import { useEffect } from "react";
import Axios from "axios";
import { useHistory } from "react-router-dom";
import NotAuth from "../Errors/NotAuth";

function TableServicios(props) {
  const { session } = useContext(SessionContext);
  const { history } = useHistory();
  const [auth, setAuth] = useState(false);
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
            setTableData(["No hay servicios disponibles para el usuario"]);
          } else {
            setTableData(resp.data);
          }
        })
        .catch((error) => {
          return error;
        });
    };
    try {
      if (session.security.roles.length <= 1) {
        setAuth(false);
      } else {
        setAuth(true);
      }
      fetchData(`/YoTeReparo/services?user=${session.username}`);
    } catch (error) {
      console.log(error.response);
    }
  }, [session.username, props.match.params.userId]);

  return (
    <>
      {!auth ? (
        <NotAuth></NotAuth>
      ) : (
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
                  <th>Visitar</th>
                </tr>
              </thead>
              <tbody>
                {tableData.length > 1 ? (
                  tableData.map((item, idx) => {
                    return (
                      <tr key={idx}>
                        <td>{item.titulo}</td>
                        <td>{item.descripcion}</td>
                        <td>{item.disponibilidad}</td>
                        <td>
                          <a
                            className="btn btn-danger btn-block"
                            href={`/servicio/${item.id}`}
                          >
                            <i className="fas fa-chevron-right fa-1x"></i>
                          </a>
                        </td>
                      </tr>
                    );
                  })
                ) : (
                  <></>
                )}
              </tbody>
            </Table>
          </div>
        </ElementContainer>
      )}
    </>
  );
}

export default TableServicios;
