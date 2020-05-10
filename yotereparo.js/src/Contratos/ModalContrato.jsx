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
import ModalValoracion from "./ModalValoracion";

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
  const [showRateContractModal, setShowRateContract] = useState(false);
  const isProvider = props.isProvider;
  const [rateObject, setRateObject] = useState({});

  const handleAcciones = () => {
    setMenuAcciones(!menuAcciones);
  };

  const showRateContract = () => {
    setShowRateContract(!showRateContractModal);
  };

  const handleChangesRateModal = (event) => {
    setRateObject({ ...rateObject, [event.target.name]: event.target.value });
  };

  const rateContractFromModal = () => {
    let tempRate = {
      valoracion:
        rateObject.valoracion === undefined
          ? contrato.valoracion
          : rateObject.valoracion,
      descripcionValoracion:
        rateObject.descripcionValoracion === undefined
          ? contrato.descripcionValoracion
          : rateObject.descripcionValoracion,
    };
    props.rateContract(contrato.id, tempRate);
  };

  return (
    <div>
      <ModalValoracion
        isOpen={showRateContractModal}
        toggle={showRateContract}
        onChange={handleChangesRateModal}
        callbackAction={rateContractFromModal}
        valoracion={{
          puntaje: contrato.valoracion,
          desc: contrato.descripcionValoracion,
        }}
      ></ModalValoracion>
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
            <Button onClick={props.cancelModal} color="info">
              Aceptar
            </Button>
            <ButtonGroup>
              <ButtonDropdown isOpen={menuAcciones} toggle={handleAcciones}>
                <DropdownToggle caret color="danger">
                  Acciones de Contrato
                </DropdownToggle>
                <DropdownMenu>
                  <DropdownItem
                    onClick={() => props.finalizeContract(contrato.id)}
                    color="info"
                    disabled={
                      contrato.estado === "CANCELADO_USUARIO_PRESTADOR" ||
                      contrato.estado === "CANCELADO_USUARIO_FINAL" ||
                      contrato.estado === "FINALIZADO" ||
                      contrato.estado === "PENDIENTE"
                        ? true
                        : false
                    }
                  >
                    Finalizar
                  </DropdownItem>
                  {contrato.estado === "FINALIZADO" && !isProvider ? (
                    <DropdownItem onClick={showRateContract} color="info">
                      Valorar
                    </DropdownItem>
                  ) : (
                    <></>
                  )}
                  <DropdownItem
                    onClick={() => props.cancelContract(contrato.id)}
                    color="info"
                    disabled={
                      contrato.estado === "CANCELADO_USUARIO_PRESTADOR" ||
                      contrato.estado === "CANCELADO_USUARIO_FINAL" ||
                      contrato.estado === "FINALIZADO"
                        ? true
                        : false
                    }
                  >
                    Cancelar
                  </DropdownItem>
                </DropdownMenu>
              </ButtonDropdown>
            </ButtonGroup>
          </ModalFooter>
        </Form>
      </Modal>
    </div>
  );
}

export default ModalContrato;
