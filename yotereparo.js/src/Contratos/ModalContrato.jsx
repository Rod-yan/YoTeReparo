import React from "react";
import {
  Button,
  ButtonGroup,
  ButtonDropdown,
  DropdownMenu,
  DropdownToggle,
  DropdownItem,
  Modal,
  ModalHeader,
  ModalBody,
  ModalFooter,
  InputGroup,
  InputGroupAddon,
  Badge,
  Input,
  Form,
  Label,
} from "reactstrap";
import { useState } from "react";
import { LogOutHandler } from "../Utils/SessionHandlers";

const convertToFecha = (fechaJson) => {
  if (fechaJson) {
    var curr = new Date(
      fechaJson.year,
      fechaJson.dayOfMonth,
      fechaJson.dayOfWeek
    );
    var date = curr.toISOString().substr(0, 10);
    return date;
  }
};

function ModalContrato(props) {
  const contrato = props.contrato;
  const fechaCreacion = convertToFecha(contrato.fechaCreacion) || "";
  const fechaFinEjecucion = convertToFecha(contrato.fechaFinEjecucion) || "";
  const [menuAcciones, setMenuAcciones] = useState(false);

  const handleAcciones = () => {
    setMenuAcciones(!menuAcciones);
  };

  return (
    <div>
      <Modal isOpen={props.isOpen} toggle={props.cancelModal}>
        <Form>
          <ModalHeader toggle={props.cancelModal}>
            Contrato <Badge color="secondary">{contrato.estado}</Badge>
          </ModalHeader>
          <ModalBody>
            <InputGroup className="mt-2 mb-2">
              <InputGroupAddon addonType="prepend">
                Numero de Contrato
              </InputGroupAddon>
              <Input disabled defaultValue={contrato.id} />
            </InputGroup>
            <InputGroup className="mt-2 mb-2">
              <InputGroupAddon addonType="prepend">
                Precio Final
              </InputGroupAddon>
              <Input disabled defaultValue={contrato.precioFinal} />
              <InputGroupAddon addonType="append">$</InputGroupAddon>
            </InputGroup>
            <InputGroup className="mt-2 mb-2">
              <InputGroupAddon addonType="prepend">Valoracion</InputGroupAddon>
              <Input
                disabled
                type="number"
                min="0"
                max="10"
                value={contrato.valoracion || 0}
              />
            </InputGroup>
            <div className="row">
              <div className="col-md-6">
                <Label className="lead">Fecha de Creacion</Label>
                <InputGroup>
                  <Input disabled value={fechaCreacion} type="date" />
                </InputGroup>
              </div>
              <div className="col-md-6">
                <Label className="lead">Fecha de Fin</Label>
                <InputGroup>
                  <Input disabled value={fechaFinEjecucion} type="date" />
                </InputGroup>
              </div>
            </div>
          </ModalBody>
          <ModalFooter>
            <ButtonGroup>
              <ButtonDropdown isOpen={menuAcciones} toggle={handleAcciones}>
                <DropdownToggle caret>Acciones de Contrato</DropdownToggle>
                <DropdownMenu>
                  <DropdownItem onClick={props.cancelModal} color="info">
                    Finalizar
                  </DropdownItem>
                  <DropdownItem onClick={props.cancelModal} color="info">
                    Archivar
                  </DropdownItem>
                  <DropdownItem onClick={props.cancelModal} color="info">
                    Cancelar
                  </DropdownItem>
                </DropdownMenu>
              </ButtonDropdown>
            </ButtonGroup>

            <Button onClick={props.cancelModal} color="danger">
              Aceptar
            </Button>
          </ModalFooter>
        </Form>
      </Modal>
    </div>
  );
}

export default ModalContrato;
