import React from "react";
import ElementContainer from "../Container/ElementContainer";
import { useLocation, useHistory } from "react-router-dom";
import { useState } from "react";
import { Button, Modal, ModalHeader, ModalBody, ModalFooter } from "reactstrap";
import "../Servicios/Servicios.css";

const Servicio = props => {
  let location = useLocation();
  let history = useHistory();

  const [modal, setModal] = useState(false);
  const body = location.state.body;
  const title = location.state.title;
  const provider = location.state.provider;
  const avaliable = location.state.avaliable;
  const estimateTime = location.state.estimateTime;
  const averagePrice = location.state.averagePrice;

  const toggle = () => setModal(!modal);

  return (
    <>
      <ElementContainer>
        <div className="row">
          <div className="col-12">
            <div className="card mb">
              <div className="card card-element">
                <div className="row no-gutters">
                  <div className="col-md-4 my-auto">
                    <img
                      src="https://via.placeholder.com/150/92c952"
                      className="card-img rounded-circle"
                      alt="placeholder"
                    ></img>
                  </div>
                  <div className="col-md-8 text-right">
                    <div className="card-body">
                      <div className="row">
                        <div className="col-md-12">
                          <h3 className="card-title">
                            {props.match.params.id}
                          </h3>
                          <h3 className="card-title"> {title}</h3>
                          {body}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div className="row mx-auto">
                  <div className="col-md-12">
                    {" "}
                    <div
                      className="btn btn-info ml-2 mr-2"
                      onClick={() => {
                        history.go(-1);
                      }}
                    >
                      <i className="fas fa-chevron-circle-left fa-2x"></i>
                    </div>
                    <div className="btn btn-danger ml-2 mr-2" onClick={toggle}>
                      <i className="fas fa-concierge-bell fa-2x"></i>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </ElementContainer>
      <Modal isOpen={modal} toggle={toggle} size="xl">
        <ModalHeader toggle={toggle}>{title}</ModalHeader>
        <ModalBody>
          El servicio que esta por contratar no se relaciona directamente con
          nuestra organizacion. Te pedimos por favor, que denuncies todo usuario
          que no cumple con las normas de YoTeReparo.com para el bien tuyo y de
          la comunidad.
          <div className="table-responsive mt-4">
            <table className="table">
              <thead className="thead-dark">
                <tr>
                  <th scope="col">Nombre del servicio</th>
                  <th scope="col">Descripcion del servicio</th>
                  <th scope="col">Proveedor</th>
                  <th scope="col">Disponiblidad</th>
                  <th scope="col">Tiempo Estimado</th>
                  <th scope="col">Precio Promedio</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <th scope="row">{title}</th>
                  <td>{body}</td>
                  <td>{provider}</td>
                  <td>{avaliable}</td>
                  <td>{estimateTime}</td>
                  <td>{averagePrice}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </ModalBody>
        <ModalFooter>
          <Button color="danger" onClick={toggle}>
            CONTRATAR
          </Button>{" "}
          <Button color="info" onClick={toggle}>
            Cancel
          </Button>
        </ModalFooter>
      </Modal>
    </>
  );
};

export default Servicio;
