import React from "react";

function CardElement(props) {
  return (
    <div className="mt-5">
      <div className="card">
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
