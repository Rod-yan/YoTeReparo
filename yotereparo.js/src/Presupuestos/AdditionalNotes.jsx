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
            placeholder="Escribí aquí cualquier detalle de importancia que quieras que lea el usuario prestador..."
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
                FECHA ESTIMADA DE INICIO DEL SERVICIO
              </Label>
			    <br/>Ingresá la fecha que te convenga para la cual 
				se iniciaría la prestación del servicio.
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
                FECHA ESTIMADA DE FIN DEL SERVICIO
              </Label>
				<br/>Ingresá la fecha para la que necesitás
				que el servicio ya se haya completado. Podés
				omitir este dato.
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
				<br/>¿A qué hora te queda bien?<br/>
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
                  ¿DESEAS INCLUIR ADICIONALES?
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
                  ¿DESEAS INCLUIR INSUMOS?
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
