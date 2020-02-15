import React from "react";

function ElementContainer(props) {
  return (
    <div className="centerItems">
      <div className="card">
        <div className="card card-element">{props.children}</div>
      </div>
    </div>
  );
}

export default ElementContainer;
