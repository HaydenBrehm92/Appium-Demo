import { useEffect, useState } from "react";

function Devices(onDeviceListHandle) {
  //let targets = ["Target 1", "Target 2", "Target 3", "Target 4", "Target 5"];

  const fetchDevices = () => {
    fetch("http://localhost:8080/output-data/get-all-devices")
      .then((response) => response.json())
      .then((json) => setdata(json))
      .catch((error) => console.error(error));
  };

  const [data, setdata] = useState(-1);

  // Hook
  const [selectedIndex, setSelectedIndex] = useState(-1);

  useEffect(() => {
    // poll fetchDevices every second
    let interval = setInterval(() => fetchDevices(), 1000);
    return () => clearInterval(interval)
  }, []);

  // JSX: Javascript XML
  // babeljs.io
  return (
    <>
      <h1>Device List</h1>
      {Object.values(data).length === 0 && <p>No Targets Found</p>}
      <ul className="list-group">
        {Object.values(data).map((data, index) => (
          <li
            className="list-group-item"
            key={index}
            /* onClick={() => {
              setSelectedIndex(index);
            }} */
          >
            {data}
            <RadioButtons data={data} index={index} onDeviceListHandle={onDeviceListHandle}/>
          </li>
        ))}
      </ul>
    </>
  );
}

var globalDeviceList: Array<String> = [];

function RadioButtons(props) {
  const [selectedRadio, setSelectedRadio] = useState("");
  //var globalDeviceList: Array<String> = [];

  function handleClick(event) {
    setSelectedRadio(event.target.value);

    if (globalDeviceList.length >= 0) {
      globalDeviceList.push(event.target.value);
      console.log("globalDeviceList push, checking size: ", globalDeviceList.length)
    }

    var deviceList = updateDevices(globalDeviceList,event.target.value);
    globalDeviceList.splice(0, globalDeviceList.length, ...deviceList);
    globalDeviceList.concat(deviceList);
    props.onDeviceListHandle.onDeviceListHandle(globalDeviceList);
  }
  

  return (
    <>
      <div className="form-check" key={props.data + "-StandardRadioButton"}>
        <input
          className="form-check-input"
          type="radio"
          name={"DeviceType" + props.index}
          id={props.data + "-Standard"}
          value={props.data + "-Standard"}
          checked={selectedRadio === props.data + "-Standard"}
          onChange={handleClick}
        />
        <label className="form-check-label" htmlFor={props.data + "-Standard"}>
          Standard
        </label>
      </div>
      <div className="form-check" key={props.data + "-RadioRadioButton"}>
        <input
          className="form-check-input"
          type="radio"
          name={"DeviceType" + props.index}
          id={props.data + "-Radio"}
          value={props.data + "-Radio"}
          checked={selectedRadio === props.data + "-Radio"}
          onChange={handleClick}
        />
        <label className="form-check-label" htmlFor={props.data + "-Radio"}>
          Radio
        </label>
      </div>
    </>
  );
}

/**
 * Note that device name is index 0 and device mode is index 1
 * @param deviceString string
 * @returns string[]
 */
function deviceInfo(deviceString: String) {
  const delimiter = "-";
  const textArray = deviceString.split(delimiter);
  return textArray;
}

/**
 * Updates device list using given existing arraylist and new device event.target.value as string
 * @param deviceList
 * @returns Array<String>, Since javascript handles static arrays better than dynamic arrays.
 */
function updateDevices(deviceList: Array<String>, device: String) {
  const device_info = deviceInfo(device)
  const updatedDeviceList: Array<String> = [];
  var device_updated = 0;
  
  deviceList.forEach(element => {
    const array_device_info = deviceInfo(element);
    if(device_info[0] == array_device_info[0] && device_info[1] != array_device_info[1]) {
      var changedDevice = array_device_info[0] + "-" + device_info[1];
      element = changedDevice;
      updatedDeviceList.push(changedDevice);
      device_updated = 1;
    }
    else {
      const new_device = array_device_info[0] + "-" + array_device_info[1];
      updatedDeviceList.push(new_device);
    }
  });
  
  //Removing extra copy from the original arraylist just to have the forEach loop.
  if(deviceList.length > 1 && device_updated == 1) {
    updatedDeviceList.pop();
  }
  

  console.log("Inside updateDevices method, deviceList", updatedDeviceList);

  return updatedDeviceList;

}

export default Devices;
