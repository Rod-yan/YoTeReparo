import React, { useState, useContext, useEffect } from "react";
import ListaServicios from "../Servicios/ListaServicios";
import Axios from "axios";
import Hoc from "../Utils/Hoc";
import ElementContainer from "../Container/ElementContainer";
import "../Find/EncontrarServicios.css";
import FloatCreateButton from "../Utils/FloatCreateButton";
import { SessionContext } from "../Utils/SessionManage";
import { Search } from "./SearchBar";

function EncontrarServicios(props) {
  const [services, setServices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState("");
  const { session } = useContext(SessionContext);

  if (!session.security || session.security === undefined) {
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
      setServices(result.data);
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
    services: services,
    loading: loading,
  };

  //TODO ADD A FILTER SERVICESDATA
  let filteredServices = ServicesData.services.filter((service) => {
    return (
      service.titulo.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1
    );
  });

  console.log(filteredServices);

  const Servicios = Hoc(ListaServicios, {
    ...ServicesData,
    services: filteredServices,
  });

  const handleSearchTerm = (event) => {
    setSearchTerm(event.target.value);
  };

  return (
    <div className="mb-5">
      <ElementContainer>
        <Search terms={searchTerm} onChange={handleSearchTerm} />
        <Servicios></Servicios>
        <FloatCreateButton></FloatCreateButton>
      </ElementContainer>
    </div>
  );
}

export default EncontrarServicios;
