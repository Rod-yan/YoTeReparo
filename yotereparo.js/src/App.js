import React, { useState, useEffect } from "react";
import Home from "./Home/Home";
import About from "./About/About";
import Header from "./Header/Header";
import { Button } from "reactstrap";
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link,
  useLocation
} from "react-router-dom";
import "./App.css";
import Container from "./Container/Container";
import SelectorDeCategorias from "./Login/SelectorDeCategorias";
import FormRegistro from "./Login/RegistroUsuarios";
import EncontrarServicios from "./Find/EncontrarServicios";
import Tour from "./Tour/Tour";
import ElementContainer from "./Container/ElementContainer";
import PerfilUsuario from "./Usuarios/PerfilUsuario";
import { createBrowserHistory } from "history";
import {
  setSessionCokie,
  deleteSessionCookie,
  getSessionCookie,
  SessionContext
} from "./Utils/SessionManage";
import Servicio from "./Servicios/Servicio";
import Axios from "axios";

const NoMatch = () => {
  let location = useLocation();

  return (
    <>
      {" "}
      <ElementContainer>
        <div className="card-center-form d-flex align-items-center mx-auto">
          <div className="row">
            <div className="col-xs-12">
              Donde sea que estes yendo, este no es el camino.
              <br></br>
              <br></br>
              <code>
                <strong>{location.pathname}</strong>
              </code>
            </div>
          </div>
        </div>
      </ElementContainer>
    </>
  );
};

const LoginHandler = ({ history }) => {
  const [username, setUsername] = useState("");
  const [loadingUser, setLoadingUser] = useState(false);

  const handleSubmit = async event => {
    event.preventDefault();
    let result;

    try {
      setLoadingUser(true);
      result = await Axios(`http://localhost:8080/YoTeReparo/users/${username}`)
        .then(response => {
          console.log(response);
          setLoadingUser(false);
        })
        .catch(error => {
          return error;
        });
    } catch (error) {
      console.log(error.response);
    }

    if (username && result.response.status !== 404) {
      setSessionCokie({ username });
      history.push("/");
      window.location.reload();
    } else {
      setLoadingUser(false);
      console.log(
        "ERROR: La request solicitada a USERS fue incorrecta [Status Code] " +
          result.response.status
      );
    }
  };

  return (
    <Container>
      <div className="centered">
        <div className="row">
          <div className="col-md-12">
            <ElementContainer>
              <div className="container">
                <h1 className="text-center">
                  <span className="fa-stack fa-2x">
                    <i
                      className={`fas fa-user fa-stack-4x fa-1x success-icon-logo`}
                    ></i>
                  </span>
                  <div className="lead">
                    <h3>INGRESAR</h3>
                  </div>
                </h1>
                <div className="lead text-center">
                  Ingresa con sus datos para empezar a buscar sus soluciones mas
                  cercanas...
                </div>
                <div className="mt-4">
                  <form onSubmit={handleSubmit}>
                    <input
                      type="string"
                      placeholder="Ingresa tu nombre de usuario"
                      value={username}
                      className="form-control btn-block"
                      onChange={event => setUsername(event.target.value)}
                      required
                    />
                    <Button color="danger" size="lg" block className="mt-4">
                      {!loadingUser ? (
                        "INGRESAR"
                      ) : (
                        <div className="spinner-border" role="status">
                          <span className="sr-only">Creando Usuario...</span>
                        </div>
                      )}
                    </Button>
                  </form>
                </div>
              </div>
            </ElementContainer>
          </div>
        </div>
      </div>
    </Container>
  );
};

const LogOutHandler = ({ history }) => {
  useEffect(() => {
    deleteSessionCookie("userSession");
    history.push("/ingresar");
    window.location.reload();
  }, [history]);

  return <div>Has salido de la aplicación</div>;
};

function App() {
  const history = createBrowserHistory();
  const [session, setSession] = useState(getSessionCookie());

  useEffect(() => {
    const newCookie = getSessionCookie();
    if (newCookie.uniqueId !== session.uniqueId) {
      setSession(newCookie);
    }
  }, [session]);

  return (
    <div>
      <SessionContext.Provider value={session}>
        <Router history={history}>
          <Header>
            <ul>
              <li>
                <Link to="/">Home</Link>
              </li>
              {session.username === undefined ? (
                <>
                  <li>
                    <Link to="/registro">Registrarte</Link>
                  </li>
                  <li>
                    <Link to="/ingresar">Ingresar</Link>
                  </li>
                </>
              ) : (
                <>
                  <li>
                    <Link to={"/perfil/" + session.username}>Perfil</Link>
                  </li>
                  <li>
                    <Link to={"/salir"}>Salir</Link>
                  </li>
                </>
              )}
            </ul>
          </Header>

          <Switch>
            <Route exact path="/">
              <Container>
                <Home />
              </Container>
            </Route>

            <Route path="/help">
              <Container>
                <About />
              </Container>
            </Route>

            <Route path="/ingresar" component={LoginHandler} />
            <Route path="/salir" component={LogOutHandler} />

            <Route
              path="/perfil/:userId"
              render={props => (
                <Container>
                  <PerfilUsuario {...props} />
                </Container>
              )}
            />
            <Route
              path="/buscar"
              render={props => (
                <Container>
                  <EncontrarServicios {...props}></EncontrarServicios>
                </Container>
              )}
            />

            <Route
              path="/servicio/:id"
              render={props => (
                <Container>
                  <Servicio {...props}></Servicio>
                </Container>
              )}
            />

            <Route path="/registro">
              <Container>
                <SelectorDeCategorias />
              </Container>
            </Route>

            <Route path="/registrar-usuario">
              <Container>
                <FormRegistro type="usuario"></FormRegistro>
              </Container>
            </Route>

            <Route path="/registrar-empleador">
              <Container>
                <FormRegistro type="empleador"></FormRegistro>
              </Container>
            </Route>

            <Route path="/tour">
              <Container>
                <Tour />
              </Container>
            </Route>

            <Route>
              <Container>
                <NoMatch></NoMatch>
              </Container>
            </Route>
          </Switch>
        </Router>
      </SessionContext.Provider>
    </div>
  );
}

export default App;
