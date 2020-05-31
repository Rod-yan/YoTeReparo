import React from "react";

export const getPrestador = (idServicio, services) => {
  let service = services?.filter((x) => x.id === idServicio).shift();
  return <div>{service?.usuarioPrestador}</div>;
};

export const getTitleService = (idServicio, services) => {
  let service = services?.filter((x) => x.id === idServicio).shift();
  return <div>{service?.titulo}</div>;
};
