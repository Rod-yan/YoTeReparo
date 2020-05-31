import CardElement from "./Card";
import React from "react";
import { Link } from "react-router-dom";
import Lottie from "react-lottie";

function CardContent(props) {
  return (
    <>
      <CardElement>
        <div className="home-icon-logo text-center">
          {props.animation != null ? (
            <Lottie options={props.animation} height={150} width={200} />
          ) : (
            <>
              <span className="fa-stack fa-2x">
                <i className="fas fa-circle fa-stack-2x"></i>
                <i
                  className={`fas ${props.cardIcon} fa-stack-1x home-icon-color`}
                ></i>
              </span>
              <hr></hr>
            </>
          )}
        </div>
        <div className="home-text-welcome">{props.cardDescription}</div>
        <div className="text-center">
          <Link to={props.cardUrl}>
            <button
              type="button"
              className={`btn btn-${props.cardButtonStyle}`}
            >
              <strong>{props.cardButtonText}</strong>
            </button>
          </Link>
        </div>
      </CardElement>
    </>
  );
}

export default CardContent;
