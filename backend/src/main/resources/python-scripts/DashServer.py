## Helper module for 
# Get List of files in the current directory.
# removes __init__.py from list
##
from asyncio.windows_events import NULL
import csv
import time
import datetime
import threading
import os
import logging
import xml.etree.ElementTree as ET #handles XML files
from pathlib import Path

import plotly.express as PX #Plotly library. 
import plotly.graph_objects as go
import plotly.subplots as SP
import pandas
import numpy as np

from dash import Dash, html, dcc, callback, Output, Input

logging.basicConfig(level=logging.INFO) #For production configure to INFO. For development configure the level to DEBUG. 

parentPath = os.path.abspath(os.path.join(os.getcwd()))
test_tempPath = os.path.dirname(os.path.realpath(__file__)) #fullpath...\python scripts
test_basePath = test_tempPath.replace("\\python scripts", "")
basePath = parentPath #test_basePath for development. parentPath for product release.

UPDATE_INTERVAL = 2000
INTERVAL_INCREMENT = 0

TEST_OUTPUT_FILES_PATH = basePath + "\\test-output"
OUTPUT_PATH = TEST_OUTPUT_FILES_PATH + "\\graph-output"

OUTPUT_CALL_PATH = OUTPUT_PATH + "\\ptx_output_summary_adb_example1.txt"
OUTPUT_HTML_DEMO = OUTPUT_PATH + "\\quickPlotlyTest.html"
OUTPUT_BATCH_GANTT_CHART = OUTPUT_PATH + "\\BatchTestGanttChart.html"
OUTPUT_TEST_GANTT_CHART = OUTPUT_PATH + "\\TestGanttChart.html"
OUTPUT_BATCH_SCATTER_CHART = OUTPUT_PATH + "\\BatchTestScatterChart.html"
OUTPUT_TEST_SCATTER_CHART = OUTPUT_PATH + "\\TestScatterChart.html"
OUTPUT_TEST_BAR_CHART = OUTPUT_PATH + "\\TestBarChart.html"

EXAMPLE_KPI_DATA_PATH = basePath + "\\example data\\KPI_Logcat" #quick Example

GENERIC_PATH = basePath + "\\graph-output"

JUNIT_TIMESTAMP_FORMAT = "%Y-%m-%dT%H:%M:%S"
LOGCAT_TIMESTAMP_FORMAT = "%m-%d %H:%M:%S.%f"
NORMAL_TIMESTAMP_FORMAT = "%Y-%m-%d at %H:%M:%S.%f"

def create_server():
    '''
    creates and returns Dash Server object.

    Returns:\n
    app: Dash, the Dash server object.
    '''
    app = Dash(__name__)
    basic_layout = html.Div([
        html.H1(children="Automation Basic Graph on HTML Dash", style={'textAlign':'center'}),
        dcc.Interval(
            id="interval-updates",
            interval=UPDATE_INTERVAL,
            n_intervals=INTERVAL_INCREMENT
        )
    ])
    app.layout = basic_layout
    return app

def start_server(app: Dash):
    '''
    starts the Dash server. default configuration.
    Host = 127.0.0.1
    Port = 8050

    Parameters:\n
    app: Dash, Dash server object.
    '''
    app.run(debug=False)

def stop_server(app: Dash):
    '''
    NOTE: Dash is stateless, so the devs had in mind that it would be pointless to have a stop server implemented,
    the way to properly stop the server would be by user interaction or stopping the thread running the server.

    Most likely this server will be running in a separate thread or separate active terminal from main 
    for the user to control. Therefore, stopping the main terminal will terminate the server a well.
    '''
    #NOT IMPLEMENTED, but will be needed. If implementating, it is needed to override the threading class to include
    #a safe manner to stop the thread. 
    logging.info("stopping server...")

#Needed to update graph
@callback(Output('graph-content', 'figure'),
            Input('interval-updates', 'n-intervals')) #Live refresh does not want to work, well user can always refresh the page to review the graph
def update_figure(app: Dash, figure: go.Figure):
    '''
    Updates app layout.

    Parameters:\n
    app: Dash, the Dash server object.
    figure: go.Figure, figure of the graph to be plot.
    '''
    #NOTE: app.layout will most likely be a html.Div object
    existing_layout = app.layout #Implement a way to copy the html.Div
    
    basic_layout = html.Div([
        dcc.Graph(id='graph-content', figure=figure),
        dcc.Interval(
            id="interval-updates",
            interval=UPDATE_INTERVAL,
            n_intervals=INTERVAL_INCREMENT
        )
    ])
    app.layout = basic_layout


#Delete test method when full implementation of this Server app
def singleTestPTTBarChart(data: list[dict[str, str]]):
    '''
    NOTE: Test Method, This method will never be called directly from this file on release. 
    Delete after completed class implementation.
    Graph type for this is Bars.
    x: Iterations
    y: Time_milliseconds
    color: based on result, green PASS, red FAIL

    Parameters:\n
    data: list[dict[str, str]]. The data from Databreakdown.
    '''

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
        subplot_titles=("PTT Calls Time to establish call", "PTT Calls Table", "PTT Calls Summary")
    )

    #Main Table
    mainTable = go.Table(
        header=dict(values=list(dataFrame.columns),
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
                "Time_milliseconds=%{customdata[1]}<br>" +
                "call_establish_start_time=%{customdata[2]}<br>" +
                "call_establish_end_time=%{customdata[3]}<br>" +
                "call_start_time=%{customdata[4]:NORMAL_TIMESTAMP_FORMAT}<br>" +
                "call_end_time=%{customdata[5]:NORMAL_TIMESTAMP_FORMAT}<br>" +
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
                "Time_milliseconds=N/A<br>" +
                "call_establish_start_time=%{customdata[2]}<br>" +
                "call_establish_end_time=%{customdata[3]}<br>" +
                "call_start_time=%{customdata[4]:NORMAL_TIMESTAMP_FORMAT}<br>" +
                "call_end_time=%{customdata[5]:NORMAL_TIMESTAMP_FORMAT}<br>" +
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
    return figure 

def data_average_calculator(dataFrame: pandas.DataFrame):
    '''
    NOTE: Test Method, This method will never be called directly from this file on release. 
    Delete after completed class implementation. 
    This task will be performed multiple times across different tests.
    It generalizes the data and grabs the average of the milliseconds that are in the
    dataframe.

    Parameters:\n
    dataFrame = pandas.DataFrame object. This will be from any of the chart generators.
    '''
    logging.debug("Entering data_average_calculator")
    result = dataFrame["Time_milliseconds"].mean()
    return result

def test_live_graph():
    '''
    Test method. Generic live graph runner handler
    '''
    app = Dash(__name__)

    sample_list = [dict(Time_milliseconds=225,
                        call_start_time="05-05 12:20:233.345",
                        call_establish_start_time="05-05 12:20:233.345",
                        call_end_time="05-05 12:20:235.345",
                        call_establish_end_time="05-05 12:20:235.345",
                        Iteration=1,
                        Result="PASS"),
                    dict(Time_milliseconds=325,
                        call_start_time="05-05 12:21:233.345",
                        call_establish_start_time="05-05 12:21:233.345",
                        call_end_time="05-05 12:21:235.345",
                        call_establish_end_time="05-05 12:21:235.345",
                        Iteration=2,
                        Result="FAIL"),
                    dict(Time_milliseconds=525,
                        call_start_time="05-05 12:21:233.345",
                        call_establish_start_time="05-05 12:21:233.345",
                        call_end_time="05-05 12:21:235.345",
                        call_establish_end_time="05-05 12:21:235.345",
                        Iteration=3,
                        Result="PASS")
                    ]
    sample_dataframe = pandas.DataFrame(sample_list)
    figure = singleTestPTTBarChart(sample_list)
    ##Retrieve This layout from the GraphData.py layout
    basic_layout = html.Div([
        html.H1(children="Automation Basic Graph on HTML Dash", style={'textAlign':'center'}),
        dcc.Graph(id='graph-content', figure=figure),
    ])

    app.layout = basic_layout

    '''
    @callback(
            Output('graph-content', 'figure'),
            Input('PTTCall', 'data')
    )
    '''

    app.run_server(debug=True)
    #figure.show()

def test_live_graph(figure: go.Figure):
    '''
    Test Method, it will be removed after class implementation is completed.
    Creates a new server displaying the given plotly figure from the parameter provided.
    Server remains active, if any code needs to be run after this call
    Parameters:\n
    figure: go.Figure, figure object with already processed plotly graph object.
    
    '''
    logging.info("Setting up server...")
    app = Dash(__name__)
    basic_layout = html.Div([
        html.H1(children="Automation Basic Graph on HTML Dash", style={'textAlign':'center'}),
        dcc.Graph(id='graph-content', figure=figure),
    ])
    app.layout = basic_layout
    app.run_server(debug=True)
    
def demo_example():
    '''
    Test Demo of server running.
    '''
    logging.info("Note that the server runs on http://127.0.0.1:8050/")
    server = create_server()
    server_thread = threading.Thread(target=start_server, args=(server,))
    server_thread.start()
    sample_list = [dict(Time_milliseconds=225,
                        call_start_time="05-05 12:20:233.345",
                        call_establish_start_time="05-05 12:20:233.345",
                        call_end_time="05-05 12:20:235.345",
                        call_establish_end_time="05-05 12:20:235.345",
                        Iteration=1,
                        Result="PASS"),
                    dict(Time_milliseconds=325,
                        call_start_time="05-05 12:21:233.345",
                        call_establish_start_time="05-05 12:21:233.345",
                        call_end_time="05-05 12:21:235.345",
                        call_establish_end_time="05-05 12:21:235.345",
                        Iteration=2,
                        Result="FAIL"),
                    dict(Time_milliseconds=525,
                        call_start_time="05-05 12:21:233.345",
                        call_establish_start_time="05-05 12:21:233.345",
                        call_end_time="05-05 12:21:235.345",
                        call_establish_end_time="05-05 12:21:235.345",
                        Iteration=3,
                        Result="PASS")
                    ]
    sample_dataframe = pandas.DataFrame(sample_list)
    figure = singleTestPTTBarChart(sample_list)
    print("waiting 10 seconds before updating figure in server...")
    time.sleep(10)
    update_figure(server, figure)
    print("Waiting for 30 seconds to stop server...")
    time.sleep(30)
    print("30 seconds has passed. server will remain active, end the server by closing the active terminal.")

if __name__ == '__main__':
    logging.info("This file should not run on main. Unless for testing purposes.")
    demo_example()
