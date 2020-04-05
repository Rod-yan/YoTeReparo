import * as Cookies from "js-cookie";
import React from "react";

export const setSessionCokie = (session) => {
  Cookies.remove("userSession");
  Cookies.set("userSession", session, { expires: 14 });
};

export const getSessionCookie = () => {
  const sessionCookie = Cookies.get("userSession");

  if (sessionCookie === undefined) {
    return {};
  } else {
    return JSON.parse(sessionCookie);
  }
};

export const deleteSessionCookie = (nameOfSession) => {
  try {
    Cookies.remove(nameOfSession);
  } catch (error) {
    throw new Error(error);
  }
};

export const SessionContext = React.createContext(getSessionCookie());
export const ProfileContext = React.createContext({});
