import React from "react";

const UnicoServicio = props => (
  <>
    <div className="row">
      <div className="col-12">
        <div className="card mb-4">
          <div className="card card-element">
            <div className="row no-gutters">
              <div className="col-md-4">
                <img
                  src="https://via.placeholder.com/150/92c952"
                  className="card-img"
                  alt="placeholder"
                ></img>
              </div>
              <div className="col-md-8 ">
                <div className="card-body">
                  <h3 className="card-title">{props.data.title}</h3>
                  <div className="card-text">
                    <p>{props.data.body}</p>
                  </div>
                  <a
                    href={"/servicio/" + props.data.id}
                    className="btn btn-dark btn-block"
                  >
                    VER SERVICIO
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </>
);

export default UnicoServicio;
