import React from "react";
import "../Container/Container.css";

function Container(props) {
  return (
    <div className={props.type}>
      <div className="centerItems">
        <div>{props.children}</div>
      </div>
    </div>
  );
}

export default Container;
