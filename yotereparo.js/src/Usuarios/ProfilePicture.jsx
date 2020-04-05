import React from "react";

function ProfilePicture(props) {
  return (
    <>
      <img
        src={props.userPicture}
        className="card-img img-thumbnail rounded-circle on-profile-click"
        alt="placeholder"
        key={Date.now()}
        ref={props.profilePicture}
        onClick={props.callbackClick}
      ></img>
      <input
        type="file"
        name="file"
        accept="image/*"
        ref={props.fileUploader}
        onChange={props.updatePicture}
        hidden
      ></input>
    </>
  );
}

export default ProfilePicture;
