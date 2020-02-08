import React from "react";

function ElementContainer(props) {
  return (
    <div className="card card-element">
      <div className="centerItems">
        <div className="text-center">{props.children}</div>
      </div>
    </div>
  );
}

export default ElementContainer;
