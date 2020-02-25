import React, { useState, useEffect } from "react";
import ListaServicios from "../Servicios/ListaServicios";
import Axios from "axios";
import Hoc from "../Utils/Hoc";
import ElementContainer from "../Container/ElementContainer";

function EncontrarServicios(props) {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchData = async urlToFetch => {
    const result = await Axios(urlToFetch);
    setUsers(result.data);
  };

  useEffect(() => {
    try {
      fetchData("https://jsonplaceholder.typicode.com/posts").then(resp => {
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

  const Servicios = Hoc(ListaServicios, ServicesData);

  return (
    <ElementContainer>
      <div className="card-center-form d-flex align-items-center mx-auto">
        <div className="row">
          <div className="col-xs-12">
            <Servicios></Servicios>
          </div>
        </div>
      </div>
    </ElementContainer>
  );
}

export default EncontrarServicios;
