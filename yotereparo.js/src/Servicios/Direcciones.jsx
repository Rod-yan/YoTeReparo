import React from "react";
import { Button, Modal, ModalHeader, ModalBody, ModalFooter } from "reactstrap";
import ElementContainer from "../Container/ElementContainer";
import ResourceNotFound from "../Errors/ResourceNotFound";
import { InputField } from "../Utils/InputField";

function Direcciones(props) {
  return (
    <div>
      <Modal isOpen={props.address} toggle={props.toggleAddress}>
        <ModalHeader toggle={props.toggleAddress}>
          {" "}
          Direcciones del usuario {props.profile.nombre}
        </ModalHeader>
        <ModalBody>
          {props.errors ? (
            <ResourceNotFound errorMessage="Estas intentado ingresar una direccion erronea"></ResourceNotFound>
          ) : (
            <ElementContainer>
              {props.profile?.direcciones.length > 0 ? (
                props.profile.direcciones.map((item, idx) => {
                  return (
                    <div key={idx}>
                      <InputField
                        fieldTitle="Calle"
                        fieldValue={item.calle}
                        fieldActivate={props.addressModify}
                        fieldChange={props.handleChange}
                        fieldId={"calle"}
                        required={true}
                      ></InputField>
                      <InputField
                        fieldTitle="Altura"
                        fieldValue={item.altura}
                        fieldActivate={props.addressModify}
                        fieldChange={props.handleChange}
                        fieldId={"altura"}
                        type={"number"}
                        required={true}
                      ></InputField>
                      <InputField
                        fieldTitle="Piso"
                        fieldValue={item.piso}
                        fieldActivate={props.addressModify}
                        fieldChange={props.handleChange}
                        fieldId={"piso"}
                        type={"number"}
                        required={true}
                      ></InputField>
                      <InputField
                        fieldTitle="Departamento"
                        fieldValue={item.departamento}
                        fieldActivate={props.addressModify}
                        fieldChange={props.handleChange}
                        fieldId={"departamento"}
                      ></InputField>
                      <InputField
                        fieldTitle="Descripcion"
                        fieldValue={item.descripcion}
                        fieldActivate={props.addressModify}
                        fieldChange={props.handleChange}
                        fieldId={"descripcion"}
                      ></InputField>
                      <div className="text-center">
                        <Button
                          disabled={!props.addressModify}
                          onClick={props.ModifyOne}
                        >
                          Modificar
                        </Button>
                      </div>
                    </div>
                  );
                })
              ) : (
                <div>
                  <InputField
                    fieldTitle="Calle"
                    fieldValue=""
                    fieldActivate={props.addressModify}
                    fieldChange={props.onChangeNewAddress}
                    fieldId={"calle"}
                  ></InputField>
                  <InputField
                    fieldTitle="Altura"
                    fieldValue=""
                    fieldActivate={props.addressModify}
                    fieldChange={props.onChangeNewAddress}
                    fieldId={"altura"}
                    type="number"
                    max={9999}
                    min={0}
                  ></InputField>
                  <InputField
                    fieldTitle="Piso"
                    fieldValue=""
                    fieldActivate={props.addressModify}
                    fieldChange={props.onChangeNewAddress}
                    fieldId={"piso"}
                    type="number"
                    max={12}
                    min={0}
                  ></InputField>
                  <InputField
                    fieldTitle="Departamento"
                    fieldValue=""
                    fieldActivate={props.addressModify}
                    fieldChange={props.onChangeNewAddress}
                    fieldId={"departamento"}
                  ></InputField>
                  <InputField
                    fieldTitle="Descripcion"
                    fieldValue=""
                    fieldActivate={props.addressModify}
                    fieldChange={props.onChangeNewAddress}
                    fieldId={"descripcion"}
                  ></InputField>
                  <div className="text-center">
                    <Button
                      disabled={!props.addressModify}
                      onClick={props.ModifyOne}
                    >
                      Agregar
                    </Button>
                  </div>
                </div>
              )}
            </ElementContainer>
          )}
        </ModalBody>
        <ModalFooter>
          <Button color="primary" onClick={props.CreateCallback}>
            Aceptar
          </Button>{" "}
          <Button color="secondary" onClick={props.toggleAddress}>
            Cerrar
          </Button>
        </ModalFooter>
      </Modal>
    </div>
  );
}

export default Direcciones;
