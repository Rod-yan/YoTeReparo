import * as React from "react";

export const InputField = (props) => {
  return (
    <div className="input-group mb-3">
      <div className="input-group-prepend">
        <span className="input-group-text" id="inputGroup-sizing-default">
          {props.fieldTitle}
        </span>
      </div>
      <input
        className="form-control"
        defaultValue={props.fieldValue}
        disabled={props.fieldActivate}
        onChange={props.fieldChange}
        type={props.type ? props.type : "text"}
        id={props.fieldId}
        min={props.min}
        max={props.max}
        required={props.required ? props.required : false}
      />
    </div>
  );
};
