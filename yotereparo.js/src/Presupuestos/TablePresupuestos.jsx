import React, { useState } from "react";
import { useContext } from "react";
import { SessionContext } from "../Utils/SessionManage";
import { useEffect } from "react";
import Axios from "axios";
import Provider from "./Provider";
import Customer from "./Customer";
import { useLocation } from "react-router-dom";
import { putData } from "../Utils/SessionHandlers";

export const ESTADOS = {
  RECHAZADO_USUARIO_PRESTADOR: "rejectprovider",
  RECHAZADO_USUARIO_FINAL: "rejectcustomer",
  ESPERANDO_USUARIO_PRESTADOR: "waitingprovider",
  ESPERANDO_USUARIO_FINAL: "waitingcustomer",
};

export const renderQuoteState = (state) => {
  switch (ESTADOS[state]) {
    case "rejectprovider":
      return (
        <>
          <i className="fas fa-ban fa-2x"></i>
        </>
      );
    case "rejectcustomer":
      return (
        <>
          <i className="fas fa-ban fa-2x"></i>
        </>
      );
    case "waitingprovider":
      return (
        <>
          <i className="fas fa-pause-circle fa-2x"></i>
        </>
      );
    case "waitingcustomer":
      return (
        <>
          <i className="fas fa-pause-circle fa-2x"></i>
        </>
      );
    default:
      break;
  }
};

function TablePresupuestos(props) {
  const { session } = useContext(SessionContext);
  const location = useLocation();
  const [tableDataCustomer, setTableDataCustomer] = useState([]);
  const [tableDataProvider, setTableDataProvider] = useState([]);
  const [loading, setLoading] = useState(false);

  let requestConfig = {
    headers: {
      "Access-Control-Allow-Origin": "*",
      Authorization: "Bearer " + session.security?.accessToken,
    },
  };

  const callbackToRender = (response) => {
    setLoading(!loading);
    console.log(response);
  };

  const rejectQuote = (idQuote) => {
    let rejectFor =
      props.prestador === true || location.state?.prestador === true
        ? "provider"
        : "customer";
    putData(
      `http://localhost:8080/YoTeReparo/quotes/${idQuote}/reject/${rejectFor}`,
      requestConfig,
      callbackToRender
    );
  };

  //From the customer
  const acceptQuote = (idQuote) => {
    putData(
      `http://localhost:8080/YoTeReparo/quotes/${idQuote}/accept`,
      requestConfig,
      callbackToRender
    );
  };

  //Response from the provider to the customer
  const responseQuote = (idQuote, idServicio) => {
    console.log(idQuote, idServicio);
  };

  //From the provider
  const archiveQuote = (idQuote) => {
    putData(
      `http://localhost:8080/YoTeReparo/quotes/${idQuote}/archive`,
      requestConfig,
      callbackToRender
    );
  };

  useEffect(() => {
    const fetchData = async (urlToFetch, callback) => {
      await Axios(urlToFetch, requestConfig)
        .then((resp) => {
          callback(resp.data);
        })
        .catch((error) => {
          return error;
        });
    };

    try {
      if (props.prestador === true || location.state?.prestador === true) {
        fetchData(
          `http://localhost:8080/YoTeReparo/quotes?userRole=provider`,
          setTableDataProvider
        );
      }
      fetchData(
        `http://localhost:8080/YoTeReparo/quotes?userRole=customer`,
        setTableDataCustomer
      );
    } catch (error) {
      console.log(error.response);
    }
  }, [session.username, props.match.params.userId, loading]);

  console.log(tableDataProvider);
  console.log(tableDataCustomer);

  return (
    <>
      <Customer
        acceptQuote={acceptQuote}
        rejectQuote={rejectQuote}
        tableDataCustomer={tableDataCustomer}
      ></Customer>
      {props.prestador === true || location.state?.prestador === true ? (
        <Provider
          responseQuote={responseQuote}
          archiveQuote={archiveQuote}
          rejectQuote={rejectQuote}
          tableDataProvider={tableDataProvider}
        ></Provider>
      ) : (
        <></>
      )}
    </>
  );
}

export default TablePresupuestos;
