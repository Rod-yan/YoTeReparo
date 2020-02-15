import React from "react";
import CardContent from "../Container/CardContent";

function About(props) {
  return (
    <div className="centered">
      <div className="row">
        <div className="col-12">
          <CardContent
            cardIcon="fa-hands-helping"
            cardDescription="About page of <strong>YoTeReparo.js</strong>"
            cardUrl="/buscar"
            cardButtonText="BUSCAR AYUDA"
          />
        </div>
      </div>
    </div>
  );
}

export default About;
