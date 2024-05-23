import { useEffect, useState } from "react";
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import './TestControlButtons.css';

//NOTE: Not tested yet because there is no such thing as JSON for the Tests List, but it will have this on the future.
// This is just a setup file to cut time.

function TestControlButtons() {
  //let targets = ["Target 1", "Target 2", "Target 3", "Target 4", "Target 5"];

  const fetchTests = () => {
    fetch("http://localhost:8080/test/all")
      .then((response) => response.json())
      .then((json) => setTestList(json))
      .catch((error) => console.error(error));
  };

  const [testList, setTestList] = useState(-1);
  const [selectedTest, setTest] = useState('')
  const [selectedTestIndex, setTestIndex] = useState(-1)

  useEffect(() => {
    fetchTests();
  }, []);



  const onSelectChanged = (event: React.ChangeEvent<HTMLSelectElement>) => {
    console.log("value", event.target.value);
    setTest(event.target.value);
  };

  // JSX: Javascript XML
  // babeljs.io
  return (
    <>
      <div id="Buttons-div">
        <h3>Buttons</h3>
        <Button type="submit" as="input" value={"Start Test"}></Button>
        <Button type="submit" as="input" value={"Pause"}></Button>
        <Button type="submit" as="input" value={"Cancel"}></Button>
      </div>
    </>
  );
}

export default TestControlButtons;
