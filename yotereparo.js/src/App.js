import React from "react";
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
import FindUsers from "./Find/FindUsers";
import Tour from "./Tour/Tour";
import ElementContainer from "./Container/ElementContainer";

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

function App() {
  return (
    <div>
      <Router>
        <Header>
          <ul>
            <li>
              <Link to="/">Home</Link>
            </li>
            <li>
              <Link to="/registro">Registrarte</Link>
            </li>
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
          <Route path="/buscar">
            <Container>
              <FindUsers></FindUsers>
            </Container>
          </Route>
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
    </div>
  );
}

export default App;
