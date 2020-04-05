import React from "react";
import { useContext } from "react";
import { SessionContext } from "./SessionManage";
import { useHistory } from "react-router-dom";

function FloatCreateButton(props) {
  var session = useContext(SessionContext);
  var history = useHistory();

  return (
    <>
      {session.security.roles.length > 0 ? (
        <div
          className="float-button"
          onClick={() => history.push("/servicio/crear")}
        >
          <div className="btn btn-danger btn-lg rounded-circle float-button">
            <span className="fa fa-plus fa-1x "></span>
          </div>
        </div>
      ) : (
        <></>
      )}
    </>
  );
}

export default FloatCreateButton;
