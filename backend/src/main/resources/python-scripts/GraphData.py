## Helper module for 
# Get List of files in the current directory.
# removes __init__.py from list
##
from asyncio.windows_events import NULL
import csv
import time
import datetime
import os
import logging
import xml.etree.ElementTree as ET #handles XML files
from pathlib import Path
import traceback

import plotly.express as PX #Plotly library. 
import plotly.graph_objects as go
import plotly.subplots as SP
import pandas
import numpy as np

logging.basicConfig(level=logging.INFO) #For production configure to INFO. For development configure the level to DEBUG. 

parentPath = os.path.abspath(os.path.join(os.getcwd()))
test_tempPath = os.path.dirname(os.path.realpath(__file__)) #fullpath...\python scripts
test_basePath = test_tempPath.replace("\\python scripts", "")
basePath = parentPath #test_basePath for development. parentPath for product release.

OUTPUT_FOLDER_NAME = "graph-output"
OUTPUT_PATH = basePath + "\\graph-output"

OUTPUT_CALL_PATH = OUTPUT_PATH + "\\ptx_output_summary_adb_example1.txt"
OUTPUT_HTML_DEMO = OUTPUT_PATH + "\\quickPlotlyTest.html"
OUTPUT_BATCH_GANTT_CHART = OUTPUT_PATH + "\\BatchTestGanttChart.html"
OUTPUT_TEST_GANTT_CHART = OUTPUT_PATH + "\\TestGanttChart.html"
OUTPUT_BATCH_SCATTER_CHART = OUTPUT_PATH + "\\BatchTestScatterChart.html"
OUTPUT_TEST_SCATTER_CHART = OUTPUT_PATH + "\\TestScatterChart.html"
OUTPUT_TEST_BAR_CHART = OUTPUT_PATH + "\\TestBarChart.html"

OUTPUT_PARAM_FOLDER_NAME_PATH = basePath

EXAMPLE_KPI_DATA_PATH = basePath + "\\example data\\KPI_Logcat" #quick Example

GENERIC_PATH = basePath + "\\graph-output"

JUNIT_TIMESTAMP_FORMAT = "%Y-%m-%dT%H:%M:%S"
LOGCAT_TIMESTAMP_FORMAT = "%m-%d %H:%M:%S.%f"

NORMAL_TIMESTAMP_FORMAT = "%Y-%m-%d at %H:%M:%S.%f"

PTT_CALL_LOGCAT_NAME = "RecentCallTest" 
PTX_LOGCAT_NAME = "PtxMessagesTest"
PTT_ADD_CONTACT_NAME = "AddContactTest"
KPI2_NAME = "VideoCallTest"
BATCH_ONE_NAME = "BatchOne"

PTX_ENTRIES = ["Time_milliseconds", "ptx_iteration_start_time", 'ptx_iteration_end_time', 'ptx_send_time_start', 'ptx_send_time_end', "Result", "ptx_text", "ptx_text_id", "ptx_ipa", "ptx_ipa_id", "ptx_location", "ptx_location_id", "ptx_image", "ptx_image_id", "ptx_voice", "ptx_voice_id", "ptx_video", "ptx_video_id", "ptx_file", "ptx_file_id", "ptx_attachment", "Iteration"]

def create_directory_graph_output():
    '''
    Creates a directory called graph-output if it does not exist.
    Each graph will be calling this method before making a graph output.
    '''
    Path(OUTPUT_PATH).mkdir(exist_ok=True)

def create_directory_graph_output_folder_name(folder_name: str):
    '''
    Creates a directory called based on the parameter given if it does not exist.
    '''
    create_directory_graph_output()
    final_path = OUTPUT_PARAM_FOLDER_NAME_PATH + f"\\{OUTPUT_FOLDER_NAME}" + f"\\{folder_name}"
    Path(final_path).mkdir(exist_ok=True)

def create_directory_graph_output_generic():
    ''' Test method. 
    generic path for creating the directory, generilizing the code to be more flexible.
    Each graph will be calling this method before making a graph output.
    '''
    Path(GENERIC_PATH).mkdir(exist_ok=True)

def test_create_directory_graph_output(path: str):
    ''' Test method. 
    This is for manually changing the path of the output directory.
    This should be called along with a test_method for the graph figure.\n
    Creates a directory called graph-output if it does not exist.
    Each graph will be calling this method before making a graph output.
    '''
    Path(path).mkdir(exist_ok=True)

def data_average_calculator(dataFrame: pandas.DataFrame):
    ''' 
    This task will be performed multiple times across different tests.
    It generalizes the data and grabs the average of the milliseconds that are in the
    dataframe.

    Parameters:\n
    dataFrame = pandas.DataFrame object. This will be from any of the chart generators.
    '''
    logging.debug("Entering data_average_calculator")
    result = dataFrame["Time_milliseconds"].mean()
    return result

def check_missing_dataframe_ptx_content(data: list[dict[str, str]]):
    dataFrame = pandas.DataFrame(data)

    missing_items_set = set()
    
    logging.debug(dataFrame.columns)
    index = dataFrame.columns
    
    for col in dataFrame.columns:
        if col in PTX_ENTRIES:
            logging.info(f"Data frame contains the entry: {col}")
        else:
            logging.warning(f"Data frame does not contain the entry: {col}")
            missing_items_set.add(col)
    logging.info("if any new name not from the PTX List show up from the list, it will most likely not be handled by this file.")

    
    missing_items = list(missing_items_set)

    logging.debug(missing_items)

    return missing_items

def singleTestPTTBarChart(data: list[dict[str, str]], folder_name = ""):
    '''
    Graph type for this is Bars.
    x: Iterations
    y: Time_milliseconds
    color: based on result, green PASS, red FAIL

    Parameters:\n
    data: list[dict[str, str]]. The data from Databreakdown.

    Output:\n
    Figure: plotly library Figure, but this is for Dash server live logging implementation.

    NOTE: NOTE: for organization of the flow of PTT calls These are the equivalencies of the field names in here 
    and the field names displayed in GraphData.
    call_establish_start = Key Down Press
    call_establish_end = Grant Tone
    Time_milliseconds = KPI_1 to Grant Tone Time

    '''
    if folder_name == "":
        create_directory_graph_output()
    else:
        create_directory_graph_output()
        create_directory_graph_output_folder_name(folder_name)

    dataFrame = pandas.DataFrame(data)

    #Pass and Failure Data processing
    try:
        total_pass = dataFrame["Result"].value_counts().PASS
    except:
        logging.info("no pass cases")
        total_pass = 0
    
    try:
        total_failures = dataFrame["Result"].value_counts().FAIL
    except:
        logging.info("no fail cases")
        total_failures = 0
    
    total_iterations = dataFrame["Iteration"].count()

    time_milliseconds_average = data_average_calculator(dataFrame)

    #Main Figure
    figure = SP.make_subplots(
        rows=3,
        cols=1,
        shared_xaxes=True,
        shared_yaxes=True,
        vertical_spacing=0.10,
        row_heights=[0.5,0.2,0.1],
        specs=[[{"type": "bar"}],
               [{"type": "table"}],
               [{"type": "table"}]
               ],
        subplot_titles=("PTT Calls Time for KPI 1 to Grant Tone", "PTT Calls Table", "PTT Calls Summary")
    )

    configured_header_names = ["KPI 1 (ms)", "Call Start Time", "PTT Key Press Time", "Call End Time", "Grant Tone Time", "Iteration", "Result"]

    #Main Table
    mainTable = go.Table(
        header=dict(values=configured_header_names,
                    align='left'),
        cells=dict(values=[dataFrame.Time_milliseconds, dataFrame.call_start_time, dataFrame.call_establish_start_time, dataFrame.call_end_time, dataFrame.call_establish_end_time, dataFrame.Iteration, dataFrame.Result],
                   align="left"),
        columnorder=[5,1,3,2,4,0,6],
        columnwidth=[150,300,200,300,200,100,100]    
    )

    #Total table
    totalTable = go.Table(
        header=dict(values=["total_iterations", "total_pass", "total_failures", "time_average"],
                    align="left"),
        cells=dict(values=[[total_iterations], [total_pass], [total_failures], [time_milliseconds_average]],
                   align="left")
    )

    #Dividing the data between Pass and Failures

    barGraph_data_pass = [dict(Test="TestPTT", Time_milliseconds="0.5", Iteration="1", Result="PASS")]
    barGraph_data_pass.remove(dict(Test="TestPTT", Time_milliseconds="0.5", Iteration="1", Result="PASS"))
    
    barGraph_data_fail = [dict(Test="TestPTT", Time_milliseconds="0.5", Iteration="1", Result="PASS")]
    barGraph_data_fail.remove(dict(Test="TestPTT", Time_milliseconds="0.5", Iteration="1", Result="PASS"))

    for item in data:
        if item.get("Result") == "PASS":
            barGraph_data_pass.append(item)
        else:
            barGraph_data_fail.append(item)
    

    #NOTE: trying to access empty entries on dataframes will cause error to be thrown.
    
    #Pass Bar Graph, if any
    if total_pass != 0:
        dataFrame_bar_pass = pandas.DataFrame(barGraph_data_pass)
        data_bar_pass_range = len(dataFrame_bar_pass["Iteration"])
        
        customdata_bar_pass = np.stack((dataFrame_bar_pass["Iteration"],
                            dataFrame_bar_pass["Time_milliseconds"],
                            dataFrame_bar_pass["call_establish_start_time"],
                            dataFrame_bar_pass["call_establish_end_time"],
                            dataFrame_bar_pass["call_start_time"],
                            dataFrame_bar_pass["call_end_time"],
                            dataFrame_bar_pass["Result"]),
                            axis=1)
        
        barGraph_pass = go.Bar(
                x=dataFrame_bar_pass["Iteration"][0:data_bar_pass_range],
                y=dataFrame_bar_pass["Time_milliseconds"][0:data_bar_pass_range],
                customdata=customdata_bar_pass,
                hovertemplate="<b>%{customdata[0]}</b><br><br>" +
                "KPI 1 (ms)=%{customdata[1]}<br>" +
                "PTT Key Press Time=%{customdata[2]}<br>" +
                "Grant Tone Time=%{customdata[3]}<br>" +
                "Call Start Time=%{customdata[4]:NORMAL_TIMESTAMP_FORMAT}<br>" +
                "Call End Time=%{customdata[5]:NORMAL_TIMESTAMP_FORMAT}<br>" +
                "Result=%{customdata[6]}<br>",
                marker=dict(color="green"),
                name="PASS",
                showlegend=True)
        figure.add_trace(barGraph_pass, row=1, col=1)
        
    
    #Fail Bar Graph, if any
    if total_failures != 0:
        dataFrame_bar_fail = pandas.DataFrame(barGraph_data_fail) 

        data_bar_fail_range = len(dataFrame_bar_fail["Iteration"])

        customdata_bar_fail = np.stack((dataFrame_bar_fail["Iteration"],
                                dataFrame_bar_fail["Time_milliseconds"],
                                dataFrame_bar_fail["call_establish_start_time"],
                                dataFrame_bar_fail["call_establish_end_time"],
                                dataFrame_bar_fail["call_start_time"],
                                dataFrame_bar_fail["call_end_time"],
                                dataFrame_bar_fail["Result"]),
                                axis=1)

        barGraph_fail = go.Bar(
                x=dataFrame_bar_fail["Iteration"][0:data_bar_fail_range],
                y=dataFrame_bar_fail["Time_milliseconds"][0:data_bar_fail_range],
                customdata=customdata_bar_fail,
                hovertemplate="<b>%{customdata[0]}</b><br><br>" +
                "KPI 1 (ms)=N/A<br>" +
                "PTT Key Press Time=%{customdata[2]}<br>" +
                "Grant Tone Time=%{customdata[3]}<br>" +
                "Call Start Time=%{customdata[4]:NORMAL_TIMESTAMP_FORMAT}<br>" +
                "Call End Time=%{customdata[5]:NORMAL_TIMESTAMP_FORMAT}<br>" +
                "Result=%{customdata[6]}<br>",
                marker=dict(color="red"),
                name="FAIL",
                showlegend=True)
        figure.add_trace(barGraph_fail, row=1, col=1)
        
    figure.add_trace(mainTable, row=2, col=1)
    figure.add_trace(totalTable, row=3, col=1)

    figure.update_layout(
        title_text="PTT Call Summary",
        height=1000,
    )
    figure.update_xaxes(title=dict(text="Call Numbers"))
    figure.update_yaxes(title=dict(text="Time for call to establish (milliseconds)"))

    if folder_name == "":
        figure.write_html(OUTPUT_PATH + f"\\{PTT_CALL_LOGCAT_NAME}_Graph.html") #Note that a graph object in plotly is Any type.
        print(f"path: {OUTPUT_PATH}\\{PTT_CALL_LOGCAT_NAME}_Graph.html")
    else:
        figure.write_html(OUTPUT_PARAM_FOLDER_NAME_PATH + f"\\{OUTPUT_FOLDER_NAME}" + f"\\{folder_name}" + f"\\{PTT_CALL_LOGCAT_NAME}_Graph.html")
        print(f"path: {OUTPUT_PARAM_FOLDER_NAME_PATH}\\{OUTPUT_FOLDER_NAME}\\{folder_name}\\{PTT_CALL_LOGCAT_NAME}_Graph.html")

    print("single PTT Test Bar Chart created...")
    return figure

def singleTest_PTX_chart(data: list[dict[str, str]], folder_name = ""):
    
    if folder_name == "":
        create_directory_graph_output()
    else:
        create_directory_graph_output()
        create_directory_graph_output_folder_name(folder_name)

    dataFrame = pandas.DataFrame(data)
    missing_items = check_missing_dataframe_ptx_content(data)

    #Pass and Failure Data Processing
    try:
        total_iteration_pass = dataFrame["Result"].value_counts().PASS

    except:
        print("no pass cases")
        total_iteration_pass = 0

    total_ptx_text_pass = 0
    total_ptx_location_pass = 0
    total_ptx_image_pass = 0
    total_ptx_voice_pass = 0
    total_ptx_file_pass = 0
    
    try:
        if dataFrame["ptx_text"].value_counts().index.array.__contains__("PASS"):
            total_ptx_text_pass = dataFrame["ptx_text"].value_counts().PASS
    except:
        total_ptx_text_pass = 0
        traceback.print_exc()
    
    try:
        if dataFrame["ptx_location"].value_counts().index.array.__contains__("PASS"):
            total_ptx_location_pass = dataFrame["ptx_location"].value_counts().PASS
    except:
        total_ptx_location_pass = 0
        traceback.print_exc()

    try:
        if dataFrame["ptx_image"].value_counts().index.array.__contains__("PASS"):
            total_ptx_image_pass = dataFrame["ptx_image"].value_counts().PASS
    except:
        total_ptx_image_pass = 0
        traceback.print_exc()

    try:
        if dataFrame["ptx_voice"].value_counts().index.array.__contains__("PASS"):
            total_ptx_voice_pass = dataFrame["ptx_voice"].value_counts().PASS
    except:
        total_ptx_voice_pass = 0
        traceback.print_exc()

    try:
        if dataFrame["ptx_file"].value_counts().index.array.__contains__("PASS"):
            total_ptx_file_pass = dataFrame["ptx_file"].value_counts().PASS
    except:
        total_ptx_file_pass = 0
        traceback.print_exc()

    total_ptx_pass = (total_ptx_text_pass + 
                          total_ptx_location_pass + 
                          total_ptx_image_pass + 
                          total_ptx_voice_pass + 
                          total_ptx_file_pass)

    try:
        total_iteration_failures = dataFrame["Result"].value_counts().FAIL
        
    except:
        print("no fail cases")
        total_iteration_failures = 0
    
    #NOTE: The only reason this is implemented in this manner for PTX Failures 
    # is due to an error crash that happens when calling dataFrame column value whose name 
    # does not exist which may happen in edge cases where only 1 value exist, 
    # ex: only images failures, therefore there is no FAIL name for text or location.
    total_ptx_text_fails = 0
    total_ptx_location_fails = 0
    total_ptx_image_fails = 0
    total_ptx_voice_fails = 0
    total_ptx_file_fails = 0
    try:
        if dataFrame["ptx_text"].value_counts().index.array.__contains__("FAIL"):
            total_ptx_text_fails = dataFrame["ptx_text"].value_counts().FAIL
    except:
        total_ptx_text_fails = 0
        traceback.print_exc()
    
    try:
        if dataFrame["ptx_location"].value_counts().index.array.__contains__("FAIL"):
            total_ptx_location_fails = dataFrame["ptx_location"].value_counts().FAIL
    except:
        total_ptx_location_fails = 0
        traceback.print_exc()
    
    try:
        if dataFrame["ptx_image"].value_counts().index.array.__contains__("FAIL"):
            total_ptx_image_fails = dataFrame["ptx_image"].value_counts().FAIL
    except:
        total_ptx_image_fails = 0
        traceback.print_exc()
    try:
        if dataFrame["ptx_voice"].value_counts().index.array.__contains__("FAIL"):
            total_ptx_voice_fails = dataFrame["ptx_voice"].value_counts().FAIL
    except:
        total_ptx_voice_fails = 0
        traceback.print_exc()
    
    try:
        if dataFrame["ptx_file"].value_counts().index.array.__contains__("FAIL"):
            total_ptx_file_fails = dataFrame["ptx_file"].value_counts().FAIL
    except:
       total_ptx_file_fails = 0
       traceback.print_exc() 

    total_ptx_failures = (total_ptx_text_fails + 
                          total_ptx_location_fails + 
                          total_ptx_image_fails + 
                          total_ptx_voice_fails + 
                          total_ptx_file_fails)

    total_ptx_messages = total_ptx_pass + total_ptx_failures 
    
    total_iterations = dataFrame["Iteration"].count()

    #Average Calculations
    time_milliseconds_average = data_average_calculator(dataFrame)

    #Main Figure
    figure = SP.make_subplots(
        rows=3,
        cols=1,
        shared_xaxes=True,
        shared_yaxes=True,
        vertical_spacing=0.10,
        row_heights=[0.7,0.4,0.1],
        specs=[[{"type": "bar"}],
               [{"type": "table"}],
               [{"type": "table"}]
               ],
        subplot_titles=("PTX Summary Bar Graph", "PTX Data Table", "PTX Data Summary")
    )

    # With dataframe the order of pulling matters
    configured_header_names = ["Iteration", "Iteration Start", "Iteration End", "Text Send Start", "Text Send End", "Text Duration", "Text", "Text_ID", "IPA", "IPA_ID", "Location", "Location_ID", "Image", "Image_ID", "Video", "Video_ID", "Voice", "Voice_ID", "File", "File_ID", "Attachment", "Iteration Result"]
    
    #Main Table
    mainTable = go.Table(
        header=dict(values=configured_header_names,
                    align='left'),
        cells=dict(values=[dataFrame.Iteration,
            dataFrame.ptx_iteration_start_time,
            dataFrame.ptx_iteration_end_time,
            dataFrame.ptx_send_time_start, 
            dataFrame.ptx_send_time_end,
            dataFrame.Time_milliseconds, 
            dataFrame.ptx_text,
            dataFrame.ptx_text_id,
            dataFrame.ptx_ipa,
            dataFrame.ptx_ipa_id,
            dataFrame.ptx_location,
            dataFrame.ptx_location_id,
            dataFrame.ptx_image,
            dataFrame.ptx_image_id,
            dataFrame.ptx_video,
            dataFrame.ptx_video_id,
            dataFrame.ptx_voice,
            dataFrame.ptx_voice_id,
            dataFrame.ptx_file,
            dataFrame.ptx_file_id,
            dataFrame.ptx_attachment,
            dataFrame.Result],
                   align="left"),
        columnwidth=[100,250,250,250,250,100,100,250,100,250,100,250,100,250,100,250,100,250,100,250,100,100]  
    )

    #Total table
    totalTable = go.Table(
        header=dict(values=["total_iterations", "total_iteration_pass", "total_iteration_failures", "total_ptx_messages_pass", "total_ptx_messages_fail", "total_ptx_messages","ptx_text_time_average"],
                    align="left"),
        cells=dict(values=[[total_iterations], [total_iteration_pass], [total_iteration_failures], [total_ptx_pass], [total_ptx_failures], [total_ptx_messages], [time_milliseconds_average]],
                   align="left")
    )

    #Dividing the data between Pass and Failures
    barGraph_data_pass = [dict(Test="TestPTT", Time_milliseconds="0.5", Iteration="1", Result="PASS")]
    barGraph_data_pass.remove(dict(Test="TestPTT", Time_milliseconds="0.5", Iteration="1", Result="PASS"))
    
    barGraph_data_fail = [dict(Test="TestPTT", Time_milliseconds="0.5", Iteration="1", Result="PASS")]
    barGraph_data_fail.remove(dict(Test="TestPTT", Time_milliseconds="0.5", Iteration="1", Result="PASS"))

    for item in data:
        if item.get("Result") == "PASS":
            barGraph_data_pass.append(item)
        else:
            barGraph_data_fail.append(item)
    

    #NOTE: trying to access empty entries on dataframes will cause error to be thrown.
    
    #pass graph
    if total_iteration_pass != 0:
        dataFrame_bar_pass = pandas.DataFrame(barGraph_data_pass)
        data_bar_pass_range = len(dataFrame_bar_pass["Iteration"])
        
        customdata_bar_pass = np.stack((dataFrame_bar_pass["Time_milliseconds"],
                            dataFrame_bar_pass["Iteration"],
                            dataFrame_bar_pass["ptx_iteration_start_time"],
                            dataFrame_bar_pass["ptx_iteration_end_time"],
                            dataFrame_bar_pass["ptx_send_time_start"],
                            dataFrame_bar_pass["ptx_send_time_end"],
                            dataFrame_bar_pass["ptx_text"],
                            dataFrame_bar_pass["ptx_ipa"],
                            dataFrame_bar_pass["ptx_location"],
                            dataFrame_bar_pass["ptx_image"],
                            dataFrame_bar_pass["ptx_video"],
                            dataFrame_bar_pass["ptx_voice"],
                            dataFrame_bar_pass["ptx_file"],
                            dataFrame_bar_pass["ptx_attachment"],
                            dataFrame_bar_pass["Result"]),
                            axis=1)
        
        barGraph_pass = go.Bar(
                x=dataFrame_bar_pass["Iteration"][0:data_bar_pass_range],
                y=dataFrame_bar_pass["Time_milliseconds"][0:data_bar_pass_range],
                customdata=customdata_bar_pass,
                hovertemplate="<b>%{customdata[1]}</b><br><br>" +
                "ptx_iteration_start_time=%{customdata[2]:NORMAL_TIMESTAMP_FORMAT}<br>" +
                "ptx_iteration_end_time=%{customdata[3]:NORMAL_TIMESTAMP_FORMAT}<br>" +
                "ptx_send_time_start=%{customdata[4]:NORMAL_TIMESTAMP_FORMAT}<br>" +
                "ptx_send_time_end=%{customdata[5]:NORMAL_TIMESTAMP_FORMAT}<br>" +
                "ptx_send_time_duration=%{customdata[0]}<br>" +
                "ptx_text=%{customdata[6]}<br>" +
                "ptx_ipa=%{customdata[7]}<br>" +
                "ptx_location=%{customdata[8]}<br>" +
                "ptx_image=%{customdata[9]}<br>" +
                "ptx_video=%{customdata[10]}<br>" +
                "ptx_voice=%{customdata[11]}<br>" +
                "ptx_file=%{customdata[12]}<br>" +
                "ptx_attachment=%{customdata[13]}<br>" +
                "Result=%{customdata[14]}<br>",
                marker=dict(color="green"),
                name="PASS",
                showlegend=True)
        figure.add_trace(barGraph_pass, row=1, col=1)
    
    if total_iteration_failures != 0:
        dataFrame_bar_fail = pandas.DataFrame(barGraph_data_fail) 

        data_bar_fail_range = len(dataFrame_bar_fail["Iteration"])

        customdata_bar_fail = np.stack((dataFrame_bar_fail["Time_milliseconds"],
                            dataFrame_bar_fail["Iteration"],
                            dataFrame_bar_fail["ptx_iteration_start_time"],
                            dataFrame_bar_fail["ptx_iteration_end_time"],
                            dataFrame_bar_fail["ptx_send_time_start"],
                            dataFrame_bar_fail["ptx_send_time_end"],
                            dataFrame_bar_fail["ptx_text"],
                            dataFrame_bar_fail["ptx_ipa"],
                            dataFrame_bar_fail["ptx_location"],
                            dataFrame_bar_fail["ptx_image"],
                            dataFrame_bar_fail["ptx_video"],
                            dataFrame_bar_fail["ptx_voice"],
                            dataFrame_bar_fail["ptx_file"],
                            dataFrame_bar_fail["ptx_attachment"],
                            dataFrame_bar_fail["Result"]),
                                axis=1)

        barGraph_fail = go.Bar(
                x=dataFrame_bar_fail["Iteration"][0:data_bar_fail_range],
                y=dataFrame_bar_fail["Time_milliseconds"][0:data_bar_fail_range],
                customdata=customdata_bar_fail,
                hovertemplate="<b>%{customdata[1]}</b><br><br>" +
                "ptx_iteration_start_time=%{customdata[2]:NORMAL_TIMESTAMP_FORMAT}<br>" +
                "ptx_iteration_end_time=%{customdata[3]:NORMAL_TIMESTAMP_FORMAT}<br>" +
                "ptx_send_time_start=%{customdata[4]:NORMAL_TIMESTAMP_FORMAT}<br>" +
                "ptx_send_time_end=%{customdata[5]:NORMAL_TIMESTAMP_FORMAT}<br>" +
                "ptx_send_time_duration=%{customdata[0]}<br>" +
                "ptx_text=%{customdata[6]}<br>" +
                "ptx_ipa=%{customdata[7]}<br>" +
                "ptx_location=%{customdata[8]}<br>" +
                "ptx_image=%{customdata[9]}<br>" +
                "ptx_video=%{customdata[10]}<br>" +
                "ptx_voice=%{customdata[11]}<br>" +
                "ptx_file=%{customdata[12]}<br>" +
                "ptx_attachment=%{customdata[13]}<br>" +
                "Result=%{customdata[14]}<br>",
                marker=dict(color="red"),
                name="FAIL",
                showlegend=True)
        figure.add_trace(barGraph_fail, row=1, col=1)
        
    

    
    figure.add_trace(mainTable, row=2, col=1)
    figure.add_trace(totalTable, row=3, col=1)

    figure.update_layout(
        title_text="PTX Messages Summary",
        height=1000,
    )
    figure.update_xaxes(title=dict(text="PTX Iterations"))
    figure.update_yaxes(title=dict(text="Time for PTX Text from sent to delivered (milliseconds)"))
    
    if folder_name == "":
        figure.write_html(OUTPUT_PATH + f"\\{PTX_LOGCAT_NAME}_Graph.html") #Note that a graph object in plotly is Any type.
        print(f"path: {OUTPUT_PATH}\\{PTX_LOGCAT_NAME}_Graph.html")
    else:
        figure.write_html(OUTPUT_PARAM_FOLDER_NAME_PATH + f"\\{OUTPUT_FOLDER_NAME}" + f"\\{folder_name}" + f"\\{PTX_LOGCAT_NAME}_Graph.html")
        print(f"path: {OUTPUT_PARAM_FOLDER_NAME_PATH}\\{OUTPUT_FOLDER_NAME}\\{folder_name}\\{PTX_LOGCAT_NAME}_Graph.html")

    print("single PTX Test Bar Chart created...")
    return figure

def singleTestVideoBarChart(data: list[dict[str, str]], folder_name = ""):
    '''
    Graph type for this is Bars.
    x: Iterations
    y: Time_milliseconds
    color: based on result, green PASS, red FAIL

    Parameters:\n
    data: list[dict[str, str]]. The data from Databreakdown.

    Output:\n
    Figure: plotly library Figure, but this is for Dash server live logging implementation.

    NOTE: for organization of the flow of PTT calls These are the equivalencies of the field names in here 
    and the field names displayed in GraphData.
    call_establish_start = MakeVideoCall
    call_establish_end = Ringing State
    Time_milliseconds = KPI_2 to Ringing Video Call Time

    '''
    if folder_name == "":
        create_directory_graph_output()
    else:
        create_directory_graph_output()
        create_directory_graph_output_folder_name(folder_name)

    dataFrame = pandas.DataFrame(data)

    #Pass and Failure Data processing
    try:
        total_pass = dataFrame["Result"].value_counts().PASS
    except:
        logging.info("no pass cases")
        total_pass = 0
    
    try:
        total_failures = dataFrame["Result"].value_counts().FAIL
    except:
        logging.info("no fail cases")
        total_failures = 0
    
    total_iterations = dataFrame["Iteration"].count()

    time_milliseconds_average = data_average_calculator(dataFrame)

    #Main Figure
    figure = SP.make_subplots(
        rows=3,
        cols=1,
        shared_xaxes=True,
        shared_yaxes=True,
        vertical_spacing=0.10,
        row_heights=[0.5,0.2,0.1],
        specs=[[{"type": "bar"}],
               [{"type": "table"}],
               [{"type": "table"}]
               ],
        subplot_titles=("Video Calls Time for KPI 2 to Ringing State", "Video Calls Table", "Video Calls Summary")
    )

    configured_header_names = ["KPI 2 (ms)", "Call Start Time", "MakeVideoCall Time", "Call End Time", "Ringing State Time", "Iteration", "Result"]

    #Main Table
    mainTable = go.Table(
        header=dict(values=configured_header_names,
                    align='left'),
        cells=dict(values=[dataFrame.Time_milliseconds, dataFrame.call_start_time, dataFrame.call_establish_start_time, dataFrame.call_end_time, dataFrame.call_establish_end_time, dataFrame.Iteration, dataFrame.Result],
                   align="left"),
        columnorder=[5,1,3,2,4,0,6],
        columnwidth=[150,300,200,300,200,100,100]    
    )

    #Total table
    totalTable = go.Table(
        header=dict(values=["total_iterations", "total_pass", "total_failures", "time_average"],
                    align="left"),
        cells=dict(values=[[total_iterations], [total_pass], [total_failures], [time_milliseconds_average]],
                   align="left")
    )

    #Dividing the data between Pass and Failures

    barGraph_data_pass = [dict(Test="TestPTT", Time_milliseconds="0.5", Iteration="1", Result="PASS")]
    barGraph_data_pass.remove(dict(Test="TestPTT", Time_milliseconds="0.5", Iteration="1", Result="PASS"))
    
    barGraph_data_fail = [dict(Test="TestPTT", Time_milliseconds="0.5", Iteration="1", Result="PASS")]
    barGraph_data_fail.remove(dict(Test="TestPTT", Time_milliseconds="0.5", Iteration="1", Result="PASS"))

    for item in data:
        if item.get("Result") == "PASS":
            barGraph_data_pass.append(item)
        else:
            barGraph_data_fail.append(item)
    

    #NOTE: trying to access empty entries on dataframes will cause error to be thrown.
    
    #Pass Bar Graph, if any
    if total_pass != 0:
        dataFrame_bar_pass = pandas.DataFrame(barGraph_data_pass)
        data_bar_pass_range = len(dataFrame_bar_pass["Iteration"])
        
        customdata_bar_pass = np.stack((dataFrame_bar_pass["Iteration"],
                            dataFrame_bar_pass["Time_milliseconds"],
                            dataFrame_bar_pass["call_establish_start_time"],
                            dataFrame_bar_pass["call_establish_end_time"],
                            dataFrame_bar_pass["call_start_time"],
                            dataFrame_bar_pass["call_end_time"],
                            dataFrame_bar_pass["Result"]),
                            axis=1)
        
        barGraph_pass = go.Bar(
                x=dataFrame_bar_pass["Iteration"][0:data_bar_pass_range],
                y=dataFrame_bar_pass["Time_milliseconds"][0:data_bar_pass_range],
                customdata=customdata_bar_pass,
                hovertemplate="<b>%{customdata[0]}</b><br><br>" +
                "KPI 2 (ms)=%{customdata[1]}<br>" +
                "MakeVideoCall Time=%{customdata[2]}<br>" +
                "Ringing State Time=%{customdata[3]}<br>" +
                "Call Start Time=%{customdata[4]:NORMAL_TIMESTAMP_FORMAT}<br>" +
                "Call End Time=%{customdata[5]:NORMAL_TIMESTAMP_FORMAT}<br>" +
                "Result=%{customdata[6]}<br>",
                marker=dict(color="green"),
                name="PASS",
                showlegend=True)
        figure.add_trace(barGraph_pass, row=1, col=1)
        
    
    #Fail Bar Graph, if any
    if total_failures != 0:
        dataFrame_bar_fail = pandas.DataFrame(barGraph_data_fail) 

        data_bar_fail_range = len(dataFrame_bar_fail["Iteration"])

        customdata_bar_fail = np.stack((dataFrame_bar_fail["Iteration"],
                                dataFrame_bar_fail["Time_milliseconds"],
                                dataFrame_bar_fail["call_establish_start_time"],
                                dataFrame_bar_fail["call_establish_end_time"],
                                dataFrame_bar_fail["call_start_time"],
                                dataFrame_bar_fail["call_end_time"],
                                dataFrame_bar_fail["Result"]),
                                axis=1)

        barGraph_fail = go.Bar(
                x=dataFrame_bar_fail["Iteration"][0:data_bar_fail_range],
                y=dataFrame_bar_fail["Time_milliseconds"][0:data_bar_fail_range],
                customdata=customdata_bar_fail,
                hovertemplate="<b>%{customdata[0]}</b><br><br>" +
                "KPI 2 (ms)=N/A<br>" +
                "MakeVideoCall Time=%{customdata[2]}<br>" +
                "Ringing State Time=%{customdata[3]}<br>" +
                "Call Start Time=%{customdata[4]:NORMAL_TIMESTAMP_FORMAT}<br>" +
                "Call End Time=%{customdata[5]:NORMAL_TIMESTAMP_FORMAT}<br>" +
                "Result=%{customdata[6]}<br>",
                marker=dict(color="red"),
                name="FAIL",
                showlegend=True)
        figure.add_trace(barGraph_fail, row=1, col=1)
        
    figure.add_trace(mainTable, row=2, col=1)
    figure.add_trace(totalTable, row=3, col=1)

    figure.update_layout(
        title_text="Video Call Summary",
        height=1000,
    )
    figure.update_xaxes(title=dict(text="Video Call Numbers"))
    figure.update_yaxes(title=dict(text="Time for video call to establish (milliseconds)"))
    
    if folder_name == "":
        figure.write_html(OUTPUT_PATH + f"\\{KPI2_NAME}_Graph.html") #Note that a graph object in plotly is Any type.
        print(f"path: {OUTPUT_PATH}\\{KPI2_NAME}_Graph.html")
    else:
        figure.write_html(OUTPUT_PARAM_FOLDER_NAME_PATH + f"\\{OUTPUT_FOLDER_NAME}" + f"\\{folder_name}" + f"\\{KPI2_NAME}_Graph.html")
        print(f"path: {OUTPUT_PARAM_FOLDER_NAME_PATH}\\{OUTPUT_FOLDER_NAME}\\{folder_name}\\{KPI2_NAME}_Graph.html")

    print("single Video Test Bar Chart created...")
    return figure


#implementation needs changing
def batchTestExecution(testNames: list[str], data: list[dict[str, str]]):
    '''
    Handles batch test runs. uses list of testName and determines which kind of test gets an output.
    Method will be called by DataBreakdown.

    testNames: list[str], list containing the names of the test cases ran by the automation.
    data: list[dict[str, str]], list of dictionary needed for graph output.
    '''
    for test in testNames:
        if test == PTT_CALL_LOGCAT_NAME:
            singleTestPTTBarChart(data)
        if test == PTX_LOGCAT_NAME:
            singleTestPTTBarChart(data) 

    logging.debug("Batch Test Execution")

#Needs updating. Hard update, implementation needs changing
def batchTestGanttChart(testNames: list[str], data: list[dict[str, str]]):
    '''
    Graph type for this is Gantt chart.
    The list should have a dictionary and this dictionary should have the following items:
    Test: str, Start: str, End: str, Result: str
    '''
    dataFrame = pandas.DataFrame(data)
    figure = PX.timeline(dataFrame, x_start="Start", x_end="End", y="Test", color="Result", title="Batch Test Timeline")
    figure.update_yaxes(autorange="reversed")
    figure.write_html(OUTPUT_BATCH_GANTT_CHART)
    print("Batch Test Gantt Chart created...")
    print(f"path: {OUTPUT_BATCH_GANTT_CHART}")
 
if __name__ == '__main__':
    logging.info("This file should not run on main. Unless for testing purposes.")
    
    




