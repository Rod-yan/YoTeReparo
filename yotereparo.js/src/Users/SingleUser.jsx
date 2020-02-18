import React from "react";
import ElementContainer from "../Container/ElementContainer";

const SingleUser = props => (
  <>
    <div className="row">
      <div className="col-xs-12">
        <h1>{props.data.id} </h1>
        <p>{props.data.title} </p>
        <p>{props.data.completed} </p>
      </div>
    </div>
  </>
);

export default SingleUser;
