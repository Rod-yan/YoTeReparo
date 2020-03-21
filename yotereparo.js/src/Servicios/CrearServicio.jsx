import React from "react";
import ElementContainer from "../Container/ElementContainer";
import { Form, FormGroup, Label, Input, CustomInput, Button } from "reactstrap";
import { useState } from "react";
import InputRange from "react-input-range";
import "react-input-range/lib/css/index.css";

const CrearServicio = props => {
  const [preciosRange, setPreciosRange] = useState({
    min: 0,
    max: 9999
  });
  const [horasEstimadasEjecucion, setHorasEstimadasEjecucion] = useState(0);
  const [isCreationService, setCreationService] = useState(false);
  const [cantidadTrabajadores, setCantidadTrabajadores] = useState(0);
  const [efectivo, setEfectivo] = useState(true);
  const [tarjeta, setTarjeta] = useState(false);

  const handleSubmit = event => {};

  const handleChange = event => {};

  console.log(efectivo, tarjeta);

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
                  placeholder="Emitir Factura"
                />
              </FormGroup>
              <FormGroup className="mb-2 mr-sm-2 mb-sm-2">
                <Label for="tipoServicio" className="mr-sm-2 font-weight-bold">
                  TIPO DE SERVICIO
                </Label>
                <Input
                  type="text"
                  name="tipoServicio"
                  id="tipoServicio"
                  placeholder="Especifique un tipo para el servicio a prestar..."
                  onChange={e => {
                    handleChange(e);
                  }}
                />
              </FormGroup>
              <FormGroup className="mb-2 mt-2 mr-sm-2 mb-sm-2">
                <Label
                  for="facturaEmitida"
                  className="mr-sm-2 font-weight-bold"
                >
                  MEDIOS DE PAGO
                </Label>
                <div className="row">
                  <div className="col-md-6">
                    {" "}
                    <Label for="facturaEmitida" className="mr-sm-2">
                      EFECTIVO
                    </Label>
                    <Input
                      className="ml-4 mr-4"
                      type="checkbox"
                      name="efectivo"
                      defaultChecked={efectivo}
                      id="efectivo"
                      placeholder="Emitir Factura"
                      onChange={e => setEfectivo(e.target.checked)}
                    />
                  </div>
                  <div className="col-md-6">
                    <Label for="facturaEmitida" className="mr-sm-2 ">
                      TARJETA
                    </Label>
                    <Input
                      className="ml-4 mr-4"
                      type="checkbox"
                      name="tarjeta"
                      defaultChecked={tarjeta}
                      id="tarjeta"
                      placeholder="Emitir Factura"
                      onChange={e => setTarjeta(e.target.checked)}
                    />
                  </div>
                </div>
              </FormGroup>
              <FormGroup className="mb-2 mr-sm-2 mb-sm-2">
                <Label for="titulo" className="mr-sm-2 font-weight-bold">
                  REQUERIMIENTOS ADICIONALES DEL SERVICIO
                </Label>
                <Input
                  type="text"
                  name="requerimientos"
                  id="requerimientos"
                  placeholder="..."
                  onChange={e => {
                    handleChange(e);
                  }}
                />
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
