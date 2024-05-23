## Helper module for 
# Handling different file types and file management
##
from asyncio.windows_events import NULL
import csv
import datetime
import logging
import os

LOGCAT_TIMESTAMP_FORMAT = "%m-%d %H:%M:%S.%f"
TESTNG_TIMESTAMP_FORMAT = "%Y-%m-%dT%H:%M:%S"
TS_TIMESTAMP_FORMAT = "%m/%d %H:%M:%S:%f" #03/21 14:49:35:084  #Issue
NORMAL_TIMESTAMP_FORMAT = "%Y-%m-%d at %H:%M:%S.%f"

def fileListfromDirectory(filePath: str, selectFileType=None):
    '''gets a list of the files in the path.
    Parameters:\n
    tempPath: str, filePath.\n
    selectFileType (optional): str or None, if None it will keep all files with the given file type.\n
    if string is given, it will filter file list with the given extension name. include "." in the string.\n 
    Example: ".xml"
    '''
    tempFileList = os.listdir(filePath)
    tempFileList = [currentfile for currentfile in tempFileList if os.path.isfile(filePath+'/'+ currentfile)]
    try:
        tempFileList.remove("__init__.py")
    except:
        logging.info("No __init__.py found")
    
    if selectFileType == None:
        return tempFileList
    
    fileList = [""]
    fileList.remove("")
    

    for item in tempFileList:
        if item.find(selectFileType) > -1:
            fileList.append(item)
    
    return fileList

def fileWriterTxt(filePath: str, data: any):
    ''' Generic file writer method for text files.
    filePath: str, path to write the file
    data: Any, whatever the data is assuming the data has multiple lines.
    '''

    #Write file
    try:
        with open(filePath, "a") as g:
            for item in data:
                logging.debug(item)
                g.write(item)
            g.close()
    except IOError as e:
        logging.error(f"Error writing file: {filePath}")
        logging.error(e)
    finally:
        logging.debug(f"writing file: {filePath} completed...")


def txtGenericFileReader(read_filePath: str, write_filePath):
    ''' Test method.
    The purpose of this method is to generically read adb logcat logs
    and test line filtering and conditions based on the content of the text file.
    '''
    #Read file
    currentTestYear = "2023"
    try:
        with open(read_filePath, "r") as f:
            for line in f.readlines():
                tempTimestampStr = line[0:18] # 02-09 10:50:03.712
                #print(tempTimestampStr)
                if line.startswith("-"):
                   logging.debug("ignoring first line")
                   logging.debug(line)
                elif line[0].isdigit() == False:
                    logging.debug("ignoring due to line not starting on alphanumeric value for date")
                    logging.debug(line)
                else:
                    #print("START-Timestamp: " + timestampStartADB)
                    #print("currentTimestamp: " + tempTimestampStr)
                    #print("END-Timestamp: " + timestampEndADB)
                    currentLineTimestamp = datetime.datetime.strptime(tempTimestampStr, LOGCAT_TIMESTAMP_FORMAT)
                    currentLineTimestamp = currentLineTimestamp.replace(year=int(currentTestYear))
                    currentLineTimestampTxt = datetime.datetime.strftime(currentLineTimestamp, NORMAL_TIMESTAMP_FORMAT)
                    deltaTimeCurrentTime = datetime.timedelta(days=currentLineTimestamp.day, minutes=currentLineTimestamp.minute, hours=currentLineTimestamp.hour, seconds=currentLineTimestamp.second, microseconds=currentLineTimestamp.microsecond)
                    
                    #Replace any if else you need in here
                    if line.find("KOD PERF: HistoryCtrl.onSendTextMsg() >> entered") > -1:
                        #Send Text Message KPI Entered
                        fileWriterTxt(write_filePath, line)
                    elif line.find("KOD PERF: Type:33 Status:1") > -1:
                        #Send Text Message KPI
                        fileWriterTxt(write_filePath, line)
                    elif line.find("KOD PERF: HistoryCtrl.onSendTextMsg() >> resolved") > -1:
                        #Data of Message Sent call.
                        fileWriterTxt(write_filePath, line)
                    elif line.find("PluginMcdataMessagingCb.msgReceiptRecvdCb()") > -1:
                        #Data of server response of the sent message to self device from a msgReceiptRecvd call confirmation.
                        fileWriterTxt(write_filePath, line)
                    elif line.find("KOD PERF: Type:33 Status:2") > -1:
                        #Server Response of sending to self device a msgReceiptRevcd call confirmation
                        fileWriterTxt(write_filePath, line)
                    elif line.find("KOD PERF: MapUtils.sendLocation() >> resolved") > -1:
                        #Server Response of sending PTX Map Location
                        fileWriterTxt(write_filePath, line)
                    elif line.find("PtxUtils.sendPtxAttachment() >> resolved") > -1:
                        #Server Response of sending any PTX Attachement
                        fileWriterTxt(write_filePath, line)
                                        
                    else:
                        p = "any other line"
        
            f.close()
    except IOError as e:
        logging.error(f"Error reading file: {read_filePath}")
        logging.error(e)

    finally:
        logging.debug(f"reading file: {read_filePath} completed...")

if __name__ == '__main__':
    #Task if called as a main
    logging.info("running file directly, this file is a helper file.")