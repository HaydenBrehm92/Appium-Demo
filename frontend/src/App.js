import Devices from './components/Devices';
import Carriers from './components/Carriers';
import ConsoleWindow from './components/ConsoleWindow';
import TestList from './components/TestList';

import { useEffect, useState } from "react";
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';

import "./App.css";

function App() {
  
  const [test_prop, setTest] = useState('');
  const [carrier_prop, setCarrier] = useState('');
  var deviceList = [];
  var carrier_val = ""
  var test_val = ""
  

  function onTestHandle(value) {
    console.log("value", value);
    setTest(value);
    test_val = value;
    console.log("test_val: ", test_val);
  }

  function onCarrierHandle(value) {
    console.log("value: ", value);
    setCarrier(value);
    carrier_val = value;
    console.log("carrier_val: ", carrier_val);
  };

  function onDeviceListHandle(list) {
    //setDeviceList(list);
    //deviceList = list;
    deviceList.splice(0, deviceList.length, ...list);
    console.log("list:", list);
    console.log("DeviceList: ", deviceList);

    deviceList.forEach(element => {
      console.log("DeviceList Value: ", element);
    });
  }

// Error on setCarrierfn, this could mean implementation for setTestfn will also fail.

  return (
  <Container className='container'>
    <Row className='row-Setup'>
      <Col className='col-DeviceSetup'>
        <Devices onDeviceListHandle={onDeviceListHandle}/>
      </Col>
      <Col className='col-TestSetup'>
        <Carriers onCarrierHandle={onCarrierHandle} />
        <TestList onTestHandle={onTestHandle}/>
      </Col>
    </Row>
    <Row className='row-Console'>
      <Col>
        <ConsoleWindow/>
      </Col>
    </Row>  
  </Container>
  );
}

export default App;
