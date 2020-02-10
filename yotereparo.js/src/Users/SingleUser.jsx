import React from "react";
import ElementContainer from "../Container/ElementContainer";

const SingleUser = props => (
  <ElementContainer>
    <p>{props.data.nombre} </p>
    <p>{props.data.apellido} </p>
    <p>{props.data.email} </p>
    <p>{props.data.ciudad} </p>
  </ElementContainer>
);

export default SingleUser;
