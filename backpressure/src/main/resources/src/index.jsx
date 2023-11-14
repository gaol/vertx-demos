import React, { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import HighchartsReact from 'highcharts-react-official';
import Highcharts from 'highcharts';

import './index.css';
import 'bootstrap/dist/css/bootstrap.min.css';

import EventBus from "@vertx/eventbus-bridge-client.js"

const eb = new EventBus("http://localhost:8000/eventbus");
eb.enableReconnect(true);

const baseRadius = 20;
const maxRadius = 400;

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

class LineChart extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      chartOptions: {
        title: {
          text: "Buffers In Memory For Downloading"
        },
        xAxis: {
          type: 'datetime',
          title: {
            text: "Time"
          }
        },
        yAxis: {
          title: {
            text: "Buffers In Memory"
          }
        },
        series: [
            {
              name: "In Memory",
              data: []
            }
        ]
      }
    };
    this.addNewPoint = this.addNewPoint.bind(this);
    window.chart = this;
  }

  addNewPoint(err, msg) {
    if (err) {
      console.log("Failed with error: " + err);
      return;
    }
    let body = msg["body"];
    let inMemory = body["buffers"];
    let times = body["times"] / 2;
    let inMemorySeries = {name: "In Memory", data: [...this.state.chartOptions.series[0].data, inMemory]};
    let newChartOptions = {...this.state.chartOptions, series: [inMemorySeries]};
    this.setState({chartOptions: newChartOptions});
  }

  render() {
    return (
      <HighchartsReact highcharts={Highcharts} options={this.state.chartOptions} />
    );
  }

}

class APP extends React.Component {

  constructor(props) {
    super(props);
    this.state = {radius: 20, color: "green", readBuffers: 0, writtenBuffers: 0, buffers: 0}
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
    let newReadBuffers = body["read"];
    let newWrittenBuffers = body["write"];
    let newBuffers = body["buffers"]
    let fileSize = body["fileSize"];
    let step = (newBuffers / fileSize) * (maxRadius - baseRadius);
    let newRadius = Math.min(Math.max(baseRadius, baseRadius + step), maxRadius);
    // set radius and color
    let color = "green";
    if (newRadius <= 100) {
      color = "green";
    } else if (newRadius > 100 && newRadius < 250) {
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
        window.chart.addNewPoint(err, msg);
      });
      console.log("vertx eventbus handler registered! ");
    };
  }

  render() {
      return (
        <div className="app">
          <table>
            <caption className="caption">
              Demo of buffer size used when downloading a big file
            </caption>
            <tbody>
              <tr>
                <td>
                    <div className="circle">
                      <div className="center">
                         <Circle radius={this.state.radius} color={this.state.color} />
                      </div>
                    </div>
                </td>
                <td>
                    <div className="chart"><LineChart /></div>
                </td>
              </tr>
            </tbody>
            <tfoot>
              <tr>
                <td colspan="2">
                  <div className="footer">
                    <span className="note">Total read: {this.state.readBuffers} bytes</span>
                    <span className="note">Total written: {this.state.writtenBuffers} bytes</span>
                    <span className="note">Buffers in memory: {this.state.buffers} bytes</span>
                  </div>
                </td>
              </tr>
            </tfoot>
          </table>
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