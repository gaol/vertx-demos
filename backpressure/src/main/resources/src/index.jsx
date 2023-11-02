import React, { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';

import './index.css';
import 'bootstrap/dist/css/bootstrap.min.css';

import EventBus from "@vertx/eventbus-bridge-client.js"

const eb = new EventBus("http://localhost:8000/eventbus");
eb.enableReconnect(true);

const baseRadius = 50;
const maxRadius = 600;

class Circle extends React.Component {
    render() {
        const circleStyle = {
          backgroundColor: this.props.color,
          width: this.props.radius,
          height: this.props.radius,
          borderRadius: this.props.radius/2,
          margin: "auto"
        };
      return (
        <div style={circleStyle} />
      );
    }
}


class APP extends React.Component {

  constructor(props) {
    super(props);
    this.state = {radius: 50, color: "green", readBuffers: 0, writtenBuffers: 0, buffers: 0}
    this.updateSize = this.updateSize.bind(this);
    window.app = this;
  }

  updateSize(err, msg) {
    // update circle dimension and color
    if (err) {
      console.log("Failed with error: " + err);
      return;
    }
    let body = msg["body"];
    let fileSize = body["fileSize"];
    let newBuffers = this.state.buffers;
    let newReadBuffers = this.state.readBuffers;
    if (body["read"]) {
      newReadBuffers = body["read"];
    }
    let newWrittenBuffers = this.state.writtenBuffers;
    if (body["write"]) {
      newWrittenBuffers = body["write"];
    }
    newBuffers = newReadBuffers - newWrittenBuffers;
    console.log("newBuffers: " + newBuffers);
    let step = (newBuffers / fileSize) * (maxRadius - baseRadius);
    let newRadius = Math.min(Math.max(baseRadius, baseRadius + step), maxRadius);
    console.log("Step is: " + step + ", new Radius is: " + newRadius + ", fileSize: " + fileSize);
    // set radius and color
    let color = "green";
    if (newRadius <= 200) {
      color = "green";
    } else if (newRadius > 200 && newRadius < 400) {
      color = "orange";
    } else {
      color = "red";
    }
    this.setState({radius: newRadius, color: color, readBuffers: newReadBuffers, writtenBuffers: newWrittenBuffers, buffers: newBuffers});
  }

  componentDidMount() {
    eb.onopen = function () {
      console.log("Connected to the event bus");
      eb.registerHandler("buffer.update", function (err, msg) {
        window.app.updateSize(err, msg);
      });
      console.log("vertx eventbus handler registered! ");
    };
  }

  render() {
      return (
        <div className="app">
            <div className="container">
              <h1>Demo of buffer size in server</h1>
              <div className="center">
               <Circle radius={this.state.radius} color={this.state.color} />
              </div>
            </div>
            <div className="footer">
              <span className="note">Total buffers read: {this.state.readBuffers} bytes</span>
              <span className="note">Total buffers written: {this.state.writtenBuffers} bytes</span>
              <div className="footer">
                <span className="note">Buffers in server currently: {this.state.buffers} bytes</span>
              </div>
            </div>
        </div>
      );
  }

}

const root = createRoot(document.getElementById('root'));
root.render(
  <StrictMode>
    <APP />
  </StrictMode>
);