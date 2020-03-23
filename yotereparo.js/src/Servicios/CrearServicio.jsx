import React from "react";
import ElementContainer from "../Container/ElementContainer";
import { Form, FormGroup, Label, Input, Button } from "reactstrap";
import { useState } from "react";
import InputRange from "react-input-range";
import { useEffect } from "react";
import { fetchData } from "../Utils/SessionHandlers";
import { intersect } from "../Utils/Math";

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
  const [horasEstimadasEjecucion, setHorasEstimadasEjecucion] = useState(0);
  const [isCreationService, setCreationService] = useState(false);
  const [cantidadTrabajadores, setCantidadTrabajadores] = useState(0);
  const [tiposServicio, setTiposServicio] = useState([]);
  const [mediosDePago, setMediosDePago] = useState([]);
  const [requerimientos, setRequerimientos] = useState([]);
  const [emitirFactura, setEmitirFactura] = useState(false);

  //We use this object in the handleSubmit in order to cross over all the data
  let [service, setService] = useState({
    titulo: "",
    descripcion: "",
    disponibilidad: "",
    precioInsumos: 0,
    precioAdicionales: 0,
    tipoServicio: "",
    mediosDePago: [1, 2],
    requerimientos: []
  });

  const handleSubmit = event => {
    setCreationService(true);
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
                      PRECIO INSUMOS
                    </Label>
                    <Input
                      type="number"
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
                      PRECIO ADICIONALES
                    </Label>
                    <Input
                      type="number"
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
                  <option value="" disabled hidden>
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
