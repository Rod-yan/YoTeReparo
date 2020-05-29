import React from "react";
import ElementContainer from "../Container/ElementContainer";
import MensajesForm from "./MensajesForm";
import { useState } from "react";
import { useEffect } from "react";
import Axios from "axios";
import { useContext } from "react";
import { SessionContext } from "../Utils/SessionManage";

function Mensajes(props) {
  const [comentario, setComentario] = useState("");
  const [mensajes, setMensajes] = useState([]);
  const [activeForm, setActiveForm] = useState(false);
  const { session } = useContext(SessionContext);
  const [reload, setReload] = useState(false);

  let requestConfig = {
    headers: {
      "Access-Control-Allow-Origin": "*",
      Authorization: "Bearer " + session.security.accessToken,
    },
  };

  const handleSubmitForm = (event) => {
    let mensajeTemp = {
      servicio: props.contentService,
      consulta: comentario.trim(),
    };
    CreateMensaje(mensajeTemp);
  };

  const CreateMensaje = (mensaje) => {
    ResetTextArea();

    Axios.post(
      "http://localhost:8080/YoTeReparo/messages",
      mensaje,
      requestConfig
    )
      .then((response) => {
        if (response.status === 400) {
          console.log(response.json);
        } else {
          console.log("200 OK");
          setReload(!reload);
        }
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const handleChangeForm = (event) => {
    setComentario(event.target.value);
  };

  function ResetTextArea() {
    setActiveForm(true);
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
    fetchData(
      `http://localhost:8080/YoTeReparo/services/${props.contentService}`
    ).then((resp) => {
      if (resp.response != null) {
      } else {
        setMensajes(resp.data.mensajes);
      }
    });
  }, [reload]);

  return (
    <>
      <div className="mt-4">
        <ElementContainer>
          <div className="lead text-center font-weight-bold">Mensajes</div>
          <hr></hr>
          <MensajesForm
            onSubmit={handleSubmitForm}
            onChange={handleChangeForm}
            numberCharLeft={200 - comentario.length}
            disableForm={activeForm}
            mensaje={comentario}
          />
          <hr></hr>
          <div className="row">
            {!(mensajes.length > 0) ? (
              <div className="col-12">
                <div className="lead text-center">
                  No hay mensajes actualmente para este servicio
                </div>
              </div>
            ) : (
              <>
                <div className="col-12 lead">
                  <div>
                    {mensajes.map((x) => {
                      return <div key={x.id}>{x.consulta}</div>;
                    })}
                  </div>
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
