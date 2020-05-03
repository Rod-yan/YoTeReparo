import React, { useState } from "react";
import { useContext } from "react";
import { SessionContext } from "../Utils/SessionManage";
import { useEffect } from "react";
import Axios from "axios";
import Provider from "./Provider";
import Customer from "./Customer";
import { useLocation } from "react-router-dom";
import { putData } from "../Utils/SessionHandlers";
import ModalRespuesta from "./ModalRespuesta";

function TablePresupuestos(props) {
  const { session } = useContext(SessionContext);
  const location = useLocation();
  const [tableDataCustomer, setTableDataCustomer] = useState([]);
  const [tableDataProvider, setTableDataProvider] = useState([]);
  const [loading, setLoading] = useState(false);
  const [quoteModal, setQuoteModal] = useState(false);
  const [quote, setQuote] = useState({});
  const [modelResponseQuote, setModelResponseQuote] = useState({});

  let requestConfig = {
    headers: {
      "Access-Control-Allow-Origin": "*",
      Authorization: "Bearer " + session.security?.accessToken,
    },
  };

  const callbackToRender = () => {
    setLoading(!loading);
  };

  //From the customer
  const rejectQuote = (idQuote) => {
    putData(
      `http://localhost:8080/YoTeReparo/quotes/${idQuote}/reject`,
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

  const getFecha = (dateObject) => {
    return (
      dateObject.year +
      "-" +
      dateObject.monthOfYear +
      "-" +
      dateObject.dayOfMonth +
      "T" +
      dateObject.hourOfDay +
      ":" +
      dateObject.minuteOfHour +
      ":" +
      dateObject.secondOfMinute
    );
  };

  //Generate Response from the provider to the customer
  const responseQuote = (idQuote, idServicio) => {
    toggleQuoteModal();
    let quoteProvider = tableDataProvider
      .filter((x) => x.id === idQuote && x.servicio === idServicio)
      .shift();
    setQuote(quoteProvider);
  };

  const sendResponseQuote = () => {
    let requestObject = {
      servicio: quote.servicio,
      usuarioFinal: quote.usuarioFinal,
      descripcionRespuesta: modelResponseQuote.descripcionRespuesta,
      precioPresupuestado:
        modelResponseQuote.precioPresupuestado || quote.precioTotal,
      incluyeInsumos: quote.incluyeInsumos,
      incluyeAdicionales: quote.incluyeAdicionales,
      fechaInicioEjecucionPropuesta: getFecha(
        quote.fechaInicioEjecucionPropuesta
      ),
      fechaFinEjecucionPropuesta: getFecha(quote.fechaFinEjecucionPropuesta),
      direccionUsuarioFinal: quote.direccionUsuarioFinal,
      estado: "ESPERANDO_USUARIO_FINAL",
    };

    console.log(requestObject);

    putData(
      `http://localhost:8080/YoTeReparo/quotes/${quote.id}`,
      requestConfig,
      callbackToRender,
      requestObject
    );

    setQuoteModal(false);
  };

  const onQuoteChange = (event) => {
    modelResponseQuote[event.target.name] = event.target.value;
    setModelResponseQuote({
      ...modelResponseQuote,
      [event.target.name]: event.target.value,
    });
  };

  const toggleQuoteModal = () => {
    setQuoteModal(!quoteModal);
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

  return (
    <>
      <ModalRespuesta
        responseQuoteModal={quoteModal}
        openResponseModel={toggleQuoteModal}
        sendResponseQuote={sendResponseQuote}
        onQuoteChange={onQuoteChange}
      />
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
