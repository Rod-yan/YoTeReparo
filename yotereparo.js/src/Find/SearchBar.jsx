import React from "react";
import "./SearchBar.css";

export const Search = (props) => {
  return (
    <div className="text-center">
      <div className="search-bar">
        <i className="fas fa-search ml-2 mr-2 mb-2 mt-2"></i>
        <input
          className="border-bar"
          type="text"
          name="searchTerm"
          id="searchTerm"
          value={props.terms}
          onChange={props.onChange}
        />
      </div>
    </div>
  );
};
