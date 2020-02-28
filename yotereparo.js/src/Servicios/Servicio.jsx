import React from "react";
import ElementContainer from "../Container/ElementContainer";
import { useLocation } from "react-router-dom";

const Servicio = props => {
  let location = useLocation();

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
            </div>
          </div>
        </div>
      </div>
    </ElementContainer>
  );
};

export default Servicio;
