## Helper module for 
# Handling different string handling task and formats that are commonly recurring.
##
from asyncio.windows_events import NULL
import csv
import datetime
import logging
import os

def bracket_removal(string: str):
    '''
    removes bracket inside the given string and returns newly modified string.
    Use this method on strings that only contains a single bracket.\n
    returns str
    '''
    tempStart = string.find("[")
    tempEnd = string.find("]")
    result = string[tempStart+1:tempEnd]
    return result

def curly_bracket_parser(string: str):
    '''
    parses the following string format into a dictionary
    Use this method on strings that only contains a single curly bracket.\n
    returns dict.\n

    Input Example:
    {"uiDataMessageID":21,"messageID":"14246a27a77848a58755ffdd9ef29e21","conversationID":"f0df79a0fc7d36b18f548b709bff9558","errorCode":1000}
    '''
    temp = string.replace("{", "")
    list = temp.replace("}","")
    list = temp.split(sep=",")
    dictionary = {"": "", "": ""}
    dictionary.clear()
    for item in list:
        list_expression = item.split(sep=":")
        name = list_expression[0].replace("\"", "")
        name = name.replace("{", "")
        key = list_expression[1].replace("\"", "")
        key = key.replace("}", "")
        dictionary.update({name:key})
    return dictionary

def curly_bracket_index_finder(string: str):
    '''
    grabs a full string containing a string with curly brackets.
    End point is lowest index.\n
    Retrieves the following from it.
    start index, end index., int
    '''
    start_index = string.find("{")
    end_index = string.find("}") + 1

    return start_index, end_index

def curly_bracket_index_finder_rfind(string: str):
    '''
    grabs a full string containing a string with curly brackets. 
    End point is highest index.\n
    Retrieves the following from it.
    start index, end index., int
    '''
    start_index = string.find("{")
    end_index = string.rfind("}") + 1

    return start_index, end_index

def bracket_index_finder(string: str):
    '''
    grabs a full string containing a string with brackets,
    retrieves the following from it.
    start index, end index., int
    '''
    start_index = string.find("[")
    end_index = string.find("]") + 1

    return start_index, end_index

if __name__ == '__main__':
    #Task if called as a main
    logging.info("running file directly, this file is a helper file.")