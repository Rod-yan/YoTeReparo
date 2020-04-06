import React from "react";

const MultiButton = (props) => {
  return (
    <div>
      <button
        type="button"
        className={props.styleButton}
        onClick={props.action}
      >
        <i className={props.type}></i>
      </button>
    </div>
  );
};

export default MultiButton;
