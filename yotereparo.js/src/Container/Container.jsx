import React from "react";
import "../Container/Container.css";

function Container(props) {
  return (
    <div className="container">
      <div className="row">
        <div className="col-12">{props.children}</div>
      </div>
    </div>
  );
}

export default Container;
