import React from "react";

function CardElement(props) {
  return (
    <div className="container">
      <div className="container-child">
        <div className="card card-element">
          <div className="row">
            <div className="col-12">{props.children}</div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default CardElement;
