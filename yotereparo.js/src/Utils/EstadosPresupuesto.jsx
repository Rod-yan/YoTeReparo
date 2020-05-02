import React from "react";

export function getStates(item, prestador = false) {
  let rejectedQuote =
    item.estado === ESTADOS.RECHAZADO_USUARIO_FINAL ||
    item.estado === ESTADOS.RECHAZADO_USUARIO_PRESTADOR
      ? true
      : false;
  let acceptedQuote =
    item.estado === ESTADOS.ACEPTADO_USUARIO_FINAL ||
    item.estado === ESTADOS.ACEPTADO_USUARIO_PRESTADOR
      ? true
      : false;
  let waitingForProvider =
    item.estado === ESTADOS.ESPERANDO_USUARIO_PRESTADOR ? true : false;
  let waitingForCustomer =
    item.estado === ESTADOS.ESPERANDO_USUARIO_FINAL ? true : false;
  return {
    rejectedQuote,
    acceptedQuote,
    waitingForProvider,
    waitingForCustomer,
  };
}

export const ESTADOS = {
  RECHAZADO_USUARIO_PRESTADOR: "RECHAZADO_USUARIO_PRESTADOR",
  RECHAZADO_USUARIO_FINAL: "RECHAZADO_USUARIO_FINAL",
  ESPERANDO_USUARIO_PRESTADOR: "ESPERANDO_USUARIO_PRESTADOR",
  ESPERANDO_USUARIO_FINAL: "ESPERANDO_USUARIO_FINAL",
  ACEPTADO_USUARIO_FINAL: "ACEPTADO_USUARIO_FINAL",
  ACEPTADO_USUARIO_PRESTADOR: "ACEPTADO_USUARIO_PRESTADOR",
};

export const renderQuoteState = (state) => {
  switch (ESTADOS[state]) {
    case "RECHAZADO_USUARIO_PRESTADOR":
      return (
        <>
          <i className="fas fa-ban fa-2x"></i>
        </>
      );
    case "RECHAZADO_USUARIO_FINAL":
      return (
        <>
          <i className="fas fa-ban fa-2x"></i>
        </>
      );
    case "ESPERANDO_USUARIO_PRESTADOR":
      return (
        <>
          <i className="fas fa-pause-circle fa-2x"></i>
        </>
      );
    case "ESPERANDO_USUARIO_FINAL":
      return (
        <>
          <i className="fas fa-pause-circle fa-2x"></i>
        </>
      );
    case "ACEPTADO_USUARIO_FINAL":
    case "ACEPTADO_USUARIO_PRESTADOR":
      return (
        <>
          <i className="fas fa-check-circle fa-2x"></i>
        </>
      );
    default:
      break;
  }
};
