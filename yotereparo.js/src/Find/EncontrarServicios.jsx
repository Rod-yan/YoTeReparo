import React, { useState, useContext, useEffect } from "react";
import ListaServicios from "../Servicios/ListaServicios";
import Axios from "axios";
import Hoc from "../Utils/Hoc";
import ElementContainer from "../Container/ElementContainer";
import "../Find/EncontrarServicios.css";
import FloatCreateButton from "../Utils/FloatCreateButton";
import { SessionContext } from "../Utils/SessionManage";

function EncontrarServicios(props) {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const { session } = useContext(SessionContext);

  if (!session.security) {
    props.history.push("/ingresar");
  }

  let requestConfig = {
    headers: {
      "Access-Control-Allow-Origin": "*",
      Authorization: "Bearer " + session.security?.accessToken,
    },
  };

  useEffect(() => {
    const fetchData = async (urlToFetch) => {
      const result = await Axios(urlToFetch, requestConfig);
      setUsers(result.data);
    };
    try {
      fetchData("http://localhost:8080/YoTeReparo/services/").then((resp) => {
        setLoading(false);
      });
    } catch (error) {
      console.log(error);
    }
  }, [loading]);

  const ServicesData = {
    users: users,
    loading: loading,
  };

  const Servicios = Hoc(ListaServicios, ServicesData);

  return (
    <ElementContainer>
      <Servicios></Servicios>
      <FloatCreateButton></FloatCreateButton>
    </ElementContainer>
  );
}

export default EncontrarServicios;
