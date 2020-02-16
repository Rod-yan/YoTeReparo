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

const NoMatch = () => {
  let location = useLocation();

  return (
    <div>
      <h3>
        No match for <code>{location.pathname}</code>
      </h3>
    </div>
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
              <Link to="/help">About</Link>
            </li>
          </ul>
        </Header>

        <Switch>
          <Route path="/registrar-usuario">
            <FormRegistro type="usuario"></FormRegistro>
          </Route>
          <Route path="/registrar-empleador">
            <FormRegistro type="empleador"></FormRegistro>
          </Route>
          <Container>
            <Route exact path="/">
              <Home />
            </Route>
            <Route path="/help">
              <About />
            </Route>
            <Route path="/buscar"></Route>
            <Route path="/encontrar"></Route>
            <Route path="/registro">
              <SelectorDeCategorias />
            </Route>
          </Container>
          <Route path="*">
            <NoMatch />
          </Route>
        </Switch>
      </Router>
    </div>
  );
}

export default App;
