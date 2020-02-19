import React, { useState, useEffect } from "react";
import UserList from "../Users/UserList";
import Axios from "axios";
import Hoc from "../Utils/Hoc";
import ElementContainer from "../Container/ElementContainer";

function FindHome(props) {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchData = async urlToFetch => {
    const result = await Axios(urlToFetch);
    setUsers(result.data);
  };

  useEffect(() => {
    try {
      fetchData("https://jsonplaceholder.typicode.com/posts");
    } catch (error) {
      console.log(error);
    }
    setLoading(false);
  }, [loading]);

  const UserData = {
    users: users,
    loading: loading
  };

  const Users = Hoc(UserList, UserData);

  return (
    <ElementContainer>
      <div className="card-center-form d-flex align-items-center mx-auto">
        <div className="row">
          <div className="col-xs-12">
            <Users></Users>
          </div>
        </div>
      </div>
    </ElementContainer>
  );
}

export default FindHome;
