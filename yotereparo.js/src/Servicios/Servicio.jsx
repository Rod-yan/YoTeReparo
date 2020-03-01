import React from "react";
import ElementContainer from "../Container/ElementContainer";
import { useLocation, useHistory } from "react-router-dom";

const Servicio = props => {
  let location = useLocation();
  let history = useHistory();

  const body = location.state.body;
  const title = location.state.title;

  return (
    <ElementContainer>
      <div className="row">
        <div className="col-12">
          <div className="card mb">
            <div className="card card-element">
              <div className="row no-gutters">
                <div className="col-md-4 my-auto">
                  <img
                    src="https://via.placeholder.com/150/92c952"
                    className="card-img rounded-circle"
                    alt="placeholder"
                  ></img>
                </div>
                <div className="col-md-8 text-right">
                  <div className="card-body">
                    <div className="row">
                      <div className="col-md-12">
                        <h3 className="card-title">{props.match.params.id}</h3>
                        <h3 className="card-title"> {title}</h3>
                        {body}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div className="row mx-auto">
                <div className="col-md-12">
                  {" "}
                  <div
                    className="btn btn-info ml-2 mr-2"
                    onClick={() => {
                      history.go(-1);
                    }}
                  >
                    <i className="fas fa-chevron-circle-left fa-2x"></i>
                  </div>
                  <div
                    className="btn btn-success ml-2 mr-2"
                    onClick={() => {
                      console.log("MESSAGE: Contactar servicio");
                    }}
                  >
                    <i className="fas fa-concierge-bell fa-2x"></i>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </ElementContainer>
  );
};

export default Servicio;
