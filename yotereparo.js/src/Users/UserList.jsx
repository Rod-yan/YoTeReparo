import React from "react";
import ElementContainer from "../Container/ElementContainer";
import SingleUser from "./SingleUser";

const UserList = props => {
  console.log(props.data);
  if (props.data.loading) {
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
    <>
      {props.data.users === undefined ? (
        <div className="col d-flex justify-content-center">
          <div className="cover-screen">No results.</div>
        </div>
      ) : (
        <div>
          {props.data.users.map((item, i) => (
            <div key={i}>
              <SingleUser data={item}></SingleUser>
            </div>
          ))}
        </div>
      )}
    </>
  );
};

export default UserList;
