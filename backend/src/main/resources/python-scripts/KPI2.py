## Handles KPI2 Data breakdown
#  KPI2 is Video Call logs.
#  As of 11/13/23, There exist no KPI log for video calls, so This will either be added later.
##
import datetime
import os
import logging
import StringHandling
import TimestampHandling
import json

logging.basicConfig(level=logging.DEBUG) #For production configure to INFO. For development configure the level to DEBUG. 

parentPath = os.path.abspath(os.path.join(os.getcwd()))
test_tempPath = os.path.dirname(os.path.realpath(__file__)) #fullpath...\python scripts  
test_basePath = test_tempPath.replace("\\python scripts", "")
basePath = parentPath  #test_basePath for development. parentPath for product release.

OUTPUT_PATH = basePath
LOGCAT_PATH = basePath

LOGCAT_TIMESTAMP_FORMAT = "%m-%d %H:%M:%S.%f"
TS_TIMESTAMP_FORMAT = "%m/%d %H:%M:%S:%f" #03/21 14:49:35:084
NORMAL_TIMESTAMP_FORMAT = "%Y-%m-%d at %H:%M:%S.%f"

TS_PTT_NAME = "PTT"
TS_LOGCAT_NAME = "Logcat"

currentTestYear = ""

def SingleTestVideoCall(filePath: str, currentTestYear: str, input_xml: dict = None):
    '''
    Task for this is to identify every single Video call that ocurred.
    
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
    call_establish_start = MakeVideoCall
    call_establish_end = Ringing State
    Time_milliseconds = KPI_2 to Ringing Video Call Time

    '''
    logging.basicConfig(level=logging.DEBUG) #For production configure to INFO. For development configure the level to DEBUG. 

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
    
    
    iterationVideoCalls = [dict(Test="TestPTT", Time_milliseconds="0.5", Iteration="1", Result="PASS")]
    iterationVideoCalls.remove(dict(Test="TestPTT", Time_milliseconds="0.5", Iteration="1", Result="PASS"))

    video_call_flow = dict(make_call="", ringing_state= "", answered_call="", ongoing_state="", disconnect="")
    make_call_flag = False # first time

    #Read file
    try:
        with open(filePath, "r", encoding="utf8") as f:
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
                    
                    #Make Video Call - NOTE: VideoCallHandler Entering makeVideoCall
                    if line.find("VideoCallHandler") > -1 and line.find("Entering makeVideoCall") > -1:
                        logging.debug("VideoCallHandler Entering makeVideoCall")
                        logging.debug("Make Video Call Line")
                        
                        
                        #Case for determining that there was an issue checking with the sequence.
                        if make_call_flag == True and (video_call_flow["make_call"] == "" or
                                                        video_call_flow["ringing_state"] == "" or
                                                        video_call_flow["answered_call"] == "" or
                                                        video_call_flow["ongoing_state"] == "" or
                                                        video_call_flow["disconnect"] == "" or
                                                        duration <= 0):
                            callStartTimeDateTime = call_establish_start_datetime.replace(year=int(currentTestYear))
                            callStartTime = datetime.datetime.strftime(callStartTimeDateTime, NORMAL_TIMESTAMP_FORMAT)
                            callEndTime = currentLineTimestampTxt
                            call_establish_start = call_establish_start
                            call_establish_end = "N/A"
                            duration = 10
                            result = "FAIL"
                            # Record
                            iterationVideoCalls.append(dict(Time_milliseconds=duration,
                                                          call_start_time=callStartTime,
                                                          call_establish_start_time=call_establish_start,
                                                          call_end_time=callEndTime,
                                                          call_establish_end_time=call_establish_end,
                                                          Iteration=iteration,
                                                          Result=result))
                            iteration = iteration + 1

                        #restarting dictionary acting as flags
                        make_call_flag = True
                        video_call_flow["make_call"] = ""
                        video_call_flow["ringing_state"] = ""
                        video_call_flow["answered_call"] = ""
                        video_call_flow["ongoing_state"] = ""
                        video_call_flow["disconnect"] = ""

                        video_call_flow["make_call"] = line

                        #checks for Timestamp format
                        string_type = TimestampHandling.check_timestamp_format(line)
                        if string_type == TS_PTT_NAME:
                            TS_line = TimestampHandling.ts_handler(line)
                        elif string_type == TS_LOGCAT_NAME:
                            TS_line = TimestampHandling.logcat_timestamp_handler(line)

                        TS_line = TimestampHandling.logcat_timestamp_handler(line)
                        call_establish_start = StringHandling.bracket_removal(TS_line)


                        logging.debug(call_establish_start)
                        call_establish_start_datetime = datetime.datetime.strptime(call_establish_start, LOGCAT_TIMESTAMP_FORMAT) #03-21 14:49:35:084
                        call_establish_start_timedelta = datetime.timedelta(days=call_establish_start_datetime.day, minutes=call_establish_start_datetime.minute, hours=call_establish_start_datetime.hour, seconds=call_establish_start_datetime.second, microseconds=call_establish_start_datetime.microsecond)
                        

                    #Video Call Ringing. NOTE: Received SIP msg of len[261]: <=======SIP/2.0 180 Ringing
                    elif line.find("Received SIP msg") > -1 and line.find("180 Ringing") > -1:
                        logging.debug("Received SIP msg of len[261]: <=======SIP/2.0 180 Ringing")
                        logging.debug("Ringing Video Call State")

                        #checks for Timestamp format
                        string_type = TimestampHandling.check_timestamp_format(line)
                        if string_type == TS_PTT_NAME:
                            TS_line = TimestampHandling.ts_handler(line)
                        elif string_type == TS_LOGCAT_NAME:
                            TS_line = TimestampHandling.logcat_timestamp_handler(line)
                            
                        call_establish_end = StringHandling.bracket_removal(TS_line)
                        logging.debug(call_establish_end)
                        call_establish_end_datetime = datetime.datetime.strptime(call_establish_end, LOGCAT_TIMESTAMP_FORMAT) #03/21 14:49:35:084
                        call_establish_end_timedelta = datetime.timedelta(days=call_establish_end_datetime.day, minutes=call_establish_end_datetime.minute, hours=call_establish_end_datetime.hour, seconds=call_establish_end_datetime.second, microseconds=call_establish_end_datetime.microsecond)

                        #Calculate start iteration with end iteration time to get current iteration duration.
                        durationDeltaTime = call_establish_end_timedelta - call_establish_start_timedelta

                        #transform durationDelta to milliseconds
                        duration = durationDeltaTime / datetime.timedelta(milliseconds=1)

                        callStartTime = currentLineTimestamp
                        callStartTimeTxt = datetime.datetime.strftime(callStartTime, NORMAL_TIMESTAMP_FORMAT)

                        video_call_flow["ringing_state"] = line

                        result = "PASS"

                    #Video Call Answered (updated from ringing to ongoing call state). NOTE: VideoCallHandler ---- onVideoCallStatusChange: 12 ---- callStatus:1
                    elif line.find("VideoCallHandler") > -1 and line.find("---- callStatus:1") > -1:
                        logging.debug("VideoCallHandler ---- onVideoCallStatusChange: ID ---- callStatus:1")
                        logging.debug("Answered Video Call state")

                        #If Timestamp needs to be recorded follow on what Makevideocall and ringing are doing
                        
                        video_call_flow["answered_call"] = line

                    #Video Call Ongoing. NOTE: VideoCallHandler ---- onVideoCallStatusChange: 12 ---- callStatus:7
                    elif line.find("VideoCallHandler") > -1 and line.find("---- callStatus:7") > -1:
                        logging.debug("VideoCallHandler ---- onVideoCallStatusChange: ID ---- callStatus:7")
                        logging.debug("Video ongoing state")

                        #If Timestamp needs to be recorded follow on what Makevideocall and ringing are doing
                        video_call_flow["ongoing_state"] = line

                    #Video Call End Transmission. NOTE: closest thing is: VideoCallHandler ENUM_FLOOR_CHANGE: 8 and may not occur when red key is pressed
                    elif line.find("VideoCallHandler") > -1 and line.find("ENUM_FLOOR_CHANGE: 8") > -1:
                        logging.debug("VideoCallHandler ENUM_FLOOR_CHANGE: 8")
                        logging.debug("End of transmission")

                        #If Timestamp needs to be recorded follow on what Makevideocall and ringing are doing
                        
                    
                    #Video Call Disconnect. NOTE: VideoCallHandler ---- onVideoCallStatusChange: 12 ---- callStatus:2
                    elif line.find("VideoCallHandler") > -1 and line.find("---- callStatus:2") > -1:
                        logging.debug("VideoCallHandler ---- onVideoCallStatusChange: ID ---- callStatus:2")
                        logging.debug("Video Call Disconnected")
                        
                        #If Timestamp needs to be recorded follow on what Makevideocall and ringing are doing

                        #Then this run would be the refer after a connect kpi, therefore this refer is for the Bye
                        if video_call_flow["ringing_state"] != "":

                            video_call_flow["disconnect"] = line

                            result = "PASS"
                            callEndTime = currentLineTimestamp
                            callEndTimeTxt = datetime.datetime.strftime(callEndTime, NORMAL_TIMESTAMP_FORMAT)

                            #fill data
                            iterationVideoCalls.append(dict(Time_milliseconds=duration,
                                                            call_start_time=callStartTimeTxt,
                                                            call_establish_start_time=call_establish_start,
                                                            call_end_time=callEndTimeTxt,
                                                            call_establish_end_time=call_establish_end,
                                                            Iteration=iteration,
                                                            Result=result))
                            iteration = iteration + 1
                            make_call_flag = False

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
            iterationVideoCalls.append(dict(Time_milliseconds=duration,
                                          call_start_time=callStartTime,
                                          call_establish_start_time=call_establish_start,
                                          call_end_time=callEndTime,
                                          call_establish_end_time=call_establish_end,
                                          Iteration=iteration,
                                          Result=result))
            iteration = iteration + 1 

    logging.debug(f"loop iterations={str(iteration)}")
    
    return iterationVideoCalls

if __name__ == '__main__':
    logging.debug(basePath)
