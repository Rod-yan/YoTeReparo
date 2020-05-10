import React from "react";

export const convertToStars = (number) => {
  if (number == null) {
    return "Ningun usuario valoro a este servicio todavia";
  } else {
    let half = false;

    if (number % 1 > 0) {
      let digit = number % 1;
      if (digit >= 0.5) {
        half = true;
      }
    }

    let list = [];

    for (let index = 0; index < number - 1; index++) {
      list.push(<i key={index} className="fas fa-star"></i>);
    }

    if (half === true) {
      list.push(<i key={list.length} className="fas fa-star-half-alt"></i>);
    }

    console.log(list);
    return list;
  }
};
