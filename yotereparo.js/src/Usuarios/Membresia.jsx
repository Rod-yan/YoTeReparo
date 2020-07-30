import React, { useState, useEffect } from "react";
import {   FormGroup,  CustomInput,  Label,Button, Modal, ModalHeader, ModalBody, ModalFooter, Input } from "reactstrap";
import ElementContainer from "../Container/ElementContainer";
import { useRef } from "react";
import { fetchData } from "../Utils/SessionHandlers";
import { intersect } from "../Utils/ArrayUtils";

function Membresia (props) {

  const refBarrios = useRef([React.createRef()]);
  let isFormEmpleador = true;
  
  const clearBarrios = () => {
    var elements = refBarrios.current.options;
    for (var i = 0; i < elements.length; i++) {
      elements[i].selected = false;
    }
  };

  let [account, setAccount] = useState({
    barrios: "",
  });

  let [cities, setCities] = useState([]);
  let [hoods, setHoods] = useState([]);
  let [hoodsDisabled, toggleHoods] = useState(true);

  let handleChange = async (event) => {
    account[event.target.name] = event.target.value;

    setAccount({ ...account, [event.target.name]: event.target.value });

    if (event.target.name === "ciudad") {
      fetchData(`/YoTeReparo/cities/${account.ciudad}`, (data) => {
        let barriosXciudad = [];

        data.barrios.forEach((barrio) => {
          barriosXciudad.push({
            id: barrio.id,
            descripcion: barrio.descripcion,
            codigoPostal: barrio.codigoPostal,
          });
        });

        barriosXciudad.length > 0 ? toggleHoods(false) : toggleHoods(true);

        if (barriosXciudad.length > 0) {
          account["barrios"] = barriosXciudad[0].id;
        } else {
        }

        setHoods(barriosXciudad);
      });
    }

    if (event.target.name === "barrios") {
      var options = event.target.options;
      var value = [];
      for (var i = 0, l = options.length; i < l; i++) {
        if (options[i].selected) {
          value.push(parseInt(options[i].value));
        }
      }
      setAccount({ ...account, [event.target.name]: value });
    }
  };
    //For the cities and the API
    useEffect(() => {
      fetchData(`/YoTeReparo/cities`, (citiesData) => {
        if (citiesData != null) {
          citiesData.forEach((city) => {
            setCities((cities) => [
              ...cities,
              { id: city.id, desc: city.descripcion },
            ]);
          });
        }
      });
    }, []);

    var barriosSelected = [];
    if (account.barrios.length > 0) {
      barriosSelected = intersect(hoods, account.barrios);
    }


  return (
    <div>
      <Modal isOpen={props.membership} toggle={props.toggleMembership}>
        <ModalHeader toggle={props.toggleMembership}>
          {" "}
          Roles del usuario {props.profile.nombre}
        </ModalHeader>
        <ModalBody>
            <ElementContainer>
                    <FormGroup className="mb-2 mt-2 mr-sm-2 mb-sm-2">
                      <Label for="rubroSelect" className="mr-sm-2 font-weight-bold">
                        MEMBRESIA
                      </Label>
                      <CustomInput
                        id="membresiaRange"
                        type="range"
                        step="1"
                        defaultValue="1"
                        min="1"
                        max="4"
                        customActivate={props.membershipModify}
                        onChange={props.onChangeMembresia}
                      ></CustomInput>
                      <div className="row text-center membership">
                        <div className="col-3">
                          {" "}
                          <div className="small membership-text">ACTUAL</div>
                        </div>
                        <div className="col-3">
                          {" "}
                          <div className="small membership-text">GRATUITA</div>
                        </div>
                        <div className="col-3">
                          {" "}
                          <div className="small membership-text">PLATA</div>
                        </div>
                        <div className="col-3">
                          {" "}
                          <div className="small membership-text">ORO</div>
                        </div>
                      </div>
                    </FormGroup>
                      <div className="text-center">
                            <Button
                              disabled={!props.membershipModify}
                              onClick={props.ModifyOne2}                        >
                              Modificar
                            </Button>
                      </div>                
            </ElementContainer>
        </ModalBody>
        <ModalFooter>
          <Button color="primary" onClick={props.CreateCallback2}>
            Aceptar
          </Button>{" "}
          <Button color="secondary" onClick={props.toggleMembership}>
            Cerrar
          </Button>
        </ModalFooter>
      </Modal>
    </div>
  );
}

export default Membresia;
