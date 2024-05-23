## Helper module for 
# Handling different string handling task and formats that are commonly recurring.
##
from asyncio.windows_events import NULL
import csv
import datetime
import logging
import os

LOGCAT_TIMESTAMP_START = 0
LOGCAT_TIMESTAMP_END = 18

PTT_TIMESTAMP_START = 0
PTT_TIMESTAMP_END = 20

LOGCAT_TIMESTAMP_FORMAT = "%m-%d %H:%M:%S.%f"
TS_TIMESTAMP_FORMAT = "%m/%d %H:%M:%S:%f" 
NORMAL_TIMESTAMP_FORMAT = "%Y-%m-%d at %H:%M:%S.%f"

def ts_handler(string: str):
    '''
    Handles Timestamp that is called by KPI logs and PTT Logs.
    TS:[04/21 16:13:04:177]
    '''
    tempStart = string.find("[")
    tempEnd = string.find("]")
    result = string[tempStart+1:tempEnd]
    return result

def logcat_timestamp_handler(string: str):
    '''
    Handles Timestamp that is called by adb logcat
    04-21 16:13:48.458 .....
    '''
    result = string[LOGCAT_TIMESTAMP_START : LOGCAT_TIMESTAMP_END]
    return result


def check_timestamp_format(string: str):
    '''
    Checks whenever the string is in the Logcat format or PTT format
    line can be the full line or the cut portion with the Timestamp
    '''
    string_type = ""
    try:
        #PTT default
        tempStart = string.find("[")
        tempEnd = string.find("]")
        tempLine = string[tempStart+1:tempEnd]
        temp = datetime.datetime.strptime(tempLine, TS_TIMESTAMP_FORMAT)
        string_type = "PTT"
    except:
        TS_line = string
        TS_line = TS_line[LOGCAT_TIMESTAMP_START : LOGCAT_TIMESTAMP_END]
        temp = datetime.datetime.strptime(TS_line, LOGCAT_TIMESTAMP_FORMAT)
        string_type = "Logcat"

    return string_type


if __name__ == '__main__':
    #Task if called as a main
    logging.info("running file directly, this file is a helper file.")
    test_string = "04-21 16:13:14.060 D/[HTML5LOG]"
    res = logcat_timestamp_handler(test_string)
    print(res)
