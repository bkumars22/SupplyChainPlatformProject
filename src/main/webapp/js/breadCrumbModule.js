/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
var BreadCrumbModule = ( function() {
	var DOM = {};

	//Cache DOM elements
	function cacheDom() {		  
	  DOM.contentFrame = document.getElementById('contentFrame');
	  return DOM;
	}
	
	function getDom(){
		return DOM;
	}
	
	//Bind events
	function bindEvents() {
		DOM.contentFrame.onload = contentFrameLoaded;
		return DOM;
	}
	
	
	function loadBreadCrumb(lastSegment, innerDoc) {
	if (lastSegment == 'searchExceptionById.do') {
			lastSegment = "searchExceptionRequest.do";
	}

	$.ajax({
	 		  type: "GET",
	 		  url: "mcm/api/bread_crumb",
	 		  contentType: "application/json; charset=utf-8",
	 		  dataType: "json",
	 		  data:{ url : lastSegment },
	 		  success: function(response) {  
	 			var pObj = response;
	 			var pageItems = pObj.pageItems;
	 			
	 			//store pageItems in mcmApp
	 			mcmApp.pageItems = pObj.pageItems;
	 			
	 			var breadList = document.createElement('ul');
	 			breadList.className = 'eto-breadcrumbs';
	 			innerDoc.body.insertBefore(breadList, innerDoc.body.firstChild);	
	 			var homePageItem = { url :'welcome', name : 'Home' };
	 			var homeLiItem = getLiElement(homePageItem);	
	 			homeLiItem.onclick = homeItemClicked;
	 			breadList.appendChild(homeLiItem);
	 			for (var i=0; i < pageItems.length; i++) {
	 			      var pageItem = pageItems[i];
	 			      var liItem =   getLiElement(pageItem);
	 			      breadList.appendChild(liItem); 	  
	 			}
				if (pageItems.length > 0) {
					document.title = pageItems[pageItems.length-1].name;
				}
	          }
	 	 });
	}
	
	function contentFrameLoaded() {		  
		  try {
			  var pageUrl = DOM.contentFrame.getAttribute("src");
			  var parts = pageUrl.split('/');
			  var lastSegment = parts.pop() || parts.pop(); 	  		 	  
			  var innerDoc = DOM.contentFrame.contentDocument || DOM.contentFrame.contentWindow.document;
			  if(lastSegment!='welcome' && !mcmApp.removeBreadcrumbs){
				  loadBreadCrumb(lastSegment, innerDoc);
			  }
			  if(mcmApp.removeBreadcrumbs){
				  mcmApp.removeBreadcrumbs = false;
			  }
		  }
		  catch (e) {
			  console.error(e);
		  }
	}

	
	function homeItemClicked(e) {
		e.preventDefault();
		DOM.contentFrame.setAttribute("src", 'welcome');
		return DOM;
	}
	
	function getLiElement(pageItem) {
		var liElement = document.createElement('li');
		var isParentMenu = false;
		if (!pageItem.url) {
			pageItem.url = "#";
			isParentMenu = true;
		}
		var aTag = getAHrefElem(pageItem);
		liElement.appendChild(aTag);
		if(isParentMenu) {
			var spanElement = document.createElement('span');
			spanElement.className = 'eto-dropdown';
			spanElement.appendChild(getButtonDropdown());
			liElement.appendChild(spanElement);
			liElement.onclick = function(e) { openHeaderMenu(e,pageItem.name); };
		}
		return liElement;
	}

	function getButtonDropdown() {
		var buttonElement = document.createElement('button');
		buttonElement.className = 'eto-dropdown__toggle';
		buttonElement.setAttribute('type','button');

		var element = document.createElement('i');
		element.className = 'md-icon md-icon--sm';
		element.innerHTML = 'keyboard_arrow_down';

		buttonElement.appendChild(element);
		return buttonElement;
	}

	function openHeaderMenu(event,filterText) {
		console.log('filterText value is : ' + filterText);
		var header;
		if(document.querySelector(".eto-header") != null)
			header = new eto.Header({ el: document.querySelector(".eto-header") });
		if(header) {
			header.filterMenuItems(filterText, true);
			if(event) {
				event.stopPropagation();
			}
		}
	}
	
	function getAHrefElem(pageItem) {
		var aTag = document.createElement('a');
        aTag.href=pageItem.url;
        aTag.text=pageItem.name;
        lastUrl = pageItem.url;
        return aTag;
	}
	
	function init() {
		cacheDom();
		bindEvents();
	}
	
	//export public methods
	 return {
		    init: init,
		    cacheDom: cacheDom,
		    bindEvents: bindEvents,
		    homeItemClicked: homeItemClicked,
		    getLiElem : getLiElement,
		    getAHrefElem: getAHrefElem,
		    loadBreadCrumb : loadBreadCrumb,
		    getDom : getDom
	};
	
}());
