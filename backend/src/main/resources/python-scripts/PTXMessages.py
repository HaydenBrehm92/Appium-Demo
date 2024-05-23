## Handles PTX Data breakdown 
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

def determine_iteration_flow(input_xml: dict, ptx_message_flag: dict):
    ''' Helper setter method for determining the flow of the iterations based on the given
    input_xml dictionary.

    This will modify the initial string value of the flags
    text="", ipa="", location="", image="", video="", voice="", file="", attachment=""

    Used dictionary names related to PTX:
    PTXMessage, BuildMessage, BuildIPA, BuildLocation, BuildVoiceRecord, BuildPicture, BuildFile
    '''
    if input_xml["BuildMessage"] == '1':
        ptx_message_flag["text"] = "FAIL"
    elif (input_xml["BuildMessage"] == '0' 
          and input_xml["BuildIPA"] == '0' 
          and input_xml["BuildLocation"] == '0'
          and input_xml["BuildVoiceRecord"] == '0'
          and input_xml["BuildPicture"] == '0'
          and input_xml["BuildFile"] == '0'):
        #default case. No custom xml.
        ptx_message_flag["text"] = "FAIL"
    else:
        ptx_message_flag["text"] = "N/A"

    if input_xml["BuildIPA"] == '1':
        ptx_message_flag["ipa"] = "FAIL"
    else:
        ptx_message_flag["ipa"] = "N/A"
    
    if input_xml["BuildLocation"] == '1':
        ptx_message_flag["location"] = "FAIL"
    else:
        ptx_message_flag["location"] = "N/A"

    if input_xml["BuildVoiceRecord"] == '1':
        ptx_message_flag["voice"] = "FAIL"
    else:
        ptx_message_flag["voice"] = "N/A"

    if input_xml["BuildPicture"] == '1':
        ptx_message_flag["image"] = "FAIL"
    else:
        ptx_message_flag["image"] = "N/A"
    
    if input_xml["BuildFile"] == '1':
        ptx_message_flag["file"] = "FAIL"
    else:
        ptx_message_flag["file"] = "N/A"

    #Video does not exist yet in the xml file. 
    ptx_message_flag["video"] = "N/A"
    '''
    if input_xml["video"] == 1:
        ptx_video_flag = "FAIL"
        ptx_message_flag["video"] = "FAIL"
    else:
        ptx_video_flag = "N/A"
        ptx_message_flag["video"] = "N/A"
    '''
    #Add video as option when xml includes video.
    if input_xml["BuildVoiceRecord"] == '1' or input_xml["BuildPicture"] == '1' or input_xml["BuildFile"] == '1':
        ptx_message_flag["attachment"] = "FAIL"
    else:
        ptx_message_flag["attachment"] = "N/A"
    
    return ptx_message_flag

def set_iteration_priority(ptx_message_flag: dict[str, str]):
    '''
    dict(text="", ipa="", location="", image="", video="", voice="", file="", attachment="")
    '''
    priority = str("")

    if ptx_message_flag["text"] == "FAIL":
        priority = "text"
    elif ptx_message_flag["location"] == "FAIL":
        priority = "location"
    elif ptx_message_flag["image"] == "FAIL":
        priority = "image"
    elif ptx_message_flag["video"] == "FAIL":
        priority = "video"
    elif ptx_message_flag["voice"] == "FAIL":
        priority = "voice"
    elif ptx_message_flag["file"] == "FAIL":
        priority = "file"
    elif ptx_message_flag["ipa"] == "FAIL":
        priority = "ipa"
    else:
        priority = "text"

    return priority

def iteration_start_time(currentLineTimestampTxt: str):
    '''

    Return:
    ptx_iteration_start_datetime: datetime
    ptx_iteration_start_timedelta: timedelta
    '''
    ptx_iteration_start = currentLineTimestampTxt
    logging.debug(ptx_iteration_start)
    ptx_iteration_start_datetime = datetime.datetime.strptime(ptx_iteration_start, NORMAL_TIMESTAMP_FORMAT) #03/21 14:49:35:084 ##ValueError: time data '2023-04-21 at 16:13:02.129000' does not match format '%m/%d %H:%M:%S:%f'
    ptx_iteration_start_timedelta = datetime.timedelta(days=ptx_iteration_start_datetime.day, 
                                                       minutes=ptx_iteration_start_datetime.minute, 
                                                       hours=ptx_iteration_start_datetime.hour, 
                                                       seconds=ptx_iteration_start_datetime.second, 
                                                       microseconds=ptx_iteration_start_datetime.microsecond)
    return ptx_iteration_start_datetime, ptx_iteration_start_timedelta

def record_ptx_iteration_success(iterationPTXMessages: list[dict[str, str]], ptx_message_flag: dict[str, str], ptx_message_id: dict[str, str], ptx_message_ts: dict[str, str], duration: str, ptx_iteration_start: str, ptx_iteration_end: str, iteration: str):
    '''
    records ptx iteration pass               
    '''
    result = "PASS"
    iterationPTXMessages.append(dict(Time_milliseconds=duration,
                                     ptx_iteration_start_time=ptx_iteration_start,
                                     ptx_iteration_end_time=ptx_iteration_end,
                                     ptx_send_time_start=ptx_message_ts["text_send"],
                                     ptx_send_time_end=ptx_message_ts["text_received"],
                                     ptx_text=ptx_message_flag["text"],
                                     ptx_text_id=ptx_message_id["text"],
                                     ptx_ipa=ptx_message_flag["ipa"],
                                     ptx_ipa_id=ptx_message_id["ipa"],
                                     ptx_location=ptx_message_flag["location"],
                                     ptx_location_id=ptx_message_id["location"],
                                     ptx_image=ptx_message_flag["image"],
                                     ptx_image_id=ptx_message_id["image"],
                                     ptx_video=ptx_message_flag["video"],
                                     ptx_video_id=ptx_message_id["video"],
                                     ptx_voice=ptx_message_flag["voice"],
                                     ptx_voice_id=ptx_message_id["voice"],
                                     ptx_file=ptx_message_flag["file"],
                                     ptx_file_id=ptx_message_id["file"],
                                     ptx_attachment=ptx_message_flag["attachment"],
                                     Iteration=iteration,
                                     Result=result))

def record_ptx_iteration_fail(iterationPTXMessages: list[dict[str, str]], ptx_message_flag: dict[str, str], ptx_message_id: dict[str, str], ptx_message_ts: dict[str, str], duration: str, ptx_iteration_start: str, ptx_iteration_end: str, iteration: str):
    '''
    records ptx iteration failures

    '''
    result = "FAIL"
    iterationPTXMessages.append(dict(Time_milliseconds=duration,
                                     ptx_iteration_start_time=ptx_iteration_start,
                                     ptx_iteration_end_time=ptx_iteration_end,
                                     ptx_send_time_start=ptx_message_ts["text_send"],
                                     ptx_send_time_end=ptx_message_ts["text_received"],
                                     ptx_text=ptx_message_flag["text"],
                                     ptx_text_id=ptx_message_id["text"],
                                     ptx_ipa=ptx_message_flag["ipa"],
                                     ptx_ipa_id=ptx_message_id["ipa"],
                                     ptx_location=ptx_message_flag["location"],
                                     ptx_location_id=ptx_message_id["location"],
                                     ptx_image=ptx_message_flag["image"],
                                     ptx_image_id=ptx_message_id["image"],
                                     ptx_video=ptx_message_flag["video"],
                                     ptx_video_id=ptx_message_id["video"],
                                     ptx_voice=ptx_message_flag["voice"],
                                     ptx_voice_id=ptx_message_id["voice"],
                                     ptx_file=ptx_message_flag["file"],
                                     ptx_file_id=ptx_message_id["file"],
                                     ptx_attachment=ptx_message_flag["attachment"],
                                     Iteration=iteration,
                                     Result=result))

def iteration_success_condition_handler(ptx_message_flag: dict[str, str]):
    '''
    handles the condition for a success case

    Parameters:\n
    ptx_message_flag: dict[str, str]
    '''
    result = False

    for key, value in ptx_message_flag.items():
        if value == "PASS" or value == "N/A":
            result = True
        else:
            result = False
         
    return result

def update_iterationPTXMessages_messageID_Flags(iterationPTXMessages: list[dict[str, str]], key: str, message_id: str, flag_value: str):
    '''
    Updates the dictionary iterationPTXMessages

    '''
    logging.debug("Entering update_iterationPTXMessages_dictionary")
    for dictionary in iterationPTXMessages:
        message_id_key_string = "ptx_" + key + "_id"
        message_flag_key_string = "ptx_" + key
        if dictionary.get(message_id_key_string) == message_id:
            dictionary[message_flag_key_string] = flag_value



def SingleTestPTX(filePath: str, currentTestYear: str, input_xml: dict = None):
    '''
    Task for this is to identify every single PTX Message that ocurred.

    Parameters:\n
    filePath: str, path to the text file containing PTX Messages Test or ADB logs with KPI calls.
    currentTestYear: str, ADB Logs and PTT Logs have no year in its timestamp logging. if using this mehtod
                     alone for other tasks, just use handle it with current time method and 
                     provided to this method as input.
    input_xml: dict, This is exclusive for EEI Automation Tool, inputConfig_xml turned into a dictionary. Optional parameter for separate method call.
    
    Output:\n
    a List of dictionary.
    '''
    logging.debug("Single test PTX txt logcat")
    logging.debug("param filePath: " + filePath)

    ptx_send_start = str("")
    ptx_send_end = str("")
    durationDeltaTime = datetime.timedelta(0,0,0,0,0,0,0)
    iteration = 1
    duration = 0.0
    currentAttachmentMessageID = str("")
    currentAttachmentTS = str("")
    result = str("")
    ptx_send_start_datetime = datetime.datetime(int(currentTestYear),1,1,1,1,1,1)
    ptx_iteration_start = str("")
    ptx_iteration_end = str("")
    attachment_lock = False

    iterationPTXMessages = [dict(Test="TestPTT", Time_milliseconds="0.5", Iteration="1", Result="PASS")]
    iterationPTXMessages.remove(dict(Test="TestPTT", Time_milliseconds="0.5", Iteration="1", Result="PASS"))

    #The strings will be the property MessageID from the data retrieved in many PTX operations
    ptx_message_flag = dict(text="", ipa="", location="", image="", video="", voice="", file="", attachment="")
    determine_iteration_flow(input_xml, ptx_message_flag)
    ptx_message_ts = dict(text_send= "", text_received="", location_send="", location_received="", ipa="", image="", video="", voice="", file="")
    ptx_message_id = dict(text="", location="", ipa="", image="", video="", voice="", file="", attachment="")
    ptx_iteration_operation_flag = False # first time
    iteration_priority = set_iteration_priority(ptx_message_flag)

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
                    currentLineTimestamp = datetime.datetime.strptime(tempTimestampStr, LOGCAT_TIMESTAMP_FORMAT)
                    currentLineTimestamp = currentLineTimestamp.replace(year=int(currentTestYear))
                    currentLineTimestampTxt = datetime.datetime.strftime(currentLineTimestamp, NORMAL_TIMESTAMP_FORMAT)
                    deltaTimeCurrentTime = datetime.timedelta(days=currentLineTimestamp.day, 
                                                              minutes=currentLineTimestamp.minute, 
                                                              hours=currentLineTimestamp.hour, 
                                                              seconds=currentLineTimestamp.second, 
                                                              microseconds=currentLineTimestamp.microsecond)
                        

                    #### PTX.
                    #Send Text Message 12.x and 11.x KPI Entered #NOTE: First is 12.x and second is 11.x
                    if line.find("KOD PERF: HistoryCtrl.onSendTextMsg() >> entered") > -1 or line.find("KOD PERF: Entering HistoryCtrl.onSendPtxMsg()") > -1:
                        logging.debug("Send Text Message KPI Entered")
                        logging.debug("Code name change to onSendPtxMsg()")
                        
                        #Case for determining that there was an issue checking with the sequence.
                        iteration_boolean = iteration_success_condition_handler(ptx_message_flag)
                        if ptx_iteration_operation_flag == True and iteration_priority == "text" and iteration_boolean == False:
                            ptx_send_start_datetime = ptx_send_start_datetime.replace(year=int(currentTestYear))
                            ptx_send_start = datetime.datetime.strftime(ptx_send_start_datetime, NORMAL_TIMESTAMP_FORMAT)
                            ptx_iteration_end = currentLineTimestampTxt
                            ptx_send_start = ptx_send_start
                            ptx_send_end = ptx_send_end if ptx_message_flag["text"] == "PASS" else "N/A"
                            duration = duration if ptx_message_flag["text"] == "PASS" else 10.0
                            
                            ptx_message_ts["text_send"] = ptx_send_start
                            ptx_message_ts["text_received"] = ptx_send_end

                            # Record
                            record_ptx_iteration_fail(iterationPTXMessages, 
                                                         ptx_message_flag,
                                                         ptx_message_id, 
                                                         ptx_message_ts, 
                                                         duration, 
                                                         ptx_iteration_start, 
                                                         ptx_iteration_end, 
                                                         iteration)
                            iteration = iteration + 1
                            ptx_iteration_operation_flag = False
                            #restarting dictionary acting as flags
                            ptx_message_id_clear(ptx_message_id)
                            ptx_message_ts_clear(ptx_message_ts)
                            determine_iteration_flow(input_xml, ptx_message_flag)

                        #True case
                        elif ptx_iteration_operation_flag == True and iteration_priority == "text" and iteration_boolean == True: #Condition needs to add N/A and all ptx options based on non N/A
                            result = "PASS"
                            ptx_iteration_end = currentLineTimestampTxt
                            record_ptx_iteration_success(iterationPTXMessages, 
                                                         ptx_message_flag,
                                                         ptx_message_id, 
                                                         ptx_message_ts, 
                                                         duration, 
                                                         ptx_iteration_start, 
                                                         ptx_iteration_end, 
                                                         iteration)
                            iteration = iteration + 1
                            ptx_iteration_operation_flag = False
                            #restarting dictionary acting as flags
                            ptx_message_id_clear(ptx_message_id)
                            ptx_message_ts_clear(ptx_message_ts)
                            determine_iteration_flow(input_xml, ptx_message_flag)

                        #ptx_iteration_operation_flag = True
                        ptx_message_ts["text_send"] = currentLineTimestampTxt
                        
                        if iteration_priority == "text":
                            ptx_iteration_start = currentLineTimestampTxt
                            logging.debug(ptx_iteration_start)
                            ptx_iteration_start_datetime = datetime.datetime.strptime(ptx_iteration_start, NORMAL_TIMESTAMP_FORMAT) #03/21 14:49:35:084 ##ValueError: time data '2023-04-21 at 16:13:02.129000' does not match format '%m/%d %H:%M:%S:%f'
                            ptx_iteration_start_timedelta = datetime.timedelta(days=ptx_iteration_start_datetime.day, minutes=ptx_iteration_start_datetime.minute, hours=ptx_iteration_start_datetime.hour, seconds=ptx_iteration_start_datetime.second, microseconds=ptx_iteration_start_datetime.microsecond)
                            ptx_iteration_operation_flag = True

                    #Send Text Message KPI - START #NOTE present in both 11.3 and below and above 12.x and above
                    elif line.find("KOD PERF: Type:33 Status:1") > -1:
                        logging.debug("Send Text Message KPI")
                        TS_index_start = line.index("TS:") + 3
                        TS_line = line[TS_index_start:]
                        ptx_send_start = TimestampHandling.ts_handler(TS_line)
                        logging.debug(ptx_send_start)
                        ptx_send_start_datetime = datetime.datetime.strptime(ptx_send_start, TS_TIMESTAMP_FORMAT) #03/21 14:49:35:084
                        ptx_send_start_timedelta = datetime.timedelta(days=ptx_send_start_datetime.day, minutes=ptx_send_start_datetime.minute, hours=ptx_send_start_datetime.hour, seconds=ptx_send_start_datetime.second, microseconds=ptx_send_start_datetime.microsecond)

                        ptx_message_ts["text_send"] = ptx_send_start
                        attachment_lock = False
                    
                    #11.x PTX Data before sending. second is 11.x before sending data and third are 11.x received delivered status message
                    elif line.find("Entering lib.PtxUtils.sendPtxMessage()-> lPtxData") > -1 or line.find("DocManager.handleMetaDoc() >> KOD PERF: forcedNotifyReq") > -1:
                        logging.debug("Entering lib.PtxUtils.sendPtxMessage()-> lPtxData")

                         #Verification that text sending was succesful or failure
                        ptx_data_indexes = StringHandling.curly_bracket_index_finder_rfind(line)
                        json_object = json.loads(line[ptx_data_indexes[0]:ptx_data_indexes[1]])
                        messageId = ""
                        
                        #Identify which PTX data it is.
                        if line.find("Entering lib.PtxUtils.sendPtxMessage()-> lPtxData") > -1:
                            logging.debug("Entered lib.PtxUtils.sendPtxMessage()-> lPtxData. IF STATEMENT")
                            messageId = json_object["ptxMeta"]["messageID"]
                            messageType = json_object["ptxMeta"]["ptxType"][0]
                            # Use PTX data identifier parsing
                            # dict(text="", location="", ipa="", image="", video="", voice="", file="", attachment="")
                            if messageType == "text":
                                ptx_message_id["text"] = messageId
                            elif messageType == "location":
                                ptx_message_id["location"] = messageId
                            elif messageType == "multimedia":
                                messageAttachmentName = json_object["attachments"][0]["fileName"]
                                if messageAttachmentName.find("IMG") > -1:
                                    ptx_message_id["image"] = messageId
                                elif messageAttachmentName.find("VID") > -1:
                                    ptx_message_id["video"] = messageId
                                elif messageAttachmentName.find("AUD") > -1:
                                    ptx_message_id["voice"] = messageId
                                elif messageAttachmentName.find("File") > -1:
                                    ptx_message_id["file"] = messageId

                        if line.find("DocManager.handleMetaDoc() >> KOD PERF: forcedNotifyReq") > -1:
                            logging.debug("DocManager.handleMetaDoc() >> KOD PERF: forcedNotifyReq. IF STATEMENT")
                            #11.x check. By the time this enters, we have previously identified the PTX type, this json does not have ptxType. You can use this line for identifying a previous line based on messageID. 
                            found_messageId: str = json_object["data"]["doc"]["messageID"]
                            data_rev: str = json_object["data"]["changes"][0]["rev"]
                            #Search through each ptx_message_id entry and match the messageID just obtained.
                            if data_rev.find("3-") > -1:
                                if ptx_message_id["image"] == found_messageId:
                                    ptx_message_flag["image"] = "PASS"
                                    update_iterationPTXMessages_messageID_Flags(iterationPTXMessages, key="image", message_id=found_messageId, flag_value="PASS")
                                    ptx_message_flag["attachment"] = "PASS"
                                elif ptx_message_id["video"] == found_messageId:
                                    ptx_message_flag["video"] = "PASS"
                                    update_iterationPTXMessages_messageID_Flags(iterationPTXMessages, key="video", message_id=found_messageId, flag_value="PASS")
                                    ptx_message_flag["attachment"] = "PASS"
                                elif ptx_message_id["voice"] == found_messageId:
                                    ptx_message_flag["voice"] = "PASS"
                                    update_iterationPTXMessages_messageID_Flags(iterationPTXMessages, key="voice", message_id=found_messageId, flag_value="PASS")
                                    ptx_message_flag["attachment"] = "PASS"
                                elif ptx_message_id["file"] == found_messageId:
                                    ptx_message_flag["file"] = "PASS"
                                    update_iterationPTXMessages_messageID_Flags(iterationPTXMessages, key="file", message_id=found_messageId, flag_value="PASS")
                                    ptx_message_flag["attachment"] = "PASS"
                            
                    #11.3 PTX Delivery Status (Location and text)
                    elif line.find("KOD PERF: HistoryHelper.updateDeliveryStatus() >> Entered") > -1:
                        logging.debug("KOD PERF: HistoryHelper.updateDeliveryStatus() >> Entered")
                        ptx_data_indexes = StringHandling.curly_bracket_index_finder_rfind(line)
                        json_object = json.loads(line[ptx_data_indexes[0]:ptx_data_indexes[1]])
                        found_messageId: str = json_object["messageID"]
                        if ptx_message_id["text"] == found_messageId:
                            ptx_message_flag["text"] = "PASS"
                        elif ptx_message_id["location"] == found_messageId:
                            ptx_message_flag["location"] = "PASS"

                    # 12.x PTX message sent confirmation
                    elif line.find("KOD PERF: HistoryCtrl.onSendTextMsg() >> resolved") > -1:
                        logging.debug("Messsage sent data")
                        ptx_message_flag["text"] = "PASS"
                        #ptx_data_info = StringHandling.bracket_removal(line[ptx_data_indexes[0]:ptx_data_indexes[1]])
                        ptx_data_info = line
                        ptx_data = StringHandling.curly_bracket_parser(ptx_data_info)
                        ptx_message_id["text"] = ptx_data["messageID"]

                    
                    # Text only. Server Response of sending to self device a msgReceiptRevcd call confirmation - END #NOTE present in both 11.3 and below and above 12.x and above
                    elif line.find("KOD PERF: Type:33 Status:2") > -1:
                        logging.debug("Refer Line")

                        TS_index_start = line.index("TS:") + 3
                        TS_line = line[TS_index_start:]
                        ptx_send_end = StringHandling.bracket_removal(TS_line)
                        logging.debug(ptx_send_end)
                        ptx_send_end_datetime = datetime.datetime.strptime(ptx_send_end, TS_TIMESTAMP_FORMAT) #03/21 14:49:35:084
                        ptx_send_end_timedelta = datetime.timedelta(days=ptx_send_end_datetime.day, minutes=ptx_send_end_datetime.minute, hours=ptx_send_end_datetime.hour, seconds=ptx_send_end_datetime.second, microseconds=ptx_send_end_datetime.microsecond)

                        #Calculate start iteration with end iteration time to get current iteration duration.
                        durationDeltaTime = ptx_send_end_timedelta - ptx_send_start_timedelta

                        #transform durationDelta to milliseconds
                        duration = durationDeltaTime / datetime.timedelta(milliseconds=1)

                        ptx_message_ts["text_received"] = ptx_send_end
                        ptx_message_flag["text"] = "PASS"
                    
                    #IPA. 11.x and 12.x NOTE: IPA is not really a PTX feature nor a Message, its just an alert. Therefore, no PTX message id
                    elif line.find("AppCallHandler.sendIpa()-> resultType") > -1:
                        logging.debug("AppCallHandler.sendIpa()-> resultType")
                        ptx_message_id["ipa"] = "None"
                        
                    elif line.find("CallStatusCB.sendIpaSuccessCB()") > -1:
                        logging.debug("CallStatusCB.sendIpaSuccessCB()")
                        ptx_message_flag["ipa"] = "PASS"
                    
                    elif line.find("CallStatusCB.sendIpaFailureCB()") > -1:
                        logging.debug("CallStatusCB.sendIpaFailureCB()")
                        ptx_message_flag["ipa"] = "FAIL"

                    #LOCATION
                    #12.x Data of server response of the sent message to self device from a msgReceiptRecvd call confirmation. #NOTE: apparently this method call is new from 12.3 and it is not present for 11.1
                    elif line.find("PluginMcdataMessagingCb.msgReceiptRecvdCb()") > -1:
                        logging.debug("msgReceiptRecvdCb")
                        #Could be either Text or Location
                        ptx_line = line
                        ptx_info = StringHandling.curly_bracket_parser(ptx_line)
                        message_id = ptx_info["messageID"]
                       
                       #compare using ptx_message_id
                        if ptx_message_id["location"] == message_id:
                            ptx_message_ts["location_received"] = currentLineTimestampTxt
                        
                        if iteration_priority == "location":
                            ptx_iteration_end = currentLineTimestampTxt
                            ptx_iteration_operation_flag = True

                        #else does not matter due to being text version ts and id are processed by other code calls.

                    

                    #12. x. Server Response of sending PTX Map Location.
                    elif line.find("KOD PERF: MapUtils.sendLocation() >> resolved") > -1:
                        logging.debug("MapUtils.sendLocation() >> resolved")

                        ###LOG
                        #Case for determining that there was an issue checking with the sequence.
                        iteration_boolean = iteration_success_condition_handler(ptx_message_flag)
                        if ptx_iteration_operation_flag == True and iteration_priority == "location" and iteration_boolean == False:
                            ptx_send_start_datetime = ptx_send_start_datetime.replace(year=int(currentTestYear))
                            ptx_send_start = datetime.datetime.strftime(ptx_send_start_datetime, NORMAL_TIMESTAMP_FORMAT)
                            ptx_iteration_end = currentLineTimestampTxt
                            ptx_send_start = ptx_send_start
                            ptx_send_end = ptx_send_end if ptx_message_flag["text"] == "PASS" else "N/A"
                            duration = duration if ptx_message_flag["text"] == "PASS" else 10.0
                            
                            ptx_message_ts["text_send"] = ptx_send_start
                            ptx_message_ts["text_received"] = ptx_send_end

                            # Record
                            record_ptx_iteration_fail(iterationPTXMessages, 
                                                         ptx_message_flag,
                                                         ptx_message_id, 
                                                         ptx_message_ts, 
                                                         duration, 
                                                         ptx_iteration_start, 
                                                         ptx_iteration_end, 
                                                         iteration)
                            iteration = iteration + 1
                            ptx_iteration_operation_flag = False
                            #restarting dictionary acting as flags
                            ptx_message_id_clear(ptx_message_id)
                            ptx_message_ts_clear(ptx_message_ts)
                            determine_iteration_flow(input_xml, ptx_message_flag)

                        #True case
                        elif ptx_iteration_operation_flag == True and iteration_priority == "location" and iteration_boolean == True: #Condition needs to add N/A and all ptx options based on non N/A
                            result = "PASS"
                            ptx_iteration_end = currentLineTimestampTxt
                            record_ptx_iteration_success(iterationPTXMessages, 
                                                         ptx_message_flag,
                                                         ptx_message_id, 
                                                         ptx_message_ts, 
                                                         duration, 
                                                         ptx_iteration_start, 
                                                         ptx_iteration_end, 
                                                         iteration)
                            iteration = iteration + 1
                            ptx_iteration_operation_flag = False
                            #restarting dictionary acting as flags
                            ptx_message_id_clear(ptx_message_id)
                            ptx_message_ts_clear(ptx_message_ts)
                            determine_iteration_flow(input_xml, ptx_message_flag)

                        ###GET VALUES
                        ptx_data_indexes = StringHandling.curly_bracket_index_finder(line)
                        ptx_location_info = StringHandling.curly_bracket_parser(line[ptx_data_indexes[0]:ptx_data_indexes[1]])
                        ptx_message_id["location"] = ptx_location_info["messageID"]
                        ptx_message_ts["location_send"] = currentLineTimestampTxt
                        ptx_message_flag["location"] = "PASS"
                        attachment_lock = False

                    #12.x Server Response of sending any PTX Attachment - IMPLEMENT. #NOTE: apparently this method call is new from 12.X and it is not present for 11.X
                    elif line.find("PtxUtils.sendPtxAttachment() >> resolved") > -1:
                        logging.debug("PtxUtils.sendPtxAttachment() >> resolved")
                        #Could be either Image, Video, Voice, File. This will be for the timestamp recording
                        #This call contains MessageID.
                        #Make a currentAttachmentMessageID and currentAttachmentTS in here and save it (one entry only)

                        ##LOG
                        #Case for determining that there was an issue checking with the sequence.
                        iteration_boolean = iteration_success_condition_handler(ptx_message_flag)
                        if ptx_iteration_operation_flag == True and (iteration_priority == "image" 
                                                                     or iteration_priority == "video"
                                                                     or iteration_priority == "voice"
                                                                     or iteration_priority == "file") and iteration_boolean == False:
                            ptx_send_start_datetime = ptx_send_start_datetime.replace(year=int(currentTestYear))
                            ptx_send_start = datetime.datetime.strftime(ptx_send_start_datetime, NORMAL_TIMESTAMP_FORMAT)
                            ptx_iteration_end = currentLineTimestampTxt
                            ptx_send_start = ptx_send_start
                            ptx_send_end = ptx_send_end if ptx_message_flag["text"] == "PASS" else "N/A"
                            duration = duration if ptx_message_flag["text"] == "PASS" else 10.0
                            
                            ptx_message_ts["text_send"] = ptx_send_start
                            ptx_message_ts["text_received"] = ptx_send_end

                            # Record
                            record_ptx_iteration_fail(iterationPTXMessages, 
                                                         ptx_message_flag,
                                                         ptx_message_id, 
                                                         ptx_message_ts, 
                                                         duration, 
                                                         ptx_iteration_start, 
                                                         ptx_iteration_end, 
                                                         iteration)
                            iteration = iteration + 1
                            ptx_iteration_operation_flag = False
                            #restarting dictionary acting as flags
                            ptx_message_id_clear(ptx_message_id)
                            ptx_message_ts_clear(ptx_message_ts)
                            determine_iteration_flow(input_xml, ptx_message_flag)

                        #True case
                        elif ptx_iteration_operation_flag == True and (iteration_priority == "image" 
                                                                     or iteration_priority == "video"
                                                                     or iteration_priority == "voice"
                                                                     or iteration_priority == "file") and iteration_boolean == True: #Condition needs to add N/A and all ptx options based on non N/A
                            result = "PASS"
                            ptx_iteration_end = currentLineTimestampTxt
                            record_ptx_iteration_success(iterationPTXMessages, 
                                                         ptx_message_flag,
                                                         ptx_message_id, 
                                                         ptx_message_ts, 
                                                         duration, 
                                                         ptx_iteration_start, 
                                                         ptx_iteration_end, 
                                                         iteration)
                            iteration = iteration + 1
                            ptx_iteration_operation_flag = False
                            #restarting dictionary acting as flags
                            ptx_message_id_clear(ptx_message_id)
                            ptx_message_ts_clear(ptx_message_ts)
                            determine_iteration_flow(input_xml, ptx_message_flag)

                        ##GET VALUE
                        ptx_data_indexes = StringHandling.curly_bracket_index_finder(line)
                        attachment_lock = True
                        ptx_attachment_info = StringHandling.curly_bracket_parser(line[ptx_data_indexes[0]:ptx_data_indexes[1]])
                        currentAttachmentMessageID = ptx_attachment_info["messageID"] #Grabs the whole PluginMcdataMessagingCb.msgReceiptRecvdCb() >> data line
                        currentAttachmentTS = currentLineTimestampTxt

                        if (iteration_priority == "image" or 
                            iteration_priority == "video" or 
                            iteration_priority == "voice" or
                            iteration_priority == "file"):
                           ptx_iteration_end = currentLineTimestampTxt
                           ptx_iteration_operation_flag = True



                    #12.x. Type of content the PTX Attachment contains - IMPLEMENT #NOTE: apparently this method call is new from 12.3 and it is not present for 11.1
                    elif line.find("MessagingHelper.getAttachmentInfo() >> meta info") > -1: 
                        logging.debug("MessagingHelper.getAttachmentInfo() >> meta info")

                        #This covers image, audio, video, file

                        #read current attachment, note that it is on json format in the form of one line string and multiple encased curly brackets.
                        shortened_line_index_start = line.find("meta info =")
                        shortened_line = line[shortened_line_index_start:]
                        data_line_index = StringHandling.bracket_index_finder(shortened_line) ####ISSUE empty meta info = [] this will occur for text and maps
                        data_line = shortened_line[data_line_index[0]:data_line_index[1]]

                        if attachment_lock:
                            #This is to avoid multiple getAttachmentInfo calls from the same attachment for the same messageID
                            attachment_lock = False
                            attachment_info = json.loads(data_line)
                            file_type = str(attachment_info[0]["contentInfo"]["fileType"]) #forcing the fileType as string.

                            if file_type.find("image") > -1:
                                ptx_message_ts["image"] = currentAttachmentTS
                                ptx_message_id["image"] = currentAttachmentMessageID
                                ptx_message_flag["image"] = "PASS"
                                ptx_message_flag["attachment"] = "PASS"

                            elif file_type.find("audio") > -1:
                                ptx_message_ts["voice"] = currentAttachmentTS
                                ptx_message_id["voice"] = currentAttachmentMessageID
                                ptx_message_flag["voice"] = "PASS"
                                ptx_message_flag["attachment"] = "PASS"

                            elif file_type.find("video") > -1:
                                ptx_message_ts["video"] = currentAttachmentTS
                                ptx_message_id["video"] = currentAttachmentMessageID
                                ptx_message_flag["video"] = "PASS"
                                ptx_message_flag["attachment"] = "PASS"

                            elif file_type.find("application") > -1:
                                ptx_message_ts["file"] = currentAttachmentTS
                                ptx_message_id["file"] = currentAttachmentMessageID
                                ptx_message_flag["file"] = "PASS"
                                ptx_message_flag["attachment"] = "PASS"
                        
                    
                    ###11.3 and below log calls. Not bothering with 11.3 unless requested. ongoing exisiting KPI analysis will be on 12.3
                    

                    else:
                        p = "this is for the result pass or fail. Checking is done at start (SUCCESS_CALL_KPI_COUNTER) and at the end (finally)"
                             
            f.close()
    except IOError as e:
        logging.error(f"Error reading file: {filePath}")
        logging.error(e)

    finally:
        logging.debug(f"reading file: {filePath} completed...")

        #EDIT THIS BELOW

        # Last iteration
        #Failure case
        iteration_boolean = iteration_success_condition_handler(ptx_message_flag)
        if ptx_iteration_operation_flag == True and iteration_boolean == False:
            ptx_send_start_datetime = ptx_send_start_datetime.replace(year=int(currentTestYear)) 
            ptx_send_start = datetime.datetime.strftime(ptx_send_start_datetime, NORMAL_TIMESTAMP_FORMAT)
            ptx_iteration_end = currentLineTimestampTxt
            ptx_send_start = ptx_send_start
            ptx_send_end = ptx_send_end if ptx_message_flag["text"] == "PASS" else "N/A"
            duration = duration if ptx_message_flag["text"] == "PASS" else 10.0
            
            ptx_message_ts["text_send"] = ptx_send_start
            ptx_message_ts["text_received"] = ptx_send_end
            # Record
            # Record
            record_ptx_iteration_fail(iterationPTXMessages,
                                      ptx_message_flag,
                                      ptx_message_id,
                                      ptx_message_ts,
                                      duration,
                                      ptx_iteration_start,
                                      ptx_iteration_end,
                                      iteration)

            iteration = iteration + 1
        
        #True case if ptx_iteration_operation_flag is True and ptx_message_id are all filled.
        #When Video check gets implemented include the check for video in here.
        #When other attachment feature selection gets added, include them on this if check. Options as and != ""
        elif ptx_iteration_operation_flag == True and iteration_boolean == True:
            ptx_attachment_flag = "PASS"
            result = "PASS"
            ptx_iteration_end = currentLineTimestampTxt
            # Record
            record_ptx_iteration_success(iterationPTXMessages,
                                         ptx_message_flag,
                                         ptx_message_id,
                                         ptx_message_ts,
                                         duration,
                                         ptx_iteration_start,
                                         ptx_iteration_end,
                                         iteration)
            iteration = iteration + 1
    logging.debug(f"loop iterations={str(iteration-1)}") #(reducing 1 due to overcount)
    
    return iterationPTXMessages

def ptx_message_id_clear(dictionary: dict[str, str]):
    ''' uses given dictionary for ptx_message_id to set the value of each key to "". does not delete key but resets the value.
    '''
    dictionary["text"] = ""
    dictionary["location"] = ""
    dictionary["image"] = ""
    dictionary["video"] = ""
    dictionary["voice"] = ""
    dictionary["file"] = ""

def ptx_message_ts_clear(dictionary: dict[str,str]):
    ''' uses given dictionary for ptx_message_ts to set the value of each key to "". does not delete key but resets the value.
    '''
    dictionary["text_send"] = ""
    dictionary["text_received"] = ""
    dictionary["location_send"] = ""
    dictionary["location_received"] = ""
    dictionary["image"] = ""
    dictionary["video"] = ""
    dictionary["voice"] = ""
    dictionary["file"] = ""

def ptx_message_id_forced_NA(dictionary: dict[str, str]):
    ''' For the mean time as the selection for PTX test to run gets implemented on the automation. This will force the values
    for making that test N/A. Update this based on what is currently is being executed. Delete this after selection implementation is complete
    '''
    dictionary["location"] = "N/A"
    dictionary["video"] = "N/A"
    dictionary["file"] = "N/A"
    dictionary["voice"] = "N/A"
    dictionary["image"] = "N/A"

def ptx_message_ts_forced_NA(dictionary: dict[str, str]):
    ''' For the mean time as the selection for PTX test to run gets implemented on the automation. This will force the values
    for making that test N/A. Update this based on what is currently is being executed. Delete this after selection implementation is complete
    '''
    dictionary["location_send"] = "N/A"
    dictionary["location_received"] = "N/A"
    dictionary["video"] = "N/A"
    dictionary["file"] = "N/A"
    dictionary["voice"] = "N/A"
    dictionary["image"] = "N/A"


if __name__ == '__main__':
    logging.debug(basePath)
    
