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
import UserList from "./Users/UserList";

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
              <Link to="/about">About</Link>
            </li>
          </ul>
        </Header>

        <Switch>
          <Route exact path="/">
            <Container type="card">
              <Home />
            </Container>
          </Route>
          <Route path="/help">
            <Container type="card">
              <About />
            </Container>
          </Route>
          <Route path="/buscar">
            <Container type="card"></Container>
          </Route>
          <Route path="/encontrar">
            <Container type="card"></Container>
          </Route>
          <Route path="*">
            <Container type="card">
              <NoMatch />
            </Container>
          </Route>
        </Switch>
      </Router>
    </div>
  );
}

export default App;
