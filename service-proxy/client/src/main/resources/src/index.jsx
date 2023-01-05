import React from 'react';
import ReactDOM from 'react-dom/client';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import Alert from 'react-bootstrap/Alert';

import './index.css';
// Importing the Bootstrap CSS
import 'bootstrap/dist/css/bootstrap.min.css';


import EventBus from "@vertx/eventbus-bridge-client.js"
import DBService from "./services/vertx-db-service-js/db_service-proxy"
import JSSource from "./jssource"

const eb = new EventBus("http://localhost:8000/eventbus");
eb.enableReconnect(true);
// eb.enablePing(true);

const dbService = new DBService(eb, "db.service");

var eb_state = "off";
var initialized = false;


eb.onclose = () => {
  eb_state = "off";
};
eb.onerror = () => {
  eb_state = "off";
};


var listener = null;

class Messages extends React.Component {
  constructor(props) {
    super(props);
    this.state = {messages: []};
    this.load = this.load.bind(this);
    this.updateMessages = this.updateMessages.bind(this);
  }

  componentDidMount() {
    this.load();
    listener = this.load;
  }

  componentWillUnmount() {
    listener = null;
  }

  load() {
    if (eb_state === "on") {
      dbService.load((err, res) => {
        if (err) {
          console.log("Failed to load messages: " + err);
        } else {
          this.updateMessages(res)
        }
      });
    } else {
      console.log("EventBus does not open!");
    }
  }

  updateMessages(messages) {
    this.setState({"messages": messages});
  }

  render() {
    if (this.state.messages) {
      return (<div>
            {this.state.messages.map((item, index) => (
              <span className='indent' key={index} >Name: {item.name}, Message: {item.message}<span><hr /></span></span>
            ))}
      </div>);
    }
    return (<div>No Messages</div>);
  }
}

class APP extends React.Component {
  render() {
    return (
      <div><div className="header">Demo</div><div className="headerLong"><hr /></div>
        <Container className="p-3" fluid="lg">
          <Row>
            <Col>
              <div><JSSource /></div>
              <div className="delimer"><hr /></div>
              <div className="messageForm"><MessageForm /></div>
            </Col>
            <Col><Messages /></Col>
          </Row>
        </Container>
      </div>
    );
  }
}

class MessageForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {name: '', message: '', response: '', error: false};

    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleChange(event) {
    const target = event.target;
    const value = target.value;
    const name = target.name;
    this.setState({
      [name]: value
    });
  }

  handleSubmit(event) {
    console.log('A name was submitted: ' + this.state.name + ', message: ' + this.state.message);
    event.preventDefault();
    dbService.save({"name": this.state.name, "message": this.state.message}, (err, res) => {
      if (err) {
        this.setState({"response": err, "error": true});
      } else {
        this.setState({"name": '', "message": '', "response": "Message Sent!", "error": false});
        // trigger load
        if (listener != null) {
          listener()
        }
      }
    });

  }

  render() {
    return (
      <Form onSubmit={this.handleSubmit}>
      <Form.Group className="mb-3">
        <Form.Label>Name</Form.Label>
        <Form.Control type="text" placeholder="Enter Name" name="name" onChange={this.handleChange} value={this.state.name} />
        <Form.Text className="text-muted">
          The Name you want to send message to.
        </Form.Text>
      </Form.Group>

      <Form.Group className="mb-3">
        <Form.Label>Message</Form.Label>
        <Form.Control as="textarea" placeholder="Input the Message" name="message" onChange={this.handleChange} value={this.state.message} />
        <Form.Text className="text-muted">
          The message you want to send.
        </Form.Text>
      </Form.Group>
      <Alert key="info" variant={this.state.error ? 'danger' : 'info'}>{this.state.response}</Alert>
      <Button variant="primary" type="submit">Send</Button>
    </Form>
    );
  }
}

eb.onopen = () => {
  if (initialized) {
    console.log("EventBus got closed, it should try again.");
    return;
  }
  eb_state = "on";
  initialized = true;
  const root = ReactDOM.createRoot(document.getElementById('root'));
  root.render(<APP />);
};