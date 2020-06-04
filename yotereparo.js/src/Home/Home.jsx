import React from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import "../Home/Home.css";
import CardContent from "../Container/CardContent";

import * as teamWork from "../Utils/Animations/21332-teamwork.json";
import * as plumber from "../Utils/Animations/14525-plumbers.json";
import * as writer from "../Utils/Animations/21333-writer.json";

import { useState } from "react";
import { useEffect } from "react";
import { SessionContext } from "../Utils/SessionManage";
import { useContext } from "react";

const descriptionServices = (
  <div align="center">
    ¿Querés <strong>contratar o publicar servicios profesionales</strong> en tu
    zona?
  </div>
);
const descriptionHelp = (
  <div align="center">
    ¿Necesitas encontrar una <strong>solución rápida y accesible</strong> a tus
    problemas del hogar?
  </div>
);

const defaultOptionsPlumbers = {
  loop: true,
  autoplay: true,
  animationData: plumber.default,
  rendererSettings: {
    preserveAspectRatio: "xMidYMid meet",
  },
};

const defaultOptionsWorkers = {
  loop: true,
  autoplay: true,
  animationData: writer.default,
  rendererSettings: {
    preserveAspectRatio: "xMidYMid meet",
  },
};


function Home(props) {
  const { session } = useContext(SessionContext);
  const [auth, setAuth] = useState(false);

  useEffect(() => {
    if ( session.username === undefined ) 
    {setAuth(false);
    } else {
      setAuth(true);
    }
  }, []);


  if (!auth){
    return (
      <div className="centered">
        <div className="row">
          <div className="col-md-6">
            <CardContent
              animation={defaultOptionsPlumbers}
              cardIcon="fa-hands-helping"
              cardDescription={descriptionHelp}
              cardUrl="/buscar"
              cardButtonText="BUSCÁ SERVICIOS"
              cardButtonStyle="danger"
            />
          </div>
          <div className="col-md-6">
            <CardContent
              animation={defaultOptionsWorkers}
              cardIcon="fa-tools"
              cardDescription={descriptionServices}
              cardUrl="/registro"
              cardButtonText="REGISTRÁ TU USUARIO"
              cardButtonStyle="danger"
            />
          </div>
        </div>
      </div>
  );}
  return(
    <div className="centered">
        <div className="row">
          <div className="col-md-12">
            <CardContent
              animation={defaultOptionsPlumbers}
              cardIcon="fa-hands-helping"
              cardDescription={descriptionHelp}
              cardUrl="/buscar"
              cardButtonText="BUSCÁ SERVICIOS"
              cardButtonStyle="danger"
            />
          </div>
        </div>
    </div>
  )
}

export default Home;
