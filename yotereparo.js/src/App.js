import React, { useState, useEffect } from "react";
import Home from "./Home/Home";
import About from "./About/About";
import Header from "./Header/Header";
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
  const [email, setEmail] = useState("");

  const handleSubmit = async event => {
    event.preventDefault();
    //GOLPEAR LA API DE LOGUEO Y DEVOLVER ID DE USUARIO LOGUEADO
    setSessionCokie({ email });
    history.push("/");
    window.location.reload();
  };

  return (
    <div style={{ marginTop: "1rem" }}>
      <form onSubmit={handleSubmit}>
        <input
          type="email"
          placeholder="Ingresa tu correo electronico"
          value={email}
          onChange={event => setEmail(event.target.value)}
        />
        <input type="submit" value="Login" />
      </form>
    </div>
  );
};

const LogOutHandler = ({ history }) => {
  useEffect(() => {
    deleteSessionCookie("userSession");
    history.push("/ingresar");
  }, [history]);

  return <div>Has salido de la aplicación</div>;
};

function App() {
  const history = createBrowserHistory();
  const [session, setSession] = useState(getSessionCookie());

  useEffect(() => {
    setSession(getSessionCookie());
  }, []);

  console.log(session);

  return (
    <div>
      <SessionContext.Provider value={session}>
        <Router history={history}>
          <Header>
            <ul>
              <li>
                <Link to="/">Home</Link>
              </li>
              {session.email === undefined ? (
                <li>
                  <Link to="/registro">Registrarte</Link>
                </li>
              ) : (
                <li>
                  <Link to={"/perfil/" + session.email}>Perfil</Link>
                </li>
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

            <Route path="/encontrar"></Route>

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
