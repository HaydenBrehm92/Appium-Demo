package core.SpringBootControllers;

import core.MyLogger;
import core.constants.AppInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

import core.constants.TestNGInfo;
import org.testng.TestNG;

@RestController
@RequestMapping("/test-data")
public class TestDataController
{
    HttpHeaders responseHeaders = new HttpHeaders();

    /**
     * Constructor to set up the response headers.
     * @author Victor Dang
     */
    @Autowired
    public TestDataController()
    {
        responseHeaders.setAccessControlAllowOrigin("http://localhost:3000");
        responseHeaders.setAccessControlAllowCredentials(true);
    }

    /**
     * Request mapping to set the carrier the user has selected.
     * @param carrierID The string of the carrier to execute the test with.
     * @return The string of whether the carrier was set along with 200 OK if setting the carrier was successful,
     * 400 Bad Request otherwise.
     */
    @GetMapping(path="/set-carrier", produces = "application/json")
    public ResponseEntity<String> SetCarrier(@RequestParam(value = "carrierID") String carrierID)
    {
        boolean validCarrier = AppInformation.getSupportedCarriers().contains(carrierID);

        MyLogger.log.debug("Setting carrier to {}", carrierID);
        return new ResponseEntity<>((validCarrier) ? "Carrier Set" : "Carrier Could Not Be Set!", responseHeaders,
                (validCarrier) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    /**
     * Request mapping to set the test the user has selected.
     * @param testID The string of the test to execute.
     * @return The string of whether the test was set along with 200 OK if setting the test was successful,
     * 400 Bad Request otherwise.
     */
    @GetMapping(path="/set-test", produces = "application/json")
    public ResponseEntity<String> SetTest(@RequestParam(value = "testID") String testID)
    {
        boolean validTest = TestNGInfo.testList.contains(testID);

        MyLogger.log.debug("Setting test suite to {}", testID);
        return new ResponseEntity<>((validTest) ? "Test Set" : "Test Could Not Be Set!", responseHeaders,
                (validTest) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}
