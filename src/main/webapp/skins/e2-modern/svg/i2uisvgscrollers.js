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

// scrollers in horizontal and vertical direction for any object

var scrollableObject;

var sbLight  = "#d1d6f0 ";
var sbDark   = "rgb(135,135,135)";
var sbShadow = "rgb(72,72,72)";

var sbXScale;
var sbYScale;
var sbVert, sbVertBackground, sbUpButton, sbDownButton, sbVertSlider;
var sbHorz, sbHorzBackground, sbLeftButton, sbRightButton, sbHorzSlider;
var sbVertSliderX;
var sbHorzSliderY;
var sbDragX = false;
var sbDragY = false;
var sbLastX = 0;
var sbLastY = 0;
var sbScrollX = 0;
var sbScrollY = 0;
var sbTimeout;

var sbVirtualHeight;

function sbButtonSetSize(button, w, h)
{
  button.bg.setAttribute("width", w);
  button.bg.setAttribute("height", h);
  button.bl.setAttribute("d", "M1.5 "+(h-1.5)+ "v-"+(h-3)+"h"+(w-3));
  button.bd.setAttribute("d", "M1.5 "+(h-0.5)+"h"+(w-2)+"v-"+(h-2));
  button.bs.setAttribute("d", "M0.5 "+(h+0.5)+"h"+w+"v-"+h);
}

function sbButtonMake(kind)
{
  var aButton = new Object();
  
  var aGroup = document.createElement("g");
  aButton.g = aGroup;
  aGroup.setAttribute("fill", "none");
  aGroup.setAttribute("stroke-linecap", "square");
  aGroup.setAttribute("shape-rendering", "optimizeSpeed");
  
  var aRect = document.createElement("rect");
  aButton.bg = aRect;
  aRect.setAttribute("fill", sbLight);
  aGroup.appendChild(aRect);

  var aPath = document.createElement("path");
  aButton.bl = aPath;
  aPath.setAttribute("stroke", "white");
  aGroup.appendChild(aPath);

  aPath = document.createElement("path");
  aButton.bd = aPath;
  aPath.setAttribute("stroke", sbDark);
  aGroup.appendChild(aPath);
  
  aPath = document.createElement("path");
  aButton.bs = aPath;
  aPath.setAttribute("stroke", sbShadow);
  aGroup.appendChild(aPath);
  
  // if not a slider button
  if(kind)
  {
    var aPath = document.createElement("path");
    aPath.setAttribute("fill", "black");
    if( kind == "up")
      aPath.setAttribute("d", 
                         "M4.5 9.5l3-3l3 3z");
    else if( kind == "down")
      aPath.setAttribute("d", 
                         "M4.5 6.5l3 3l3-3z");
    else if( kind == "right")
      aPath.setAttribute("d",
                         "M5.89645 10.8964 L8.89645 7.89645 L5.89645 4.89645 z");
    else if( kind == "left")
      aPath.setAttribute("d", 
                     "M9.10355 10.8964 L6.10355 7.89645 L9.10355 4.89645 z");

    aGroup.appendChild(aPath);
  }
  sbButtonSetSize(aButton, 15, 15);
  return aButton;
}

function sbNewRect(x, y, w, h, fill)
{
  var aRect = document.createElement("rect");
  aRect.setAttribute("x", x);
  aRect.setAttribute("y", y);
  aRect.setAttribute("width", h);
  aRect.setAttribute("height", w);
  aRect.setAttribute("fill", fill);
  return aRect;
}

function sbMake(obj)
{
  scrollableObject = obj;

  // create only once !!
  if (sbVert == null && sbHorz == null)
  {
    sbVert = document.createElement("g");
    sbVert.setAttribute("id", "VertScrollbar");
    scrollableObject.owner.appendChild(sbVert);

    sbHorz = document.createElement("g");
    sbHorz.setAttribute("id", "HorzScrollbar");
    scrollableObject.owner.appendChild(sbHorz);

    if(document.getElementById("sbBgPatt") == null)
    {
    	var p = document.createElement("pattern");
    	p.setAttribute("id", "sbBgPatt");
    	sbVert.appendChild(p);
    	sbHorz.appendChild(p);
    	p.setAttribute("width", "2");
    	p.setAttribute("height", "2");
    	p.setAttribute("patternUnits", "userSpaceOnUse");
    	p.setAttribute("shape-rendering", "optimizeSpeed");
    	p.appendChild(sbNewRect(0, 0, 2, 2, "white"));
    	p.appendChild(sbNewRect(0, 0, 1, 1, sbLight));
    	p.appendChild(sbNewRect(1, 1, 1, 1, sbLight));
    }

    sbVertBackground = document.createElement("rect");
    sbVertBackground.setAttribute("fill", "url(#sbBgPatt)");
    sbVertBackground.setAttribute("width", "16");
    sbVert.appendChild(sbVertBackground);

    sbHorzBackground = document.createElement("rect");
    sbHorzBackground.setAttribute("fill", "url(#sbBgPatt)");
    sbHorzBackground.setAttribute("height", "16");
    sbHorz.appendChild(sbHorzBackground);

    sbUpButton = sbButtonMake("up");
    sbVert.appendChild(sbUpButton.g);

    sbLeftButton = sbButtonMake("left");
    sbHorz.appendChild(sbLeftButton.g);

    sbVertSlider = sbButtonMake();
    sbVert.appendChild(sbVertSlider.g);
    
    sbHorzSlider = sbButtonMake();
    sbHorz.appendChild(sbHorzSlider.g);

    sbDownButton = sbButtonMake("down");
    sbVert.appendChild(sbDownButton.g);

    sbRightButton = sbButtonMake("right");
    sbHorz.appendChild(sbRightButton.g);
  }
  sbSync();
}

function sbSync()
{
  var r = scrollableObject.innerSVG;
  var s = 1/r.currentScale;
  var ct = r.currentTranslate;

  var screenwidth  = getInnerWidth();
  var screenheight = getInnerHeight();
  var realheight   = parseInt(scrollableObject.outerSVG.getAttribute('height'));
  var realwidth    = parseInt(scrollableObject.outerSVG.getAttribute('width'));
  var scrollheight = parseInt(scrollableObject.innerSVG.getAttribute('height'));
  var scrollwidth  = parseInt(scrollableObject.innerSVG.getAttribute('width'));

  var effH = realheight - (2*16) - 1;
  var effW = realwidth - (2*16) - 1;

  sbVertBackground.setAttribute("height", realheight);
  sbVertBackground.setAttribute("x", scrollableObject.offsetLeft+realwidth+scrollableObject.marginRight+scrollableObject.marginRight3);
  sbVertBackground.setAttribute("y", scrollableObject.offsetTop);
  sbHorzBackground.setAttribute("width", realwidth);
  sbHorzBackground.setAttribute("x", scrollableObject.offsetLeft);
  sbHorzBackground.setAttribute("y", scrollableObject.offsetTop+realheight+scrollableObject.marginBottom);

  sbUpButton.g.setAttribute("transform", "translate("+(scrollableObject.offsetLeft+realwidth+scrollableObject.marginRight+scrollableObject.marginRight3)+" "+scrollableObject.offsetTop+")");
  sbLeftButton.g.setAttribute("transform", "translate("+scrollableObject.offsetLeft+" "+(scrollableObject.offsetTop+realheight+scrollableObject.marginBottom)+")");

  sbDownButton.g.setAttribute("transform", "translate("+(scrollableObject.offsetLeft+realwidth+scrollableObject.marginRight+scrollableObject.marginRight3)+" "+(scrollableObject.offsetTop+realheight-16)+")");
  sbRightButton.g.setAttribute("transform", "translate("+(scrollableObject.offsetLeft+realwidth+scrollableObject.marginRight3+scrollableObject.marginRight)+" "+(scrollableObject.offsetTop+realheight+scrollableObject.marginBottom)+")");

  sbYScale = effH*s/scrollheight;
  sbXScale = effW*s/scrollwidth;
  var cy = -Math.round(ct.y*sbYScale);
  var cx = -Math.round(ct.x*sbXScale);
  
  var ch = realheight*sbYScale;
  var cw = realwidth*sbXScale;

  if(ch > effH)
    ch = effH;
  if(cy + ch > effH)
    cy = effH - ch;
  if(cy < 0)
    cy = 0;

  if(cw > effW)
    cw = effW;
  if(cx + cw > effW)
    cx = effW - cw;
  if(cx < 0)
    cx = 0;

  if(ch == effH)
    sbVert.setAttribute("display", "none");
  else
    sbVert.setAttribute("display", "inherit");
  
  if(cw == effW)
    sbHorz.setAttribute("display", "none");
  else
    sbHorz.setAttribute("display", "inherit");
          
  sbVertSliderX = scrollableObject.offsetLeft+realwidth+scrollableObject.marginRight+scrollableObject.marginRight3;
  sbHorzSliderY = scrollableObject.offsetTop+realheight+scrollableObject.marginBottom;

  sbVertSlider.g.setAttribute("transform", "translate("+(scrollableObject.offsetLeft+realwidth+scrollableObject.marginRight+scrollableObject.marginRight3)+" "+(scrollableObject.offsetTop+16)+")");
  sbButtonSetSize(sbVertSlider, 15, ch);
  
  sbHorzSlider.g.setAttribute("transform", "translate("+(16+scrollableObject.offsetLeft)+" "+(scrollableObject.offsetTop+realheight+scrollableObject.marginBottom)+")");

  // cw can be too little
  cw += scrollableObject.marginRight + scrollableObject.marginRight2;
  
  sbButtonSetSize(sbHorzSlider, cw, 15);

  sbUpButton.g.addEventListener("mousedown", sbUnitUp, false);
  sbLeftButton.g.addEventListener("mousedown", sbUnitLeft, false);
  sbDownButton.g.addEventListener("mousedown", sbUnitDown, false);
  sbRightButton.g.addEventListener("mousedown", sbUnitRight, false);
  sbVertSlider.g.addEventListener("mousedown", sbStartDragY, false);
  sbHorzSlider.g.addEventListener("mousedown", sbStartDragX, false);

  sbUpButton.g.addEventListener("mouseup", sbStopScroll, false);
  sbDownButton.g.addEventListener("mouseup", sbStopScroll, false);
  sbLeftButton.g.addEventListener("mouseup", sbStopScroll, false);
  sbRightButton.g.addEventListener("mouseup", sbStopScroll, false);
  scrollableObject.mainSVG.addEventListener("mouseup", sbStopScroll, false);
  scrollableObject.mainSVG.addEventListener("mouseup", sbEndDrag, false);
  sbVertSlider.g.addEventListener("mouseup", sbEndDrag, false);
  sbHorzSlider.g.addEventListener("mouseup", sbEndDrag, false);

  sbVertBackground.addEventListener("click", sbScrollPageY, false);
  sbHorzBackground.addEventListener("click", sbScrollPageX, false);

  sbVertBackground.addEventListener("mousemove", sbDoDrag, false);
  sbHorzBackground.addEventListener("mousemove", sbDoDrag, false);
  sbVertSlider.g.addEventListener("mousemove", sbDoDrag, false);
  sbHorzSlider.g.addEventListener("mousemove", sbDoDrag, false);

  var h = scrollableObject.outerSVG.getAttribute('height');
  sbVirtualHeight = (h-(2*16)-1)/sbYScale - h;
}
function sbGetScrollY()
{
  if(sbVert.getAttribute("display") == "none")
    return 0;
  else
    return sbScrollY;
}
function sbGetScrollX()
{
  if(sbHorz.getAttribute("display") == "none")
    return 0;
  else
    return sbScrollX;
}
function sbSetScrollY(y)
{
  if(y < 0)
    y = 0;
  else 
  if(y > sbVirtualHeight)
    y = sbVirtualHeight;
  sbScrollY = y;
  scrollableObject.innerSVG.setAttribute('y', -sbScrollY);
  scrollableObject.vertSVG.setAttribute('y', -sbScrollY+aScrollableObject.offsetTop);

  var nts = 'translate('+sbVertSliderX+','+(scrollableObject.offsetTop+16+sbScrollY*sbYScale)+')';
  sbVertSlider.g.setAttribute('transform',nts);
}
function sbSetScrollX(x)
{
  var remainingwidth = scrollableObject.innerSVG.getAttribute('width') - Math.abs(scrollableObject.innerSVG.getAttribute('x')) - scrollableObject.outerSVG.getAttribute('width');
  var currentx = scrollableObject.innerSVG.getAttribute('x');
  var movement = x + (currentx - 0);
  if (x < 0)
    x = 0;
  else
  if (movement > remainingwidth ||
      remainingwidth < 0)
    x = scrollableObject.innerSVG.getAttribute('width') - scrollableObject.outerSVG.getAttribute('width');
  sbScrollX = x;
  scrollableObject.innerSVG.setAttribute('x', -sbScrollX);
  scrollableObject.horzSVG.setAttribute('x', -sbScrollX+aScrollableObject.offsetLeft+aScrollableObject.offsetLeft2);

  var nts = "translate("+(scrollableObject.offsetLeft+16+(sbScrollX*sbXScale))+","+sbHorzSliderY+")";
  sbHorzSlider.g.setAttribute('transform',nts);
}
function sbStartDragY(evt)
{
  sbDragY = true;
  sbDragX = false;
  sbLastY = evt.clientY;
  sbScrollY = -scrollableObject.innerSVG.getAttribute('y');

  if (scrollableObject.mainSVG != null)
    scrollableObject.mainSVG.style.setProperty('pointer-events', 'all');
}
function sbStartDragX(evt)
{
  sbDragY = false;
  sbDragX = true;
  sbLastX = evt.clientX;
  sbScrollX = -scrollableObject.innerSVG.getAttribute('x');
  
  if (scrollableObject.mainSVG != null)
    scrollableObject.mainSVG.style.setProperty('pointer-events', 'all');
}
function sbEndDrag(evt)
{
  sbDragX = false;
  sbDragY = false;
  
  if (scrollableObject.mainSVG != null)
    scrollableObject.mainSVG.style.setProperty('pointer-events', 'none');
}
function sbDoDrag(evt)
{
  if(sbDragY)
  {
    var y = evt.clientY;
    var ry = Math.round((y - sbLastY)/sbYScale);
    if(ry == 0)
      return;

    sbLastY += ry*sbYScale;
    sbSetScrollY(sbScrollY + ry);
  }
  else
  if(sbDragX)
  {
    var x = evt.clientX;
    var rx = Math.round((x - sbLastX)/sbXScale);
    if(rx == 0)
      return;

    sbLastX += rx*sbXScale;
    sbSetScrollX(sbScrollX + rx);
  }
}
function sbUnitDown(evt)
{
  sbSetScrollY(-scrollableObject.innerSVG.getAttribute('y')+scrollableObject.vertDelta);
  if (sbTimeout)
    clearTimeout(sbTimeout);
  sbTimeout = setTimeout("sbUnitDown()", (evt?200:50));
}
function sbUnitRight(evt)
{
 sbSetScrollX(-scrollableObject.innerSVG.getAttribute('x')+scrollableObject.horzDelta);
 if (sbTimeout)
   clearTimeout(sbTimeout);
 sbTimeout = setTimeout("sbUnitRight()", (evt?200:50));
}
function sbUnitUp(evt)
{
  sbSetScrollY(-scrollableObject.innerSVG.getAttribute('y')-scrollableObject.vertDelta);
  if(sbTimeout)
    clearTimeout(sbTimeout);
  sbTimeout = setTimeout("sbUnitUp()", (evt?200:50));
}
function sbUnitLeft(evt)
{
  sbSetScrollX(-scrollableObject.innerSVG.getAttribute('x')-scrollableObject.horzDelta);
  if(sbTimeout)
    clearTimeout(sbTimeout);
  sbTimeout = setTimeout( "sbUnitLeft()", (evt?200:50));
}
function sbScrollPageY(evt)
{
  var y = evt.clientY;
  var ty = parseFloat(scrollableObject.innerSVG.getAttribute('y'));
  var sy = parseFloat(scrollableObject.outerSVG.getAttribute('y')) + (16-Math.round(ty*sbYScale));
  var dy = parseFloat(scrollableObject.outerSVG.getAttribute('height'));
  if(y <= sy)
    dy = -dy;
  sbSetScrollY((-ty)+dy);
}
function sbScrollPageX(evt)
{
  var x = evt.clientX;
  var tx = parseFloat(scrollableObject.innerSVG.getAttribute('x'));
  var sx = parseFloat(scrollableObject.outerSVG.getAttribute('x')) + (16-Math.round(tx*sbXScale));
  var dx = parseFloat(scrollableObject.outerSVG.getAttribute('width'));
  if(x <= sx)
    dx = -dx;
  sbSetScrollX((-tx)+dx);
}
function sbStopScroll()
{
  if(sbTimeout)
    clearTimeout(sbTimeout);
  sbTimeout = null;
}

