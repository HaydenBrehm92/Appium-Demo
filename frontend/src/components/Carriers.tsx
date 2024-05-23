import { useEffect, useState } from "react";
import Dropdown from 'react-bootstrap/Dropdown';
import DropdownButton from 'react-bootstrap/DropdownButton';
import DropdownItem from "react-bootstrap/esm/DropdownItem";
import Form from 'react-bootstrap/Form'

//compatible bootstrap is the react-bootstrap that is compatible with JSX files, the one with bootstrap only works for JS files

function Carriers(onCarrierHandle) {
  //let targets = ["Target 1", "Target 2", "Target 3", "Target 4", "Target 5"];


  // below in parent now
  // const [carrier, setCarrier] = useState('');

  useEffect(() => {
    fetchCarriers();
  }, []);

  const fetchCarriers = () => {
      fetch("http://localhost:8080/output-data/get-all-carriers")
        .then((response) => response.json())
        .then((json) => setCarrierList(json))
        .catch((error) => console.error(error));
    };

  const [carrierList, setCarrierList] = useState(-1);
  const [selectedCarrier, setCarrier] = useState('')
  const [selectedCarrierIndex, setCarrierIndex] = useState(-1)

  const onSelectCarrierChanged = (event: React.ChangeEvent<HTMLSelectElement>) => {
    console.log("value", event.target.value);
    setCarrier(event.target.value);
    fetch("http://localhost:8080/test-data/set-carrier?carrierID=" + event.target.value);
    onCarrierHandle.onCarrierHandle(event.target.value);
  };

  // JSX: Javascript XML
  // babeljs.io

  return (
    <>
      <div id="Carrier-div">
        <h3>Carrier</h3>
        <Form.Select aria-label="Select an option"
                    value={selectedCarrier}
                    onChange={onSelectCarrierChanged} >
          <option>Select a carrier.</option>
            { //Dynamic options
            Object.values(carrierList).map((item, index) => (
            <option
              className={
                selectedCarrierIndex === index
                  ? "list-group-item active"
                  : "list-group-item"
              }
              key={item}
              onClick={() => {
                setCarrier(item);
                setCarrierIndex(index);
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

export default Carriers;
