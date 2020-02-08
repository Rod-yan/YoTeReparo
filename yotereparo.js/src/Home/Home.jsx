import React from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import ElementContainer from "../Container/ElementContainer";
import { useState, useEffect } from "react";
import Axios from "axios";

function Home(props) {
  const [users, setUsers] = useState([]);

  useEffect(() => {
    async function fetchData() {
      const result = await Axios("http://localhost:8080/yotereparo/users/");
      setUsers(result.data);
    }
    fetchData();
  }, []);

  if (Array.isArray(users) && users.length) {
    console.log(users);
  }

  return (
    <div className="row">
      <div className="col-12">
        <ElementContainer>
          Welcome to <strong>YoTeReparo</strong>
        </ElementContainer>
      </div>
    </div>
  );
}

export default Home;
