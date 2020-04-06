import React from "react";

const Errors = (props) => {
  return (
    <div>
      {props.formErrors.errors.length >= 1 ? (
        <div className="errors-list">
          {props.formErrors.errors.map((error, i) => (
            <p key={i} className="font-weight-light">
              <span className="fa-stack fa-1x">
                <i className={`fas fa-times fa-stack-1x`}></i>
              </span>{" "}
              {error.message}
            </p>
          ))}
        </div>
      ) : (
        <></>
      )}
    </div>
  );
};

export default Errors;
