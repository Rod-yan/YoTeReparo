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
import ModalContrato from "../Contratos/ModalContrato";
import NotAuth from "../Errors/NotAuth";

function TablePresupuestos(props) {
  const { session } = useContext(SessionContext);
  const location = useLocation();
  const [tableDataCustomer, setTableDataCustomer] = useState([]);
  const [tableDataProvider, setTableDataProvider] = useState([]);
  const [loading, setLoading] = useState(false);
  const [quoteModal, setQuoteModal] = useState(false);
  const [contractModal, setContractModal] = useState(false);
  const [isProviderForContract, setIsProviderForContract] = useState(false);
  const [quote, setQuote] = useState({});
  const [modelResponseQuote, setModelResponseQuote] = useState({});
  const [contrato, setContrato] = useState({});
  const [auth, setAuth] = useState(false);

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
      `/YoTeReparo/quotes/${idQuote}/reject`,
      requestConfig,
      callbackToRender
    );
  };

  //From the customer
  const acceptQuote = (idQuote) => {
    putData(
      `/YoTeReparo/quotes/${idQuote}/accept`,
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

  const handleContractModal = () => {
    setContractModal(!contractModal);
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
      incluyeInsumos: modelResponseQuote.incluyeInsumos || quote.incluyeInsumos,
      incluyeAdicionales:
        modelResponseQuote.incluyeAdicionales || quote.incluyeAdicionales,
      fechaInicioEjecucionPropuesta: getFecha(
        quote.fechaInicioEjecucionPropuesta
      ),
      direccionUsuarioFinal: quote.direccionUsuarioFinal,
      estado: "ESPERANDO_USUARIO_FINAL",
    };

    if (quote.fechaFinEjecucionPropuesta != null) {
      requestObject = {
        ...requestObject,
        fechaFinEjecucionPropuesta: getFecha(quote.fechaFinEjecucionPropuesta),
      };
    }

    putData(
      `/YoTeReparo/quotes/${quote.id}`,
      requestConfig,
      callbackToRender,
      requestObject
    );

    setQuoteModal(false);
  };

  const showContract = (quoteId, isProvider) => {
    setContractModal(true);
    setIsProviderForContract(isProvider);
    fetchData(`/YoTeReparo/contracts/${quoteId}`, setContrato);
  };

  const onQuoteChange = (event) => {
    if (event.target.type == "checkbox") {
      modelResponseQuote[event.target.name] = event.target.checked;
      setModelResponseQuote({
        ...modelResponseQuote,
        [event.target.name]: event.target.checked,
      });
    } else {
      modelResponseQuote[event.target.name] = event.target.value;
      setModelResponseQuote({
        ...modelResponseQuote,
        [event.target.name]: event.target.value,
      });
    }
    console.log(modelResponseQuote);
  };

  const toggleQuoteModal = () => {
    setQuoteModal(!quoteModal);
  };

  //From the provider
  const archiveQuote = (idQuote) => {
    putData(
      `/YoTeReparo/quotes/${idQuote}/archive`,
      requestConfig,
      callbackToRender
    );
  };

  const rateContract = (idContract, rateObject) => {
    putData(
      `/YoTeReparo/contracts/${idContract}`,
      requestConfig,
      callbackToRender,
      rateObject
    );
  };

  const cancelContract = (idContract) => {
    putData(
      `/YoTeReparo/contracts/${idContract}/cancel`,
      requestConfig,
      callbackToRender
    );
    setContractModal(false);
  };

  const finalizeContract = (idContract) => {
    putData(
      `/YoTeReparo/contracts/${idContract}/finish`,
      requestConfig,
      callbackToRender
    );
    setContractModal(false);
  };

  const fetchData = async (urlToFetch, callback) => {
    await Axios(urlToFetch, requestConfig)
      .then((resp) => {
        callback(resp.data);
      })
      .catch((error) => {
        return error;
      });
  };

  useEffect(() => {
    try {
      if (props.prestador === true || location.state?.prestador === true) {
        fetchData(`/YoTeReparo/quotes?userRole=provider`, setTableDataProvider);
      }
      fetchData(`/YoTeReparo/quotes?userRole=customer`, setTableDataCustomer);
      if (session.security.roles.length <= 0) {
        setAuth(false);
      } else {
        setAuth(true);
      }
    } catch (error) {
      console.log(error.response);
    }
  }, [session.username, props.match.params.userId, loading]);

  if (auth) {
    return (
      <>
        <ModalRespuesta
          responseQuoteModal={quoteModal}
          openResponseModel={toggleQuoteModal}
          sendResponseQuote={sendResponseQuote}
          onQuoteChange={onQuoteChange}
          dataQuote={quote}
        />
        <ModalContrato
          isOpen={contractModal}
          cancelModal={handleContractModal}
          contrato={contrato}
          finalizeContract={finalizeContract}
          isProvider={isProviderForContract}
          cancelContract={cancelContract}
          rateContract={rateContract}
        />
        <Customer
          acceptQuote={acceptQuote}
          rejectQuote={rejectQuote}
          showContract={showContract}
          tableDataCustomer={tableDataCustomer}
        ></Customer>
        {props.prestador === true || location.state?.prestador === true ? (
          <Provider
            responseQuote={responseQuote}
            archiveQuote={archiveQuote}
            rejectQuote={rejectQuote}
            showContract={showContract}
            tableDataProvider={tableDataProvider}
          ></Provider>
        ) : (
          <></>
        )}
      </>
    );
  }
  return <NotAuth></NotAuth>;
}

export default TablePresupuestos;
