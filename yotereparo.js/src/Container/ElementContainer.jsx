import React from "react";

function ElementContainer(props) {
  return (
    <div className="card card-element">
      <div className="centerItems">{props.children}</div>
    </div>
  );
}

export default ElementContainer;
