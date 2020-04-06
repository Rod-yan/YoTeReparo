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
  FormText,
} from "reactstrap";
import ElementContainer from "../Container/ElementContainer";
import { useHistory } from "react-router-dom";
import Axios from "axios";
import "../Login/RegistroUsuarios.css";
import { fetchData } from "../Utils/SessionHandlers";
import { validateEmail, validatePassword } from "../Utils/Security";
import { deleteSessionCookie } from "../Utils/SessionManage";
import { processErrors } from "../Utils/Errors";
import { intersect } from "../Utils/ArrayUtils";
import Errors from "../Errors/Errors";

const FormRegistro = (props) => {
  let isFormEmpleador = props.type === "empleador" ? true : false;
  // let isFormUsuario = props.type === "usuario" ? true : false;

  let history = useHistory();
  let membresiaObject = isFormEmpleador ? "GRATUITA" : null;

  let [account, setAccount] = useState({
    email: "",
    password: "",
    nombre: "",
    apellido: "",
    ciudad: "",
    barrios: "",
    validate: {
      emailState: "",
      passwordState: true,
    },
  });
  let [direccion, setDireccion] = useState({
    calle: "",
    altura: 0,
    piso: "",
    departamento: "",
    descripcion: "",
  });
  let [cities, setCities] = useState([]);
  let [hoods, setHoods] = useState([]);
  let [hoodsDisabled, toggleHoods] = useState(true);
  let [formErrors, setErrors] = useState({
    errors: [],
  });

  let [isCreatingUser, setIsCreatingUser] = useState(false);

  let [infomessage, setMessage] = useState("");

  //For the account
  useEffect(() => {
    if (account["validate"].emailState === "") {
      setMessage("Ingresa los datos necesarios...");
    } else {
      setMessage("");
    }
  }, [account]);

  //For the cities and the API
  useEffect(() => {
    fetchData(`http://localhost:8080/YoTeReparo/cities`, (citiesData) => {
      citiesData.forEach((city) => {
        setCities((cities) => [
          ...cities,
          { id: city.id, desc: city.descripcion },
        ]);
      });
    });
  }, []);

  //Fetch hoods for only one city
  let handleSubmit = (event) => {
    event.preventDefault();

    setIsCreatingUser(true);

    let requestData = {};

    if (isFormEmpleador) {
      let barriosSelected = intersect(hoods, account.barrios);

      requestData = {
        id: account.nombre + account.apellido,
        nombre: account.nombre,
        apellido: account.apellido,
        ciudad: account.ciudad,
        barrios: barriosSelected,
        direcciones: [direccion],
        email: account.email,
        contrasena: account.password,
        membresia: membresiaObject,
      };
    } else {
      requestData = {
        id: account.nombre + account.apellido,
        nombre: account.nombre,
        apellido: account.apellido,
        ciudad: account.ciudad,
        email: account.email,
        contrasena: account.password,
        membresia: membresiaObject,
      };
    }

    //TODO: SET membresia en funcion del formulario de entrada

    let requestConfig = {
      headers: {
        "Access-Control-Allow-Origin": "*",
      },
    };

    Axios.post(
      "http://localhost:8080/YoTeReparo/auth/signup",
      requestData,
      requestConfig
    )
      .then((response) => {
        console.log(response.status);
        if (response.status === 400) {
          console.log(response.json);
        } else {
          deleteSessionCookie("userSession");
          history.push({
            pathname: "/tour",
            state: { user: account },
          });
        }
      })
      .catch((error) => {
        let errors = processErrors(error.response.data);
        setErrors({ ...formErrors, errors });
        setIsCreatingUser(false);
      });
  };

  let handleChange = async (event) => {
    account[event.target.name] = event.target.value;

    setAccount({ ...account, [event.target.name]: event.target.value });

    if (event.target.name === "ciudad" && isFormEmpleador) {
      fetchData(
        `http://localhost:8080/YoTeReparo/cities/${account.ciudad}`,
        (data) => {
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
        }
      );
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

  let updateMembresia = (event) => {
    switch (event.target.value) {
      case "1":
        membresiaObject = "GRATUITA";
        break;
      case "2":
        membresiaObject = "PLATA";
        break;
      case "3":
        membresiaObject = "ORO";
        break;
      default:
        break;
    }
  };

  return (
    <div className="registercentered card-center-form">
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
            <Errors formErrors={formErrors}></Errors>
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
                  onChange={(e) => {
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
                  onChange={(e) => {
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
                  onChange={(e) => {
                    handleChange(e);
                  }}
                >
                  <option value="" disabled hidden>
                    Selecciona una ciudad
                  </option>
                  {cities.map((city) => (
                    <option key={city.id} value={city.id}>
                      {city.desc}
                    </option>
                  ))}
                </Input>
              </FormGroup>
              {isFormEmpleador ? (
                <>
                  <FormGroup className="mb-2 mr-sm-2 mb-sm-2">
                    <Label
                      for="ciudadLabel"
                      className="mr-sm-2 font-weight-bold"
                    >
                      BARRIOS
                    </Label>
                    <Input
                      multiple={isFormEmpleador}
                      type="select"
                      name="barrios"
                      id="barriosLabel"
                      onChange={(e) => {
                        handleChange(e);
                      }}
                      disabled={hoodsDisabled}
                    >
                      <option value="" disabled hidden>
                        Selecciona un barrio
                      </option>
                      {hoods.map((hood) => (
                        <option key={hood.id} value={hood.id}>
                          {hood.descripcion}
                        </option>
                      ))}
                    </Input>
                  </FormGroup>
                  <FormGroup className="mb-2 mr-sm-2 mb-sm-2">
                    <div className="row">
                      <div className="col-md-6">
                        <Label
                          for="direccionLabelCalle"
                          className="mr-sm-2 font-weight-bold"
                        >
                          CALLE
                        </Label>
                        <Input
                          type="text"
                          name="calle"
                          id="calle"
                          onChange={(e) => {
                            setDireccion({
                              ...direccion,
                              [e.target.name]: e.target.value,
                            });
                          }}
                        />
                      </div>
                      <div className="col-md-2">
                        <Label
                          for="direccionLabelPiso"
                          className="mr-sm-2 font-weight-bold"
                        >
                          PISO
                        </Label>
                        <Input
                          type="text"
                          name="piso"
                          id="piso"
                          onChange={(e) => {
                            setDireccion({
                              ...direccion,
                              [e.target.name]: e.target.value,
                            });
                          }}
                        />
                      </div>
                      <div className="col-md-2">
                        {" "}
                        <Label
                          for="direccionLabelaltura"
                          className="mr-sm-2 font-weight-bold"
                        >
                          ALTURA
                        </Label>
                        <Input
                          type="number"
                          name="altura"
                          id="altura"
                          onChange={(e) => {
                            setDireccion({
                              ...direccion,
                              [e.target.name]: e.target.value,
                            });
                          }}
                        />
                      </div>
                      <div className="col-md-2">
                        {" "}
                        <Label
                          for="direccionLabeldepartamento"
                          className="mr-sm-2 font-weight-bold"
                        >
                          DEPARTAMENTO
                        </Label>
                        <Input
                          type="text"
                          name="departamento"
                          id="departamento"
                          onChange={(e) => {
                            setDireccion({
                              ...direccion,
                              [e.target.name]: e.target.value,
                            });
                          }}
                        />
                      </div>
                    </div>
                    <div className="row">
                      <div className="col-md-12 mt-2">
                        {" "}
                        <Label
                          for="direccionLabeldescripcion"
                          className="mr-sm-2 font-weight-bold"
                        >
                          DESCRIPCION
                        </Label>
                        <Input
                          type="text"
                          name="descripcion"
                          id="descripcion"
                          onChange={(e) => {
                            setDireccion({
                              ...direccion,
                              [e.target.name]: e.target.value,
                            });
                          }}
                        />
                      </div>
                    </div>
                  </FormGroup>
                </>
              ) : (
                <></>
              )}

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
                  onChange={(e) => {
                    handleChange(e);
                    validateEmail(e, account);
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
                  onChange={(e) => {
                    handleChange(e);
                    validatePassword(e, account);
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
                      <div className="small membership-text">GRATUITA</div>
                    </div>

                    <div className="col-4">
                      {" "}
                      <div className="small membership-text">PLATA</div>
                    </div>
                    <div className="col-4">
                      {" "}
                      <div className="small membership-text">ORO</div>
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
