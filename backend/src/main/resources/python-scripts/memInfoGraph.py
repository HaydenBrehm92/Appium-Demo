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
from pathlib import Path

import plotly.express as PX #Plotly library. 
import plotly.graph_objects as go
import plotly.subplots as SP
import pandas
import numpy as np

logging.basicConfig(level=logging.INFO) #For production configure to INFO. For development configure the level to DEBUG. 

package = ""
parentPath = os.path.abspath(os.path.join(os.getcwd()))
test_tempPath = os.path.dirname(os.path.realpath(__file__)) #fullpath...\python scripts  
test_basePath = test_tempPath.replace("\\memInfo", "")
basePath = parentPath  #test_basePath for development. parentPath for product release.
print(basePath)

OUTPUT_FOLDER_NAME = "graph-output"
OUTPUT_PATH = basePath + "\\graph-output"
OUTPUT_MEMINFO = OUTPUT_PATH + "\\MemInfo.html"

OUTPUT_PARAM_FOLDER_NAME_PATH = basePath


memInfoList: list[str] = []
MEMINFO_DETAILS_NAME = "MemInfo_Details"
APP_SUMMARY_NAME = "MemInfo_App_Summary"
CSV_FILE_NAME_ALL = "memInfo_output"
CSV_APP_SUMMARY = "formatted_AppSummary"
CSV_MEMINFO_DETAILS = "formatted_MemInfo"


MEMINFO_APP_STRINGS =  ['MEMINFO', 'App Summary', 'TOTAL PSS']
PACKAGE_NAMES = ['com.att.eptt', 'com.motorolasolutions.waveoncloudptt', 'com.sprint.sdcplus', 'com.bell.ptt', 'com.verizon.pushtotalkplus']



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

def memInfo_details_bar_graph_figure(data: pandas.DataFrame):
    ''' Creates a Figure for Bar Graph.
    This is for memInfo details bar graph.

    returns:
    memInfo_details_bar: go.Figure, Graph Object for Figure type, it is a Bar graph. 
    This may return or be interpreted as Any type.
    '''

    memInfo_details_bar_figure = go.Figure()

    #Below is list of entries in the data. pandas.DataFrame
    '''
        dataFrame.row_title, 
        dataFrame['Pss Total'], 
        dataFrame['Private Dirty'], 
        dataFrame['Private Clean'], 
        dataFrame['SwapPss Dirty'], 
        dataFrame['Rss Total'], 
        dataFrame['Heap Size'], 
        dataFrame['Heap Alloc'], 
        dataFrame['Heap Free'], 
        dataFrame.Date
    '''
    correct_swap_dirty_name = ""
    try:
        temp_val = data["SwapPss Dirty"]
        name = "SwapPss Dirty"
        correct_swap_dirty_name = name
    except:
        name = "Swap Dirty"
        correct_swap_dirty_name = name

    #Filter only TOTAL row_titles
    data_total = data[data['row_title'] == "TOTAL"]
    logging.debug(data_total.head())
    customdata_template = np.stack((data_total["row_title"],
                            data_total["Pss Total"],
                            data_total["Private Dirty"],
                            data_total["Private Clean"],
                            data_total[correct_swap_dirty_name],
                            data_total["Rss Total"],
                            data_total["Heap Size"],
                            data_total["Heap Alloc"],
                            data_total['Heap Free'],
                            data_total['Date']),
                            axis=1)

    #dataFrame['Pss Total'], 1
    title_name = "Pss Total"
    for index, row in data_total.iterrows():
        pss_total_bar = go.Bar(
                x=[title_name],
                y=[row[title_name]],
                hovertemplate=f"<b>{title_name}</b><br><br>" +
                f"{title_name}={row[title_name]}<br>" +
                f"Date={row['Date']}<br>", 
                name=title_name,
                legendgroup=title_name,
                marker=dict(color="red"),
                showlegend=True)
        memInfo_details_bar_figure.add_trace(pss_total_bar)
    
    #dataFrame['Private Dirty'], 2
    title_name = "Private Dirty"
    for index, row in data_total.iterrows():
        private_dirty_bar = go.Bar(
                x=[title_name],
                y=[row[title_name]],
                hovertemplate=f"<b>{title_name}</b><br><br>" +
                f"{title_name}={row[title_name]}<br>" +
                f"Date={row['Date']}<br>", 
                name=title_name,
                legendgroup=title_name,
                marker=dict(color="blue"),
                showlegend=True)
        memInfo_details_bar_figure.add_trace(private_dirty_bar)
    
    #dataFrame['Private Clean'], 3
    title_name = "Private Clean"
    for index, row in data_total.iterrows():
        private_clean_bar = go.Bar(
                x=[title_name],
                y=[row[title_name]],
                hovertemplate=f"<b>{title_name}</b><br><br>" +
                f"{title_name}={row[title_name]}<br>" +
                f"Date={row['Date']}<br>", 
                name=title_name,
                legendgroup=title_name,
                marker=dict(color="yellow"),
                showlegend=True)
        memInfo_details_bar_figure.add_trace(private_clean_bar)
    
    #dataFrame['SwapPss Dirty'] or dataFrame['Swap Dirty'], 4
    title_name = correct_swap_dirty_name
    
    for index, row in data_total.iterrows():
        swappss_dirty_bar = go.Bar(
                x=[title_name],
                y=[row[title_name]],
                hovertemplate=f"<b>{title_name}</b><br><br>" +
                f"{title_name}={row[title_name]}<br>" +
                f"Date={row['Date']}<br>", 
                name=title_name,
                legendgroup=title_name,
                marker=dict(color="orange"),
                showlegend=True)
        memInfo_details_bar_figure.add_trace(swappss_dirty_bar)
    
    #dataFrame['Rss Total'], 5
    title_name = "Rss Total"
    for index, row in data_total.iterrows():
        rss_total_bar = go.Bar(
                x=[title_name],
                y=[row[title_name]],
                hovertemplate=f"<b>{title_name}</b><br><br>" +
                f"{title_name}={row[title_name]}<br>" +
                f"Date={row['Date']}<br>", 
                name=title_name,
                legendgroup=title_name,
                marker=dict(color="purple"),
                showlegend=True)
        memInfo_details_bar_figure.add_trace(rss_total_bar)
    
    #dataFrame['Heap Size'], 6
    title_name = "Heap Size"
    for index, row in data_total.iterrows():
        heap_size_bar = go.Bar(
                x=[title_name],
                y=[row[title_name]],
                hovertemplate=f"<b>{title_name}</b><br><br>" +
                f"{title_name}={row[title_name]}<br>" +
                f"Date={row['Date']}<br>", 
                name=title_name,
                legendgroup=title_name,
                marker=dict(color="green"),
                showlegend=True,
                )
        memInfo_details_bar_figure.add_trace(heap_size_bar)
    
    #dataFrame['Heap Alloc'], 7
    title_name = "Heap Alloc"
    for index, row in data_total.iterrows():
        heap_alloc_bar = go.Bar(
                x=[title_name],
                y=[row[title_name]],
                hovertemplate=f"<b>{title_name}</b><br><br>" +
                f"{title_name}={row[title_name]}<br>" +
                f"Date={row['Date']}<br>", 
                name=title_name,
                legendgroup=title_name,
                marker=dict(color="brown"),
                showlegend=True,
                )
        memInfo_details_bar_figure.add_trace(heap_alloc_bar)
    
    #dataFrame['Heap Free'], 8 
    title_name = "Heap Free"
    for index, row in data_total.iterrows():
        heap_free_bar = go.Bar(
                x=[title_name],
                y=[row[title_name]],
                hovertemplate=f"<b>{title_name}</b><br><br>" +
                f"{title_name}={row[title_name]}<br>" +
                f"Date={row['Date']}<br>", 
                name=title_name,
                legendgroup=title_name,
                marker=dict(color="indigo"),
                showlegend=True,
                )
        memInfo_details_bar_figure.add_trace(heap_free_bar)
    
    #Eliminates repeated name legends
    legend_names = list()
    memInfo_details_bar_figure.for_each_trace(
        lambda trace:
            trace.update(showlegend=False)
            if (trace.name in legend_names) 
            else legend_names.append(trace.name)
    )

    return memInfo_details_bar_figure

def memInfo_details_input_load_bar_graph_figure(data: pandas.DataFrame):
    ''' Creates a Figure for Bar Graph.
    This is for memInfo details bar graph.

    Difference from regular version is Date changed to Source

    returns:
    memInfo_details_bar: go.Figure, Graph Object for Figure type, it is a Bar graph. 
    This may return or be interpreted as Any type.
    '''

    memInfo_details_bar_figure = go.Figure()

    #Below is list of entries in the data. pandas.DataFrame
    '''
        dataFrame.row_title, 
        dataFrame['Pss Total'], 
        dataFrame['Private Dirty'], 
        dataFrame['Private Clean'], 
        dataFrame['SwapPss Dirty'], 
        dataFrame['Rss Total'], 
        dataFrame['Heap Size'], 
        dataFrame['Heap Alloc'], 
        dataFrame['Heap Free'], 
        dataFrame.Source
    '''
    try:
        temp_val = data["SwapPss Dirty"]
        name = "SwapPss Dirty"
        correct_swap_dirty_name = name
    except:
        name = "Swap Dirty"
        correct_swap_dirty_name = name

    #Filter only TOTAL row_titles
    data_total = data[data['row_title'] == "TOTAL"]
    logging.debug(data_total.head())
    customdata_template = np.stack((data_total["row_title"],
                            data_total["Pss Total"],
                            data_total["Private Dirty"],
                            data_total["Private Clean"],
                            data_total[correct_swap_dirty_name],
                            data_total["Rss Total"],
                            data_total["Heap Size"],
                            data_total["Heap Alloc"],
                            data_total['Heap Free'],
                            data_total['Source']),
                            axis=1)

    #dataFrame['Pss Total'], 1
    title_name = "Pss Total"
    for index, row in data_total.iterrows():
        pss_total_bar = go.Bar(
                x=[title_name],
                y=[row[title_name]],
                hovertemplate=f"<b>{title_name}</b><br><br>" +
                f"{title_name}={row[title_name]}<br>" +
                f"Source={row['Source']}<br>", 
                name=title_name,
                legendgroup=title_name,
                marker=dict(color="red"),
                showlegend=True)
        memInfo_details_bar_figure.add_trace(pss_total_bar)
    
    #dataFrame['Private Dirty'], 2
    title_name = "Private Dirty"
    for index, row in data_total.iterrows():
        private_dirty_bar = go.Bar(
                x=[title_name],
                y=[row[title_name]],
                hovertemplate=f"<b>{title_name}</b><br><br>" +
                f"{title_name}={row[title_name]}<br>" +
                f"Source={row['Source']}<br>", 
                name=title_name,
                legendgroup=title_name,
                marker=dict(color="blue"),
                showlegend=True)
        memInfo_details_bar_figure.add_trace(private_dirty_bar)
    
    #dataFrame['Private Clean'], 3
    title_name = "Private Clean"
    for index, row in data_total.iterrows():
        private_clean_bar = go.Bar(
                x=[title_name],
                y=[row[title_name]],
                hovertemplate=f"<b>{title_name}</b><br><br>" +
                f"{title_name}={row[title_name]}<br>" +
                f"Source={row['Source']}<br>", 
                name=title_name,
                legendgroup=title_name,
                marker=dict(color="yellow"),
                showlegend=True)
        memInfo_details_bar_figure.add_trace(private_clean_bar)
    
    #dataFrame['SwapPss Dirty'], 4
    title_name = "SwapPss Dirty"
    title_name = correct_swap_dirty_name
    for index, row in data_total.iterrows():
        swappss_dirty_bar = go.Bar(
                x=[title_name],
                y=[row[title_name]],
                hovertemplate=f"<b>{title_name}</b><br><br>" +
                f"{title_name}={row[title_name]}<br>" +
                f"Source={row['Source']}<br>", 
                name=title_name,
                legendgroup=title_name,
                marker=dict(color="orange"),
                showlegend=True)
        memInfo_details_bar_figure.add_trace(swappss_dirty_bar)
    
    #dataFrame['Rss Total'], 5
    title_name = "Rss Total"
    for index, row in data_total.iterrows():
        rss_total_bar = go.Bar(
                x=[title_name],
                y=[row[title_name]],
                hovertemplate=f"<b>{title_name}</b><br><br>" +
                f"{title_name}={row[title_name]}<br>" +
                f"Source={row['Source']}<br>", 
                name=title_name,
                legendgroup=title_name,
                marker=dict(color="purple"),
                showlegend=True)
        memInfo_details_bar_figure.add_trace(rss_total_bar)
    
    #dataFrame['Heap Size'], 6
    title_name = "Heap Size"
    for index, row in data_total.iterrows():
        heap_size_bar = go.Bar(
                x=[title_name],
                y=[row[title_name]],
                hovertemplate=f"<b>{title_name}</b><br><br>" +
                f"{title_name}={row[title_name]}<br>" +
                f"Source={row['Source']}<br>", 
                name=title_name,
                legendgroup=title_name,
                marker=dict(color="green"),
                showlegend=True,
                )
        memInfo_details_bar_figure.add_trace(heap_size_bar)
    
    #dataFrame['Heap Alloc'], 7
    title_name = "Heap Alloc"
    for index, row in data_total.iterrows():
        heap_alloc_bar = go.Bar(
                x=[title_name],
                y=[row[title_name]],
                hovertemplate=f"<b>{title_name}</b><br><br>" +
                f"{title_name}={row[title_name]}<br>" +
                f"Source={row['Source']}<br>", 
                name=title_name,
                legendgroup=title_name,
                marker=dict(color="brown"),
                showlegend=True,
                )
        memInfo_details_bar_figure.add_trace(heap_alloc_bar)
    
    #dataFrame['Heap Free'], 8 
    title_name = "Heap Free"
    for index, row in data_total.iterrows():
        heap_free_bar = go.Bar(
                x=[title_name],
                y=[row[title_name]],
                hovertemplate=f"<b>{title_name}</b><br><br>" +
                f"{title_name}={row[title_name]}<br>" +
                f"Source={row['Source']}<br>", 
                name=title_name,
                legendgroup=title_name,
                marker=dict(color="indigo"),
                showlegend=True,
                )
        memInfo_details_bar_figure.add_trace(heap_free_bar)
    
    #Eliminates repeated name legends
    legend_names = list()
    memInfo_details_bar_figure.for_each_trace(
        lambda trace:
            trace.update(showlegend=False)
            if (trace.name in legend_names) 
            else legend_names.append(trace.name)
    )

    return memInfo_details_bar_figure

def app_summary_bar_graph_figure(data: pandas.DataFrame):
    ''' Creates a Figure for Bar Graph.
    This is for App Summary bar graph.

    returns:
    memInfo_details_bar: go.Figure, Graph Object for Figure type, it is a Bar graph. 
    This may return or be interpreted as Any type.
    '''
    
    app_summary_bar = go.Figure()

    #Below is list of entries in the data. pandas.DataFrame
    '''
        dataFrame.row_title, X value
        dataFrame['Pss(KB)'], Y
        dataFrame['Rss(KB)'], Y
        dataFrame['Swap Pss(KB)'], Y
        dataFrame.Date
    '''
    
    field_names = data["row_title"].to_list()
    pss_data = data["Pss(KB)"].to_list()
    rss_data = data["Rss(KB)"].to_list()
    swap_data = data["Swap Pss(KB)"].to_list()
    date_data = data["Date"].to_list()

    #Filter only TOTAL row_titles
    data_total = data[data['row_title'] == "TOTAL"]
    logging.debug(data_total.head())
    
    custom_data = np.stack((data["row_title"],
                            data["Pss(KB)"],
                            data["Rss(KB)"],
                            data["Swap Pss(KB)"],
                            data["Date"]),
                            axis=1)
    
    pss_bar = go.Bar(
        base="relative",
        x=field_names,
        y=pss_data,
        customdata=custom_data,
        hovertemplate='<b>%{x}</b><br><br>' +
        "%{x}=%{y}<br>" +
        "Date=%{customdata[4]}<br>",
        name="Pss(KB)",
        showlegend=True
    )

    rss_bar = go.Bar(
        base="relative",
        x=field_names,
        y=rss_data,
        customdata=custom_data,
        hovertemplate="<b>%{x}</b><br><br>" +
        "%{x}=%{y}<br>" +
        "Date=%{customdata[4]}<br>",
        name="Rss(KB)",
        showlegend=True
    )

    swap_bar = go.Bar(
        base="relative",
        x=field_names,
        y=swap_data,
        customdata=custom_data,
        hovertemplate="<b>%{x}</b><br><br>" +
        "%{x}=%{y}<br>" +
        "Date=%{customdata[4]}<br>",
        name="Swap Pss(KB)",
        showlegend=True
    )
    
    #dataFrame['Heap Free'], 8 
    '''
    title_name = "Heap Free"
    for index, row in data_total.iterrows():
        heap_free_bar = go.Bar(
                x=[title_name],
                y=[row[title_name]],
                hovertemplate=f"<b>{title_name}</b><br><br>" +
                f"{title_name}={row[title_name]}<br>" +
                f"Date={row['Date']}<br>", 
                name=title_name,
                legendgroup=title_name,
                marker=dict(color="indigo"),
                showlegend=True,
                )
        memInfo_details_bar_figure.add_trace(heap_free_bar)
    '''

    app_summary_bar.add_traces([pss_bar, rss_bar, swap_bar])
    
    #Eliminates repeated name legends
    """
    legend_names = list()
    app_summary_bar.for_each_trace(
        lambda trace:
            trace.update(showlegend=False)
            if (trace.name in legend_names) 
            else legend_names.append(trace.name)
    )
    """

    return app_summary_bar

def app_summary_input_load_bar_graph_figure(data: pandas.DataFrame):
    ''' Creates a Figure for Bar Graph.
    This is for App Summary bar graph.

    Difference from regular version is Date changed to Source
    
    returns:
    memInfo_details_bar: go.Figure, Graph Object for Figure type, it is a Bar graph. 
    This may return or be interpreted as Any type.
    '''
    
    app_summary_bar = go.Figure()

    #Below is list of entries in the data. pandas.DataFrame
    '''
        dataFrame.row_title, X value
        dataFrame['Pss(KB)'], Y
        dataFrame['Rss(KB)'], Y
        dataFrame['Swap Pss(KB)'], Y
        dataFrame.Source
    '''
    
    field_names = data["row_title"].to_list()
    pss_data = data["Pss(KB)"].to_list()
    rss_data = data["Rss(KB)"].to_list()
    swap_data = data["Swap Pss(KB)"].to_list()
    source_data = data["Source"].to_list()

    #Filter only TOTAL row_titles
    data_total = data[data['row_title'] == "TOTAL"]
    logging.debug(data_total.head())
    
    custom_data = np.stack((data["row_title"],
                            data["Pss(KB)"],
                            data["Rss(KB)"],
                            data["Swap Pss(KB)"],
                            data["Source"]),
                            axis=1)
    
    pss_bar = go.Bar(
        base="relative",
        x=field_names,
        y=pss_data,
        customdata=custom_data,
        hovertemplate='<b>%{x}</b><br><br>' +
        "%{x}=%{y}<br>" +
        "Source=%{customdata[4]}<br>",
        name="Pss(KB)",
        showlegend=True
    )

    rss_bar = go.Bar(
        base="relative",
        x=field_names,
        y=rss_data,
        customdata=custom_data,
        hovertemplate="<b>%{x}</b><br><br>" +
        "%{x}=%{y}<br>" +
        "Source=%{customdata[4]}<br>",
        name="Rss(KB)",
        showlegend=True
    )

    swap_bar = go.Bar(
        base="relative",
        x=field_names,
        y=swap_data,
        customdata=custom_data,
        hovertemplate="<b>%{x}</b><br><br>" +
        "%{x}=%{y}<br>" +
        "Source=%{customdata[4]}<br>",
        name="Swap Pss(KB)",
        showlegend=True
    )
    
    #dataFrame['Heap Free'], 8 
    '''
    title_name = "Heap Free"
    for index, row in data_total.iterrows():
        heap_free_bar = go.Bar(
                x=[title_name],
                y=[row[title_name]],
                hovertemplate=f"<b>{title_name}</b><br><br>" +
                f"{title_name}={row[title_name]}<br>" +
                f"Source={row['Source']}<br>", 
                name=title_name,
                legendgroup=title_name,
                marker=dict(color="indigo"),
                showlegend=True,
                )
        memInfo_details_bar_figure.add_trace(heap_free_bar)
    '''

    app_summary_bar.add_traces([pss_bar, rss_bar, swap_bar])
    
    #Eliminates repeated name legends
    """
    legend_names = list()
    app_summary_bar.for_each_trace(
        lambda trace:
            trace.update(showlegend=False)
            if (trace.name in legend_names) 
            else legend_names.append(trace.name)
    )
    """

    return app_summary_bar

def memInfo_details_output_bar_chart(dataPath: str, folder_name = ""):
    ''' using Plotly library. Outputs the MemInfo details as a bar graph.

        parameters:
        dataPath: str, full path of the csv file. 
    '''
    
    create_directory_graph_output()
    dataFrame = pandas.read_csv(dataPath)
    figure = go.Figure()

    correct_swap_dirty_name = ""
    try:
        temp_val = dataFrame["SwapPss Dirty"]
        name = "SwapPss Dirty"
        correct_swap_dirty_name = name
    except:
        name = "Swap Dirty"
        correct_swap_dirty_name = name

    #Main Figure
    figure = SP.make_subplots(
        rows=2,
        cols=1,
        shared_xaxes=True,
        shared_yaxes=True,
        vertical_spacing=0.10,
        row_heights=[0.5,0.2],
        specs=[[{"type": "bar"}],
               [{"type": "table"}]
               ],
        subplot_titles=("MemInfo Details Graph (KB)", "MemInfo Details Table (KB)")
    )

    
    #Main Table
    mainTable = go.Table(
        header=dict(values=list(dataFrame.columns),
                    align='left'),
        cells=dict(values=[dataFrame.row_title, dataFrame['Pss Total'], dataFrame['Private Dirty'], dataFrame['Private Clean'], dataFrame[correct_swap_dirty_name], dataFrame['Rss Total'], dataFrame['Heap Size'], dataFrame['Heap Alloc'], dataFrame['Heap Free'], dataFrame.Date],
                   align="left"),
        columnwidth=[150,200,200,200,200,200,200,200,200,300]    
    )

    #Graph
    memInfo_details_bar: go.Figure = memInfo_details_bar_graph_figure(dataFrame)

    #figure.add_trace(memInfo_details_bar.data[0], row=1, col=1)
    for item_data in memInfo_details_bar.data:
        figure.add_trace(item_data, row=1, col=1)
    figure.add_trace(mainTable, row=2, col=1)    
    
    if folder_name == "":
        figure.write_html(OUTPUT_PATH + f"\\{MEMINFO_DETAILS_NAME}_Graph.html")
        print(f"path: {OUTPUT_PATH}\\{MEMINFO_DETAILS_NAME}_Graph.html")
    else:
        figure.write_html(OUTPUT_PARAM_FOLDER_NAME_PATH + f"\\{OUTPUT_FOLDER_NAME}" + f"\\{folder_name}" + f"\\{MEMINFO_DETAILS_NAME}_Graph.html")
        print(f"path: {OUTPUT_PARAM_FOLDER_NAME_PATH}\\{OUTPUT_FOLDER_NAME}\\{folder_name}\\{MEMINFO_DETAILS_NAME}_Graph.html")

    print("single MemInfo Details Graph created...")
    print(f"path: {OUTPUT_PATH}\\{MEMINFO_DETAILS_NAME}_Graph.html")

    return figure

def memInfo_details_input_load_output_bar_chart(dataPath: str):
    ''' using Plotly library. Outputs the MemInfo details as a bar graph.

        Difference from regular version is Date changed to Source.

        parameters:
        dataPath: str, full path of the csv file. 
    '''
    create_directory_graph_output()
    dataFrame = pandas.read_csv(dataPath)
    figure = go.Figure()

    #Main Figure
    figure = SP.make_subplots(
        rows=2,
        cols=1,
        shared_xaxes=True,
        shared_yaxes=True,
        vertical_spacing=0.10,
        row_heights=[0.5,0.2],
        specs=[[{"type": "bar"}],
               [{"type": "table"}]
               ],
        subplot_titles=("MemInfo Details Graph (KB)", "MemInfo Details Table (KB)")
    )

    correct_swap_dirty_name = ""
    try:
        temp_val = dataFrame["SwapPss Dirty"]
        name = "SwapPss Dirty"
        correct_swap_dirty_name = name
    except:
        name = "Swap Dirty"
        correct_swap_dirty_name = name

    #Main Table
    mainTable = go.Table(
        header=dict(values=list(dataFrame.columns),
                    align='left'),
        cells=dict(values=[dataFrame.row_title, dataFrame['Pss Total'], dataFrame['Private Dirty'], dataFrame['Private Clean'], dataFrame[correct_swap_dirty_name], dataFrame['Rss Total'], dataFrame['Heap Size'], dataFrame['Heap Alloc'], dataFrame['Heap Free'], dataFrame.Source],
                   align="left"),
        columnwidth=[150,200,200,200,200,200,200,200,200,300]    
    )

    #Graph
    memInfo_details_bar: go.Figure = memInfo_details_input_load_bar_graph_figure(dataFrame)

    #figure.add_trace(memInfo_details_bar.data[0], row=1, col=1)
    for item_data in memInfo_details_bar.data:
        figure.add_trace(item_data, row=1, col=1)
    figure.add_trace(mainTable, row=2, col=1)    
    
    figure.write_html(OUTPUT_PATH + f"\\{MEMINFO_DETAILS_NAME}_Graph.html")

    print("single MemInfo Details Graph created...")
    print(f"path: {OUTPUT_PATH}\\{MEMINFO_DETAILS_NAME}_Graph.html")

    return figure

def app_summary_output_bar_chart(dataPath: str, folder_name = ""):
    ''' using Plotly library. Outputs the MemInfo app summary as a bar graph.

        parameters:
        dataPath: str, full path of the csv file. 
    '''
    create_directory_graph_output()
    dataFrame = pandas.read_csv(dataPath)
    figure = go.Figure()

    #Main Figure
    figure = SP.make_subplots(
        rows=2,
        cols=1,
        shared_xaxes=True,
        shared_yaxes=True,
        vertical_spacing=0.10,
        row_heights=[0.5,0.2],
        specs=[[{"type": "bar"}],
               [{"type": "table"}]
               ],
        subplot_titles=("MemInfo App Summary Graph (KB)", "MemInfo App Summary Table (KB)")
    )

    

    #Main Table
    mainTable = go.Table(
        header=dict(values=list(dataFrame.columns),
                    align='left'),
        cells=dict(values=[dataFrame.row_title, dataFrame['Pss(KB)'], dataFrame['Rss(KB)'], dataFrame['Swap Pss(KB)'], dataFrame.Date],
                   align="left"),
        columnwidth=[150,200,200,200,300]    
    )

    #Graph
    app_summary_bar: go.Figure = app_summary_bar_graph_figure(dataFrame)

    #figure.add_trace(memInfo_details_bar.data[0], row=1, col=1)
    for item_data in app_summary_bar.data:
        figure.add_trace(item_data, row=1, col=1)
    figure.add_trace(mainTable, row=2, col=1) 
    

    if folder_name == "":
        figure.write_html(OUTPUT_PATH + f"\\{APP_SUMMARY_NAME}_Graph.html")
        print(f"path: {OUTPUT_PATH}\\{APP_SUMMARY_NAME}_Graph.html")
    else:
        figure.write_html(OUTPUT_PARAM_FOLDER_NAME_PATH + f"\\{OUTPUT_FOLDER_NAME}" + f"\\{folder_name}" + f"\\{APP_SUMMARY_NAME}_Graph.html")
        print(f"path: {OUTPUT_PATH}\\{OUTPUT_FOLDER_NAME}\\{folder_name}\\{APP_SUMMARY_NAME}_Graph.html")

    print("single PTT Test Bar Chart created...")

    return figure

def app_summary_input_load_output_bar_chart(dataPath: str):
    ''' using Plotly library. Outputs the MemInfo app summary as a bar graph.

        Difference from regular version is Date changed to Source.

        parameters:
        dataPath: str, full path of the csv file. 
    '''
    create_directory_graph_output()
    dataFrame = pandas.read_csv(dataPath)
    figure = go.Figure()

    #Main Figure
    figure = SP.make_subplots(
        rows=2,
        cols=1,
        shared_xaxes=True,
        shared_yaxes=True,
        vertical_spacing=0.10,
        row_heights=[0.5,0.2],
        specs=[[{"type": "bar"}],
               [{"type": "table"}]
               ],
        subplot_titles=("MemInfo App Summary Graph (KB)", "MemInfo App Summary Table (KB)")
    )

    #Main Table
    mainTable = go.Table(
        header=dict(values=list(dataFrame.columns),
                    align='left'),
        cells=dict(values=[dataFrame.row_title, dataFrame['Pss(KB)'], dataFrame['Rss(KB)'], dataFrame['Swap Pss(KB)'], dataFrame.Source],
                   align="left"),
        columnwidth=[150,200,200,200,300]    
    )

    #Graph
    app_summary_bar: go.Figure = app_summary_input_load_bar_graph_figure(dataFrame)

    #figure.add_trace(memInfo_details_bar.data[0], row=1, col=1)
    for item_data in app_summary_bar.data:
        figure.add_trace(item_data, row=1, col=1)
    figure.add_trace(mainTable, row=2, col=1) 
    


    figure.write_html(OUTPUT_PATH + f"\\{APP_SUMMARY_NAME}_Graph.html")

    print("single PTT Test Bar Chart created...")
    print(f"path: {OUTPUT_PATH}\\{APP_SUMMARY_NAME}_Graph.html")

    return figure

if __name__ == '__main__':
    logging.info("This file should not run on main. Unless for testing purposes.")
    
    




