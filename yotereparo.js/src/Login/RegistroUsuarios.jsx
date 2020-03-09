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
import { useHistory } from "react-router-dom";
import Axios from "axios";
import "../Login/RegistroUsuarios.css";

const FormRegistro = props => {
  let isFormEmpleador = props.type === "empleador" ? true : false;
  // let isFormUsuario = props.type === "usuario" ? true : false;

  let history = useHistory();
  let membresiaObject = isFormEmpleador ? "BASICA" : null;

  let [account, setAccount] = useState({
    email: "",
    password: "",
    nombre: "",
    apellido: "",
    ciudad: "",
    validate: {
      emailState: "",
      passwordState: true
    }
  });

  let [formErrors, setErrors] = useState({
    errors: []
  });

  let [isCreatingUser, setIsCreatingUser] = useState(false);

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

    setIsCreatingUser(true);

    //TODO: SET membresia en funcion del formulario de entrada

    let requestData = {
      id: account.nombre + account.apellido,
      nombre: account.nombre,
      apellido: account.apellido,
      ciudad: account.ciudad,
      email: account.email,
      contrasena: account.password,
      membresia: membresiaObject
    };

    let requestHeaders = {
      "Access-Control-Allow-Origin": "*"
    };

    Axios.post(
      "http://localhost:8080/YoTeReparo/users/",
      requestData,
      requestHeaders
    )
      .then(response => {
        console.log(response.status);
        if (response.status === 400) {
          console.log(response.json);
        } else {
          history.push({
            pathname: "/tour",
            state: { user: account }
          });
        }
      })
      .catch(error => {
        processErrors(error.response);
        setIsCreatingUser(false);
      });
  };

  let processErrors = incomingErrors => {
    let errors = [];

    if (incomingErrors.data.length >= 1) {
      incomingErrors.data.forEach(error => {
        errors.push({
          type: error.field,
          message: error.defaultMessage
        });
      });
    } else {
      errors.push({
        type: incomingErrors.data.field,
        message: incomingErrors.data.defaultMessage
      });
    }

    setErrors({ ...formErrors, errors });
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

  let updateMembresia = event => {
    console.log(event.target.value);
    switch (event.target.value) {
      case "1":
        membresiaObject = "BASICA";
        break;
      case "2":
        membresiaObject = "PREMIUM";
        break;
      case "3":
        membresiaObject = "GOLD";
        break;
      default:
        break;
    }
    console.log(membresiaObject);
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
            {formErrors.errors.length >= 1 ? (
              <div className="errors-list">
                {formErrors.errors.map((error, i) => (
                  <p key={i} className="font-weight-light">
                    <span className="fa-stack fa-1x">
                      <i className={`fas fa-times fa-stack-1x`}></i>
                    </span>{" "}
                    {error.message}
                  </p>
                ))}
              </div>
            ) : (
              <></>
            )}
            <Form onSubmit={handleSubmit}>
              <FormGroup className="mb-2 mr-sm-2 mb-sm-2">
                <Label for="nombreLabel" className="mr-sm-2 font-weight-bold">
                  NOMBRE
                </Label>
                <Input
                  type="text"
                  name="nombre"
                  id="nombreLabel"
                  placeholder="Tu nombre..."
                  onChange={e => {
                    handleChange(e);
                  }}
                />
              </FormGroup>
              <FormGroup className="mb-2 mr-sm-2 mb-sm-2">
                <Label for="apellidoLabel" className="mr-sm-2 font-weight-bold">
                  APELLIDO
                </Label>
                <Input
                  type="text"
                  name="apellido"
                  id="apellidoLabel"
                  placeholder="Tu apellido..."
                  onChange={e => {
                    handleChange(e);
                  }}
                />
              </FormGroup>
              <FormGroup className="mb-2 mr-sm-2 mb-sm-2">
                <Label for="ciudadLabel" className="mr-sm-2 font-weight-bold">
                  CIUDAD
                </Label>
                <Input
                  type="select"
                  name="ciudad"
                  id="ciudadLabel"
                  defaultValue=""
                  onChange={e => {
                    handleChange(e);
                  }}
                >
                  <option value="" disabled hidden>
                    Selecciona una ciudad
                  </option>
                  <option value="rosario">Rosario</option>
                  <option value="cordoba">Cordoba</option>
                </Input>
              </FormGroup>
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
                    max="3"
                    onChange={updateMembresia}
                  ></CustomInput>
                  <div className="row text-center membership">
                    <div className="col-4">
                      <div className="small membership-text">BASICA</div>
                    </div>

                    <div className="col-4">
                      {" "}
                      <div className="small membership-text">PREMIUM</div>
                    </div>
                    <div className="col-4">
                      {" "}
                      <div className="small membership-text">GOLD</div>
                    </div>
                  </div>
                </FormGroup>
              ) : (
                <></>
              )}
              {infomessage === "" ? (
                <></>
              ) : (
                <FormText className="mt-4 mb-1 text-center alert alert-warning">
                  {infomessage}
                </FormText>
              )}
              <div className="text-center">
                <Button color="primary" size="lg" block className="mt-4">
                  {!isCreatingUser ? (
                    "INGRESAR"
                  ) : (
                    <div className="spinner-border" role="status">
                      <span className="sr-only">Creando Usuario...</span>
                    </div>
                  )}
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
