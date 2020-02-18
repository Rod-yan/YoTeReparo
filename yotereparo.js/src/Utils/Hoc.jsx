import React from "react";

export default function Hoc(Component, data) {
  return class extends React.Component {
    constructor(props) {
      super(props);
      this.state = {
        data: data
      };
    }

    render() {
      return <Component data={this.state.data} {...this.props}></Component>;
    }
  };
}
