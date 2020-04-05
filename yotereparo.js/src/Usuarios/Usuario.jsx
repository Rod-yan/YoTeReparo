import * as React from "react";
import ElementContainer from "../Container/ElementContainer";
import { useContext } from "react";
import { ProfileContext, SessionContext } from "../Utils/SessionManage";
import { useEffect } from "react";
import { useRef } from "react";
import Axios from "axios";
import { useState } from "react";
import { toDataURL } from "../Utils/Images";
import ProfilePicture from "./ProfilePicture";
import ProfileInformation from "./ProfileInformation";
import Loading from "../Loading/Loading";

function Usuario(props) {
  const session = useContext(SessionContext);
  const profile = useContext(ProfileContext);
  const fileUploader = useRef(null);
  const profilePicture = useRef(null);
  const [changePicture, updateProfilePicture] = useState(0);
  const [pictureLoading, setPictureLoading] = useState(true);
  const [userPicture, setUserPicture] = useState(
    "https://api.adorable.io/avatars/216/yotereparo"
  );

  let requestConfig = {
    headers: {
      Authorization: "Bearer " + session.security.accessToken,
      "Content-Type": "application/json; charset=UTF-8",
    },
  };

  //const isPrestador = profile.membresia != null ? true : false;

  var keyPress = false;

  const handleChange = (event) => {
    profile[event.target.id] = event.target.value;
  };

  const handleKeyPress = (event) => {
    if (keyPress) {
    } else {
      let code = event.charCode || event.keyCode;

      //Manage the scape and the enter key
      if (
        code === 27 &&
        props.updatingUser === false &&
        props.modify === false
      ) {
        event.preventDefault();
        event.stopPropagation();
        props.activateEdit();
        keyPress = true;
      }
      if (code === 13 && props.modify === false) {
        event.preventDefault();
        event.stopPropagation();
        props.activateSave();
        keyPress = true;
      }
    }
  };

  const handleKeyRelease = () => {
    keyPress = false;
  };

  useEffect(() => {
    document.addEventListener("keydown", handleKeyPress);
    document.addEventListener("keyup", handleKeyRelease);
    return () => {
      document.removeEventListener("keydown", handleKeyPress);
      document.removeEventListener("keyup", handleKeyRelease);
    };
  });

  useEffect(() => {
    const fetchData = () => {
      Axios.get(
        `http://localhost:8080/YoTeReparo/users/${profile.id}/photo`,
        requestConfig
      )
        .then((response) => {
          if (response.status === 400) {
            setUserPicture("https://api.adorable.io/avatars/216/yotereparo");
          } else {
            toDataURL(
              `http://localhost:8080/YoTeReparo/users/${profile.id}/photo`,
              requestConfig
            ).then((dataUrl) => {
              setUserPicture(dataUrl);
              setPictureLoading(false);
            });
          }
        })
        .catch((error) => {
          console.error(error);
          setUserPicture("https://api.adorable.io/avatars/216/yotereparo");
          setPictureLoading(false);
        });
    };
    fetchData();
  }, [profile.id, changePicture, updateProfilePicture]);

  const updatePicture = (event) => {
    var file = event.target.files[0];
    var reader = new FileReader();
    let base64picture = null;

    reader.readAsBinaryString(file);

    reader.onload = () => {
      base64picture = btoa(reader.result);

      let requestPhoto = {
        foto: base64picture,
      };

      Axios.put(
        `http://localhost:8080/YoTeReparo/users/${profile.id}/photo`,
        requestPhoto,
        requestConfig
      )
        .then((response) => {
          if (response.status === 400) {
            console.log(response.json);
          } else {
            console.log("Imagen subida correctamente");
            toDataURL(
              `http://localhost:8080/YoTeReparo/users/${profile.id}/photo`,
              requestConfig
            ).then((dataUrl) => {
              setUserPicture(dataUrl);
              setPictureLoading(false);
            });
          }
        })
        .catch((error) => {
          console.log(error);
        });
    };
    reader.onerror = () => {
      throw new Error("There is a problem with the image that you are sending");
    };
  };

  return (
    <>
      {pictureLoading ? (
        <Loading loadingMessage="Cargando la informacion del usuario..."></Loading>
      ) : (
        <ElementContainer>
          <div className="d-flex align-items-center mx-auto">
            <div className="row">
              <div className="col-xs-12">
                <div className="row">
                  <div className="col-12">
                    <div className="card mb-2">
                      <div className="card card-element">
                        <div className="row no-gutters">
                          <div className="col-md-4 my-auto">
                            <ProfilePicture
                              userPicture={userPicture}
                              callbackClick={() => fileUploader.current.click()}
                              profilePicture={profilePicture}
                              fileUploader={fileUploader}
                              updatePicture={updatePicture}
                            ></ProfilePicture>
                          </div>
                          <div className="col-md-8 text-right">
                            <div className="card-body">
                              <div className="row">
                                <div className="col-md-12">
                                  <ProfileInformation
                                    profile={profile}
                                    handleChange={handleChange}
                                    modify={props.modify}
                                    modifyAddress={props.modifyAddress}
                                    activateSave={props.activateSave}
                                    activateEdit={props.activateEdit}
                                  ></ProfileInformation>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </ElementContainer>
      )}
    </>
  );
}

export default Usuario;
