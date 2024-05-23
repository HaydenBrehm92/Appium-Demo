import { useEffect, useState } from "react";
import Dropdown from 'react-bootstrap/Dropdown';
import DropdownButton from 'react-bootstrap/DropdownButton';
import DropdownItem from "react-bootstrap/esm/DropdownItem";
import Form from 'react-bootstrap/Form'

function TestList(onTestHandle) {
  //let targets = ["Target 1", "Target 2", "Target 3", "Target 4", "Target 5"];

  const fetchTests = () => {
    fetch("http://localhost:8080/output-data/available-tests")
      .then((response) => response.json())
      .then((json) => setTestList(json))
      .catch((error) => console.error(error));
  };

  const [testList, setTestList] = useState(-1);
  const [selectedTest, setTest] = useState('')  //This is in parent now
  const [selectedTestIndex, setTestIndex] = useState(-1)

  useEffect(() => {
    fetchTests();
  }, []);

  function onTestSelectChanged(event) {
    console.log("value", event.target.value);
    setTest(event.target.value);
    onTestHandle.onTestHandle(event.target.value);
    fetch("http://localhost:8080/test-data/set-test?testID=" + event.target.value);
  };
  // JSX: Javascript XML
  // babeljs.io
  return (
    <>
      <div id="TestList-div">
        <h3>Test</h3>
        <Form.Select aria-label="Select an option"
                    value={selectedTest}
                    onChange={onTestSelectChanged} >
          <option>Select a test.</option>
          { //Dynamic options
          Object.values(testList).map((item, index) => (
          <option
            className={
              selectedTestIndex === index
                ? "list-group-item active"
                : "list-group-item"
            }
            key={item}
            onClick={() => {
              setTestIndex(index);
            }}
          >
            {item}
          </option>
          ))}
        </Form.Select>
      </div>
    </>
  );
}

export default TestList;
