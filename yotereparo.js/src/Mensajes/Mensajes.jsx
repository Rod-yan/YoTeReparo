import React from "react";
import ElementContainer from "../Container/ElementContainer";
import MensajesForm from "./MensajesForm";
import { useState } from "react";
import { useEffect } from "react";
import Axios from "axios";
import { useContext } from "react";
import { SessionContext } from "../Utils/SessionManage";
import Mensaje from "./Mensaje";
import ModalRespuestaMensaje from "./ModalRespuestaMensaje";

function Mensajes(props) {
  const [comentario, setComentario] = useState("");
  const [respuesta, setRespuesta] = useState("");
  const [idPregunta, setIdPregunta] = useState(null);
  const [mensajes, setMensajes] = useState([]);
  const [activeForm, setActiveForm] = useState(false);
  const [errorsValidation, setErrorsValidation] = useState(false);
  const [loading, setLoading] = useState(true);
  const { session } = useContext(SessionContext);
  const [reload, setReload] = useState(false);
  const [isReplyModalOpen, SetIsReplyModalOpen] = useState(false);

  var isFormEmpleador = session.security.roles.length > 1 ? true : false;

  let requestConfig = {
    headers: {
      "Access-Control-Allow-Origin": "*",
      Authorization: "Bearer " + session.security?.accessToken,
    },
  };

  const handleSubmitForm = () => {
    let mensajeTemp = {
      servicio: props.contentService,
      consulta: comentario.trim(),
    };
    CreateMensaje(mensajeTemp);
  };

  const CreateMensaje = (mensaje) => {
    ResetTextArea();
    setLoading(true);
    Axios.post("/YoTeReparo/messages", mensaje, requestConfig)
      .then((response) => {
        if (response.status === 400) {
          console.log(response.json);
        } else {
          console.log("200 OK");
          setReload(!reload);
          ActiveTextArea();
        }
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const CreateRespuesta = (respuesta) => {
    var temp = mensajes.filter((x) => x.id == idPregunta);
    temp[0].state = "loading";

    Axios.put(`/YoTeReparo/messages/${idPregunta}`, respuesta, requestConfig)
      .then((response) => {
        if (response.status === 400) {
          console.log(response.json);
        } else {
          console.log("200 OK");
          setReload(!reload);
          temp[0].state = "ready";
        }
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const handleChangeForm = (event) => {
    setComentario(event.target.value);
  };

  const handleModalReplyText = (event) => {
    setRespuesta(event.target.value);
  };

  const handleReply = (idPregunta) => {
    showReplyForm();
    setIdPregunta(idPregunta);
  };

  const onModalReply = () => {
    if (respuesta.length < 1) {
      setErrorsValidation(true);
    } else {
      let tempObject = {
        respuesta: respuesta,
      };
      CreateRespuesta(tempObject);
      SetIsReplyModalOpen(false);
      setLoading(false);
    }
  };

  function showReplyForm() {
    SetIsReplyModalOpen(!isReplyModalOpen);
  }

  function ResetTextArea() {
    setActiveForm(true);
    setComentario("");
  }

  function ActiveTextArea() {
    setActiveForm(false);
    setComentario("");
  }

  useEffect(() => {
    const fetchData = async (urlToFetch) => {
      const result = await Axios(urlToFetch, requestConfig)
        .then((resp) => {
          return resp;
        })
        .catch((error) => {
          return error;
        });
      return result;
    };
    fetchData(`/YoTeReparo/services/${props.contentService}`).then((resp) => {
      if (resp.response != null) {
      } else {
        var mensajesWithState = [];
        resp.data.mensajes.map((x) => {
          let temp = { id: x.id, data: x, state: "ready" };
          mensajesWithState.push(temp);
        });
        setMensajes(mensajesWithState);
        setLoading(false);
      }
    });
  }, [reload]);

  return (
    <>
      <div className="mt-4">
        <ElementContainer>
          <div className="lead text-center font-weight-bold">Mensajes</div>
          <ModalRespuestaMensaje
            isReplyModalOpen={isReplyModalOpen}
            openReplyModal={showReplyForm}
            onModalReply={onModalReply}
            isErrors={errorsValidation}
            onChangeText={handleModalReplyText}
            textRespuesta={respuesta}
          />
          {!isFormEmpleador && (
            <>
              <hr></hr>
              <MensajesForm
                onSubmit={handleSubmitForm}
                onChange={handleChangeForm}
                numberCharLeft={200 - comentario.length}
                disableForm={activeForm}
                mensaje={comentario}
              />
            </>
          )}

          <hr></hr>
          <div className="row">
            {!(mensajes.length > 0) ? (
              loading === true ? (
                <div className="col-12 text-center">
                  <div className="spinner-border" role="status">
                    <span className="sr-only">Loading...</span>
                  </div>
                </div>
              ) : (
                <div className="col-12">
                  <div className="lead text-center">
                    No hay mensajes actualmente para este servicio
                  </div>
                </div>
              )
            ) : (
              <>
                <div className="col-12 lead">
                  <>
                    {mensajes.map((x) => {
                      return (
                        <div key={x.id}>
                          <Mensaje
                            onReply={handleReply}
                            esPrestador={isFormEmpleador}
                            content={{
                              object: x.data,
                              loading: x.state,
                            }}
                          />
                        </div>
                      );
                    })}
                  </>
                </div>
              </>
            )}

            <div className="col-4 muted"></div>
          </div>
        </ElementContainer>
      </div>
    </>
  );
}

export default Mensajes;
