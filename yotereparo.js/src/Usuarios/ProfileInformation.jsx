import React from "react";
import { InputField } from "../Utils/InputField";
import { ButtonSave } from "../Buttons/ButtonSave";

function ProfileInformation(props) {
  return (
    <>
      <h5>
        {props.profile.roles?.map((rol) => {
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
        fieldTitle="Usuario"
        fieldValue={props.profile.id}
        fieldActivate={true}
        fieldChange={props.handleChange}
        fieldId={"id"}
      ></InputField>
      <div className="card-text">
        <InputField
          fieldTitle="Email"
          fieldValue={props.profile.email}
          fieldActivate={props.modify}
          fieldChange={props.handleChange}
          type="email"
          fieldId={"email-field"}
        ></InputField>
        <InputField
          fieldTitle="Nombre"
          fieldValue={props.profile.nombre}
          fieldActivate={props.modify}
          fieldChange={props.handleChange}
          fieldId={"nombre"}
        ></InputField>
        <InputField
          fieldTitle="Apellido"
          fieldValue={props.profile.apellido}
          fieldActivate={props.modify}
          fieldChange={props.handleChange}
          fieldId={"apellido"}
        ></InputField>
        <InputField
          fieldTitle="Ciudad"
          fieldValue={props.profile.ciudad}
          fieldActivate={true}
          fieldChange={props.handleChange}
          fieldId={"ciudad"}
        ></InputField>
        <div className="text-center">
          {
            <ButtonSave
              modify={props.modify}
              activateOnSave={props.activateSave}
              activateOnEdit={props.activateEdit}
              activateOnCancel={props.activateCancel}
            ></ButtonSave>
          }
        </div>
        <div className="text-center">
          <button
            type="button"
            className="btn btn-success btn-block mt-2"
            onClick={props.modifyAddress}
          >
            DIRECCIONES
          </button>
        </div>
      </div>
    </>
  );
}

export default ProfileInformation;
