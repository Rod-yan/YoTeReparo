import React, { useState, useEffect } from "react";
import "../Home/Home.css";
import {
  Button,
  Form,
  FormGroup,
  FormFeedback,
  Label,
  Input,
  FormText
} from "reactstrap";
import ElementContainer from "../Container/ElementContainer";

const FormRegistro = props => {
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
      setMessage("Ingresa correctamente los datos...");
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

  return (
    <div className="centered">
      <div className="row">
        <div className="col-md-12">
          <ElementContainer>
            <Form className="text-center" onSubmit={handleSubmit}>
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
                <FormFeedback>
                  El email que ingresaste no cumple con las poltiicas
                </FormFeedback>
              </FormGroup>
              <FormGroup className="mb-2 mr-sm-2 mb-sm-0">
                <Label for="passwordLabel" className="mr-sm-2 font-weight-bold">
                  CONTRASEÑA
                </Label>
                <Input
                  type="password"
                  name="password"
                  id="passwordLabel"
                  placeholder="********"
                  onChange={handleChange}
                />
                <FormFeedback>
                  La contraseña que ingresaste no cumple con las politicas
                </FormFeedback>
              </FormGroup>
              <Button className="mt-4">INGRESAR</Button>
              <FormText>{infomessage}</FormText>
            </Form>
          </ElementContainer>
        </div>
      </div>
    </div>
  );
};

export default FormRegistro;
