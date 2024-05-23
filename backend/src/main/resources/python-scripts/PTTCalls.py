## Handles PTT Calls Data breakdown
#   
##
import datetime
import os
import logging
import StringHandling
import TimestampHandling
import json

parentPath = os.path.abspath(os.path.join(os.getcwd()))
test_tempPath = os.path.dirname(os.path.realpath(__file__)) #fullpath...\python scripts  
test_basePath = test_tempPath.replace("\\python scripts", "")
basePath = parentPath  #test_basePath for development. parentPath for product release.

OUTPUT_PATH = basePath
LOGCAT_PATH = basePath

LOGCAT_TIMESTAMP_FORMAT = "%m-%d %H:%M:%S.%f"
TS_TIMESTAMP_FORMAT = "%m/%d %H:%M:%S:%f" #03/21 14:49:35:084
NORMAL_TIMESTAMP_FORMAT = "%Y-%m-%d at %H:%M:%S.%f"

currentTestYear = ""

def SingleTestPTTRecentCall(filePath: str, currentTestYear: str, input_xml: dict = None):
    '''
    Task for this is to identify every single PTT call that ocurred.
    
    Parameters:\n
    filePath: str, path to the text file containing PTT Recent Call Test or ADB logs with KPI calls.
    currentTestYear: str, ADB Logs and PTT Logs have no year in its timestamp logging. if using this mehtod
                     alone for other tasks, just use handle it with current time method and 
                     provided to this method as input.
    input_xml: dict, This is exclusive for EEI Automation Tool, inputConfig_xml turned into a dictionary. Optional parameter for separate method call.
    
    Output:\n
    a List of dictionary.

    NOTE: for organization of the flow of PTT calls These are the equivalencies of the field names in here 
    and the field names displayed in GraphData.
    call_establish_start = Key Down Press
    call_establish_end = Grant Tone
    Time_milliseconds = KPI_1 to Grant Tone Time

    '''
    logging.debug("Single test PTT txt logcat")
    callStartTime = str("")
    callEndTime = str("")
    call_establish_start = str("")
    call_establish_end = str("")
    result = str("")
    duration = 0
    durationDeltaTime = datetime.timedelta(0,0,0,0,0,0,0)
    iteration = 1
    call_establish_start_datetime = datetime.datetime(int(currentTestYear),1,1,1,1,1,1)
    callStartTimeDateTime = datetime.datetime(int(currentTestYear),1,1,1,1,1,1)
    call_establish_end_datetime = datetime.datetime(int(currentTestYear),1,1,1,1,1,1)
    call_establish_start_timedelta = datetime.timedelta(days=call_establish_start_datetime.day, 
                                                        minutes=call_establish_start_datetime.minute, 
                                                        hours=call_establish_start_datetime.hour, 
                                                        seconds=call_establish_start_datetime.second, 
                                                        microseconds=call_establish_start_datetime.microsecond)
    
    call_establish_end_timedelta = datetime.timedelta(days=call_establish_end_datetime.day, 
                                                        minutes=call_establish_end_datetime.minute, 
                                                        hours=call_establish_end_datetime.hour, 
                                                        seconds=call_establish_end_datetime.second, 
                                                        microseconds=call_establish_end_datetime.microsecond)
    
    
    iterationPTTCalls = [dict(Test="TestPTT", Time_milliseconds="0.5", Iteration="1", Result="PASS")]
    iterationPTTCalls.remove(dict(Test="TestPTT", Time_milliseconds="0.5", Iteration="1", Result="PASS"))

    ptt_call_flow = dict(make_call= "", refer_connect="", connect="", grant="", refer_bye="")
    make_call_flag = False # first time

    #Read file
    try:
        with open(filePath, "r") as f:
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
                    
                    #Make Call - NOTE: Status 1 = private. Status 2 = group
                    if line.find("KOD PERF: Type:11") > -1:
                        logging.debug("Make Call Line")
                        
                        #Case for determining that there was an issue checking with the sequence.
                        if make_call_flag == True and (ptt_call_flow["make_call"] == "" or
                                                        ptt_call_flow["refer_connect"] == "" or
                                                        ptt_call_flow["grant"] == "" or
                                                        ptt_call_flow["refer_bye"] == "" or
                                                        duration <= 0):
                            callStartTimeDateTime = call_establish_start_datetime.replace(year=int(currentTestYear))
                            callStartTime = datetime.datetime.strftime(callStartTimeDateTime, NORMAL_TIMESTAMP_FORMAT)
                            callEndTime = currentLineTimestampTxt
                            call_establish_start = call_establish_start
                            call_establish_end = "N/A"
                            duration = 10
                            result = "FAIL"
                            # Record
                            iterationPTTCalls.append(dict(Time_milliseconds=duration,
                                                          call_start_time=callStartTime,
                                                          call_establish_start_time=call_establish_start,
                                                          call_end_time=callEndTime,
                                                          call_establish_end_time=call_establish_end,
                                                          Iteration=iteration,
                                                          Result=result))
                            iteration = iteration + 1

                        #restarting dictionary acting as flags
                        make_call_flag = True
                        ptt_call_flow["make_call"] = ""
                        ptt_call_flow["refer_connect"] = ""
                        ptt_call_flow["connect"] = ""
                        ptt_call_flow["refer_bye"] = ""
                        ptt_call_flow["grant"] = ""

                        ptt_call_flow["make_call"] = line

                        TS_index_start = line.index("TS:") + 3
                        TS_line = line[TS_index_start:]
                        call_establish_start = StringHandling.bracket_removal(TS_line)
                        logging.debug(call_establish_start)
                        call_establish_start_datetime = datetime.datetime.strptime(call_establish_start, TS_TIMESTAMP_FORMAT) #03/21 14:49:35:084
                        call_establish_start_timedelta = datetime.timedelta(days=call_establish_start_datetime.day, minutes=call_establish_start_datetime.minute, hours=call_establish_start_datetime.hour, seconds=call_establish_start_datetime.second, microseconds=call_establish_start_datetime.microsecond)

                    #Grant Tone
                    elif line.find("KOD PERF: Type:10 Status:3") > -1:
                        logging.debug("Grant line")
                        TS_index_start = line.index("TS:") + 3
                        TS_line = line[TS_index_start:]
                        call_establish_end = StringHandling.bracket_removal(TS_line)
                        logging.debug(call_establish_end)
                        call_establish_end_datetime = datetime.datetime.strptime(call_establish_end, TS_TIMESTAMP_FORMAT) #03/21 14:49:35:084
                        call_establish_end_timedelta = datetime.timedelta(days=call_establish_end_datetime.day, minutes=call_establish_end_datetime.minute, hours=call_establish_end_datetime.hour, seconds=call_establish_end_datetime.second, microseconds=call_establish_end_datetime.microsecond)

                        #Calculate start iteration with end iteration time to get current iteration duration.
                        durationDeltaTime = call_establish_end_timedelta - call_establish_start_timedelta

                        #transform durationDelta to milliseconds
                        duration = durationDeltaTime / datetime.timedelta(milliseconds=1)

                        callStartTime = currentLineTimestamp
                        callStartTimeTxt = datetime.datetime.strftime(callStartTime, NORMAL_TIMESTAMP_FORMAT)

                        ptt_call_flow["grant"] = line

                        result = "PASS"
                    
                   
                    #Refer   
                    elif line.find("KOD PERF: Type:10 Status:1") > -1:
                        logging.debug("Refer Line")

                        TS_index_start = line.index("TS:") + 3
                        TS_line = line[TS_index_start:]
                        TS_refer_timestamp = StringHandling.bracket_removal(TS_line)

                        #Then this run would be the refer after a connect kpi, therefore this refer is for the Bye
                        if ptt_call_flow["refer_connect"] != "":

                            ptt_call_flow["refer_bye"] = line

                            result = "PASS"
                            callEndTime = currentLineTimestamp
                            callEndTimeTxt = datetime.datetime.strftime(callEndTime, NORMAL_TIMESTAMP_FORMAT)

                            #fill data
                            iterationPTTCalls.append(dict(Time_milliseconds=duration,
                                                            call_start_time=callStartTimeTxt,
                                                            call_establish_start_time=call_establish_start,
                                                            call_end_time=callEndTimeTxt,
                                                            call_establish_end_time=call_establish_end,
                                                            Iteration=iteration,
                                                            Result=result))
                            iteration = iteration + 1
                            make_call_flag = False

                        #This would be the case for refer for the connect.
                        elif ptt_call_flow["refer_connect"] == "":
                            ptt_call_flow["refer_connect"] = line

                        #This would be the case for any other refer that should not occur.
                        else:
                            u = "unkown condition, this would mean that there is one extra refer for some reason."

                    else:
                        p = "this is for the result pass or fail. Checking is done at start (SUCCESS_CALL_KPI_COUNTER) and at the end (finally)"
                        
                        
            #iterationPTTCalls.append(dict(Test="PTT Calls"))        
            f.close()
    except IOError as e:
        logging.error(f"Error reading file: {filePath}")
        logging.error(e)

    finally:
        logging.debug(f"reading file: {filePath} completed...")
        if(make_call_flag):
            #Failure if flag is true.
            callStartTimeDateTime = call_establish_start_datetime.replace(year=int(currentTestYear))
            callStartTime = datetime.datetime.strftime(callStartTimeDateTime, NORMAL_TIMESTAMP_FORMAT)
            callEndTime = currentLineTimestampTxt
            call_establish_start = call_establish_start
            call_establish_end = "N/A"
            duration = 10
            result = "FAIL"
            # Record
            iterationPTTCalls.append(dict(Time_milliseconds=duration,
                                          call_start_time=callStartTime,
                                          call_establish_start_time=call_establish_start,
                                          call_end_time=callEndTime,
                                          call_establish_end_time=call_establish_end,
                                          Iteration=iteration,
                                          Result=result))
            iteration = iteration + 1 

    logging.debug(f"loop iterations={str(iteration)}")
    
    return iterationPTTCalls

if __name__ == '__main__':
    logging.debug(basePath)
