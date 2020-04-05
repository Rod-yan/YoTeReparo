import React from "react";
import Spinner from "../Utils/Spinner";
import MultiButton from "./MultiButton";

export const ButtonSave = (props) => {
  if (props.updatingUser) {
    return (
      <button
        type="button"
        className="btn btn-success btn-block"
        onClick={props.activateSave}
      >
        <Spinner></Spinner>
      </button>
    );
  } else {
    if (!props.modify) {
      return (
        <MultiButton
          action={props.activateOnSave}
          styleButton="btn btn-success btn-block"
          type="fas fa-save fa-2x"
        ></MultiButton>
      );
    } else {
      return (
        <MultiButton
          action={props.activateOnEdit}
          styleButton="btn btn-danger btn-block"
          type="fas fa-edit fa-2x"
        ></MultiButton>
      );
    }
  }
};
