import React, { useState, useEffect } from "react";
import "../Home/Home.css";
import {
  Button,
  Form,
  FormGroup,
  CustomInput,
  FormFeedback,
  Label,
  Input,
  FormText
} from "reactstrap";
import ElementContainer from "../Container/ElementContainer";

const FormRegistro = props => {
  let isFormEmpleador = props.type === "empleador" ? true : false;
  // let isFormUsuario = props.type === "usuario" ? true : false;

  let [account, setAccount] = useState({
    email: "",
    password: "",
    validate: {
      emailState: "",
      passwordState: true
    }
  });

  let [infomessage, setMessage] = useState("");

  useEffect(() => {
    if (account["validate"].emailState === "") {
      setMessage("Ingresa los datos necesarios...");
    } else {
      setMessage("");
    }
  }, [account]);

  let handleSubmit = event => {
    event.preventDefault();
    console.log(account);
  };

  let handleChange = async event => {
    account[event.target.name] = event.target.value;
    setAccount({ ...account, [event.target.name]: event.target.value });
  };

  let validateEmail = event => {
    const emailRex = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    if (emailRex.test(event.target.value)) {
      account["validate"].emailState = "success";
    } else {
      account["validate"].emailState = "danger";
    }
  };

  let validatePassword = event => {
    var strongRegex = new RegExp("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.{8,})");
    if (strongRegex.test(event.target.value)) {
      account["validate"].passwordState = "success";
    } else {
      account["validate"].passwordState = "danger";
    }
  };

  return (
    <div className="centered card-center-form">
      <div className="row">
        <div className="col-md-12">
          <ElementContainer>
            <div className="text-center">
              <div className="lead mb-2">
                {isFormEmpleador ? (
                  <strong>REGISTRARSE COMO TRABAJADOR</strong>
                ) : (
                  <strong>REGISTRARSE COMO USUARIO</strong>
                )}
              </div>
            </div>
            <Form onSubmit={handleSubmit}>
              <FormGroup className="mb-2 mr-sm-2 mb-sm-2">
                <Label for="emailLabel" className="mr-sm-2 font-weight-bold">
                  EMAIL
                </Label>
                <Input
                  valid={account["validate"].emailState === "success"}
                  invalid={account["validate"].emailState === "danger"}
                  type="email"
                  name="email"
                  id="emailLabel"
                  placeholder="tuemail@email.com"
                  onChange={e => {
                    handleChange(e);
                    validateEmail(e);
                  }}
                />
                <FormFeedback className="lead box-message-letter">
                  El email que ingresaste no cumple con las politicas
                </FormFeedback>
              </FormGroup>
              <FormGroup className="mb-2 mr-sm-2 mb-sm-0">
                <Label for="passwordLabel" className="mr-sm-2 font-weight-bold">
                  CONTRASEÑA
                </Label>
                <Input
                  valid={account["validate"].passwordState === "success"}
                  invalid={account["validate"].passwordState === "danger"}
                  type="password"
                  name="password"
                  id="passwordLabel"
                  placeholder="********"
                  onChange={e => {
                    handleChange(e);
                    validatePassword(e);
                  }}
                />
                <FormFeedback className="lead box-message-letter">
                  La contraseña que ingresaste debe poseer una letra mayuscula,
                  un caracter numerico y debe ser de 8 caracteres o más.
                </FormFeedback>
              </FormGroup>
              {isFormEmpleador ? (
                <FormGroup className="mb-2 mt-2 mr-sm-2 mb-sm-0">
                  <Label for="rubroSelect" className="mr-sm-2 font-weight-bold">
                    RUBRO
                  </Label>
                  <CustomInput type="checkbox" id="rubroHogar" label="Hogar" />
                  <CustomInput
                    type="checkbox"
                    id="rubroInformatica"
                    label="Informatica/Electronica"
                  />
                  <CustomInput
                    type="checkbox"
                    id="rubroOtros"
                    label="Otros..."
                  />
                </FormGroup>
              ) : (
                <></>
              )}
              {infomessage === "" ? (
                <></>
              ) : (
                <FormText className="mt-4 mb-1 alert alert-warning">
                  {infomessage}
                </FormText>
              )}
              <div className="text-center">
                <Button color="primary" size="lg" block className="mt-4">
                  INGRESAR
                </Button>
              </div>
            </Form>
          </ElementContainer>
        </div>
      </div>
    </div>
  );
};

export default FormRegistro;
