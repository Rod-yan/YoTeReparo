import React, { useEffect, useState } from "react";
import Axios from "axios";
import ElementContainer from "../Container/ElementContainer";
import SingleUser from "./SingleUser";

const UserList = props => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchData() {
      const result = await Axios("http://localhost:8080/yotereparo/users/");
      setUsers(result.data);
      setLoading(false);
    }
    fetchData();
  }, []);

  if (Array.isArray(users) && users.length) {
    console.log(users);
  }

  if (loading) {
    return (
      <ElementContainer>
        <div>
          <div className="col d-flex justify-content-center">
            <div className="cover-screen">Loading, please wait...</div>
          </div>
        </div>
      </ElementContainer>
    );
  }
  return (
    <ElementContainer>
      {users === undefined ? (
        <div className="col d-flex justify-content-center">
          <div className="cover-screen">No results.</div>
        </div>
      ) : (
        <div>
          {users.map(item => (
            <div>
              <SingleUser data={item}></SingleUser>
            </div>
          ))}
        </div>
      )}
    </ElementContainer>
  );
};

export default UserList;
