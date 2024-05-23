import { useEffect, useRef, useState } from "react";
import "./ConsoleWindow.css";

function ConsoleWindow() {
  const [message, addMessage] = useState([]);
  const messageEnd = useRef<null | HTMLDivElement>(null);
  const ScrollToBottom = () => {
    messageEnd.current?.scrollIntoView({ behavior: "smooth" });
  };

  /* const ScrollToBottom = () => {
    const messageEnd = useRef<null | HTMLDivElement>(null);
    useEffect(() =>
      messageEnd.current?.scrollIntoView({
        behavior: "smooth",
        block: "end",
        inline: "nearest",
      })
    );
    return <div ref={messageEnd} />;
  }; */

  useEffect(() => {
    const sse = new EventSource("http://localhost:8080/sse", {
      withCredentials: true,
    });
    function getRealtimeData(data) {
      //addMessage(data);
      //addMessage((message) => message + "\n" + data);
      //addMessage(data);
      addMessage((message) => message.concat(data));
      console.log(message);
      ScrollToBottom();
    }
    sse.onmessage = (e) => getRealtimeData(e.data);
    sse.onerror = () => {
      console.log("Error Occurred With Console Logs!");
      sse.close();
    };

    return () => {
      sse.close();
    };
  }, []);

  return (
    <>
      <div className="mb-1 px-2 py-2" id="exampleFormControlTextarea1">
        <label htmlFor="exampleFormControlTextarea1" className="form-label">
          Logs
        </label>

        {/* <textarea
          className="form-control-plaintext px-2 py-2"
          value={message}
          id="exampleFormControlTextarea1"
          rows={3}
          readOnly
          disabled
        /> */}
        <div
          className="form-control-plaintext px-2 py-2"
          id="textArea1"
          aria-readonly
          aria-disabled
        >
          {message.map((message, index) => (
            <p key={index}>{message}</p>
          ))}
          <div ref={messageEnd} />
        </div>
      </div>
    </>
  );
}

export default ConsoleWindow;
