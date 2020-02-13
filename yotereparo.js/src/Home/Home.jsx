import React from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import ElementContainer from "../Container/ElementContainer";
import "../Home/Home.css";
import CardElement from "../Container/Card";

function Home(props) {
  return (
    <div className="centered">
      <div className="row">
        <div className="col-md-6">
          {" "}
          <CardElement>
            <div className="row">
              <div className="col-12">
                <div className="home-icon-logo text-center">
                  <span class="fa-stack fa-2x">
                    <i class="fas fa-circle fa-stack-2x"></i>
                    <i class="fas fa-hands-helping fa-stack-1x home-icon-color"></i>
                  </span>
                </div>
                <div className="home-text-welcome">
                  Si estas buscando solucionar <strong>tus</strong> problemas...
                </div>
                <div className="text-center">
                  <button type="button" className="btn btn-danger">
                    <strong>BUSCAR AYUDA</strong>
                  </button>
                </div>
              </div>
            </div>
          </CardElement>
        </div>

        <div className="col-md-6">
          <CardElement>
            <div className="row">
              <div className="col-12">
                <div className="home-icon-logo text-center">
                  <span class="fa-stack fa-2x">
                    <i class="fas fa-circle fa-stack-2x"></i>
                    <i class="fas fa-tools fa-stack-1x home-icon-color"></i>
                  </span>
                </div>
                <div className="home-text-welcome">
                  Si estas buscando solucionar{" "}
                  <strong>problemas de otras personas </strong>...
                </div>
                <div className="text-center">
                  <button type="button" className="btn btn-danger">
                    <strong>ENCONTRAR TRABAJOS</strong>
                  </button>
                </div>
              </div>
            </div>
          </CardElement>
        </div>
      </div>
    </div>
  );
}

export default Home;
