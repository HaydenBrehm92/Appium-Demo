package core.SpringBootControllers;

import core.ADB;
import core.constants.AppInformation;
import core.constants.TestNGInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequestMapping("/output-data")
public class OutputDataController
{
    HttpHeaders responseHeaders = new HttpHeaders();

    /**
     * Constructor to set up the response headers.
     * @author Victor Dang
     */
    @Autowired
    public OutputDataController()
    {
        responseHeaders.setAccessControlAllowOrigin("http://localhost:3000");
        responseHeaders.setAccessControlAllowCredentials(true);
    }

    /**
     * Gets all the carriers that are supported by this tool. This data is obtained through a pre-defined XML sheet.
     * @return The list of all supported carriers along with 200 OK.
     * @author Victor Dang
     */
    @GetMapping(path="/get-all-carriers", produces = "application/json")
    public ResponseEntity<Collection<String>> getAllCarriers()
    {
        return new ResponseEntity<>(AppInformation.getSupportedCarriers(), responseHeaders, HttpStatus.OK);
    }

    /**
     * Gets all the devices currently plugged into the device. This data is obtained through ADB.
     * @return The list of all devices along with 200 OK.
     * @author Hayden Brehm, Victor Dang
     */
    @GetMapping(path="/get-all-devices", produces = "application/json")
    public ResponseEntity<Collection<String>> getAllDevices()
    {
        return new ResponseEntity<>(ADB.getConnectedDevices(), responseHeaders, HttpStatus.OK);
    }

    /**
     * Gets all the available tests. This data is obtained through a pre-defined XML sheet.
     * @return The list of available tests along with 200 OK.
     * @author Edgar Bermudez, Victor Dang
     */
    @GetMapping(path="/available-tests", produces = "application/json")
    public ResponseEntity<Collection<String>> getAllTest()
    {
        return new ResponseEntity<>(TestNGInfo.testList, responseHeaders, HttpStatus.OK);
    }
}
