import React from "react";

const SingleUser = props => (
  <>
    <div className="row">
      <div className="col-12">
        <h1>{props.data.id} </h1>
        <p>{props.data.title} </p>
        <p>{props.data.completed} </p>
      </div>
    </div>
  </>
);

export default SingleUser;
