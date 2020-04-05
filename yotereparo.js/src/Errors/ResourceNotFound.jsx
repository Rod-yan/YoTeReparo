import React from "react";
import ElementContainer from "../Container/ElementContainer";

const ResourceNotFound = (props) => {
  return (
    <ElementContainer>
      <div>
        <div className="col d-flex justify-content-center">
          <div className="cover-screen">{props.errorMessage}</div>
        </div>
      </div>
    </ElementContainer>
  );
};

export default ResourceNotFound;
