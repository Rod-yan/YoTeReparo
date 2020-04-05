import React from "react";
import ElementContainer from "../Container/ElementContainer";

const Loading = (props) => {
  return (
    <ElementContainer>
      <div>
        <div className="col d-flex justify-content-center">
          <div className="cover-screen">{props.loadingMessage}</div>
        </div>
      </div>
    </ElementContainer>
  );
};

export default Loading;
