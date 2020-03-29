import React from "react";
import ElementContainer from "../Container/ElementContainer";
import { Form, FormGroup, Label, Input, Button } from "reactstrap";
import { useState } from "react";
import InputRange from "react-input-range";
import { useEffect } from "react";
import { fetchData } from "../Utils/SessionHandlers";
import { SessionContext } from "../Utils/SessionManage";
import { useContext } from "react";
import { intersect } from "../Utils/ArrayUtils";
import { processErrors } from "../Utils/Errors";
import Axios from "axios";
//import { intersect } from "../Utils/ArrayUtils";

// -> GET: /YoTeReparo/requirements
// -> GET: /YoTeReparo/requirements/{id}
// -> GET: /YoTeReparo/paymentmethods
// -> GET: /YoTeReparo/paymentmethods/{id}
// -> GET: /YoTeReparo/servicetypes
// -> GET: /YoTeReparo/servicetypes/{id}
//TODO: Generate Service object and POST to the databbase

const CrearServicio = props => {
  const [preciosRange, setPreciosRange] = useState({
    min: 0,
    max: 9999
  });
  const [formErrors, setErrors] = useState({ errors: [] });
  const [horasEstimadasEjecucion, setHorasEstimadasEjecucion] = useState(0);
  const [isCreationService, setIsCreationService] = useState(false);
  const [cantidadTrabajadores, setCantidadTrabajadores] = useState(0);
  const [tiposServicio, setTiposServicio] = useState([]);
  const [mediosDePago, setMediosDePago] = useState([]);
  const [requerimientos, setRequerimientos] = useState([]);
  const [emitirFactura, setEmitirFactura] = useState(false);

  const session = useContext(SessionContext);

  //We use this object in the handleSubmit in order to cross over all the data
  let [service, setService] = useState({
    titulo: "",
    descripcion: "",
    disponibilidad: "",
    precioInsumos: 0,
    precioAdicionales: 0,
    tipoServicio: "",
    mediosDePago: [],
    requerimientos: []
  });

  const handleSubmit = event => {
    event.preventDefault();

    setIsCreationService(true);

    let mediosDePagoSeleccionados = intersect(
      mediosDePago,
      service.mediosDePago
    );

    let requerimientosSeleccionados = intersect(
      requerimientos,
      service.requerimientos
    );

    let tipoServicioSeleccionado = intersect(tiposServicio, [
      parseInt(service.tipoServicio)
    ]);

    let requestService = {
      usuarioPrestador: session.username,
      titulo: service.titulo,
      descripcion: service.descripcion,
      disponibilidad: service.disponibilidad,
      precioMaximo: preciosRange.max,
      precioMinimo: preciosRange.min,
      precioInsumos: service.precioInsumos,
      precioAdicionales: service.precioAdicionales,
      horasEstimadasEjecucion: horasEstimadasEjecucion,
      cantidadTrabajadores: cantidadTrabajadores,
      facturaEmitida: !emitirFactura,
      tipoServicio:
        tipoServicioSeleccionado[0] === undefined
          ? "null"
          : tipoServicioSeleccionado[0].descripcion,
      mediosDePago: mediosDePagoSeleccionados,
      requerimientos: requerimientosSeleccionados
    };

    let requestHeaders = {
      "Access-Control-Allow-Origin": "*"
    };

    Axios.post(
      "http://localhost:8080/YoTeReparo/services/",
      requestService,
      requestHeaders
    )
      .then(response => {
        console.log(response.status);
        if (response.status === 400) {
          console.log(response.json);
        } else {
          console.log(response.data);
          // history.push({
          //   pathname: "/buscar",
          //   state: { service: requestService }
          // });
        }
      })
      .catch(error => {
        console.log(error.response);
        let errors = processErrors(error.response);
        setErrors({ ...formErrors, errors });
        setIsCreationService(false);
      });
  };

  const handleChange = event => {
    service[event.target.name] = event.target.value;
    setService({ ...service, [event.target.name]: event.target.value });

    if (
      event.target.name === "mediosDePago" ||
      event.target.name === "requerimientos"
    ) {
      setService({
        ...service,
        [event.target.name]: get(event.target.options)
      });
    }
  };

  const get = options => {
    var value = [];
    for (var i = 0, l = options.length; i < l; i++) {
      if (options[i].selected) {
        value.push(parseInt(options[i].value));
      }
    }
    return value;
  };

  console.log(formErrors);

  useEffect(() => {
    fetchData(
      "http://localhost:8080/YoTeReparo/servicetypes",
      setTiposServicio
    );
    fetchData(
      "http://localhost:8080/YoTeReparo/paymentmethods",
      setMediosDePago
    );

    fetchData(
      "http://localhost:8080/YoTeReparo/requirements",
      setRequerimientos
    );
  }, []);

  return (
    <div className="registercentered card-center-form">
      <div className="row">
        <div className="col-md-12">
          <ElementContainer>
            <div className="text-center">
              <div className="lead mb-2">Crear Servicio</div>
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
                <Label for="titulo" className="mr-sm-2 font-weight-bold">
                  TITULO SERVICIO
                </Label>
                <Input
                  type="text"
                  name="titulo"
                  id="titulo"
                  placeholder="Especifique un titulo para el servicio a prestar..."
                  onChange={e => {
                    handleChange(e);
                  }}
                />
              </FormGroup>
              <FormGroup className="mb-2 mr-sm-2 mb-sm-2">
                <Label for="descripcion" className="mr-sm-2 font-weight-bold">
                  DESCRIPCION SERVICIO
                </Label>
                <Input
                  type="text"
                  name="descripcion"
                  id="descripcion"
                  placeholder="Especifique una descripcion para el servicio a prestar..."
                  onChange={e => {
                    handleChange(e);
                  }}
                />
              </FormGroup>
              <FormGroup className="mb-2 mr-sm-2 mb-sm-2">
                <Label
                  for="disponibilidad"
                  className="mr-sm-2 font-weight-bold"
                >
                  DISPONIBILIDAD SERVICIO
                </Label>
                <Input
                  type="text"
                  name="disponibilidad"
                  id="disponibilidad"
                  placeholder="Cuanto estas disponible?"
                  onChange={e => {
                    handleChange(e);
                  }}
                />
              </FormGroup>
              <FormGroup className="mb-2 mt-2 mr-sm-2 mb-sm-2">
                <Label for="rubroSelect" className="mr-sm-2 font-weight-bold">
                  PRECIO DEL SERVICIO
                </Label>
                <div className="mx-4 my-4">
                  <InputRange
                    formatLabel={value => `${value} $`}
                    maxValue={9999}
                    minValue={0}
                    value={preciosRange}
                    onChange={value => setPreciosRange(value)}
                  />
                </div>
              </FormGroup>
              <FormGroup className="mb-2 mt-2 mr-sm-2 mb-sm-2">
                <div className="row">
                  <div className="col-md-6">
                    <Label
                      for="precioInsumo"
                      className="mr-sm-2 font-weight-bold"
                    >
                      PRECIO INSUMOS ($)
                    </Label>
                    <Input
                      type="number"
                      step="50"
                      min="0"
                      name="precioInsumos"
                      id="precioInsumos"
                      placeholder="Insumos"
                      onChange={e => {
                        handleChange(e);
                      }}
                    />
                  </div>
                  <div className="col-md-6">
                    <Label
                      for="precioAdicionales"
                      className="mr-sm-2 font-weight-bold"
                    >
                      PRECIO ADICIONALES ($)
                    </Label>
                    <Input
                      type="number"
                      min="0"
                      step="50"
                      name="precioAdicionales"
                      id="precioAdicionales"
                      placeholder="Adicionales"
                      onChange={e => {
                        handleChange(e);
                      }}
                    />
                  </div>
                </div>
              </FormGroup>
              <FormGroup className="mb-2 mt-2 mr-sm-2 mb-sm-2">
                <Label for="horasRange" className="mr-sm-2 font-weight-bold">
                  HORAS ESTIMADAS
                </Label>
                <div className="mx-4 my-4">
                  <InputRange
                    formatLabel={value => `${value} hs`}
                    maxValue={10}
                    minValue={0}
                    step={0.5}
                    value={horasEstimadasEjecucion}
                    onChange={value => setHorasEstimadasEjecucion(value)}
                  />
                </div>
              </FormGroup>
              <FormGroup className="mb-2 mt-2 mr-sm-2 mb-sm-2">
                <Label
                  for="cantidadTrabajadores"
                  className="mr-sm-2 font-weight-bold"
                >
                  TRABAJADORES ESTIMADOS
                </Label>
                <div className="mx-4 my-4">
                  <InputRange
                    formatLabel={value => `${value}`}
                    maxValue={10}
                    minValue={0}
                    value={cantidadTrabajadores}
                    onChange={value => setCantidadTrabajadores(value)}
                  />
                </div>
              </FormGroup>
              <FormGroup className="mb-2 mt-2 mr-sm-2 mb-sm-2">
                <Label
                  for="facturaEmitida"
                  className="mr-sm-2 font-weight-bold"
                >
                  EMITIR FACTURA
                </Label>
                <Input
                  className="ml-4 mr-4"
                  type="checkbox"
                  name="emitirFactura"
                  id="emitirFactura"
                  defaultChecked={emitirFactura}
                  placeholder="Emitir Factura"
                  onChange={e => {
                    setEmitirFactura(!e.target.checked);
                  }}
                />
              </FormGroup>
              <FormGroup className="mb-2 mr-sm-2 mb-sm-2">
                <Label for="tipoServicio" className="mr-sm-2 font-weight-bold">
                  TIPO DE SERVICIO
                </Label>
                <Input
                  type="select"
                  name="tipoServicio"
                  id="tipoServicio"
                  onChange={e => {
                    handleChange(e);
                  }}
                >
                  <option value="" hidden>
                    Seleccione un servicio
                  </option>
                  {tiposServicio.map(type => (
                    <option key={type.id} value={type.id}>
                      {type.descripcion}
                    </option>
                  ))}
                </Input>
              </FormGroup>
              <FormGroup className="mb-2 mt-2 mr-sm-2 mb-sm-2">
                <Label
                  for="facturaEmitida"
                  className="mr-sm-2 font-weight-bold"
                >
                  MEDIOS DE PAGO
                </Label>
                <Input
                  type="select"
                  multiple
                  name="mediosDePago"
                  id="mediosDePago"
                  onChange={e => {
                    handleChange(e);
                  }}
                >
                  <option value="" disabled hidden>
                    Seleccione uno o mas metodos de pago
                  </option>
                  {mediosDePago.map(type => (
                    <option key={type.id} value={type.id}>
                      {type.descripcion}
                    </option>
                  ))}
                </Input>
              </FormGroup>
              <FormGroup className="mb-2 mr-sm-2 mb-sm-2">
                <Label for="titulo" className="mr-sm-2 font-weight-bold">
                  REQUERIMIENTOS ADICIONALES DEL SERVICIO
                </Label>
                <Input
                  type="select"
                  multiple
                  name="requerimientos"
                  id="requerimientos"
                  onChange={e => {
                    handleChange(e);
                  }}
                >
                  <option value="" disabled hidden>
                    Seleccione uno o mas requerimientos del servicio
                  </option>
                  {requerimientos.map(type => (
                    <option key={type.id} value={type.id}>
                      {type.descripcion}
                    </option>
                  ))}
                </Input>
              </FormGroup>
              <div className="text-center">
                <Button color="primary" size="lg" block className="mt-4">
                  {!isCreationService ? (
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

export default CrearServicio;