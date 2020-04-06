import React from "react";
import ElementContainer from "../Container/ElementContainer";

const Information = (props) => {
  return (
    <ElementContainer>
      <div>
        <div className="col d-flex justify-content-center">
          <div className="cover-screen">{props.informationMessage}</div>
        </div>
      </div>
    </ElementContainer>
  );
};

export default Information;
