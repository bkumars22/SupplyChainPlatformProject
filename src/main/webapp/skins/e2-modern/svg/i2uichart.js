/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
/////////////////////////////////////////////////////////////////////////////
// (c) Copyright 2000 - 2003, i2 Technologies, Inc. (formerly Intellection)//
// ALL RIGHTS RESERVED.                                                    //
//                                                                         //
// This UNPUBLISHED PROPRIETARY software is  subject to the full copyright //
// notice in the COPYRIGHT file in this directory.                         //
/////////////////////////////////////////////////////////////////////////////

var MIN_ITEM_WIDTH = 35;
var DEFAULT_NUMY = 6;
var SVGDocument = null;
var origMenu;

var Values           = new Array();
var SumValues        = new Array();
var StackValues      = new Array();
var DatasetToStack   = new Array();
var UniqueValues     = new Array();
var Names            = new Array();
var Tickmarks        = new Array();
var Legends          = new Array();
var LegendNames      = new Array();
var DatasetType      = new Array();
var Elements         = new Array();
var Threshholds      = new Array();
var Menus            = new Array();
var YAxisTickMark    = new Array();
var YAxisTickMarkText= new Array();

var XAxisAngle = 30;
// sin and cos use radians not degrees
var CosAngle = Math.cos(XAxisAngle*2*Math.PI/360);
var SinAngle = Math.sin(XAxisAngle*2*Math.PI/360);
var XAxisMatrix = "matrix("+CosAngle+" "+SinAngle+" -"+SinAngle+" "+CosAngle+" ";

var minItemWidth = 5;
var YAxisWidth = 70;
var MaxIndex = 0;
var MaxSize = 0;
var MinSize = 0;
var YSize   = 0;
var XAxisCount = 0;
var LastItemWidth = 0;
var OrigItemWidth = 0;
var LabelShift = 0;
var NumY = DEFAULT_NUMY;
var YRatio = 1;

var ItemsPerX = 0;
var WidthPerX = 10;
var saveHeight = 0;
var saveWidth  = 0;

var aScrollableObject = new Object();

var embedobj = null;

function i2uiSetGlobalOnclickHandler(func)
{
  // mask out all interactions with data portion of chart
  var obj = SVGDocument.getElementById("chartcontainer");
  if (func != null && obj != null)
  {
    var obj2 = SVGDocument.createElement("rect")
    obj2.setAttribute("width", "100%")
    obj2.setAttribute("height", "100%")
    obj2.setAttribute("x", "0")
    obj2.setAttribute("y", "0")
    obj2.getStyle().setProperty("opacity",0);
    obj2.setAttribute("onclick", "parent."+func+"()");
    obj.appendChild(obj2);
  }
}

function i2uiOnloadChart(LoadEvent)
{
  SVGDocument = LoadEvent.getTarget().getOwnerDocument()
  ParentGroup = SVGDocument.getElementById("elements")
  TooltipRect = SVGDocument.getElementById("tooltiprect")
  origMenu = printNode(contextMenu);

  i2uiProcessChart();

  try
  {
    if (embedobj != null)
      parent.i2uiChartLoaded(embedobj.getAttribute("id"));
  }
  catch(e)
  {
  }
}

function i2uiProcessChart()
{
  // locate my embed object
  var htmldoc = parent.document;
  var embedslen = htmldoc.embeds.length;
  for (var i=0; i<embedslen; i++)
  {
    // check if embed is of svg type
    if (htmldoc.embeds[i].outerHTML.indexOf("image/svg") != -1)
    {
      if (htmldoc.embeds[i].getSVGDocument() == SVGDocument)
      {
        embedobj = htmldoc.embeds[i];
        break;
      }
    }
  }

  if (embedobj != null)
  {
    var objXmlDom = new ActiveXObject("Microsoft.XMLDOM");
    // use xml attribute in embed tag
    //var xmldata = embedobj.getAttribute("xml");
    // use xml data island
    var xmldata = htmldoc.getElementById(embedobj.getAttribute("id")+"_xml");
    if (objXmlDom.loadXML(xmldata.xml))
    {
      // set chart's title
      var chartnode = objXmlDom.selectNodes("/chart");
      if (chartnode.length > 0)
      {
        NumY = chartnode[0].getAttribute("points_on_y_axis");
        if (NumY == null)
          NumY = DEFAULT_NUMY;
        else
          NumY = NumY * 1;

        var svg_height = chartnode[0].getAttribute("height");
        if (svg_height * 1 != svg_height) 
        {
          svg_height = svg_height.substring(0,svg_height.length-1);
          svg_height = parent.document.body.offsetHeight * (svg_height/100);
        }
        var svg_width  = chartnode[0].getAttribute("width");
        if (svg_width * 1 != svg_width) 
        {
          svg_width = svg_width.substring(0,svg_width.length-1);
          svg_width = parent.document.body.offsetWidth * (svg_width/100);
        }

        var xaxisnodes = chartnode[0].selectNodes("xaxis");
        var xaxis = xaxisnodes[0].text.split(xaxisnodes[0].getAttribute("separator"));
        var len2 = xaxis.length;

        var datasets = chartnode[0].selectNodes("series");
        var len = datasets.length;

        i2uiInitChart(len, svg_height, svg_width);

        i2uiSetYAxisLabel(chartnode[0].getAttribute("y_axis_label"));

        for (var i=0; i<len2; i++) 
        {
          i2uiAddXAxisValue(xaxis[i]);
        }

        var values = new Array(len);
        var charttype = new Array(len);
        for (var i=0; i<len; i++) 
        {
          charttype[i] = datasets[i].getAttribute("type");
          i2uiAddLegend(i, 
                        datasets[i].getAttribute("name"),
                        chartnode[0].getAttribute("legend_per"),
                        'dataset',
                        i,
                        datasets[i].getAttribute("classindex"));

          i2uiSetDatasetType(i, charttype[i]);
          values[i] = datasets[i].text.split(datasets[i].getAttribute("separator"));
        }

        for (var i=0; i<len; i++) 
        {
          var clickHandler  = datasets[i].getAttribute("onclick");
          var menuName      = datasets[i].getAttribute("onclickmenu");
          var changeHandler = datasets[i].getAttribute("onchange");
          var datasetname   = datasets[i].getAttribute("name");
          var classIndex    = datasets[i].getAttribute("classindex");

          var len2 = values[i].length;
          for (var j=0; j<len2; j++) 
            i2uiAddChartValue(values[i][j],i,charttype[i],i,j,
                              clickHandler,changeHandler,
                              datasetname,menuName,classIndex);
        }

        var threshholds = chartnode[0].selectNodes("threshhold");
        var len = threshholds.length;
        for (var i=0; i<len; i++) 
        {
          i2uiSetThreshhold(threshholds[i].text,
                            i,
                            threshholds[i].getAttribute("classindex"));
          i2uiAddLegend(datasets.length+i, 
                        threshholds[i].getAttribute("name"),
                        chartnode[0].getAttribute("legend_per"),
                        'threshhold',
                        i,
                        threshholds[i].getAttribute("classindex"));
        }

        var menus = chartnode[0].selectNodes("menu");
        var len = menus.length;
        for (var i=0; i<len; i++) 
        {
          var menuname = menus[i].getAttribute("name");
          Menus[menuname] = '<menu id="'+menuname+'"><header>MENU '+menuname+'</header>';
          
          var menuoptions = menus[i].selectNodes("menuoption");
          var len2 = menuoptions.length;
          for (var j=0; j<len2; j++) 
          {
            var menuaction = menuoptions[j].getAttribute("onclick");
            Menus[menuname] += "<item onactivate=\"i2uiMenuPressed('"+menuaction+"')\">"+menuoptions[j].text+"</item>";
          }
          Menus[menuname] += '</menu>';
        }

        i2uiRefreshChart(chartnode[0].getAttribute("item_width"),true);
  
        // forces scrollers to be correct (LPM not very efficient tho)
        i2uiRefreshChart(LastItemWidth, false);
        
        i2uiTileLegends(chartnode[0].getAttribute("legend_per"));
        
        i2uiDrawGrid();
      }
    }
    else
    {
      alert("Error: invalid charting data");
    }
  }
}

function i2uiInitChart(numDatasets, h, w)
{
  i2uiSizeChart(h,w)

  // walk children and delete
  while ((obj = ParentGroup.getFirstChild()) != null) 
  {
    ParentGroup.removeChild(obj);
  }

  for (var i=0; i<Legends.length; i++)
  {
    SVGDocument.getElementById("legends").removeChild(Legends[i]);
    SVGDocument.getElementById("legends").removeChild(LegendNames[i]);
  }

  for (var i=0; i<Names.length; i++)
    SVGDocument.getElementById("labels").removeChild(Names[i])
  for (var i=0; i<Tickmarks.length; i++)
    SVGDocument.getElementById("labels").removeChild(Tickmarks[i])

  DatasetType   = new Array()
  Elements      = new Array()
  Values        = new Array()
  StackValues   = new Array()
  for (var i=0; i<numDatasets; i++)
  {
    Values[i]      = new Array()   
    StackValues[i] = new Array()   
    Elements[i]    = new Array()   
  }
  SumValues      = new Array()
  DatasetToStack = new Array()
  UniqueValues   = new Array()
  Names          = new Array()
  Tickmarks      = new Array()
  Legends        = new Array()
  LegendNames    = new Array()

  MaxSize = 0;
  MinSize = 0;
  XAxisCount = 0;
}

// during a resize call, 
// you can control the invocation of best fiy for items or
// specify a specific item width.  ALso the number of items
// along y axis can be specified
function i2uiResizeChart(h, w, bestfit_itemwidth, desired_itemwidth, desired_numy)
{
  // need to handle existing zooming or reset to default values
  YRatio = 1;

  i2uiSizeChart(h,w);

  if (bestfit_itemwidth == true) 
    i2uiRefreshChart(null,false);
  else
  {
    if (desired_itemwidth == null)
    {
      if (bestfit_itemwidth == null) 
        i2uiRefreshChart(LastItemWidth, false);
      else
        i2uiRefreshChart(bestfit_itemwidth, true);
    }
    else
      i2uiRefreshChart(desired_itemwidth, false);
  }

  if (desired_numy != null)
    NumY = desired_numy;

  i2uiDrawGrid();

  // forces scrollers to be correct
  i2uiRefreshChart(LastItemWidth, false);
}

function i2uiSizeChart(h, w)
{
  // handle percentages
  if (w != w * 1 && w.indexOf("%") != -1) 
  {
    var temp = w.substring(0, w.length - 1);
    w = (temp / 100) * parent.document.body.offsetWidth;
    //LPM for now
    w -= 18
  }
  if (h != h * 1 && h.indexOf("%") != -1) 
  {
    var temp = h.substring(0, h.length - 1);
    h = (temp / 100) * parent.document.body.offsetHeight;
    //LPM for now
    h -= 22
  }

  saveHeight = h-90+4;
  saveWidth  = w;

  ChartHeight = h - 90 - 16; // leave room for scroller too 
  ChartWidth  = w;

  BottomMargin = 70;
  // if legend beneath chart, increase margin
  BottomMargin += 16;

  // set dimension of enclosing embed
  embedobj.setAttribute("width",  w);
  embedobj.setAttribute("height", h);
  
  // set dimension of main svg object
  SVGDocument.rootElement.setAttribute("width",  w);
  SVGDocument.rootElement.setAttribute("height", h);

  SVGDocument.getElementById("xaxis").setAttribute("x2",w-20);
  SVGDocument.getElementById("xaxis").setAttribute("y1",h-BottomMargin);
  SVGDocument.getElementById("xaxis").setAttribute("y2",h-BottomMargin);
  SVGDocument.getElementById("yaxis").setAttribute("y1",h-BottomMargin);
  
  // y is now x and  x is now y when rotated
  SVGDocument.getElementById("yaxis_label").setAttribute("x",(h/2)*-1);
  SVGDocument.getElementById("yaxis_label").setAttribute("y",10);

  SVGDocument.getElementById("chartcontainer").setAttribute("width",w-20);
  SVGDocument.getElementById("chartcontainer").setAttribute("height",h-90+4);
  
  SVGDocument.getElementById("yaxiscontainer").setAttribute("height",h);

  SVGDocument.getElementById("chart").setAttribute("width",w-20);
  SVGDocument.getElementById("chart").setAttribute("height",h-90+4);

  SVGDocument.getElementById("elements").setAttribute("transform","translate(0,"+(h-BottomMargin)+")");

  SVGDocument.getElementById("xaxiscontainer").setAttribute("width",w-20);
  SVGDocument.getElementById("xaxiscontainer").setAttribute("x",YAxisWidth);
  SVGDocument.getElementById("xaxiscontainer").setAttribute("y",(h-BottomMargin+2));
  
  SVGDocument.getElementById("xaxisrect").setAttribute("width",w-20);
  SVGDocument.getElementById("xaxisrect").setAttribute("x",YAxisWidth);
  SVGDocument.getElementById("xaxisrect").setAttribute("y",(h-BottomMargin+2));

  SVGDocument.getElementById("yaxisrect").setAttribute("width",YAxisWidth);
  SVGDocument.getElementById("yaxisrect").setAttribute("height",h-90+4);

  // if legend at bottom
  SVGDocument.getElementById("legends").setAttribute("transform","translate(45,"+(h-20)+")");
}

function i2uiAddLegend(i, legend, legend_per, elementtype, j, classIndex)
{
  if (classIndex == null || classIndex.length == 0)
    classIndex = j;
  
  Legends[i] = SVGDocument.createElement("rect")
  Legends[i].setAttribute("drawn", 1)
  Legends[i].setAttribute("width", "8")
  Legends[i].setAttribute("height", "8")
  if (elementtype == 'threshhold')
  {
    Legends[i].setAttribute("class", "threshhold"+classIndex);
    Legends[i].getStyle().setProperty("stroke-dasharray","none");
  }
  else
    Legends[i].setAttribute("class", "bar"+classIndex);
  Legends[i].setAttribute("id", "legend");
  Legends[i].setAttribute("onclick", "i2uiToggleElement("+i+",'"+elementtype+"',"+j+")")

  LegendNames[i] = SVGDocument.createElement("text")
  LegendNames[i].appendChild(SVGDocument.createTextNode(legend + ""))

  SVGDocument.getElementById("legends").appendChild(LegendNames[i]);
  SVGDocument.getElementById("legends").appendChild(Legends[i]);
}

function i2uiTileLegends(LegendPer)
{
  var width1, width2, j;
  var LegendY = 0
  var LegendX = 30
  var len = Legends.length;

  if (LegendPer == null)
    LegendPer = Math.ceil(len/2);
  else
    LegendPer = Math.min(len, LegendPer);

  for (var i=0; i<LegendPer; i++)
  {
    Legends[i].setAttribute("x", LegendX)
    Legends[i].setAttribute("y", LegendY-8)
    LegendNames[i].setAttribute("x", LegendX+10)
    LegendNames[i].setAttribute("y", LegendY)

    width1 = Math.ceil(LegendNames[i].getBBox().width);
    try
    {
      j = i+(1*LegendPer);
      width2 = Math.ceil(LegendNames[j].getBBox().width);
      Legends[j].setAttribute("x", LegendX)
      Legends[j].setAttribute("y", LegendY-8+12)
      LegendNames[j].setAttribute("x", LegendX+10)
      LegendNames[j].setAttribute("y", LegendY+12)
    }
    catch(e)
    {
      width2 = 0;
    }
    LegendX += Math.max(width1, width2) + 20;
  }
  LegendX -= 20;

  SVGDocument.getElementById("legendbox").setAttribute("width",LegendX);
  SVGDocument.getElementById("legendbox").setAttribute("height",(Math.ceil(len/LegendPer)*10)+10);
}

function i2uiSetYAxisLabel(value)
{
  if (value != null)
  {
    // remove old values
    var obj = SVGDocument.getElementById("yaxis_label").getFirstChild();
    while (obj != null)
    {
      SVGDocument.getElementById("yaxis_label").removeChild(obj);
      obj = SVGDocument.getElementById("yaxis_label").getFirstChild();
    }

    SVGDocument.getElementById("yaxis_label").appendChild(SVGDocument.createTextNode(value + ""))
  }
}

function i2uiSetDatasetType(i, ItemType)
{
  DatasetType[i] = ItemType;
  DatasetToStack[i] = i;
  if (i > 0 && ItemType == 'stackbar') 
    DatasetToStack[i] = DatasetToStack[i-1];
  else
    DatasetToStack[i] = i;
}

function i2uiAddXAxisValue(Name)
{
  XAxisCount++

  if (Name=="" || Name==null)
    Name = " ";

  Names[Names.length] = SVGDocument.createElement("text");

  Names[Names.length - 1].setAttribute("y", 2);
  Names[Names.length - 1].setAttribute("class", "xAxisValue");
  Names[Names.length - 1].appendChild(SVGDocument.createTextNode(Name + ""));
  SVGDocument.getElementById("labels").appendChild(Names[Names.length - 1]);
  
  Tickmarks[Tickmarks.length] = SVGDocument.createElement("line");
  Tickmarks[Tickmarks.length-1].setAttribute("x1", 0);
  Tickmarks[Tickmarks.length-1].setAttribute("y1", -10);
  Tickmarks[Tickmarks.length-1].setAttribute("x2", 0);
  Tickmarks[Tickmarks.length-1].setAttribute("y2", 5);
  Tickmarks[Tickmarks.length-1].setAttribute("class", "xAxisTickMark");
  SVGDocument.getElementById("labels").appendChild(Tickmarks[Tickmarks.length-1]);
}

function i2uiSetThreshhold(Value, ColorId, classIndex)
{
  Threshholds[Threshholds.length] = SVGDocument.createElement("line")
  if (classIndex == null || classIndex.length == 0)
    Threshholds[Threshholds.length-1].setAttribute("class", "threshhold"+ColorId);
  else
    Threshholds[Threshholds.length-1].setAttribute("class", "threshhold"+classIndex);
  Threshholds[Threshholds.length-1].getStyle().setProperty("fill","none");
  Threshholds[Threshholds.length-1].setAttribute("x1", "0");
  Threshholds[Threshholds.length-1].setAttribute("x2", ChartWidth+10);
  Threshholds[Threshholds.length-1].setAttribute("value", Value);
  Threshholds[Threshholds.length-1].setAttribute("y1", Value);
  Threshholds[Threshholds.length-1].setAttribute("y2", Value);
  Threshholds[Threshholds.length-1].setAttribute("onmouseover", "i2uiSetTooltipCoords(evt)");
  Threshholds[Threshholds.length-1].setAttribute("id",  "threshhold"+(Threshholds.length-1));

  i2uiDefineTooltip(SVGDocument.getElementById("svgchart"),
      "threshhold"+(Threshholds.length-1), 
      Value);

  ParentGroup.appendChild(Threshholds[Threshholds.length-1])
}

function i2uiAddChartValue(Value, ColorId, ItemType, dataset, index, 
                           clickHandler, changeHandler, 
                           datasetName, menuName, classIndex)
{
  Value = Value * 1
  if (isNaN(Value))
  {
    // Textual or null values are not allowed
    return;
  }
    
  Values[dataset][index] = Value * 1

  if (ItemType == "stackbar")
  {
    var index2 = DatasetToStack[dataset];
    if (dataset > 0 && (DatasetType[dataset-1] == 'stackbar' || DatasetType[dataset-1] == 'bar') ) 
    {
      SumValues[SumValues.length] = SumValues[SumValues.length-XAxisCount] + (Value * 1);

      if (DatasetType[dataset-1] == 'stackbar')
        StackValues[index2][index] = StackValues[index2][index] + (Value * 1);
      else
        StackValues[index2][index] = Values[dataset-1][index] + (Value * 1);
    }
    else
    {
      // this should NEVER happen
      SumValues[SumValues.length] = Value * 1;
      StackValues[index2][index] = Value * 1;
    }
  }
  else
  {
    SumValues[SumValues.length] = Value * 1
  }
  
  // unqiue values is for line, step, bar and summed value for stackbar
  UniqueValues[UniqueValues.length] = SumValues[SumValues.length-1]

  // compute any new max and min for chart
  if (SumValues[SumValues.length-1] > MaxSize) 
    MaxSize = SumValues[SumValues.length-1] * 1
  if (SumValues[SumValues.length-1] < MinSize) 
    MinSize = SumValues[SumValues.length-1]
  
  if (ItemType == "bar" || ItemType == "stackbar")
  {
    Elements[dataset][index] = SVGDocument.createElement("path");
  
    if (classIndex == null || classIndex.length == 0)
      Elements[dataset][index].setAttribute("class", "bar"+ColorId);
    else
      Elements[dataset][index].setAttribute("class", "bar"+classIndex);
    if (clickHandler != null)
      Elements[dataset][index].setAttribute("onclick", "parent."+clickHandler+"("+dataset+",'"+datasetName+"',"+index+","+Value+")");
    if (menuName != null) 
      Elements[dataset][index].setAttribute("onmousedown", "i2uiRightClickHandler(evt,'"+clickHandler+"','"+menuName+"',"+dataset+",'"+datasetName+"',"+index+","+Value+")");
    else
      Elements[dataset][index].setAttribute("onmousedown", "i2uiRightClickHandler(evt,null,'noop')");

    Elements[dataset][index].setAttribute("onmouseover", "i2uiSetTooltipCoords(evt)");
    Elements[dataset][index].setAttribute("id", "element"+dataset+"x"+index);

    ParentGroup.appendChild(Elements[dataset][index]);
  }
  else
  if (ItemType == "line")
  {
    if (Elements[dataset][-1] == null)
    {
      Elements[dataset][-1] = SVGDocument.createElement("polyline");
      if (classIndex == null || classIndex.length == 0)
        Elements[dataset][-1].setAttribute("class", "line"+ColorId);
      else
        Elements[dataset][-1].setAttribute("class", "line"+classIndex);
      Elements[dataset][-1].setAttribute("points", "-10,-10");
      ParentGroup.appendChild(Elements[dataset][-1]);
    }
    Elements[dataset][index] = SVGDocument.createElement("circle");
    if (classIndex == null || classIndex.length == 0)
      Elements[dataset][index].setAttribute("class", "bar"+ColorId);
    else
      Elements[dataset][index].setAttribute("class", "bar"+classIndex);
    Elements[dataset][index].setAttribute("r", 3);
    if (clickHandler != null) 
      Elements[dataset][index].setAttribute("onclick", "parent."+clickHandler+"("+dataset+",'"+datasetName+"',"+index+","+Value+")");

    Elements[dataset][index].setAttribute("onmouseover", "i2uiSetTooltipCoords(evt)");
    Elements[dataset][index].setAttribute("id", "element"+dataset+"x"+index);

    ParentGroup.appendChild(Elements[dataset][index])
  }
  else
  if (ItemType == "step")
  {
    if (Elements[dataset][-1] == null)
    {
      Elements[dataset][-1] = SVGDocument.createElement("polyline");
      if (classIndex == null || classIndex.length == 0)
        Elements[dataset][-1].setAttribute("class", "line"+ColorId);
      else
        Elements[dataset][-1].setAttribute("class", "line"+classIndex);
      Elements[dataset][-1].setAttribute("points", "-10,-10");
      ParentGroup.appendChild(Elements[dataset][-1]);
    }
    Elements[dataset][index] = SVGDocument.createElement("circle");
    Elements[dataset][index].setAttribute("class", "bar"+ColorId);
    if (classIndex == null || classIndex.length == 0)
      Elements[dataset][index].setAttribute("class", "bar"+ColorId);
    else
      Elements[dataset][index].setAttribute("class", "bar"+classIndex);
    Elements[dataset][index].setAttribute("r", 3);
    if (clickHandler != null) 
      Elements[dataset][index].setAttribute("onclick", "parent."+clickHandler+"("+dataset+",'"+datasetName+"',"+index+","+Value+")")

    Elements[dataset][index].setAttribute("onmouseover", "i2uiSetTooltipCoords(evt)");
    Elements[dataset][index].setAttribute("id", "element"+dataset+"x"+index);

    ParentGroup.appendChild(Elements[dataset][index])
  }

  if (ItemType != "stackbar")
    i2uiDefineTooltip(SVGDocument.getElementById("svgchart"),
                      "element"+dataset+"x"+index, 
                      SumValues[SumValues.length-1]);
}
  
function i2uiWidenChart()
{
  var y = sbGetScrollY();
  var x = sbGetScrollX();
  var factor = LastItemWidth;
  i2uiRefreshChart(LastItemWidth * 1.5, false)
  // rescroll to keep same leftmost item in view
  factor = LastItemWidth / factor;
  if (x > 0)
    sbSetScrollX(x*factor);
  sbSetScrollY(y);
}

function i2uiNarrowChart()
{
  var y = sbGetScrollY();
  var x = sbGetScrollX();
  var factor = LastItemWidth;
  var newItemWidth = Math.max(minItemWidth, LastItemWidth / 1.5);
  i2uiRefreshChart(newItemWidth, false)
  // rescroll to keep same leftmost item in view
  factor = LastItemWidth / factor;
  if (x > 0)
    sbSetScrollX(x*factor);
  if (y > 0)
    sbSetScrollY(y);
}

function i2uiRestoreWidth()
{
  var y = sbGetScrollY();
  var x = sbGetScrollX();
  i2uiRefreshChart(OrigItemWidth, false);
  if (x > 0)
    sbSetScrollX(0);
  if (y > 0)
    sbSetScrollY(y);
}

function i2uiWidthZoomMenu(evt)
{
  // handle right-click
  if(evt.getButton() == 2)
  {
    // display menu here
    var zoomMenu = '<menu id="zoom"><header>MENU Zoom</header><item onactivate="i2uiWidenChart()">Zoom In</item><item onactivate="i2uiNarrowChart()">Zoom Out</item><item onactivate="i2uiRestoreWidth()">Restore</item></menu>';
    var newMenuRoot = parseXML(zoomMenu, contextMenu);
    contextMenu.replaceChild(newMenuRoot, contextMenu.firstChild);
    evt.stopPropagation();
    return;
  }
}

function i2uiRestoreHeight()
{
  if (YRatio != 1)
  {
    YRatio = 1;
    var h = saveHeight*YRatio;
    ChartHeight = h - 16; // leave room for scroller

    var x = sbGetScrollX();

    // assign new virtual height
    SVGDocument.getElementById("chart").setAttribute("height",h);
    SVGDocument.getElementById("yaxiscontainer").setAttribute("height",h);
    SVGDocument.getElementById("elements").setAttribute("transform","translate(0,"+h+")");

    i2uiRefreshChart(LastItemWidth, false);

    i2uiDrawGrid(-4);

    sbSetScrollX(x);
    sbSetScrollY(0);
  }
}

function i2uiHeightZoomMenu(evt)
{
  // handle right-click
  if(evt.getButton() == 2)
  {
    // display menu here
    var zoomMenu = '<menu id="zoom"><header>MENU Zoom</header><item onactivate="i2uiRestoreHeight()">Restore</item></menu>';
    var newMenuRoot = parseXML(zoomMenu, contextMenu);
    contextMenu.replaceChild(newMenuRoot, contextMenu.firstChild);
    evt.stopPropagation();
    return;
  }
}

function i2uiWidthZoomAbsolute(evt)
{
  var y = sbGetScrollY();
  var x = evt.clientX - YAxisWidth;
  var x2 = sbGetScrollX();
  var ratio = (saveWidth-YAxisWidth)/(x2+x);
  var newItemWidth = (ratio*LastItemWidth);
  
  // if reached lowest zoom level
  if (newItemWidth < LastItemWidth &&
      LastItemWidth == minItemWidth)
  {
    evt.preventDefault();
    evt.stopPropagation();
    return;
  }
  
  i2uiRefreshChart(Math.max(minItemWidth, newItemWidth), false);
  sbSetScrollY(y);
  sbSetScrollX(0);
}

// determine new value for top of chart
function i2uiDataZoomAbsolute(evt)
{
  var target = SVGDocument.getElementById("yaxiscontainer");
  var y = evt.clientY-18;
  var currtop = sbGetScrollY();
  var virtualHeight = target.getAttribute("height");
  var notShowing = (currtop/virtualHeight)*MaxSize;
  var showing = MaxSize-notShowing;

  var range = MaxSize / YRatio;
  var fromtop = (y/saveHeight)*range;
  var relative = (range - fromtop);
  if (showing - range > 18) 
    relative = showing - range + relative;
  YRatio = Math.max(1,(MaxSize/saveHeight) / (relative/saveHeight));

  if (YRatio > 50)
  {
    alert("You can not zoom to this point.  The zoom factor would be exceeded.");
    evt.preventDefault();
    evt.stopPropagation();
    return;
  }

  var delta = -4;
  if (YRatio > 1)
    delta = -2;
  
  var h = saveHeight*YRatio;
  ChartHeight = h - 16; // leave room for scroller

  // assign new virtual height
  SVGDocument.getElementById("chart").setAttribute("height",h);
  SVGDocument.getElementById("yaxiscontainer").setAttribute("height",h);
  SVGDocument.getElementById("elements").setAttribute("transform","translate(0,"+h+")");

  i2uiRefreshChart(LastItemWidth, false);
  
  i2uiDrawGrid(delta);

  sbSetScrollY(h);
}

function i2uiDataZoomIn()
{
  if (UniqueValues.length > 1)
  {
    MaxIndex = Math.min(MaxIndex+1, UniqueValues.length-2);
    i2uiRefreshChart(LastItemWidth, false);
    i2uiDrawGrid();
  }
}

function i2uiDataZoomOut()
{
  MaxIndex = Math.max(MaxIndex-1, 0);
  i2uiRefreshChart(LastItemWidth, false);
  i2uiDrawGrid();
}

function i2uiValueCompare(value1, value2)
{
  // descending order
  return (value2 * 1) - (value1 * 1);
}

function i2uiSortUniqueValues()
{
  UniqueValues.sort(i2uiValueCompare);
  var len = UniqueValues.length;
  for (var i=0; i<len-1; i++)
  {
    if (UniqueValues[i] == UniqueValues[i+1])
    {
      var tempArray1 = UniqueValues.slice(0,i+1);
      var tempArray2 = UniqueValues.slice(i+2);
      UniqueValues = tempArray1.concat(tempArray2); 
      len--;
      i--;
    }
  }
}

function i2uiManageScrollers()
{
    
  aScrollableObject.vertDelta = 10;
  aScrollableObject.horzDelta = 10;
  aScrollableObject.outerSVG = SVGDocument.getElementById("chartcontainer");
  aScrollableObject.innerSVG = SVGDocument.getElementById("chart");
  aScrollableObject.vertSVG = SVGDocument.getElementById("yaxiscontainer");
  aScrollableObject.horzSVG = SVGDocument.getElementById("xaxiscontainer");
  aScrollableObject.mainSVG = SVGDocument.getElementById("mousemask");

  aScrollableObject.offsetTop    = 0;
  aScrollableObject.offsetLeft   = 70;
  aScrollableObject.offsetLeft2  = 0;
  aScrollableObject.marginRight  = -70;
  aScrollableObject.marginRight2 = 20;
  aScrollableObject.marginRight3 = 4;
  aScrollableObject.marginBottom = 35;
  aScrollableObject.owner = SVGDocument.getElementById('svgchart');

  sbMake(aScrollableObject);
}

function i2uiRefreshChart(fixedItemWidth, bFirstRender)
{
  if (bFirstRender == null || bFirstRender)
  {
    i2uiSortUniqueValues();
    i2uiManageScrollers();
  }

  // round min and max to integer multiple of 10
  if (MinSize != 0 && Math.floor(MinSize/10)*10 != MinSize)
  {
    MinSize = (Math.ceil(MinSize/10)*10)-10;
  }
  MaxSize = Math.ceil(UniqueValues[MaxIndex]/10)*10;

  YSize = MaxSize - MinSize;

  LabelShift = (MinSize/YSize) * ChartHeight;

  if (Values.length > 0)
  {
    var BarStart = 5;
    ItemsPerX = 0;
    for (var i=0; i<DatasetType.length; i++)
    {
      if (DatasetType[i] == 'bar' ||
          (DatasetType[i] == 'stackbar' &&
           i > 0 && 
           !(DatasetType[i-1] == 'stackbar' ||
           DatasetType[i-1] == 'bar')) )
        ItemsPerX++;
    }
    // handle if only lines are present
    ItemsPerX = Math.max(1, ItemsPerX);

    WidthPerX = (ChartWidth - 90) / XAxisCount;
    var ItemWidth = Math.max(MIN_ITEM_WIDTH,(WidthPerX-10) / ItemsPerX);
    WidthPerX = (ItemWidth * ItemsPerX) + 10;

    if (fixedItemWidth != null) 
    {
      var maxItemWidth = (ChartWidth - 90) / ItemsPerX;
      ItemWidth = Math.min(maxItemWidth,fixedItemWidth * 1);
      WidthPerX = (ItemWidth * ItemsPerX) + 10;
      SVGDocument.getElementById("chart").setAttribute("width", (WidthPerX*Names.length)+40+16);
      SVGDocument.getElementById("xaxisrect").setAttribute("width", (WidthPerX*Names.length)+40+16);
      SVGDocument.getElementById("xaxiscontainer").setAttribute("width", (WidthPerX*Names.length)+40+16);
    }
    sbSync();
    LastItemWidth = ItemWidth;
    aScrollableObject.horzDelta = LastItemWidth;

    if (bFirstRender == null || bFirstRender)
      OrigItemWidth = ItemWidth;

    // process x axis
    for (var i=0; i<Names.length; i++)
    {
      var x = 10 + (i * WidthPerX);

      Names[i].setAttribute("x", x);
      var x1 = -(x*CosAngle)+x;
      var y1 = -(x*SinAngle);
      Names[i].setAttribute("transform", XAxisMatrix+x1+" "+y1+")");
      
      Tickmarks[i].setAttribute("x1", x - 5);
      Tickmarks[i].setAttribute("x2", x - 5);
    }
    
    // process in dataset order for bars, lines, steps
    for (var i=0; i<DatasetType.length; i++)
    {
      if (DatasetType[i] == 'bar') 
      {
        var end = Values[i].length;
        var BarX = BarStart;
        for (var j=0; j<end; j++)
        {
          i2uiDrawBarSegment(BarX, Values[i][j]/YSize, Elements[i][j], (MinSize/YSize)*ChartHeight, null, ItemWidth);
          BarX += WidthPerX;
        }
        // if next is not stackbar, then increment x offset
        if (i < DatasetType.length-1 && DatasetType[i+1] != 'stackbar')
          BarStart += ItemWidth;
      }
      else
      if (DatasetType[i] == 'stackbar') 
      {
        // if next is not stackbar, then increment x offset
        if (i < DatasetType.length-1 && DatasetType[i+1] != 'stackbar')
          BarStart += ItemWidth;
      }
      else
      if (DatasetType[i] == 'line') 
      {
        var LineStart = 5;
        var points = "";
        var end = Values[i].length;
        for (var j=0; j<end; j++)
        {
          points += LineStart+","+(((Values[i][j]-MinSize)/YSize)*ChartHeight*-1)+" ";
          i2uiDrawLineSegment(LineStart, (Values[i][j]-MinSize)/YSize, Elements[i][j]);
          LineStart += WidthPerX;
        }
        Elements[i][-1].setAttribute("points", points);
      }
      else
      if (DatasetType[i] == 'step') 
      {
        var LineStart = 5;
        var points = "";
        var end = Values[i].length;
        for (var j=0; j<end; j++)
        {
          points += LineStart+","+(((Values[i][j]-MinSize) / YSize)* ChartHeight* -1)+" ";
          i2uiDrawStepSegment(LineStart, (Values[i][j]-MinSize)/YSize, Elements[i][j]);
          // after every odd-numbered value, increment x offset
          if (j % 2 == 1) 
            LineStart += WidthPerX;
        }
        Elements[i][-1].setAttribute("points", points);
      }
    }
    
    // process in x axis order for stackbar
    var owner = SVGDocument.getElementById("barsidetext");
    for (var i=0; i<XAxisCount; i++)
    {
      BarStart = (i * WidthPerX) + 5;
      var yBasis=(MinSize/YSize)*ChartHeight;
      var StartY=yBasis;
      for (var j=0; j<DatasetType.length; j++)
      {
        if (DatasetType[j] == 'bar') 
        {
          // if next is stackbar
          if (j < DatasetType.length-1 && DatasetType[j+1] == 'stackbar')
          {
            if (bFirstRender)
            {
              // add new x of y label 
              var l = DatasetToStack[j];
              i2uiDefineTooltip(SVGDocument.getElementById("svgchart"),
                  "element"+j+"x"+i, 
                  Values[j][i]+" of "+StackValues[l][i]);
            }
          }
          else
            BarStart += ItemWidth;

          // determine StartY based on height of bar
          StartY = yBasis + (Values[j][i]/YSize * ChartHeight * -1);
        }
        else
        if (DatasetType[j] == 'stackbar') 
        {
          k = (j * XAxisCount) + i;
          if (bFirstRender)
          {
            var l = DatasetToStack[j];
            i2uiDefineTooltip(SVGDocument.getElementById("svgchart"),
                "element"+j+"x"+i, 
                Values[j][i]+" of "+StackValues[l][i]);
          }
          StartY = i2uiDrawBarSegment(BarStart, Values[j][i]/YSize, Elements[j][i], StartY, SumValues[k]/YSize, ItemWidth);
          if (j < DatasetType.length-1 && DatasetType[j+1] == 'bar')
            BarStart += ItemWidth;
        }
      }
    }

    // process threshholds according to scaled chart
    for (var i=0; i<Threshholds.length; i++)
    {
      var value = Threshholds[i].getAttribute("value");
      var value2 = (value-MinSize)/YSize * ChartHeight;
      Threshholds[i].setAttribute("y1", value2*-1);
      Threshholds[i].setAttribute("y2", value2*-1);
    }
  }

  var zerorefline  = SVGDocument.getElementById("zeroreferenceline");
  if (zerorefline != null)
  {
    if (MinSize != 0)
    { 
      var newy = (MinSize/YSize)*ChartHeight + ChartHeight + 20;
      zerorefline.setAttribute("transform", "translate(0,"+newy+")");
      zerorefline.getStyle().setProperty('visibility', 'inherit');
    }
    else
    {
      zerorefline.getStyle().setProperty('visibility', 'hidden');
    }
  }
}
  
function i2uiDrawLineSegment(Start, Height, Element)
{
  Element.setAttribute("cx", Start);
  Element.setAttribute("cy", Height * ChartHeight * -1);
}

function i2uiDrawStepSegment(Start, Height, Element)
{
  Element.setAttribute("cx", Start);
  Element.setAttribute("cy", Height * ChartHeight * -1);
}

function i2uiDrawBarSegment(StartX, Height, Element, StartY, SumHeight, Width)
{
  if (StartY == null)
    StartY = 0;

  if (Height < 0)
  {
    StartY += (Height * ChartHeight * -1);
    Height = Math.abs(Height);
  }

  XOffset3D = 10;
  YOffset3D = 5;
  PathData = "M" + (StartX + Width) + ","+ StartY;
  PathData = PathData + "h" + (Width * -1);
  PathData = PathData + "v" + (Height * ChartHeight * -1);
  PathData = PathData + "l" + XOffset3D + ",-" + YOffset3D;
  PathData = PathData + "h" + Width;
  PathData = PathData + "v" + (Height * ChartHeight);
  PathData = PathData + "l-" + XOffset3D + "," + YOffset3D;
  PathData = PathData + "v" + (Height * ChartHeight * -1);
  PathData = PathData + "h" + (Width * -1);
  PathData = PathData + "h" + Width;
  PathData = PathData + "l" + XOffset3D + ",-" + YOffset3D;
  PathData = PathData + "l-" + XOffset3D + "," + YOffset3D;
  
  Element.setAttribute("d", PathData);
  
  if (SumHeight != null)
    return StartY + (Height * ChartHeight * -1);
  else
    return StartX + Width;
}
  
function i2uiSetAxis(Text)
{
  NewItem = SVGDocument.createTextNode(Text + "");
  SVGDocument.getElementById("axis").replaceChild(NewItem, SVGDocument.getElementById("axis").getFirstChild());
}
  
var states = new Array("hidden","inherit");
function i2uiToggleElement(legendIndex, elementtype, index)
{
  var flag = Legends[legendIndex].getAttribute("drawn");
  flag = ((flag * 1) + 1) % 2;
  Legends[legendIndex].setAttribute("drawn",flag);
  Legends[legendIndex].getStyle().setProperty("opacity",(flag+0.3));

  var element = null;
  if (elementtype == 'threshhold')
  {
    element = Threshholds[index];
    if (element != null)
      element.getStyle().setProperty("visibility", states[flag]);
  }
  else
  if (elementtype == 'dataset')
  {
    if (DatasetType[index] == 'bar' || 
        DatasetType[index] == 'stackbar')
    {
      var len = Elements[index].length;
      for (var i=0; i<len; i++) 
        Elements[index][i].getStyle().setProperty("visibility", states[flag]);
    }
    else
    if (DatasetType[index] == 'line' || 
        DatasetType[index] == 'step')
    {
      var len = Elements[index].length;
      for (var i=-1; i<len; i++) 
        Elements[index][i].getStyle().setProperty("visibility", states[flag]);
    }
  }
}

function i2uiMenuPressed(menuHandler)
{
  var action = "parent."+menuHandler+"("+menudataset+",'"+menudatasetName+"',"+menuindex+","+menuValue+")";
  eval(action);
}

function i2uiRightClickHandler(evt, clickHandler, menuName, dataset, datasetName, index, Value)
{
  // handle right-click
  if(evt.getButton() == 2)
  {
    if (menuName == 'noop')
    {
      evt.preventDefault();
      evt.stopPropagation();
      return;
    }
    if (menuName == null)
    {
      var node = parseXML(origMenu, contextMenu);
      contextMenu.replaceChild(node, contextMenu.firstChild);
      return;
    }

    menudataset     = dataset;
    menudatasetName = datasetName;
    menuindex       = index;
    menuValue       = Value;

    var newMenuRoot = parseXML(Menus[menuName], contextMenu);
    contextMenu.replaceChild(newMenuRoot, contextMenu.firstChild);

    evt.stopPropagation();
  }
  else
  {
    if (clickHandler == null && menuName == null)
      return;
  
    if (clickHandler != null)
    {
      var action = "parent."+clickHandler+"("+dataset+",'"+datasetName+"',"+index+","+Value+")";
      eval(action);
    }
  }
}

var Groupings = new Array(2,5,10,25,50,100,500,1000,10000,100000,1000000,10000000,100000000,1000000000,10000000000,100000000000,1000000000000);
function i2uiDrawGrid(offset)
{
  if (offset == null)
    offset = 0;
  
  MaxSize = Math.ceil(UniqueValues[MaxIndex]/10)*10;
  YSize = MaxSize - MinSize;
  var myMinSize = Math.min(YSize,-1*MinSize);
  var every1 = Groupings[0];
  var every2 = Groupings[0];
  var len = Groupings.length;
  var SizePer;

  // compute grid for positive values
  var Count = Math.floor(NumY*YRatio);
  if (MaxSize > 0)
    for (var i=1; i<len; i++)
    {
      every1 = Groupings[i-1];
      Count = Math.floor(MaxSize / every1);
      SizePer = MaxSize/Count;
      if (SizePer < Groupings[i] &&
          Count < NumY*YRatio + 2)
        break;
    }

  // compute grid for negative values
  if (MinSize < 0)
  {
    Count = Math.floor(NumY*YRatio);
    for (var i=1; i<len; i++)
    {
      every2 = Groupings[i-1];
      Count = Math.floor(myMinSize/every2);
      SizePer = myMinSize/Count;
      if (SizePer < Groupings[i] &&
          Count < NumY*YRatio + 2)
        break;
    }
  }
  var total = 0;
  var Shift = 0;
  var every = Math.max(every1, every2);
  
  if (MaxSize > 0)
  {
    Count = Math.floor(MaxSize / every);
    Shift = ((MaxSize - (Count * every))/YSize)*ChartHeight;
    total = Count * every;
  }
  else
  {
    Count = 0;
    if (myMinSize != -1*MinSize)
      Shift = ((myMinSize-(Math.floor(myMinSize/every)*every))/YSize)*ChartHeight;
  }

  // account for room above chart
  Shift += offset + 18;
  
  Count += Math.floor(myMinSize/every)+1;

  var owner = SVGDocument.getElementById("barsidetext");

  // remove old objects
  for (var i=0; i<YAxisTickMark.length; i++)
  {
    owner.removeChild(YAxisTickMark[i]);
  }
  for (var i=0; i<YAxisTickMarkText.length; i++)
  {
    owner.removeChild(YAxisTickMarkText[i]);
  }
  YAxisTickMark     = new Array();
  YAxisTickMarkText = new Array();
  for (var i=0; i<Count; i++)
  {
    YAxisTickMark[i] = SVGDocument.createElement("rect");
    YAxisTickMark[i].setAttribute("class","yAxisTickMark");
    YAxisTickMark[i].setAttribute("x",64);
    YAxisTickMark[i].setAttribute("y",
                               (i*every/YSize)*ChartHeight + Shift);
    YAxisTickMark[i].setAttribute("width",6);
    YAxisTickMark[i].setAttribute("height",2);

    owner.appendChild(YAxisTickMark[i]);

    YAxisTickMarkText[i] = SVGDocument.createElement("text");
    YAxisTickMarkText[i].setAttribute("class","yAxisValue");
    YAxisTickMarkText[i].setAttribute("x",64);
    YAxisTickMarkText[i].setAttribute("y",
                               (i*every/YSize)*ChartHeight + Shift);
    YAxisTickMarkText[i].appendChild(SVGDocument.createTextNode(total + ""));
    total -= every;

    owner.appendChild(YAxisTickMark[i]);
    owner.appendChild(YAxisTickMarkText[i]);
  }
}

var tooltipX=100;
var tooltipY=100;
var tooltip = null;
function i2uiSetTooltipCoords(evt)
{
  tooltipX = evt.clientX;
  tooltipY = evt.clientY;
  
  TooltipRect.setAttribute("x", tooltipX - 5);
  TooltipRect.setAttribute("y", tooltipY - 13);
}
function i2uiRemoveTooltip(evt)
{
  TooltipRect.setAttribute("display","none");
}
function i2uiShowTooltipRect()
{
  if (tooltip != null)
  {
    TooltipRect.setAttribute("width", 
                             Math.ceil(tooltip.getBBox().width+10));
    TooltipRect.setAttribute("height", 
                             Math.ceil(tooltip.getBBox().height+10));
    TooltipRect.setAttribute("display","inline");
  }
}
function i2uiPlaceTooltip(evt)
{
  tooltip = evt.getTarget().getParentNode();
  if (tooltip != null) 
  {
    tooltip.setAttribute("x", tooltipX);
    tooltip.setAttribute("y", tooltipY);
    // must delay in order for text to appear before computing bounding box
    setTimeout("i2uiShowTooltipRect()",50);
  }
}

function i2uiDefineTooltip(owner, id, text)
{
  var tooltip = SVGDocument.getElementById(id+"tooltip");
  if (tooltip != null)
    owner.removeChild(tooltip);  
  
  // must use template in order for onbegin to work
  tooltip = SVGDocument.getElementById("tooltiptemplate");
  if (tooltip != null)
  {
    tooltip = tooltip.cloneNode(true);
    tooltip.setAttribute("id",id+"tooltip");
    var tooltipText = tooltip.getFirstChild();
    if (tooltipText != null)
    {
      tooltipText.data = text;

      var tooltipSet = tooltipText.nextSibling;
      if (tooltipSet != null)
      {
        tooltipSet.setAttribute("begin",id+".mouseover");
        tooltipSet.setAttribute("end",id+".mouseout");
      }
      // this replaces the current node with the new text
      tooltip.appendChild(tooltipText);
    }
    owner.appendChild(tooltip);
  }
}
