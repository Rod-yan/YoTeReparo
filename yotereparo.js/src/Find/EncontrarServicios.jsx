import React, { useState, useContext, useEffect } from "react";
import ListaServicios from "../Servicios/ListaServicios";
import Axios from "axios";
import Hoc from "../Utils/Hoc";
import ElementContainer from "../Container/ElementContainer";
import "../Find/EncontrarServicios.css";
import FloatCreateButton from "../Utils/FloatCreateButton";
import { SessionContext } from "../Utils/SessionManage";
import { Search } from "./SearchBar";
import ResourceNotFound from "../Errors/ResourceNotFound";

function EncontrarServicios(props) {
  const [services, setServices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [errors, setError] = useState(false);
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
      try {
        const result = await Axios(urlToFetch, requestConfig);
        return result;
      } catch {
        setError(true);
      }
    };
    try {
      fetchData("/YoTeReparo/services/").then((resp) => {
        if (resp !== undefined) {
          setLoading(false);
          setError(false);
          setServices(resp.data);
        }
      });
    } catch {
      setError(true);
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

  const Servicios = Hoc(ListaServicios, {
    ...ServicesData,
    services: filteredServices,
  });

  const handleSearchTerm = (event) => {
    setSearchTerm(event.target.value);
  };

  return (
    <>
      {errors ? (
        <ResourceNotFound errorMessage="Hubo un error con la conexion."></ResourceNotFound>
      ) : (
        <div className="mb-5">
          <ElementContainer>
            <Search terms={searchTerm} onChange={handleSearchTerm} />
            <Servicios></Servicios>
            <FloatCreateButton></FloatCreateButton>
          </ElementContainer>
        </div>
      )}
    </>
  );
}

export default EncontrarServicios;
