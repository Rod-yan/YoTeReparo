import React from "react";
import ElementContainer from "../Container/ElementContainer";
import { useLocation, useHistory } from "react-router-dom";
import { useState } from "react";
import "../Servicios/Servicios.css";
import { useEffect } from "react";
import Axios from "axios";
import { useContext } from "react";
import { SessionContext } from "../Utils/SessionManage";
import ResourceNotFound from "../Errors/ResourceNotFound";
import Loading from "../Loading/Loading";
import ModalServicio from "./ModalServicio";

const Servicio = (props) => {
  let location = useLocation();
  let history = useHistory();
  let session = useContext(SessionContext);

  const [loading, setLoading] = useState(true);
  const [errorValidation, setErrors] = useState(null);
  const [modal, setModal] = useState(false);
  const [properties, setProperties] = useState({});

  useEffect(() => {
    if (location.state !== undefined) {
      setProperties({
        body: location.state.body,
        title: location.state.title,
        provider: location.state.provider,
        avaliable: location.state.avaliable,
        estimateTime: location.state.estimateTime,
        averagePrice: location.state.averagePrice,
      });
      setLoading(false);
    } else {
      const fetchData = async (urlToFetch) => {
        let requestConfig = {
          headers: {
            "Access-Control-Allow-Origin": "*",
            Authorization: "Bearer " + session.security.accessToken,
          },
        };

        const result = await Axios(urlToFetch, requestConfig)
          .then((resp) => {
            return resp;
          })
          .catch((error) => {
            return error;
          });
        return result;
      };
      fetchData(
        `http://localhost:8080/YoTeReparo/services/${props.match.params.id}`
      ).then((resp) => {
        setLoading(false);
        if (resp.response != null) {
          setErrors(resp.response.data);
        } else {
          console.log("aca");
          setProperties({
            body: resp.data.descripcion,
            title: resp.data.titulo,
            provider: resp.data.usuarioPrestador,
            avaliable: resp.data.disponibilidad,
            estimateTime: resp.data.horasEstimadasEjecucion,
            averagePrice: resp.data.precioPromedio,
          });
        }
      });
    }
  }, [location, props.match.params.id, session]); //if a loop is here check this session variable

  if (session.username === undefined) {
    history.push("/ingresar");
  }

  const toggle = () => setModal(!modal);

  if (errorValidation != null) {
    return (
      <ResourceNotFound
        errorMessage={errorValidation.defaultMessage}
      ></ResourceNotFound>
    );
  } else {
    if (loading) {
      return (
        <Loading loadingMessage="Cargando el servicio. Por favor espera."></Loading>
      );
    } else {
      return (
        <>
          <ElementContainer>
            <div className="row">
              <div className="col-12">
                <div className="card mb">
                  <div className="card card-element">
                    <div className="row no-gutters">
                      <div className="col-md-4 my-auto">
                        <img
                          src="https://via.placeholder.com/150/92c952"
                          className="card-img rounded-circle on-profile-click"
                          alt="placeholder"
                        ></img>
                      </div>
                      <div className="col-md-8 text-right">
                        <div className="card-body">
                          <div className="row">
                            <div className="col-md-12">
                              <h3 className="card-title">
                                {props.match.params.id}
                              </h3>
                              <h3 className="card-title">
                                {" "}
                                {properties.title}
                              </h3>
                              {properties.body}
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div className="row mx-auto">
                      <div className="col-md-12">
                        {" "}
                        <div
                          className="btn btn-info ml-2 mr-2"
                          onClick={() => {
                            history.go(-1);
                          }}
                        >
                          <i className="fas fa-chevron-circle-left fa-2x"></i>
                        </div>
                        <div
                          className="btn btn-danger ml-2 mr-2"
                          onClick={toggle}
                        >
                          <i className="fas fa-concierge-bell fa-2x"></i>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </ElementContainer>
          <ModalServicio
            toggle={toggle}
            modal={modal}
            properties={properties}
          ></ModalServicio>
        </>
      );
    }
  }
};

export default Servicio;
