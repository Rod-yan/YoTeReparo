import React, { useState } from "react";
import ElementContainer from "../Container/ElementContainer";
import { useContext } from "react";
import { SessionContext } from "../Utils/SessionManage";
import { Table } from "reactstrap";
import { useEffect } from "react";
import Axios from "axios";
import Provider from "./Provider";
import Customer from "./Customer";
import { useLocation } from "react-router-dom";

function TablePresupuestos(props) {
  const { session } = useContext(SessionContext);
  const location = useLocation();
  const [tableDataCustomer, setTableDataCustomer] = useState([]);
  const [tableDataProvider, setTableDataProvider] = useState([]);

  let requestConfig = {
    headers: {
      "Access-Control-Allow-Origin": "*",
      Authorization: "Bearer " + session.security?.accessToken,
    },
  };

  useEffect(() => {
    const fetchData = async (urlToFetch, callback) => {
      await Axios(urlToFetch, requestConfig)
        .then((resp) => {
          callback(resp.data);
        })
        .catch((error) => {
          return error;
        });
    };

    try {
      if (props.prestador === true || location.state?.prestador === true) {
        fetchData(
          `http://localhost:8080/YoTeReparo/quotes?userRole=provider`,
          setTableDataProvider
        );
      }
      fetchData(
        `http://localhost:8080/YoTeReparo/quotes?userRole=customer`,
        setTableDataCustomer
      );
    } catch (error) {
      console.log(error.response);
    }
  }, [session.username, props.match.params.userId]);

  return (
    <>
      <Customer tableDataCustomer={tableDataCustomer}></Customer>
      {props.prestador === true || location.state?.prestador === true ? (
        <Provider tableDataProvider={tableDataProvider}></Provider>
      ) : (
        <></>
      )}
    </>
  );
}

export default TablePresupuestos;
