import React from "react";
import "../Header/Header.css";
import { Link } from "react-router-dom";
import imgSrcLogo from "../android-chrome-512x512.png";

const Header = (props) => {
  return (
    <div className="navbar">
      <nav>{props.children}</nav>
      <div className="stick-header">
        <Link to={"/"}>
          <img
            src={imgSrcLogo}
            alt="Logo YoTeReparo.js"
            className="icon-picture"
          />
        </Link>
      </div>
    </div>
  );
};

export default Header;
