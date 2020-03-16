import React, { useState, useEffect } from "react";
import ListaServicios from "../Servicios/ListaServicios";
import Axios from "axios";
import Hoc from "../Utils/Hoc";
import ElementContainer from "../Container/ElementContainer";
import { Link } from "react-router-dom";
import "../Find/EncontrarServicios.css";
import { SessionContext } from "../Utils/SessionManage";
import { useContext } from "react";

function EncontrarServicios(props) {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchData = async urlToFetch => {
    const result = await Axios(urlToFetch);
    setUsers(result.data);
  };

  useEffect(() => {
    try {
      fetchData("http://localhost:8080/YoTeReparo/services/").then(resp => {
        setLoading(false);
      });
    } catch (error) {
      console.log(error);
    }
  }, [loading]);

  const ServicesData = {
    users: users,
    loading: loading
  };

  const session = useContext(SessionContext);
  const Servicios = Hoc(ListaServicios, ServicesData);

  return (
    <ElementContainer>
      <div className="card-center-form d-flex align-items-center mx-auto">
        <div className="row">
          <div className="col-xs-12">
            <Servicios></Servicios>
          </div>
          {session.username != null ? (
            <div className="float-button">
              <div className="btn btn-danger btn-lg rounded-circle float-button">
                <span className="fa fa-plus fa-1x ">
                  <Link to="/servicio/crear"></Link>
                </span>
              </div>
            </div>
          ) : (
            <></>
          )}
        </div>
      </div>
    </ElementContainer>
  );
}

export default EncontrarServicios;
