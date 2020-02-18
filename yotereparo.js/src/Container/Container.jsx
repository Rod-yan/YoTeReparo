import React from "react";
import "../Container/Container.css";

function Container(props) {
  return (
    <div className="container-fluid">
      <div className="row">
        <div className="col-xs-12">{props.children}</div>
      </div>
    </div>
  );
}

export default Container;
