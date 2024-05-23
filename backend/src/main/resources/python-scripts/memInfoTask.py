## 
# HOW TO USE (Simple):
# 1. Have single device plugged in via USB with usb debugging enabled.
# 2. Only one PTT application is installed on the device.
# 3. Have both this file memInfoTask.py and StringHandling.py in the same folder.
# 4. run this python file, no arguments.
# 5. Once script finishes, a text file and 3 csv file will be generated containing memory info.
#    Graphs will be located in graph-ouput folder.
# 6. Since the goal of the script is to quickly run the procedures to obtain meminfo data, 
# The output of the csv file will be appeneded with all previous runs of the script.
# Once testing is completed, delete the csv file or delete the data inside the csv file.
# All data will be in the meminfo_logs folder.
#
# HOW TO USE (Advanced):
# This script accepts 2 additional arguments. 
# The first parameter is the name of the folder the output will be stored, no need to be setup, the script handles this.
# The second parameter is the serial number of the device to use if there are multiple devices plugged in.
# The parameters are optional, but the order and quantity of them are not.
# 
# Usage 1 (Simple): memInfoTask.py
# Usage 2: memInfoTask.py <folder_name> <serial_name/device_name>
# Usage 3: memInfoTask.py test-multiple R3CW507AFEL
# Usage 4: memInfoTask.py test-multiple
# Usage 5: memInfoTask.py "" R3CW507AFEL
#
# NOTE: ignore <> symbols, this is just for example purposes.
#
# This is built in this automated manner since it is planned to be included in the EEI Automation Tool.
#
# If user wants to manually get memInfo graphs and data. Run the inputLoadRunner.py file.
# 
# @author: Edgar Bermudez
##
from asyncio.windows_events import NULL
import datetime
import sys
import logging
import os
import traceback
import subprocess
import csv
import time
import StringHandling
import pandas
import memInfoGraph
from pathlib import Path

package = ""
parentPath = os.path.abspath(os.path.join(os.getcwd()))
test_tempPath = os.path.dirname(os.path.realpath(__file__)) #fullpath...\python scripts  
test_basePath = test_tempPath.replace("\\memInfo", "")
basePath = parentPath  #test_basePath for development. parentPath for product release.
print(basePath)

memInfoList: list[str] = []
MEMINFO_FOLDER_NAME = "meminfo_logs"
MEMINFO_FOLDER_PATH = basePath + f"\\{MEMINFO_FOLDER_NAME}"

PARAM_FOLDER_NAME_PATH = basePath
OUTPUT_FOLDER_NAME = "graph-output"

BATTERY_INFO_APP_NAME = "batteryinfo_App"
MEMINFO_DEVICE_NAME = "meminfo_Device"
MEMINFO_APP_NAME = "meminfo_App"
MANUAL_MEMINFO_APP_NAME = "meminfo"
CPU_INFO_NAME = "cpuinfo"
CSV_FILE_NAME = "memInfo_output"

BATTERY_STRINGS = ['Time on battery', 'Total run time', 'Cellular', 'Wifi', 'GPS', 'Bluetooth']
MEMINFO_DEVICE_STRINGS = ['MemTotal', 'MemFree', 'MemAvailable', 'SwapTotal', 'SwapFree', 'VmallocTotal', 'VmallocUsed', 'VmallocChunk']
MEMINFO_APP_STRINGS =  ['MEMINFO', 'App Summary', 'TOTAL PSS']
PACKAGE_NAMES = ['com.att.eptt', 'com.motorolasolutions.waveoncloudptt', 'com.sprint.sdcplus', 'com.bell.ptt', 'com.verizon.pushtotalkplus']

logging.basicConfig(level=logging.INFO) #For production configure to INFO. For development configure the level to DEBUG. 

def create_directory_memInfo_logs():
    '''
    Creates a directory called graph-output if it does not exist.
    Each graph will be calling this method before making a graph output.
    '''
    Path(MEMINFO_FOLDER_PATH).mkdir(exist_ok=True)
    logging.debug(f"created folder: {MEMINFO_FOLDER_PATH}")

def create_directory_meminfo_logs_folder_name(folder_name: str):
    '''
    Creates a directory called based on the parameter given if it does not exist.
    '''
    create_directory_memInfo_logs()
    final_path = PARAM_FOLDER_NAME_PATH + f"\\{MEMINFO_FOLDER_NAME}" + f"\\{folder_name}"
    Path(final_path).mkdir(exist_ok=True)
    logging.debug(f"created folder: {final_path}")

def setPackage(new_package: str):
    '''
    sets current Package to speedup things and avoid passing parameters and implementing detection.
    Parameters:\n
    package: str, package string.\n
    '''
    global package
    package = new_package

def readMeminfoApp(path: str = MEMINFO_FOLDER_PATH):
    logging.debug("Inside readMeminfoApp().")
    global package
    tempPath = path
    #Add logic to determine if it is the before test or after test version.
    filePath = f"{tempPath}\\{MEMINFO_APP_NAME}.txt"
    found_lines: list[str] = []
    record_flag = False
    try:
        with open(filePath, "r") as f:
            for line in f.readlines():
                if record_flag == True:
                    found_lines.append(line)
                for string in MEMINFO_APP_STRINGS:
                    if line.find(string) > -1:
                        if string == "MEMINFO":
                            found_lines.append(line)
                            record_flag = True
                            temp = StringHandling.bracket_removal(line) #is returning MEMINF, not package
                            package = temp
                            setPackage(temp)
                            logging.debug(f"Set package to: {package}")
                            break
                        if string == "App Summary":
                            record_flag = True
                            logging.debug(f"App Summary found so toogling record_flag to True")
                            break
                        if string == "TOTAL PSS":
                            record_flag = False
                            logging.debug(f"Total PSS line found so toogling record_flag to False")
                            break
                        logging.debug(line)
            f.close()
    except IOError as e:
        logging.error(f"IOError, error reading file: {filePath}")
        logging.error(e)
        logging.error("Error message below:")
        traceback.print_exc()
        error_list = ["error"]
        return error_list

    finally:
        logging.debug(f"reading file: {filePath} completed...")
    
    return found_lines

def readMeminfoApp_manual_mode(iteration: str, path: str = MEMINFO_FOLDER_PATH):
    logging.debug("Inside readMeminfoApp().")
    global package
    tempPath = path
    #Add logic to determine if it is the before test or after test version.
    filePath = f"{tempPath}\\{MANUAL_MEMINFO_APP_NAME}_{iteration}.txt"
    found_lines: list[str] = []
    record_flag = False
    try:
        with open(filePath, "r") as f:
            for line in f.readlines():
                if record_flag == True:
                    found_lines.append(line)
                for string in MEMINFO_APP_STRINGS:
                    if line.find(string) > -1:
                        if string == "MEMINFO":
                            found_lines.append(line)
                            record_flag = True
                            temp = StringHandling.bracket_removal(line) #is returning MEMINF, not package
                            package = temp
                            setPackage(temp)
                            logging.debug(f"Set package to: {package}")
                            break
                        if string == "App Summary":
                            record_flag = True
                            logging.debug(f"App Summary found so toogling record_flag to True")
                            break
                        if string == "TOTAL PSS":
                            record_flag = False
                            logging.debug(f"Total PSS line found so toogling record_flag to False")
                            break
                        logging.debug(line)
            f.close()
    except IOError as e:
        logging.error(f"IOError, error reading file: {filePath}")
        logging.error(e)
        logging.error("Error message below:")
        traceback.print_exc()
        error_list = ["error"]
        return error_list

    finally:
        logging.debug(f"reading file: {filePath} completed...")
    
    return found_lines

def identify_package(UUID: str = ""):
    ''' Run this before recording the raw memory info app data.
    Normally one would use Android development libraries and use Package Manager method.
    But this is a Python script outside the app, and using the third party libraries to be able to call Java functions in
    Python is just... not ok. So I think it would be best to brute force shell and do package name list comparison

    Assume only 1 PTT app is installed on the device.

    modifies global package and returns resulting first matching package.
    Return:
    package_name: str, package found name.
    '''
    #adb shell pm list packages -e
    found_flag = False
    found_package = ""
    if UUID == "":
        process = subprocess.Popen('adb shell pm list packages -e', shell=True, stdout=subprocess.PIPE)
    else:
        os.environ['ANDROID_SERIAL'] = UUID
        process = subprocess.Popen(f'adb shell pm list packages -e', shell=True, stdout=subprocess.PIPE)
        #process = subprocess.Popen(f'adb -s {UUID} shell pm list packages -e', shell=True, stdout=subprocess.PIPE)
        

    for command_output in iter(process.stdout.readline, ''):
        logging.debug(command_output.decode())
        for package_name in PACKAGE_NAMES:
            if command_output.decode().find(package_name) > -1:
                found_package = package_name
                setPackage(found_package)
                return package_name
    return found_package

def recordRawMemInfo(UUID: str = ""):
    '''
     adbCommand("adb -s" + devID + "shell \"dumpsys meminfo " + DeviceInformation.getUnlockPackage() +
                    " > /sdcard/Documents/meminfo_App.txt\"");

                turn this to python
    '''
    global package
    if UUID == "":
        subprocess.call(f"adb shell \"dumpsys meminfo {package} > /sdcard/Documents/meminfo_App.txt\"", shell=True)
    else:
        os.environ['ANDROID_SERIAL'] = UUID
        subprocess.call(f"adb shell \"dumpsys meminfo {package} > /sdcard/Documents/meminfo_App.txt\"", shell=True)

    return 0

def extract_MemInfo_logs(folder_name = ""):
    '''
        folder_path parameter is optional. if anything given, it will add the folder path to it.
    '''
    subprocess.call(f"adb pull /sdcard/Documents/meminfo_App.txt", shell=True)
    #Moving memInfo logs to a new place.
    if folder_name == "":
        subprocess.call(f"move \"meminfo_App.txt\" \"{MEMINFO_FOLDER_PATH}\"", shell=True)
    else:
        folder_path = PARAM_FOLDER_NAME_PATH + f"\\{MEMINFO_FOLDER_NAME}" + f"\\{folder_name}"
        subprocess.call(f"move \"meminfo_App.txt\" \"{folder_path}\"", shell=True)

def csv_output_writer(data: list[str], path: str = MEMINFO_FOLDER_PATH):
    ''' Writes the data into a csv file, similarly to output that is from the output of a meminfo command.
    This data is based on the txt file from MemInfo, which the command does not append.
    This file created will store (by appending) every ran Meminfo txt file and writen to csv for faster reading.
    The data formatted as it is incompatible with csv readers. This file can be observed in table format if exported
    to google spreadsheet.
    '''
    tempPath = path
    filePath = f"{tempPath}\\{CSV_FILE_NAME}.csv"
    timestamp = time.time()
    string_time = time.ctime(timestamp)
    writing_header = False
    writing_memInfo_details = True
    writing_App_Summary = False
    writing_unknown = False
    beginning_of_file = True
    header_first_time = True

    try:
        with open(filePath, 'a') as f:
            csvWriter = csv.writer(f)
            for line in data:
                #removing empty space from a line starting with an empty space.
                if line[0] == " ":
                    line = line.lstrip(' ')

                if line.find("** MEMINFO") >= 0:
                    #write data normally. PID info (one line)
                    temp = line.strip('\n')
                    row = [temp, f"time processed at {string_time}"]
                    csvWriter.writerow(row)
                    writing_header = True
                    writing_memInfo_details = True
                elif writing_header == True and writing_memInfo_details == True:
                    #write data skip first line (empty space filled). Headers
                    row = ['row_title']
                    temp = line.strip('\n')
                    split_data = temp.split()
                    row.extend(split_data)
                    #csvWriter.writerow(row)
                    if "------" in line:
                        writing_header = False
                        header_first_time = True
                        csvWriter.writerow(header_name_row)
                        csvWriter.writerow(row)
                    else:
                        if header_first_time == True:
                            header_name_row = row
                            header_first_time = False
                        else:
                            for header_name_index in range(0,len(row)):
                                header_old_name = header_name_row[header_name_index]
                                header_new_name = row[header_name_index]
                                #header_name_row.insert(header_name_index, f"{header_old_name} {header_new_name}") #STOPPED HERE 08-15-2023. add the header name with new header name
                                header_name_row[header_name_index] = f"{header_old_name} {header_new_name}" #STOPPED HERE 08-15-2023. add the header name with new header name

                elif writing_header == False and writing_memInfo_details == True:
                    #write data normally. Overall Data.
                    #Detect Field Name
                    field_name = str()
                    temp_line = line
                    temp_list = temp_line.split()
                    field_name_index_end = -1
                    for item in temp_list:
                        if item.isalpha() or item.startswith('.'):
                            field_name = field_name + " " + item
                            field_name_index_end += 1
                    field_name = field_name.lstrip()
                    if field_name_index_end == 0:
                        #write normally
                        csvWriter.writerow(temp_list)
                    else:
                        #field name contains more than one string
                        for index in range(0, field_name_index_end+1):
                            temp_list.pop(0)
                        row = [field_name]
                        row.extend(temp_list)
                        csvWriter.writerow(row)
                    if "TOTAL" in line:
                        writing_memInfo_details = False
                elif "App Summary" in line:
                    #write data normally. App Summary Title. (one line)
                    temp = line.strip('\n')
                    row = [temp, f"time processed at {string_time}"]
                    csvWriter.writerow(row)
                    writing_App_Summary = True
                    writing_header = True
                elif writing_header == True and writing_App_Summary == True:
                    #write data skip first line (empty space filled). PSS and RSS headers.
                    row = ['row_title']
                    temp = line.strip('\n')
                    split_data = temp.split()
                    row.extend(split_data)
                    csvWriter.writerow(row)
                    if "----" in line:
                        writing_header = False
                elif writing_header == False and writing_App_Summary == True:
                    #write data normally. App Summary Values generic.
                    #Detect Field Name
                    field_name = str()
                    temp_line = line
                    temp_list = temp_line.split()
                    field_name_index_end = -1
                    for item in temp_list:
                        if item.isalpha() or item.startswith('.') or item.endswith(':'):
                            field_name = field_name + " " + item
                            field_name_index_end += 1
                    field_name = field_name.lstrip()
                    if field_name_index_end == 0:
                        #write normally
                        if "Unknown" in field_name:
                            #Task for unknown
                            row = [temp_list[0], "", temp_list[1]]
                            csvWriter.writerow(row)
                        else:
                            csvWriter.writerow(temp_list)
                    else:
                        #field name contains more than one string
                        if "TOTAL" in line:
                            writing_App_Summary = False
                            #Special write in here for this total
                            ####STOPPED HERE, IMPLEMENT THIS ONE AND ITS OVER
                            row = ["TOTAL PSS:", temp_list[2], "TOTAL RSS:", temp_list[5], "TOTAL SWAP PSS:"
                            , temp_list[9]]
                            csvWriter.writerow(row)
                        else:    
                            for index in range(0, field_name_index_end+1):
                                temp_list.pop(0)
                            row = [field_name]
                            row.extend(temp_list)
                            csvWriter.writerow(row)
            f.close()
    except IOError as e:
        logging.error(f"IOError, error reading file: {filePath}")
        logging.error(e)
        logging.error("Error message below:")
        traceback.print_exc()
        error_list = ["error"]
        return error_list

    finally:
        logging.debug(f"wrote file at: {filePath} ...")

def csv_output_writer_input_loader(data: list[str], source_name: str, path: str = MEMINFO_FOLDER_PATH):
    ''' Writes the data into a csv file, similarly to output that is from the output of a meminfo command.
    This data is based on the txt file from MemInfo, which the command does not append.
    This file created will store (by appending) every ran Meminfo txt file and writen to csv for faster reading.
    The data formatted as it is incompatible with csv readers. This file can be observed in table format if exported
    to google spreadsheet.

    Difference from this one and the regular one is that this one stores the name of the file instead of the date recorded.
    '''
    tempPath = path
    filePath = f"{tempPath}\\{CSV_FILE_NAME}.csv"
    timestamp = time.time()
    string_time = time.ctime(timestamp)
    source = source_name
    writing_header = False
    writing_memInfo_details = True
    writing_App_Summary = False
    writing_unknown = False
    beginning_of_file = True
    header_first_time = True

    try:
        with open(filePath, 'a') as f:
            csvWriter = csv.writer(f)
            for line in data:
                #removing empty space from a line starting with an empty space.
                if line[0] == " ":
                    line = line.lstrip(' ')

                if line.find("** MEMINFO") >= 0:
                    #write data normally. PID info (one line)
                    temp = line.strip('\n')
                    row = [temp, source]
                    csvWriter.writerow(row)
                    writing_header = True
                    writing_memInfo_details = True
                elif writing_header == True and writing_memInfo_details == True:
                    #write data skip first line (empty space filled). Headers
                    row = ['row_title']
                    temp = line.strip('\n')
                    split_data = temp.split()
                    row.extend(split_data)
                    #csvWriter.writerow(row)
                    if "------" in line:
                        writing_header = False
                        header_first_time = True
                        csvWriter.writerow(header_name_row)
                        csvWriter.writerow(row)
                    else:
                        if header_first_time == True:
                            header_name_row = row
                            header_first_time = False
                        else:
                            for header_name_index in range(0,len(row)):
                                header_old_name = header_name_row[header_name_index]
                                header_new_name = row[header_name_index]
                                #header_name_row.insert(header_name_index, f"{header_old_name} {header_new_name}") #STOPPED HERE 08-15-2023. add the header name with new header name
                                header_name_row[header_name_index] = f"{header_old_name} {header_new_name}" #STOPPED HERE 08-15-2023. add the header name with new header name

                elif writing_header == False and writing_memInfo_details == True:
                    #write data normally. Overall Data.
                    #Detect Field Name
                    field_name = str()
                    temp_line = line
                    temp_list = temp_line.split()
                    field_name_index_end = -1
                    for item in temp_list:
                        if item.isalpha() or item.startswith('.'):
                            field_name = field_name + " " + item
                            field_name_index_end += 1
                    field_name = field_name.lstrip()
                    if field_name_index_end == 0:
                        #write normally
                        csvWriter.writerow(temp_list)
                    else:
                        #field name contains more than one string
                        for index in range(0, field_name_index_end+1):
                            temp_list.pop(0)
                        row = [field_name]
                        row.extend(temp_list)
                        csvWriter.writerow(row)
                    if "TOTAL" in line:
                        writing_memInfo_details = False
                elif "App Summary" in line:
                    #write data normally. App Summary Title. (one line)
                    temp = line.strip('\n')
                    row = [temp, source]
                    csvWriter.writerow(row)
                    writing_App_Summary = True
                    writing_header = True
                elif writing_header == True and writing_App_Summary == True:
                    #write data skip first line (empty space filled). PSS and RSS headers.
                    row = ['row_title']
                    temp = line.strip('\n')
                    split_data = temp.split()
                    row.extend(split_data)
                    csvWriter.writerow(row)
                    if "----" in line:
                        writing_header = False
                elif writing_header == False and writing_App_Summary == True:
                    #write data normally. App Summary Values generic.
                    #Detect Field Name
                    field_name = str()
                    temp_line = line
                    temp_list = temp_line.split()
                    field_name_index_end = -1
                    for item in temp_list:
                        if item.isalpha() or item.startswith('.') or item.endswith(':'):
                            field_name = field_name + " " + item
                            field_name_index_end += 1
                    field_name = field_name.lstrip()
                    if field_name_index_end == 0:
                        #write normally
                        if "Unknown" in field_name:
                            #Task for unknown
                            row = [temp_list[0], "", temp_list[1]]
                            csvWriter.writerow(row)
                        else:
                            csvWriter.writerow(temp_list)
                    else:
                        #field name contains more than one string
                        if "TOTAL" in line:
                            writing_App_Summary = False
                            #Special write in here for this total
                            ####STOPPED HERE, IMPLEMENT THIS ONE AND ITS OVER
                            row = ["TOTAL PSS:", temp_list[2], "TOTAL RSS:", temp_list[5], "TOTAL SWAP PSS:"
                            , temp_list[9]]
                            csvWriter.writerow(row)
                        else:    
                            for index in range(0, field_name_index_end+1):
                                temp_list.pop(0)
                            row = [field_name]
                            row.extend(temp_list)
                            csvWriter.writerow(row)
            f.close()
    except IOError as e:
        logging.error(f"IOError, error reading file: {filePath}")
        logging.error(e)
        logging.error("Error message below:")
        traceback.print_exc()
        error_list = ["error"]
        return error_list

    finally:
        logging.debug(f"wrote file at: {filePath} ...")

def csv_file_exist(path = MEMINFO_FOLDER_PATH):
    ''' Checks for existence of the output csv file in the same folder path of this script.
    
    Return:
    result: Boolean, result of the existence of the csv file.
    '''
    result = False
    tempFileList = os.listdir(path)
    tempFileList = [currentfile for currentfile in tempFileList if os.path.isfile(path + '/' + currentfile)]
    try:
        tempFileList.remove("__init__.py")
    except:
        logging.debug("No __init__.py found")
    
    if tempFileList.count(f"{CSV_FILE_NAME}.csv") >= 1:
        result = True

    return result

def csv_count_lines():
    ''' Reads for number of lines in the csv file.
    
    Return:
    result: Boolean, result of the existence of the csv file.
    '''
    csv_data = pandas.read_csv(f"{MEMINFO_FOLDER_PATH}\\{CSV_FILE_NAME}.csv")
    return len(csv_data)

def read_csv_output_MemInfo(path: str = MEMINFO_FOLDER_PATH):
    ''' Reads csv file memInfo_output.csv and retrieves a properly csv, list formatted properly for easier
    dataframe generation when it becomes called by pandas. This is for Memory Details section.

    Returns:
    output: list[list[str]], First list represents the line as a whole, second list represents the content of the line itself, this value is a string.
    '''
    tempPath = path
    filePath = f"{tempPath}\\{CSV_FILE_NAME}.csv"
    writing_memInfo_details = True
    header_first_time = True
    date = str()
    header_index_size = 0
    #output = list[list[str]]
    output = list(list())
    try:
        with open(filePath, 'r') as f:
            for line in f.readlines():
                temp_line = line.rstrip("\n")
                temp_list = temp_line.split(sep=',')
                if "** MEMINFO" in temp_line:
                    date = temp_list[1]
                    writing_memInfo_details = True
                elif "row_title" in temp_line and header_first_time == True:
                    header_first_time = False
                    temp_list[0] = "row_title" #Renaming header from "row_title row_title" to "row_title"
                    temp_list.append("Date")
                    output.append(temp_list)
                elif "TOTAL" in temp_line and writing_memInfo_details == True:
                    temp_list.append(date)
                    output.append(temp_list)
                    writing_memInfo_details = False
                elif "---" in temp_line and writing_memInfo_details == True:
                    #Skip Line
                    logging.debug("Skipping \"---\" lines")
                elif line.startswith("\n") and writing_memInfo_details == True:
                    #Skip Empty Lines, comparison uses line instead of temp_line
                    logging.debug("Skipping \"\n\" lines, new line lines.")
                elif temp_line.startswith("row_title") and writing_memInfo_details == True and header_first_time == False:
                    #Skip Line
                    logging.debug("Skipping subsequent header lines in other header repeated data.")
                elif writing_memInfo_details == True:
                    #data line
                    if len(temp_list) == 9:
                        #all headers filled.
                        temp_list.append(date)
                        output.append(temp_list)
                    elif len(temp_list) < 9:
                        #line does not have all its headers filled.
                        while len(temp_list) < 9:
                            temp_list.append("0")
                        temp_list.append(date)
                        output.append(temp_list)            
    except IOError as e:
        logging.error(f"IOError, error reading file: {filePath}")
        logging.error(e)
        logging.error("Error message below:")
        traceback.print_exc()
        error_list = ["error"]
        return error_list
    finally:
        logging.debug(f"read file at: {filePath} ...")
    return output

def read_csv_output_MemInfo_input_loader(path: str = MEMINFO_FOLDER_PATH):
    ''' Reads csv file memInfo_output.csv and retrieves a properly csv, list formatted properly for easier
    dataframe generation when it becomes called by pandas. This is for Memory Details section.

    Difference from regular one is that Date is changed to Source

    Returns:
    output: list[list[str]], First list represents the line as a whole, second list represents the content of the line itself, this value is a string.
    '''
    tempPath = path
    filePath = f"{tempPath}\\{CSV_FILE_NAME}.csv"
    writing_memInfo_details = True
    header_first_time = True
    source = str()
    header_index_size = 0
    #output = list[list[str]]
    output = list(list())
    try:
        with open(filePath, 'r') as f:
            for line in f.readlines():
                temp_line = line.rstrip("\n")
                temp_list = temp_line.split(sep=',')
                if "** MEMINFO" in temp_line:
                    source = temp_list[1]
                    writing_memInfo_details = True
                elif "row_title" in temp_line and header_first_time == True:
                    header_first_time = False
                    temp_list[0] = "row_title" #Renaming header from "row_title row_title" to "row_title"
                    temp_list.append("Source")
                    output.append(temp_list)
                elif "TOTAL" in temp_line and writing_memInfo_details == True:
                    temp_list.append(source)
                    output.append(temp_list)
                    writing_memInfo_details = False
                elif "---" in temp_line and writing_memInfo_details == True:
                    #Skip Line
                    logging.debug("Skipping \"---\" lines")
                elif line.startswith("\n") and writing_memInfo_details == True:
                    #Skip Empty Lines, comparison uses line instead of temp_line
                    logging.debug("Skipping \"\n\" lines, new line lines.")
                elif temp_line.startswith("row_title") and writing_memInfo_details == True and header_first_time == False:
                    #Skip Line
                    logging.debug("Skipping subsequent header lines in other header repeated data.")
                elif writing_memInfo_details == True:
                    #data line
                    if len(temp_list) == 9:
                        #all headers filled.
                        temp_list.append(source)
                        output.append(temp_list)
                    elif len(temp_list) < 9:
                        #line does not have all its headers filled.
                        while len(temp_list) < 9:
                            temp_list.append("0")
                        temp_list.append(source)
                        output.append(temp_list)            
    except IOError as e:
        logging.error(f"IOError, error reading file: {filePath}")
        logging.error(e)
        logging.error("Error message below:")
        traceback.print_exc()
        error_list = ["error"]
        return error_list
    finally:
        logging.debug(f"read file at: {filePath} ...")
    return output

def read_csv_output_App(path: str = MEMINFO_FOLDER_PATH):
    ''' Reads csv file memInfo_output.csv and retrieves a properly csv, list formatted properly for easier
    dataframe generation when it becomes called by pandas. This is for App Summary section.

    Returns:
    output: list[list[str]], First list represents the line as a whole, second list represents the content of the line itself, this value is a string.
    '''
    tempPath = path
    filePath = f"{tempPath}\\{CSV_FILE_NAME}.csv"
    writing_app_summary = False
    header_first_time = True
    date = str()
    header_index_size = 0
    #output = list[list[str]]
    output = list(list())
    try:
        with open(filePath, 'r') as f:
            for line in f.readlines():
                temp_line = line.rstrip("\n")
                temp_list = temp_line.split(sep=',')
                if "App Summary" in temp_line:
                    date = temp_list[1]
                    writing_app_summary = True
                elif "row_title" in temp_line and header_first_time == True and writing_app_summary == True:
                    header_first_time = False
                    temp_list[0] = "row_title" #Renaming header from "row_title row_title" to "row_title"
                    temp_list.append("Swap Pss(KB)")
                    temp_list.append("Date")
                    output.append(temp_list)
                elif "TOTAL" in temp_line and writing_app_summary == True:
                    #temp_list.append(date)
                    total_list = list(str())
                    total_list = ["TOTAL:", temp_list[1], temp_list[3], temp_list[5]]
                    total_list.append(date)
                    output.append(total_list)
                    writing_app_summary = False
                elif "---" in temp_line and writing_app_summary == True:
                    #Skip Line
                    logging.debug("Skipping \"---\" lines")
                elif line.startswith("\n") and writing_app_summary == True:
                    #Skip Empty Lines, comparison uses line instead of temp_line
                    logging.debug("Skipping \"\n\" lines, new line lines.")
                elif line.startswith("\"") and writing_app_summary == True:
                    #Skip Empty Lines, cotains only ""
                    logging.debug("Skipping \"\" lines, new line lines.")
                elif temp_line.startswith("row_title") and writing_app_summary == True and header_first_time == False:
                    #Skip Line
                    logging.debug("Skipping subsequent header lines in other header repeated data.")
                elif writing_app_summary == True:
                    #data line
                    if len(temp_list) == 3:
                        #all headers filled.
                        if "Unknown" in temp_list[0]:
                            temp_list[1] = "0"
                        temp_list.append("0") #Swap Pss(KB)
                        temp_list.append(date)
                        output.append(temp_list)
                    elif len(temp_list) < 3:
                        #line does not have all its headers filled.
                        while len(temp_list) < 3:
                            temp_list.append("0")
                        temp_list.append("0") #Swap Pss(KB)
                        temp_list.append(date)
                        output.append(temp_list)            
    except IOError as e:
        logging.error(f"IOError, error reading file: {filePath}")
        logging.error(e)
        logging.error("Error message below:")
        traceback.print_exc()
        error_list = ["error"]
        return error_list
    finally:
        logging.debug(f"read file at: {filePath} ...")
    return output

def read_csv_output_App_input_loader(path: str = MEMINFO_FOLDER_PATH):
    ''' Reads csv file memInfo_output.csv and retrieves a properly csv, list formatted properly for easier
    dataframe generation when it becomes called by pandas. This is for App Summary section.

    Difference from regular one is that Date is changed to Source

    Returns:
    output: list[list[str]], First list represents the line as a whole, second list represents the content of the line itself, this value is a string.
    '''
    tempPath = path
    filePath = f"{tempPath}\\{CSV_FILE_NAME}.csv"
    writing_app_summary = False
    header_first_time = True
    source = str()
    header_index_size = 0
    #output = list[list[str]]
    output = list(list())
    try:
        with open(filePath, 'r') as f:
            for line in f.readlines():
                temp_line = line.rstrip("\n")
                temp_list = temp_line.split(sep=',')
                if "App Summary" in temp_line:
                    source = temp_list[1]
                    writing_app_summary = True
                elif "row_title" in temp_line and header_first_time == True and writing_app_summary == True:
                    header_first_time = False
                    temp_list[0] = "row_title" #Renaming header from "row_title row_title" to "row_title"
                    temp_list.append("Swap Pss(KB)")
                    temp_list.append("Source")
                    output.append(temp_list)
                elif "TOTAL" in temp_line and writing_app_summary == True:
                    #temp_list.append(date)
                    total_list = list(str())
                    total_list = ["TOTAL:", temp_list[1], temp_list[3], temp_list[5]]
                    total_list.append(source)
                    output.append(total_list)
                    writing_app_summary = False
                elif "---" in temp_line and writing_app_summary == True:
                    #Skip Line
                    logging.debug("Skipping \"---\" lines")
                elif line.startswith("\n") and writing_app_summary == True:
                    #Skip Empty Lines, comparison uses line instead of temp_line
                    logging.debug("Skipping \"\n\" lines, new line lines.")
                elif line.startswith("\"") and writing_app_summary == True:
                    #Skip Empty Lines, cotains only ""
                    logging.debug("Skipping \"\" lines, new line lines.")
                elif temp_line.startswith("row_title") and writing_app_summary == True and header_first_time == False:
                    #Skip Line
                    logging.debug("Skipping subsequent header lines in other header repeated data.")
                elif writing_app_summary == True:
                    #data line
                    if len(temp_list) == 3:
                        #all headers filled.
                        if "Unknown" in temp_list[0]:
                            temp_list[1] = "0"
                        temp_list.append("0") #Swap Pss(KB)
                        temp_list.append(source)
                        output.append(temp_list)
                    elif len(temp_list) < 3:
                        #line does not have all its headers filled.
                        while len(temp_list) < 3:
                            temp_list.append("0")
                        temp_list.append("0") #Swap Pss(KB)
                        temp_list.append(source)
                        output.append(temp_list)            
    except IOError as e:
        logging.error(f"IOError, error reading file: {filePath}")
        logging.error(e)
        logging.error("Error message below:")
        traceback.print_exc()
        error_list = ["error"]
        return error_list
    finally:
        logging.debug(f"read file at: {filePath} ...")
    return output

def write_csv_MemInfo_side(data: list,path: str = MEMINFO_FOLDER_PATH):
    tempPath = path
    filePath = f"{tempPath}\\formatted_MemInfo.csv"
    try:
        with open(filePath, 'w') as file:
            csvWriter = csv.writer(file)
            for line in data:
                csvWriter.writerow(line) 
        file.close()
    except IOError as e:
        logging.error(f"IOError, error reading file: {filePath}")
        logging.error(e)
        logging.error("Error message below:")
        traceback.print_exc()
        error_list = ["error"]
        return error_list
    finally:
        logging.debug(f"wrote file at: {filePath} ...")

def write_csv_AppSummary_side(data: list,path: str = MEMINFO_FOLDER_PATH):
    tempPath = path
    filePath = f"{tempPath}\\formatted_AppSummary.csv"
    try:
        with open(filePath, 'w') as file:
            csvWriter = csv.writer(file)
            for line in data:
                csvWriter.writerow(line) 
        file.close()
    except IOError as e:
        logging.error(f"IOError, error reading file: {filePath}")
        logging.error(e)
        logging.error("Error message below:")
        traceback.print_exc()
        error_list = ["error"]
        return error_list
    finally:
        logging.debug(f"wrote file at: {filePath} ...")

def csv_count_MemInfo_log(path: str = MEMINFO_FOLDER_PATH):
    '''
    '''
    tempPath = path
    filePath = f"{tempPath}\\{CSV_FILE_NAME}.csv"
    #csv_data = pandas.read_csv(f"{path}\\{CSV_FILE_NAME}.csv")
    counter = 0
    try:
        with open(filePath, 'r') as f:
            for line in f.readlines():
                if "** MEMINFO" in line:
                    counter += 1             
    except IOError as e:
        logging.error(f"IOError, error reading file: {filePath}")
        logging.error(e)
        logging.error("Error message below:")
        traceback.print_exc()
        error_list = ["error"]
        return error_list
    finally:
        logging.debug(f"wrote file at: {filePath} ...")
    return counter

def csv_read_MemInfo_all(path: str = MEMINFO_FOLDER_PATH):
    ''' 
    Reads for number of lines in the csv file.
    
    Return:
    csv_data: pandas.DataFrame, returns information of the csv data .
    '''
    csv_data = pandas.read_csv(filepath_or_buffer=f"{path}\\{CSV_FILE_NAME}.csv", sep=',')
    return csv_data

def main(argv, arc):
    #argv is a list
    #the minimum sys.argv possible is the same file that is being called.
    if arc == 1:
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
    #Task if called as a main, no arguments
    logging.debug("running file directly, this file is a runner file.")
    create_directory_memInfo_logs()
    logging.debug("calling method identify_package()")
    identify_package()
    logging.debug(package)
    logging.info("recording memory info on device...")
    recordRawMemInfo()
    time.sleep(1)
    logging.info("extracting memory info...")
    extract_MemInfo_logs()
    time.sleep(1)
    logging.info("reading memory info file...")
    memInfoList = readMeminfoApp()
    logging.info("writing csv file...")
    csv_output_writer(memInfoList)

    csv_exist = csv_file_exist()
    if csv_exist:
        #Output here
        logging.debug("csv file exist!")
        memInfo_counter = csv_count_MemInfo_log()
        print(f"Number of memInfo logs: {memInfo_counter}")
        if memInfo_counter >= 1:
            csv_data_memInfo = read_csv_output_MemInfo()
            write_csv_MemInfo_side(data=csv_data_memInfo)
            csv_data_App_side = read_csv_output_App()
            write_csv_AppSummary_side(csv_data_App_side)
            #proceed to output graph
            print("starting with graphing the csv file...")
            memInfoGraph.memInfo_details_output_bar_chart(f"{MEMINFO_FOLDER_PATH}\\formatted_MemInfo.csv")
            memInfoGraph.app_summary_output_bar_chart(f"{MEMINFO_FOLDER_PATH}\\formatted_AppSummary.csv")

def arg_task(folder_name: str, UUID: str = ""):
    '''Task to be performed when arguments are provided.
    folder_name: string. User provided folder name.
    UUID: string. Device's UUID, used when calling ADB commands. If not provided, then this is running on single device mode.
    '''
    logging.debug("perform task with given arguments.")
    folder_path = PARAM_FOLDER_NAME_PATH + f"\\{folder_name}"
    meminfo_folder_path = PARAM_FOLDER_NAME_PATH + f"\\{MEMINFO_FOLDER_NAME}" + f"\\{folder_name}"
    graph_output_folder_path = PARAM_FOLDER_NAME_PATH + f"\\{OUTPUT_FOLDER_NAME}" + f"\\{folder_name}"
    #Task if called as a main, no arguments
    logging.debug("running file directly, this file is a runner file.")
    create_directory_meminfo_logs_folder_name(folder_name)
    logging.debug("calling method identify_package()")
    identify_package(UUID)
    logging.debug(package)
    logging.info("recording memory info on device...")
    recordRawMemInfo(UUID)
    time.sleep(1)
    logging.info("extracting memory info...")
    extract_MemInfo_logs(folder_name)
    time.sleep(1)
    logging.info("reading memory info file...")
    memInfoList = readMeminfoApp(meminfo_folder_path)
    logging.info("writing csv file...")
    csv_output_writer(memInfoList, meminfo_folder_path)

    csv_exist = csv_file_exist(meminfo_folder_path)
    if csv_exist:
        #Output here
        logging.debug("csv file exist!")
        memInfo_counter = csv_count_MemInfo_log(meminfo_folder_path)
        print(f"Number of memInfo logs: {memInfo_counter}")
        if memInfo_counter >= 1:
            csv_data_memInfo = read_csv_output_MemInfo(meminfo_folder_path)
            write_csv_MemInfo_side(data=csv_data_memInfo, path=meminfo_folder_path)
            csv_data_App_side = read_csv_output_App(meminfo_folder_path)
            write_csv_AppSummary_side(data=csv_data_App_side, path=meminfo_folder_path)
            #proceed to output graph
            print("starting with graphing the csv file...")
            memInfoGraph.create_directory_graph_output_folder_name(folder_name)
            memInfoGraph.memInfo_details_output_bar_chart(f"{meminfo_folder_path}\\formatted_MemInfo.csv", folder_name=folder_name)
            memInfoGraph.app_summary_output_bar_chart(f"{meminfo_folder_path}\\formatted_AppSummary.csv", folder_name=folder_name)

if __name__ == '__main__':
    logging.debug(sys.argv)
    logging.debug(len(sys.argv))
    main(sys.argv, len(sys.argv))
    print("All Task completed, output should be on the same folder path where this python file ran.")




