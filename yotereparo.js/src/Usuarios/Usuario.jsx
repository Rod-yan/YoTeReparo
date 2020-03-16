import * as React from "react";
import ElementContainer from "../Container/ElementContainer";
import { InputField } from "../Utils/InputField";
import { useContext } from "react";
import { ProfileContext } from "../Utils/SessionManage";
import { useEffect } from "react";

function Usuario(props) {
  const profile = useContext(ProfileContext);

  var keyPress = false;

  const handleChange = event => {
    profile[event.target.id] = event.target.value;
  };

  const handleKeyPress = event => {
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

  const ButtonSave = () => {
    if (props.updatingUser) {
      return (
        <button
          type="button"
          className="btn btn-success btn-block"
          onClick={props.activateSave}
        >
          <div className="spinner-border" role="status">
            <span className="sr-only">Creando Usuario...</span>
          </div>
        </button>
      );
    } else {
      if (!props.modify) {
        return (
          <button
            type="button"
            className="btn btn-success btn-block"
            onClick={props.activateSave}
          >
            <i className="fas fa-save fa-2x"></i>
          </button>
        );
      } else {
        return (
          <button
            type="button"
            className="btn btn-danger btn-block"
            onClick={props.activateEdit}
          >
            <i className="fas fa-user-edit fa-2x"></i>
          </button>
        );
      }
    }
  };

  return (
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
                        <img
                          src="https://via.placeholder.com/150/92c952"
                          className="card-img rounded-circle on-profile-click"
                          alt="placeholder"
                          onClick={() => alert("Hello")}
                        ></img>
                      </div>
                      <div className="col-md-8 text-right">
                        <div className="card-body">
                          <div className="row">
                            <div className="col-md-12">
                              <h5>
                                {profile.roles.map(rol => {
                                  return (
                                    <span
                                      key={rol.id}
                                      className="badge badge-pill badge-danger mt-1 mb-1 mr-1 ml-1"
                                    >
                                      {rol.descripcion.toUpperCase()}
                                    </span>
                                  );
                                })}
                              </h5>
                              <InputField
                                fieldTitle="Username"
                                fieldValue={profile.id}
                                fieldActivate={true}
                                fieldChange={handleChange}
                                fieldId={"id"}
                              ></InputField>

                              <div className="card-text">
                                <InputField
                                  fieldTitle="Email"
                                  fieldValue={profile.email}
                                  fieldActivate={true}
                                  fieldChange={handleChange}
                                  fieldId={"email"}
                                ></InputField>
                                <InputField
                                  fieldTitle="Nombre"
                                  fieldValue={profile.nombre}
                                  fieldActivate={props.modify}
                                  fieldChange={handleChange}
                                  fieldId={"nombre"}
                                ></InputField>
                                <InputField
                                  fieldTitle="Apellido"
                                  fieldValue={profile.apellido}
                                  fieldActivate={props.modify}
                                  fieldChange={handleChange}
                                  fieldId={"apellido"}
                                ></InputField>
                                <InputField
                                  fieldTitle="Ciudad"
                                  fieldValue={profile.ciudad}
                                  fieldActivate={true}
                                  fieldChange={handleChange}
                                  fieldId={"ciudad"}
                                ></InputField>
                                <div className="text-center">
                                  {<ButtonSave></ButtonSave>}
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
        </div>
      </div>
    </ElementContainer>
  );
}

export default Usuario;
