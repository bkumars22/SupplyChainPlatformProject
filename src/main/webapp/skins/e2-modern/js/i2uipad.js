/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
i2uiPad.width='160px';i2uiPad.bottomHeight='10px';i2uiPad.delay1=200; // millisec before starting to repeat scrolling.
i2uiPad.delay2=10;  // millisec between each scrolling.
i2uiPad.barBackground        ="/scrollbar_background.jpg";i2uiPad.cornerIcon           ="/scrollbar_corner.png";i2uiPad.upUpIcon             ="/scrollbar_up.png";i2uiPad.downUpIcon           ="/scrollbar_up_active.png";i2uiPad.noUpIcon             ="/scrollbar_up_disabled.png";i2uiPad.upDownIcon           ="/scrollbar_down.png";i2uiPad.downDownIcon         ="/scrollbar_down_active.png";i2uiPad.noDownIcon           ="/scrollbar_down_disabled.png";i2uiPad.topVerticalStub      ="/scrollbar_vert_slider_top.png";i2uiPad.middleVerticalStub   ="/scrollbar_vert_slider_middle.png";i2uiPad.bottomVerticalStub   ="/scrollbar_vert_slider_bottom.png";i2uiPad.upLeftIcon           ="/scrollbar_left.png";i2uiPad.downLeftIcon         ="/scrollbar_left_active.png";i2uiPad.noLeftIcon           ="/scrollbar_left_disabled.png";i2uiPad.upRightIcon          ="/scrollbar_right.png";i2uiPad.downRightIcon        ="/scrollbar_right_active.png";i2uiPad.noRightIcon          ="/scrollbar_right_disabled.png";i2uiPad.leftHorizontalStub   ="/scrollbar_horz_slider_left.png";i2uiPad.middleHorizontalStub ="/scrollbar_horz_slider_middle.png";i2uiPad.rightHorizontalStub  ="/scrollbar_horz_slider_right.png";i2uiPad.instances = new Array();i2uiPad.count = 0;i2uiPad.lastHighlightedPadItem = null;i2uiPad.lastHighlightedPadItemName = null;i2uiPad.lastHighlightedInstancePadItem = null;i2uiPad.lastHighlightedInstancePadItemName = null;function i2uiPad(obj,title,editaction){i2uiPad.instances[i2uiPad.count] = this;var swidth,sheight;var visible='inherit';var x=10, y=10;var titleHeight = i2uiPadTitleHeight;var titleHeight2 = titleHeight-2;this.intervalcount = 0;this.refreshid = 0;this.index = i2uiPad.count;this.Vcoef=0.000001;swidth=obj.clip.width;sheight=obj.clip.height;visible=obj.visibility;var titlecolor;if (i2uiPad.count > 0){titlecolor = i2uiApplicationPadTitleBgcolor;}
else{titlecolor = i2uiSolutionPadTitleBgcolor;this.hbar = 'never';this.vbar = 'never';}
var titleWidth = swidth - 2;if (editaction != "")
editaction = '<a href="'+editaction+'"><img border=0 src='+i2uiImageDirectory+'/cstmz_actv.png></a>';var s=
"<layer name=i2uipad>"+
'<layer z-index=1 name=title height='+titleHeight+' width='+i2uiPad.width+' bgcolor='+i2uiPadBorderBgcolor+' visibility=hide>'+
'<layer name=text top=1 left=1 height='+titleHeight2+' width='+titleWidth+' bgcolor='+titlecolor+'>'+
'&nbsp;'+
'<a href="javascript:i2uiPad.instances['+i2uiPad.count+'].toggleContents()">'+
'<img border=0 name=toggler src='+i2uiImageDirectory+'/container_collapse.png>'+
'</a>'+
'&nbsp;'+
"<FONT POINT-SIZE='9' FACE='verdana,sans-serif'><B>"+
title+
'</B></FONT>'+
'&nbsp;'+
editaction+
'</layer>'+
'</layer>'+
"<layer z-index=1 name=h width="+swidth+" visibility=hide>"+
'<layer name=left top=0 left=0 bgcolor='+i2uiPadBorderBgcolor+'>'+
'<img name=left src='+i2uiImageDirectory+i2uiPad.upLeftIcon+'>'+
'</layer>'+
'<layer name=right top=0 bgcolor='+i2uiPadBorderBgcolor+'>'+
'<img name=right src='+i2uiImageDirectory+i2uiPad.upRightIcon+'>'+
'</layer>'+
'<layer name=empty top=0 left=16 background="'+i2uiImageDirectory+i2uiPad.barBackground+'" >'+
'<layer name=rect left=0 top=0>'+
'<layer><img src='+i2uiImageDirectory+i2uiPad.leftHorizontalStub+'></layer>'+
'<layer left=2 height=16 background='+i2uiImageDirectory+i2uiPad.middleHorizontalStub+' ></layer>'+
'<layer><img src='+i2uiImageDirectory+i2uiPad.rightHorizontalStub+'></layer>'+
'</layer>'+
'</layer>'+
"</layer>"+
"<layer top="+titleHeight+" z-index=1 name=v height="+sheight+" width=16 visibility=hide>"+
'<layer name=up width=16 top=0 left=0 bgcolor='+i2uiPadBorderBgcolor+'>'+
'<img name=up src='+i2uiImageDirectory+i2uiPad.upUpIcon+'>'+
'<layer></layer>'+
'</layer>'+
'<layer name=down width=16 top=0 left=0 bgcolor='+i2uiPadBorderBgcolor+'>'+
'<img name=down src='+i2uiImageDirectory+i2uiPad.upDownIcon+'>'+
'</layer>'+
'<layer name=empty left=0 top=16 background="'+i2uiImageDirectory+i2uiPad.barBackground+'">'+
'<layer name=rect top=0>'+
'<layer><img src='+i2uiImageDirectory+i2uiPad.topVerticalStub+'></layer>'+
'<layer top=2 width=16 background='+i2uiImageDirectory+i2uiPad.middleVerticalStub+' ></layer>'+
'<layer><img src='+i2uiImageDirectory+i2uiPad.bottomVerticalStub+'></layer>'+
'</layer>'+
'</layer>'+
"</layer> "+
'<layer z-index=1 name=resizer visibility=hide><img src="'+i2uiImageDirectory+i2uiPad.cornerIcon+'"></layer>';s+= '</layer>';document.write(s);var layer=document.layers.i2uipad;layer.left=x;layer.top=y;layer.visibility=visible;if (obj.constructor==Layer){layer.pageX=obj.pageX;layer.pageY=obj.pageY;layer.clip.width=obj.clip.width;layer.clip.height=obj.clip.height+titleHeight;obj.moveBelow(layer.document.layers.title);obj.top += titleHeight;delete layer.document.layers.main;layer.document.layers.main=obj;}
var main=layer.document.layers.main;main.i2uipad=this;this.layer=layer;layer.i2uipad=this;this.main=main;this.contentHeight = 0;main.contentWidth  = i2uiPad.width - 2;main.vbarok = false;main.hbarok = false;var v=layer.document.layers.v;var vempty=v.document.layers.empty;vempty.captureEvents(Event.MOUSEDOWN | Event.MOUSEUP);vempty.onmousedown=i2uiPad.v_rectdown;vempty.onmousemove=i2uiPad.v_rectmove;vempty.onmouseup=i2uiPad.v_rectup;vempty.onmouseout=i2uiPad.v_rectup;var up=v.document.layers.up;up.captureEvents(Event.MOUSEDOWN | Event.MOUSEUP);up.document.images.up.layer=up;up.document.images.up.onmousedown=i2uiPad.v_updown;up.document.images.up.onmouseup=i2uiPad.v_upup;up.document.images.up.onmouseout=i2uiPad.v_upup;var down=v.document.layers.down;down.document.images.down.layer=down;down.captureEvents(Event.MOUSEDOWN | Event.MOUSEUP);down.document.images.down.onmousedown=i2uiPad.v_downdown;down.document.images.down.onmouseup=i2uiPad.v_downup;down.document.images.down.onmouseout=i2uiPad.v_downup;var h=layer.document.layers.h;var hempty=h.document.layers.empty;hempty.captureEvents(Event.MOUSEDOWN | Event.MOUSEUP);hempty.onmousedown=i2uiPad.h_rectdown;hempty.onmousemove=i2uiPad.h_rectmove;hempty.onmouseup=i2uiPad.h_rectup;var left=h.document.layers.left;left.captureEvents(Event.MOUSEDOWN | Event.MOUSEUP);left.document.images.left.layer=left;left.document.images.left.onmousedown=i2uiPad.h_leftdown;left.document.images.left.onmouseup=i2uiPad.h_leftup;left.document.images.left.onmouseout=i2uiPad.h_leftup;var right=h.document.layers.right;right.document.images.right.layer=right;right.captureEvents(Event.MOUSEDOWN | Event.MOUSEUP);right.document.images.right.onmousedown=i2uiPad.h_rightdown;right.document.images.right.onmouseup=i2uiPad.h_rightup;right.document.images.right.onmouseout=i2uiPad.h_rightup;this.refresh();i2uiPad.count++;return this;}
i2uiPad.prototype.vincr=5;i2uiPad.prototype.hincr=5;i2uiPad.prototype.scrollTo=function (x,y) {this.Hupdate(x/this.Hcoef);this.Vupdate(y/this.Vcoef);}
i2uiPad.prototype.scrollBy = function (x,y) {this.Hupdate((this.main.clip.left+x)/this.Hcoef);this.Vupdate((this.main.clip.top+y)/this.Vcoef);}
i2uiPad.prototype.tile=function (){var i = 0;var availableHeight = window.innerHeight - 6;var neededHeight = 0;var openCount = 0;var scrollingCount = 0;for (i=0; i<i2uiPad.count; i++){var main=i2uiPad.instances[i].layer.document.layers.main;var main2=i2uiPad.instances[i].layer;var title=i2uiPad.instances[i].layer.document.layers.title;availableHeight -= title.clip.height;if (main.visibility != 'hide'){if (i == 0){if (main2.contentHeight > 0)
availableHeight -= main2.contentHeight;else
availableHeight -= main.clip.height;}
else{openCount++;if (main2.contentHeight > 0)
neededHeight += main2.contentHeight;else
neededHeight += main.clip.height;if (main.contentWidth > i2uiPad.width - 2)
neededHeight += 16;}}}
if (openCount > 0){var avgHeight = Math.floor(availableHeight / openCount);var maxavg = avgHeight;var used = 0;var spread;for (var j=0; j<5; j++){spread = 0;scrollingCount = 0;used = 0;for (var i=1; i<i2uiPad.count; i++){var main=i2uiPad.instances[i].layer.document.layers.main;if (main.visibility != 'hide'){var main2=i2uiPad.instances[i].layer;maxavg = Math.max(maxavg, main2.contentHeight);if (main2.contentHeight > avgHeight)
scrollingCount++;used += Math.min(avgHeight, main2.contentHeight);}}
if (used <= availableHeight && scrollingCount == 0){break;}
else
if (scrollingCount > 0){var delta = used - availableHeight;spread = Math.floor(delta / scrollingCount);avgHeight -= spread;}
else
if (openCount > 0){var delta = used - availableHeight;spread = Math.floor(delta / openCount);avgHeight -= spread;}
avgHeight = Math.min(avgHeight, maxavg);}
for (i=1; i<i2uiPad.count; i++){var main=i2uiPad.instances[i].layer.document.layers.main;if (main.visibility != 'hide'){var main2=i2uiPad.instances[i].layer;if (main2.contentHeight <= avgHeight){if ((main2.contentHeight > 0 &&main.clip.height != main2.contentHeight) ||main.clip.height != main.document.height){main.clip.height = main2.contentHeight;if (main.vbarok)
main.clip.height+= 16;main2.clip.height = main.clip.height + i2uiPadTitleHeight;i2uiPad.instances[i].intervalcount = 0;setTimeout("i2uiPad.instances["+i+"].refresh()", 250);}
else{main.clip.height = main2.contentHeight;main2.clip.height = main.clip.height + i2uiPadTitleHeight;}}
else{var newHeight = avgHeight - 16;if (main.clip.height != newHeight+16 || main.vbarok){main.clip.height = newHeight;if (main.vbarok)
main.clip.height+= 16;main2.clip.height = main.clip.height + i2uiPadTitleHeight;i2uiPad.instances[i].intervalcount = 0;setTimeout("i2uiPad.instances["+i+"].refresh()", 250);}
else{main.clip.height = newHeight + 16;main2.clip.height = main.clip.height + i2uiPadTitleHeight;}}}}}
var y = i2uiPad.instances[0].layer.top;var x = i2uiPad.instances[0].layer.left;for (i=0; i<i2uiPad.count; i++){var main=i2uiPad.instances[i].layer.document.layers.main;i2uiPad.instances[i].layer.moveTo(x,y);if (i == 0){i2uiPad.instances[i].layer.clip.height = main.clip.height + i2uiPadTitleHeight;}
if (main.visibility=='hide')
y += i2uiPadTitleHeight;else{y += i2uiPad.instances[i].layer.clip.height;}}}
i2uiPad.prototype.resizeTo=function (x,y){this.layer.clip.height = y;this.layer.clip.width = x;var title=this.layer.document.layers.title;title.clip.width = x;var titletext=this.layer.document.layers.title.document.layers.text;titletext.clip.width = x-2;this.refresh();this.tile();}
i2uiPad.prototype.resizeBy=function (x,y){this.layer.clip.height += y;this.layer.clip.width  += x;var title=this.layer.document.layers.title;title.clip.width  += x;var titletext=this.layer.document.layers.title.document.layers.text;titletext.clip.width  += x;this.intervalcount = 0;this.refresh();this.tile();}
i2uiPad.prototype.moveTo=function (x,y){this.layer.moveTo(x,y);this.refresh();}
i2uiPad.prototype.placeOnTop=function (){this.layer.zIndex=99;this.refresh();}
i2uiPad.prototype.toggleContents=function (){var obj=this.layer.document.layers.main;var img=this.layer.document.layers.title.document.layers.text.document.images.toggler;if (obj.visibility=='hide'){obj.visibility='inherit';img.src=i2uiImageDirectory+"/container_collapse.png";if (this.vbar != 'never')
this.vbar = 'auto';if (this.hbar != 'never')
this.hbar = 'auto';}
else{obj.visibility='hide';img.src=i2uiImageDirectory+"/container_expand.png";if (this.vbar != 'never')
this.vbar = 'hide';if (this.hbar != 'never')
this.hbar = 'hide';}
this.tile();this.intervalcount = 0;setTimeout("i2uiPad.instances["+this.index+"].refresh()", 250);}
i2uiPad.prototype.refresh=function (){var main=this.layer.document.layers.main;this.intervalcount++;main.clip.width=this.layer.clip.width;var vbarok=(this.vbar=='show')||(main.contentWidth>i2uiPad.width && this.layer.contentHeight-16>main.clip.height+2)||(main.contentWidth<i2uiPad.width && this.layer.contentHeight>main.clip.height+2);var hbarok=(this.hbar=='show')||(vbarok && main.contentWidth>main.clip.width+16)||(!vbarok && main.contentWidth>main.clip.width);if (this.vbar=='hide'||this.vbar=='never') vbarok=false;if (this.hbar=='hide'||this.hbar=='never') hbarok=false;main.vbarok = vbarok;main.hbarok = hbarok;var v=this.layer.document.layers.v;var h=this.layer.document.layers.h;var title=this.layer.document.layers.title;title.visibility='inherit';if (this.layer.contentHeight > 0 && !vbarok){v.clip.height=this.layer.contentHeight-title.clip.height;}
else{v.clip.height=this.layer.clip.height-title.clip.height;}
h.clip.width=this.layer.clip.width;if (vbarok){main.clip.width-=16;h.clip.width=main.clip.width;v.visibility='inherit';}
else{v.visibility='hide';}
if (hbarok){main.clip.height=this.layer.clip.height-16-title.clip.height;v.clip.height=main.clip.height;h.visibility='inherit';}
else{h.visibility='hide';}
if (vbarok && hbarok){var corner=this.layer.document.layers.resizer;corner.left=main.clip.width;corner.top=main.clip.height+title.clip.height;corner.visibility='inherit';}
else{this.layer.document.layers.resizer.visibility='hide';}
var empty=v.document.layers.empty;var up=v.document.layers.up;var down=v.document.layers.down;var rect=empty.document.layers.rect;v.left=this.layer.clip.width-v.clip.width;down.top=v.clip.height-down.clip.height;empty.top=up.clip.height;empty.clip.height=down.top-empty.top;if (this.layer.contentHeight > 0)
this.Vcoef=this.layer.contentHeight/empty.clip.height;else
this.Vcoef=main.document.height/empty.clip.height;if (this.Vcoef==0)
this.Vcoef=0.000001;this.Vupdate(main.clip.top/this.Vcoef);var bCompute = false;if (this.layer.contentHeight > 0){if (this.layer.contentHeight<=main.clip.height){bCompute = true;}}
else
if (main.document.height<=main.clip.height){bCompute = true;}
var barHeight = 0;if (bCompute){barHeight=empty.clip.height;up.document.images.up.src=i2uiImageDirectory+i2uiPad.noUpIcon;down.document.images.down.src=i2uiImageDirectory+i2uiPad.noDownIcon;v.noscroll=true;}
else{if (this.layer.contentHeight > 0)
barHeight=main.clip.height*empty.clip.height/this.layer.contentHeight;else
barHeight=main.clip.height*empty.clip.height/main.document.height;if (up.document.images.up.src.indexOf(i2uiImageDirectory+i2uiPad.noUpIcon)!=-1)
up.document.images.up.src=i2uiImageDirectory+i2uiPad.upUpIcon;if (down.document.images.down.src.indexOf(i2uiImageDirectory+i2uiPad.noDownIcon)!=-1)
down.document.images.down.src=i2uiImageDirectory+i2uiPad.upDownIcon;v.noscroll=false;}
if (barHeight > 0){rect.clip.height=barHeight;rect.document.layers[0].top=0;rect.document.layers[1].top=2;rect.document.layers[1].clip.height=barHeight-4;rect.document.layers[2].top=barHeight-2;}
var empty=h.document.layers.empty;var left=h.document.layers.left;var right=h.document.layers.right;var rect=empty.document.layers.rect;h.top=this.layer.clip.height-h.clip.height;right.left=h.clip.width-left.clip.width;empty.left=left.clip.width;empty.clip.width=right.left-empty.left;this.Hcoef=main.contentWidth/empty.clip.width;if (this.Hcoef==0)
this.Hcoef=0.000001;this.Hupdate(main.clip.left/this.Hcoef);if (main.contentWidth<=main.clip.width){var barWidth=empty.clip.width;left.document.images.left.src=i2uiImageDirectory+i2uiPad.noLeftIcon;right.document.images.right.src=i2uiImageDirectory+i2uiPad.noRightIcon;h.noscroll=true;}
else{var barWidth=main.clip.width*empty.clip.width/main.contentWidth;if (left.document.images.left.src==i2uiImageDirectory+i2uiPad.noLeftIcon)
left.document.images.left.src=i2uiImageDirectory+i2uiPad.upLeftIcon;if (right.document.images.right.src==i2uiImageDirectory+i2uiPad.noRightIcon)
right.document.images.right.src=i2uiImageDirectory+i2uiPad.upRightIcon;h.noscroll=false;}
rect.clip.width=barWidth;rect.document.layers[0].left=0;rect.document.layers[1].left=2;rect.document.layers[1].clip.width=barWidth-4;rect.document.layers[2].left=barWidth-2;if (this.intervalcount <= 2)
setTimeout("i2uiPad.instances["+this.index+"].refresh()", 250);}
i2uiPad.v_rectdown= function (e){var empty=this;empty.captureEvents(Event.MOUSEMOVE | Event.MOUSEOUT);var rect=empty.document.layers.rect;if (e.target==empty.document){empty.parentLayer.parentLayer.i2uipad.Vupdate(e.layerY);}
rect.curY=e.pageY-rect.top;return false;}
i2uiPad.v_rectup= function (){this.releaseEvents(Event.MOUSEMOVE | Event.MOUSEOUT);return true;}
i2uiPad.v_rectmove= function(e){var empty=this;var rect=empty.document.layers.rect;var newpos=0;if (e.target==empty.document){newpos=e.layerY;}
else{newpos=e.pageY-rect.curY;}
empty.parentLayer.parentLayer.i2uipad.Vupdate(newpos);return false;}
i2uiPad.prototype.Vupdate=function (y){var empty=this.layer.document.layers.v.document.layers.empty
var rect=empty.document.layers.rect;if (y < 0)
y = 0;if (y > empty.clip.height-rect.clip.height)
y = empty.clip.height - rect.clip.height;var offset=Math.max(0,Math.floor(y*this.Vcoef));var sh=this.main.clip.height;if (offset < 2000)
this.main.clip.top=offset;offset -= i2uiPadTitleHeight;if (offset < 2000)
this.main.top=-offset;this.main.clip.height=sh;this.layer.document.layers.v.document.layers.empty.document.layers.rect.top=y;rect.top=y;this.pageYOffset=offset;}
i2uiPad.v_updown=function (){if (this.layer.parentLayer.noscroll)
return false;var that=this.layer;var rect=that.parentLayer.document.layers.empty.document.layers.rect;this.src=i2uiImageDirectory+i2uiPad.downUpIcon;clearTimeout(i2uiPad.timer1);clearInterval(i2uiPad.timer2);i2uiPad.upinterval(rect);i2uiPad.timer1=setTimeout(i2uiPad.uptimeout,i2uiPad.delay1,rect);that.captureEvents(Event.MOUSEMOVE | Event.MOUSEOUT);return false;}
i2uiPad.uptimeout= function (rect){i2uiPad.timer2=setInterval(i2uiPad.upinterval,i2uiPad.delay2,rect);}
i2uiPad.upinterval = function (rect){var thePad=rect.parentLayer.parentLayer.parentLayer.i2uipad;thePad.Vupdate(rect.top - thePad.vincr);}
i2uiPad.v_upup= function (){if (this.layer.parentLayer.noscroll)
return false;this.src=i2uiImageDirectory+i2uiPad.upUpIcon;clearTimeout(i2uiPad.timer1);clearInterval(i2uiPad.timer2);this.layer.releaseEvents(Event.MOUSEMOVE | Event.MOUSEOUT);return false;}
i2uiPad.v_downdown=function (){if (this.layer.parentLayer.noscroll)
return false;var that=this.layer;var rect=that.parentLayer.document.layers.empty.document.layers.rect;this.src=i2uiImageDirectory+i2uiPad.downDownIcon;clearTimeout(i2uiPad.timer1);clearInterval(i2uiPad.timer2);i2uiPad.downinterval(rect);i2uiPad.timer1=setTimeout(i2uiPad.downtimeout,i2uiPad.delay1,rect);that.captureEvents(Event.MOUSEMOVE | Event.MOUSEOUT);return false;}
i2uiPad.downtimeout= function (rect){i2uiPad.timer2=setInterval(i2uiPad.downinterval,i2uiPad.delay2,rect);}
i2uiPad.downinterval= function (rect){var thePad=rect.parentLayer.parentLayer.parentLayer.i2uipad;thePad.Vupdate(rect.top + thePad.vincr);}
i2uiPad.v_downup= function (){if (this.layer.parentLayer.noscroll)
return false;this.src=i2uiImageDirectory+i2uiPad.upDownIcon;clearTimeout(i2uiPad.timer1)
clearTimeout(i2uiPad.timer2)
this.layer.releaseEvents(Event.MOUSEMOVE | Event.MOUSEOUT);return false;}
i2uiPad.h_rectdown= function (e){var empty=this;empty.captureEvents(Event.MOUSEMOVE);var rect=empty.document.layers.rect;if (e.target==empty.document){empty.parentLayer.parentLayer.i2uipad.Hupdate(e.layerX);}
rect.curX=e.pageX-rect.left;return true;}
i2uiPad.h_rectup= function (){this.releaseEvents(Event.MOUSEMOVE);return true;}
i2uiPad.h_rectmove= function(e){var empty=this;var rect=empty.document.layers.rect;var newpos=0;if (e.target==empty.document){newpos=e.layerX;}
else{newpos=e.pageX-rect.curX;}
empty.parentLayer.parentLayer.i2uipad.Hupdate(newpos);return false;}
i2uiPad.prototype.Hupdate=function (x){var empty=this.layer.document.layers.h.document.layers.empty
var rect=empty.document.layers.rect;if (x < 0)
x=0;if (x > empty.clip.width - rect.clip.width)
x=empty.clip.width-rect.clip.width;var offset=Math.round(x*this.Hcoef);var sh=this.main.clip.width;this.main.clip.left=offset;this.main.left=-offset;this.main.clip.width=sh;rect.left=x;this.pageXOffset=offset;}
i2uiPad.h_leftdown=function (){if (this.layer.parentLayer.noscroll)
return false;var that=this.layer;var rect=that.parentLayer.document.layers.empty.document.layers.rect;this.src=i2uiImageDirectory+i2uiPad.downLeftIcon;clearTimeout(i2uiPad.timer1);clearInterval(i2uiPad.timer2);i2uiPad.leftinterval(rect);i2uiPad.timer1=setTimeout(i2uiPad.lefttimeout,i2uiPad.delay1,rect);that.captureEvents(Event.MOUSEMOVE | Event.MOUSEOUT);return false;}
i2uiPad.lefttimeout= function (rect){i2uiPad.timer2=setInterval(i2uiPad.leftinterval,i2uiPad.delay2,rect);}
i2uiPad.leftinterval = function (rect){var thePad=rect.parentLayer.parentLayer.parentLayer.i2uipad;thePad.Hupdate(rect.left - thePad.hincr)}
i2uiPad.h_leftup= function (){if (this.layer.parentLayer.noscroll)
return false;this.src=i2uiImageDirectory+i2uiPad.upLeftIcon;clearTimeout(i2uiPad.timer1);clearInterval(i2uiPad.timer2);this.layer.releaseEvents(Event.MOUSEMOVE | Event.MOUSEOUT);return false;}
i2uiPad.h_rightdown=function (){if (this.layer.parentLayer.noscroll)
return false;var that=this.layer;var rect=that.parentLayer.document.layers.empty.document.layers.rect;this.src=i2uiImageDirectory+i2uiPad.downRightIcon;clearTimeout(i2uiPad.timer1);clearInterval(i2uiPad.timer2);i2uiPad.rightinterval(rect);i2uiPad.timer1=setTimeout(i2uiPad.righttimeout,i2uiPad.delay1,rect);that.captureEvents(Event.MOUSEMOVE | Event.MOUSEOUT);return false;}
i2uiPad.righttimeout= function (rect){i2uiPad.timer2=setInterval(i2uiPad.rightinterval,i2uiPad.delay2,rect);}
i2uiPad.rightinterval= function (rect){var thePad=rect.parentLayer.parentLayer.parentLayer.i2uipad;thePad.Hupdate(rect.left + thePad.hincr);}
i2uiPad.h_rightup= function (){if (this.layer.parentLayer.noscroll)
return false;this.src=i2uiImageDirectory+i2uiPad.upRightIcon;clearTimeout(i2uiPad.timer1)
clearTimeout(i2uiPad.timer2)
this.layer.releaseEvents(Event.MOUSEMOVE | Event.MOUSEOUT);return false;}
function i2uiManagePadScroller(padname){}
function i2uiTilePads(){var summedApplContentHeight = 0;var openCount = 0;var scrollingCount = 0;var obj;var scroller_obj;var content_obj;var avgHeight;var navareaTop = i2uiComputeTop('i2uinavarea');var navareaHeight = getAdjustedHeight() - navareaTop - i2uiPad.bottomHeight;var pad0height = 0;if (i2uiPad.count == 1){if (navareaTop > 10){navareaHeight -= 32;}
content_obj = document.getElementById(i2uiPad.instances[0]);if (content_obj != null){scroller_obj = document.getElementById(i2uiPad.instances[0]+"_scroller");var content_obj_height = content_obj.clientHeight;if (content_obj_height >= navareaHeight) {scroller_obj.style.height = navareaHeight + 'px';}
else {scroller_obj.style.height = '1px';scroller_obj.style.height = 'auto';}}
i2uiManagePadScroller(i2uiPad.instances[0]);return;}
content_obj = document.getElementById(i2uiPad.instances[0]);if (content_obj != null){pad0height = content_obj.clientHeight;if (content_obj.clientWidth > i2uiPad.width){i2uiManagePadScroller(i2uiPad.instances[0]);scroller_obj = document.getElementById(i2uiPad.instances[0]+"_scroller");if (scroller_obj != null){if (content_obj.clientHeight == scroller_obj.clientHeight &&content_obj.clientHeight > 0){scroller_obj.style.height = parseInt(content_obj.scrollHeight) + 'px';}
pad0height = scroller_obj.clientHeight;}}}
if (navareaTop > 10){navareaHeight -= 16;}
for (var i=1; i<i2uiPad.count; i++){content_obj = document.getElementById(i2uiPad.instances[i]);if (content_obj != null){scroller_obj = document.getElementById(i2uiPad.instances[i]+"_scroller");if (scroller_obj != null &&scroller_obj.clientHeight != content_obj.clientHeight){scrollingCount++;}
summedApplContentHeight += content_obj.clientHeight;if (scroller_obj != null &&content_obj.clientHeight > 0 &&scroller_obj.clientWidth != scroller_obj.scrollWidth &&scroller_obj.scrollWidth > i2uiPad.width){var delta = 16;  //LPM should be computed
summedApplContentHeight += delta;if (content_obj.clientHeight == scroller_obj.clientHeight){scroller_obj.style.height = parseInt(content_obj.scrollHeight + delta) + 'px';}}
if (content_obj.clientHeight > 0)
openCount++;}}
var titleHeight = document.getElementById("PAD_"+i2uiPad.instances[0]).clientHeight - pad0height;var summedTitleHeight = i2uiPad.count * titleHeight;var availContentHeight = navareaHeight - pad0height - summedTitleHeight;if (availContentHeight > summedApplContentHeight){for (var i=1; i<i2uiPad.count; i++){content_obj = document.getElementById(i2uiPad.instances[i]);scroller_obj = document.getElementById(i2uiPad.instances[i]+"_scroller");if (content_obj != null && scroller_obj != null){if (scroller_obj.clientHeight < content_obj.clientHeight){scroller_obj.style.height = content_obj.scrollHeight + 'px';}
else{scroller_obj.style.height = content_obj.clientHeight + 'px';}
i2uiManagePadScroller(i2uiPad.instances[i]);}}
if (scrollingCount == 0)
return;}
avgHeight = Math.floor(availContentHeight / openCount);var maxavg = avgHeight;var used = 0;var spread;for (var j=0; j<5; j++){spread = 0;scrollingCount = 0;used = 0;for (var i=1; i<i2uiPad.count; i++){scroller_obj = document.getElementById(i2uiPad.instances[i]+"_scroller");content_obj = document.getElementById(i2uiPad.instances[i]);maxavg = Math.max(maxavg, content_obj.clientHeight);if (content_obj.clientHeight > avgHeight)
scrollingCount++;used += Math.min(avgHeight, content_obj.clientHeight);}
if (used <= availContentHeight && scrollingCount == 0){break;}
else
if (scrollingCount > 0){var delta = used - availContentHeight;spread = Math.floor(delta / scrollingCount);avgHeight -= spread;}
else
if (openCount > 0){var delta = used - availContentHeight;spread = Math.floor(delta / openCount);avgHeight -= spread;}
avgHeight = Math.min(avgHeight, maxavg);}
for (var i=1; i<i2uiPad.count; i++){scroller_obj = document.getElementById(i2uiPad.instances[i]+"_scroller");if (scroller_obj != null){content_obj = document.getElementById(i2uiPad.instances[i]);if (content_obj.clientHeight >= avgHeight){scroller_obj.style.height = avgHeight + 'px';}
else{if (content_obj.clientHeight > 0 &&scroller_obj.clientWidth != scroller_obj.scrollWidth &&scroller_obj.scrollWidth > i2uiPad.width){scroller_obj.style.height = parseInt(content_obj.scrollHeight + 16) + 'px';}
else if(content_obj.clientHeight > 0){scroller_obj.style.height = content_obj.scrollHeight + 'px';}}}
i2uiManagePadScroller(i2uiPad.instances[i]);}}
function i2uiToggleNavArea(){i2uiToggleItemVisibility('i2uinavarea');}
function i2uiHighlightPadItem(id, padtype){var obj = document.getElementById(id);if (obj != null){obj = obj.parentNode;if (obj != null){if (obj.tagName == 'TR'){if (padtype == null || padtype != "instance"){if (i2uiPad.lastHighlightedPadItem != null)
i2uiPad.lastHighlightedPadItem.id = null;}
else
if (padtype == "instance"){if (i2uiPad.lastHighlightedInstancePadItem != null)
i2uiPad.lastHighlightedInstancePadItem.id = null;}
obj.id = 'applicationHighlightedPadContent';if (padtype == null || padtype != "instance"){i2uiPad.lastHighlightedPadItem = obj;i2uiPad.lastHighlightedPadItemName = id;}
else
if (padtype == "instance"){i2uiPad.lastHighlightedInstancePadItem = obj;i2uiPad.lastHighlightedInstancePadItemName = id;}}}}}
function i2uiCollapsePad(name){var obj = document.getElementById(name);if (obj != null){var parentobj = obj.parentElement;while (parentobj != null && parentobj.tagName != 'TABLE'){parentobj = parentobj.parentElement;}
if (parentobj != null){var imagelist = parentobj.getElementsByTagName("IMG");if (imagelist.length > 0){imagelist[0].src = i2uiImageDirectory+"/container_expand.png";i2uiToggleContent(obj,1,'i2uiTilePads()');}}}
return;}
