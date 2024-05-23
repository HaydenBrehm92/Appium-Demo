## Databreakdown / Main
# Main class of the whole python portion of the data analysis after the automation ran.
# This file will be the one that gets generated into a an executable file. .exe
##
import datetime
import os
import sys
import xml.etree.ElementTree as ET #handles XML files
import re
import logging
import GraphData
import json
import PTTCalls as PTT_Call
import PTXMessages as PTX
import KPI2 as KPI2
import DashServer as Dash_Server
import traceback

logging.basicConfig(level=logging.INFO) #For production configure to INFO. For development configure the level to DEBUG. 

parentPath = os.path.abspath(os.path.join(os.getcwd()))
test_tempPath = os.path.dirname(os.path.realpath(__file__)) #fullpath...\python scripts  
test_basePath = test_tempPath.replace("\\python scripts", "")
basePath = parentPath  #test_basePath for development. parentPath for product release.

OUTPUT_PATH = basePath
MAIN_PATH = basePath
LOG_FOLDER_NAME = "logs"
TEST_OUTPUT_FILES_PATH = basePath + "\\test-output"
TESTNG_RESULTS_PATH = TEST_OUTPUT_FILES_PATH + "\\testng-results.xml"
TESTINGPTTAPP_PATH = TEST_OUTPUT_FILES_PATH + "\\Testing PTT app"
OUTPUT_TEST_PATH = OUTPUT_PATH + "\\ptx_output_summary_adb_example1.txt"
INPUT_CONFIG_XML_PATH = basePath + "\\inputConfig.xml"

EXAMPLE_KPI_DATA_PATH = basePath + "\\example data\\KPI_Logcat" #test env constant
EXAMPLE_KPI_LOGCAT = EXAMPLE_KPI_DATA_PATH + "\\ADB_03.21.2023_14.49.29.92_crafted.txt" #test env constant
OUTPUT_KPI_TXT = EXAMPLE_KPI_DATA_PATH + "\\DataResult.txt" #test env constant

LOGCAT_TIMESTAMP_FORMAT = "%m-%d %H:%M:%S.%f"
TESTNG_TIMESTAMP_FORMAT = "%Y-%m-%dT%H:%M:%S"
TS_TIMESTAMP_FORMAT = "%m/%d %H:%M:%S:%f" #03/21 14:49:35:084
NORMAL_TIMESTAMP_FORMAT = "%Y-%m-%d at %H:%M:%S.%f"

currentTestYear = ""

PTT_CALL_LOGCAT_NAME = "RecentCallTest" 
PTX_LOGCAT_NAME = "PtxMessagesTest"
PTT_ADD_CONTACT_NAME = "AddContactTest"
KPI2_NAME = "VideoCallTest"

BATCH_ONE_NAME = "BatchOne"

def fileReader_XmlTestNGResults():
    ''' 
    Returns:\n
    tuple[str, list[str], list[str], list[str], list[str]], tuple of 5 items.\n 
    The following are retrieved:
    testName, testStartTimestamp, testEndTimestamp, testDuration, testIterations.
    '''
    testName = ""
    testStartTimestamp = []
    testEndTimestamp = []
    testDuration = []
    testIterations = []
    test_method_names = []

    try:
        tree = ET.parse(TESTNG_RESULTS_PATH)
        root = tree.getroot()
        logging.debug(root.attrib)

        suite = root.find("suite")
        test = suite.find("test")
        testName = test.get("name")
        #Batch xml structure
        if testName == BATCH_ONE_NAME:
            logging.info("Batch test mode xml structure")
            testMethodClass = test.findall("class")
            for testClass in testMethodClass:
                test_method = testClass.find("test-method")
                test_method_names.append(testClass.get("name"))
                testStartTimestamp.append(test_method.get("started-at")[:-4])
                testEndTimestamp.append(test_method.get("finished-at")[:-4])
                testDuration.append(test_method.get("duration-ms"))
                logging.debug(testStartTimestamp[0])
        else:
            logging.info("Single Test mode xml structure")
            test_method_names.append(test.get("name"))
            testStartTimestamp.append(test.get("started-at")[:-4])
            testEndTimestamp.append(test.get("finished-at")[:-4])
            testDuration.append(test.get("duration-ms")) #duration is in milliseconds

            item = testStartTimestamp[0]
    except IOError as e:
        logging.error(e)
        exit()

    except:
        logging.error("Error reading file")
        logging.error("Exiting execution...")
        logging.error("Error message below:")
        traceback.print_exc()
        exit()
        
    finally:
        print(f"reading file: {TESTNG_RESULTS_PATH} completed...")
    logging.debug("testName = " + testName)
    logging.debug("testStartTimestamp = " + testStartTimestamp[0])
    logging.debug("testEndTimestamp = " + testEndTimestamp[0])
    logging.debug("testDuration = " + testDuration[0])
    global currentTestYear
    currentTestYear = testStartTimestamp[0][0:4]

    return testName, test_method_names, testStartTimestamp, testEndTimestamp, testDuration

def fileReader_inputConfigXml():
    ''' 
    Returns:\n
    tuple[str, str, str, str, str], tuple of 10 items.\n 
    The following are retrieved:
    device, carrier, test, PTXMessage, BuildMessage, BuildIPA, BuildLocation, BuildVoiceRecord, BuildPicture, BuildFile.
    '''
    logging.debug("Inside inputConfigXml")

    device = str("")
    carrier = str("")
    ptxMessage = str("")
    build_message = str("")
    build_ipa = str("") 
    build_location = str("")
    build_picture = str("") 
    build_voice = str("")
    build_file = str("")

    try:
        tree = ET.parse(INPUT_CONFIG_XML_PATH)
        config = tree.getroot()
        logging.debug(config.tag)
        #inputs = config.findall("input")

        #Device
        device_input = config.find(".//*[@Type='Device']")
        device = device_input.findtext("Value")
        logging.debug(device)

        #Carrier
        carrier_input = config.find(".//*[@Type='Carrier']")
        carrier = carrier_input.findtext("Value")
        logging.debug(carrier)

        #Test
        test_input = config.find(".//*[@Type='Test']")
        test = test_input.findtext("Value")
        logging.debug(test)

        #PTXMessage
        ptxMessage_input = config.find(".//*[@Type='PTXMessage']")
        ptxMessage = ptxMessage_input.findtext("Value")
        logging.debug(ptxMessage)

        #BuildMessage
        build_message_input = config.find(".//*[@Type='BuildMessage']")
        build_message = build_message_input.findtext("Value")
        logging.debug(build_message)

        #BuildIPA
        build_ipa_input = config.find(".//*[@Type='BuildIPA']")
        build_ipa = build_ipa_input.findtext("Value")
        logging.debug(build_ipa)

        #BuildLocation
        build_location_input = config.find(".//*[@Type='BuildLocation']")
        build_location = build_location_input.findtext("Value")
        logging.debug(build_location)

        #BuildVoiceRecord
        build_voice_input = config.find(".//*[@Type='BuildVoiceRecord']")
        build_voice = build_voice_input.findtext("Value")
        logging.debug(build_voice)

        #BuildPicture
        build_picture_input = config.find(".//*[@Type='BuildPicture']")
        build_picture = build_picture_input.findtext("Value")
        logging.debug(build_picture)

        #BuildFile
        build_file_input = config.find(".//*[@Type='BuildFile']")
        build_file = build_file_input.findtext("Value")
        logging.debug(build_file)

        #CustomTests
        custom_test_input = config.find(".//*[@Type='CustomTests']")
        custom_test = custom_test_input.findtext("Value")
        logging.debug(custom_test)

    except IOError as e:
        logging.error(e)
        exit()

    except:
        logging.error("Error reading file")
        logging.error("Exiting execution...")
        logging.error("Error message below:")
        traceback.print_exc()
        exit()
        
    finally:
        print(f"reading file: {TESTNG_RESULTS_PATH} completed...")

    input_xml = dict(device=device,
                    carrier=carrier,
                    test=test,
                    PTXMessage=ptxMessage,
                    BuildMessage=build_message,
                    BuildIPA=build_ipa,
                    BuildLocation=build_location,
                    BuildVoiceRecord=build_voice,
                    BuildPicture=build_picture,
                    BuildFile=build_file,
                    CustomTests=custom_test)

    return input_xml

def test_list_runner(test_name: str, input_xml: dict, folder_name: str = ""):
    '''
    List of the tests available in the automation.
    Uses the test name provided in the parameter to run 
    the proper analyzer for the log type.

    Building batch in a generic manner, until batch test are implemented properly.

    Parameters:\n
    test_name: str, name to compare if the current test is the right test.
    adb_path: str, path of the txt file to read. ADB logs or the automation grepped logs.
    input_xml: dict, dictionary of all the inputs parameters defined on the method fileReader_inputConfigXml.

    Output:\n
    None by itself, but it calls GraphData methods which will generate an output.
    '''
    if(test_name == PTT_CALL_LOGCAT_NAME):
        if folder_name == "":
            pttPath = MAIN_PATH + f"\\{LOG_FOLDER_NAME}" + f"\\{PTT_CALL_LOGCAT_NAME}.txt"
        else:
            pttPath = MAIN_PATH + f"\\{LOG_FOLDER_NAME}" + f"\\{folder_name}" + f"\\{PTT_CALL_LOGCAT_NAME}.txt"
        ptt_call_data = PTT_Call.SingleTestPTTRecentCall(pttPath, currentTestYear, input_xml)
        GraphData.singleTestPTTBarChart(ptt_call_data, folder_name)
    elif(test_name == PTX_LOGCAT_NAME):
        if folder_name == "":
            pttPath = MAIN_PATH + f"\\{LOG_FOLDER_NAME}" + f"\\{PTX_LOGCAT_NAME}.txt"
        else:
            pttPath = MAIN_PATH + f"\\{LOG_FOLDER_NAME}" + f"\\{folder_name}" + f"\\{PTX_LOGCAT_NAME}.txt"
        ptx_data = PTX.SingleTestPTX(pttPath, currentTestYear, input_xml)
        GraphData.singleTest_PTX_chart(ptx_data, folder_name)
    elif(test_name == KPI2_NAME):
        if folder_name == "":
            pttPath = MAIN_PATH + f"\\{LOG_FOLDER_NAME}" + f"\\{KPI2_NAME}.txt"
        else:
            pttPath = MAIN_PATH + f"\\{LOG_FOLDER_NAME}" + f"\\{folder_name}" + f"\\{KPI2_NAME}.txt"
        kpi2_data = KPI2.SingleTestVideoCall(pttPath, currentTestYear, input_xml)
        GraphData.singleTestVideoBarChart(kpi2_data, folder_name)
    elif(test_name == PTT_ADD_CONTACT_NAME):
        if folder_name == "":
            pttPath = MAIN_PATH + f"\\{LOG_FOLDER_NAME}" + f"\\{PTT_ADD_CONTACT_NAME}.txt"
        else:
            pttPath = MAIN_PATH + f"\\{LOG_FOLDER_NAME}" + f"\\{folder_name}" + f"\\{PTT_ADD_CONTACT_NAME}.txt"
        add_contact_data = "not implemented yet"
    else:
        logging.debug(f"Test does not exist. Name: {test_name}")
        print(f"Test does not exist. Name: {test_name}")

def main(argv, arc):
    #argv is a list
    #the minimum sys.argv possible is the same file that is being called.
    if arc == 1:
        #Note that when you call any script, the sys reserves the first argument to be the path of the current file even when no parameters are provided.
        logging.debug("no arguments")
        print("No Arguments, default mode run")
        no_arg_task()
    elif arc == 2:
        logging.debug("1 arguments")
        print("1 Argument. Folder name")
        folder_name = argv[1]
        arg_task(folder_name)
    elif arc == 3:
        logging.debug("2 Arguments. Folder Name, UUID")
        folder_name = argv[1]
        uuid = argv[2]
        arg_task(folder_name, uuid)
    else:
        logging.debug("please provide only one argument or no arguments. The argument passed would be the string of the folder name.")


def no_arg_task():
    logging.debug(basePath)
    testNGResult = fileReader_XmlTestNGResults()
    input_config_param = fileReader_inputConfigXml()

    if input_config_param["CustomTests"] == "0":
        #Default runner, test name based on TestNG data.
        #Single test mode
        logging.debug("running in single test mode, test name based on TestNG/GUI dropdown")
        test_list_runner(testNGResult[0], input_config_param)
    else:
        #CustomTests mode, test name based on inputConfig.xml
        logging.debug("running in custom test mode, test name based on inputConfig.xml CustomTests values")
        test_names = input_config_param["CustomTests"].split(";")
        for test_name in test_names:
            test_list_runner(test_name, input_config_param)

def arg_task(folder_name: str, UUID: str = ""):
    logging.debug(basePath)
    testNGResult = fileReader_XmlTestNGResults()
    input_config_param = fileReader_inputConfigXml()

    if input_config_param["CustomTests"] == "0":
        #Default runner, test name based on TestNG data.
        #Single test mode
        logging.debug("running in single test mode, test name based on TestNG/GUI dropdown")
        test_list_runner(testNGResult[0], input_config_param, folder_name)
    else:
        #CustomTests mode, test name based on inputConfig.xml
        logging.debug("running in custom test mode, test name based on inputConfig.xml CustomTests values")
        test_names = input_config_param["CustomTests"].split(";")
        for test_name in test_names:
            test_list_runner(test_name, input_config_param, folder_name)

if __name__ == '__main__':
    logging.debug(sys.argv)
    logging.debug(len(sys.argv))
    main(sys.argv, len(sys.argv))
