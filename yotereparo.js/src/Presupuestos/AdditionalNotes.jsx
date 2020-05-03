import React, { useRef } from "react";
import { Label, Input, Form, FormGroup } from "reactstrap";

function AdditionalNotes(props) {
  const minDate = useRef(null);

  return (
    <>
      <Form>
        <FormGroup className="mb-2 mr-sm-2 mb-sm-2">
          <Label
            for="cantidadTrabajadores"
            className="mr-sm-2 font-weight-bold"
          >
            NOTAS ADICIONALES
          </Label>
          <Input
            type="text"
            name="descripcionSolicitud"
            id="descripcionSolicitud"
            placeholder="Especifique una descripcion adicional para el servicio a solicitar..."
            onChange={(e) => {
              props.onHandleChange(e);
            }}
          ></Input>
        </FormGroup>
        <FormGroup className="mb-2 mr-sm-2 mb-sm-2">
          <div className="row">
            <div className="col-md-6">
              <Label
                for="fechaInicioEjecucionPropuesta"
                className="mr-sm-2 font-weight-bold"
              >
                FECHA ESTIMADA / INICIO DE VISITA
              </Label>
              <Input
                type="date"
                name="fechaInicioEjecucionPropuesta"
                ref={minDate}
                id="fechaInicioEjecucionPropuesta"
                onChange={(e) => {
                  props.onHandleChange(e);
                }}
              ></Input>
            </div>
            <div className="col-md-6">
              <Label
                for="fechaFinEjecucionPropuesta"
                className="mr-sm-2 font-weight-bold"
              >
                FECHA ESTIMADA / FIN DE VISITA
              </Label>
              <Input
                type="date"
                name="fechaFinEjecucionPropuesta"
                id="fechaFinEjecucionPropuesta"
                min={minDate.current?.value}
                onChange={(e) => {
                  props.onHandleChange(e);
                }}
              ></Input>
            </div>
          </div>
        </FormGroup>
        <FormGroup className="mb-2 mt-2 mr-sm-2 mb-sm-2">
          <Label
            for="horaInicioEjecucionPropuesta"
            className="mr-sm-2 font-weight-bold"
          >
            HORA ESTIMADA DE VISITA
          </Label>
          <Input
            type="time"
            step="2"
            defaultValue="00:00:00"
            name="horaInicioEjecucionPropuesta"
            id="horaInicioEjecucionPropuesta"
            onChange={(e) => {
              props.onHandleChange(e);
            }}
          ></Input>
        </FormGroup>
        <FormGroup className="mb-2 mt-2 mr-sm-2 mb-sm-2">
          <div className="row">
            <div className="col-md-6">
              <div className="text-center">
                <Label
                  for="incluyeAdicionales"
                  className="mr-sm-2 font-weight-bold"
                >
                  INLCUYE ADICIONALES
                </Label>
                <Input
                  className="ml-4 mr-4"
                  type="checkbox"
                  name="incluyeAdicionales"
                  id="incluyeAdicionales"
                  defaultChecked={props.adicionales}
                  placeholder="Incluye Adicionales"
                  onChange={(e) => {
                    props.setAdicionales(e.target.checked);
                  }}
                />
              </div>
            </div>
            <div className="col-md-6 ">
              {" "}
              <div className="text-center">
                <Label
                  for="incluyeInsumos"
                  className="mr-sm-2 font-weight-bold"
                >
                  INCLUYE INSUMOS
                </Label>
                <Input
                  className="ml-4 mr-4"
                  type="checkbox"
                  name="incluyeInsumos"
                  id="incluyeInsumos"
                  defaultChecked={props.insumos}
                  placeholder="Incluye Insumos"
                  onChange={(e) => {
                    props.setInsumos(e.target.checked);
                  }}
                />
              </div>
            </div>
          </div>
        </FormGroup>
      </Form>
    </>
  );
}

export default AdditionalNotes;
