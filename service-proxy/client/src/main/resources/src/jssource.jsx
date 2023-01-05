import SyntaxHighlighter from "react-syntax-highlighter";
import github from "react-syntax-highlighter/dist/esm/styles/hljs/github";

const code = `
import EventBus from "@vertx/eventbus-bridge-client.js"
import DBService from "./services/vertx-db-service-js/db_service-proxy"
const eb = new EventBus("http://localhost:8000/eventbus");
const dbService = new DBService(eb, "db.service");
dbService.load((err, res) => {
  if (err) {
    console.log("Failed to load messages: " + err);
  } else {
    this.updateMessages(res)
  }
});
`

function JSSource() {
  return <SyntaxHighlighter children={code} language="javascript" style={github} />;
}

export default JSSource