import React from "react";
import "../Header/Header.css";

const Header = props => {
  return (
    <div className="navbar">
      <nav>{props.children}</nav>
    </div>
  );
};

export default Header;
