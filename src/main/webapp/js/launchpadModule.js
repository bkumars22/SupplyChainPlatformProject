/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
/*
 * This is called when user clicks on a menu link in MTCM in Launchpad enabled mode
 */
function openLauncpadEnabledMenuItem(url) {
	 document.getElementById('contentFrame').setAttribute("src", url);			 
}

var clpSaveFavorite;
var favoriteFolders;
var userUpdatedFavorite = false;
var _addFabModal = new eto.Modal({ el: document.querySelector('#add-fav-modalclp') });
var _selectInput = new eto.SelectInput({ el: document.querySelector('#saveInSelect') });
var _currentFavoriteLabel;
var _saveButtonOnFavoritesModalClicked = false;

var toast = new eto.ToastContainer({
	  el: document.querySelector('#scplatformsgToast')
	});

favoritesDomModify();

function setFavoriteFolderOptions(favoriteFolders) {
	var options = [];
	options.push({ value:-1, label: "Favorites Bar",selected : true});
	
	for (var i=0;i<favoriteFolders.length;i++) {
		var folder =  favoriteFolders[i];
		options.push({value: folder.id, label: folder.text});	
	}
	options.push({value: 0, label: "New Folder"})
	_selectInput.setOptions(options);
}

function addFavorite(saveFavorite, favorites) {
	clpSaveFavorite = saveFavorite;
	favoriteFolders = getFavoriteFolders(favorites);
	setFavoriteFolderOptions(favoriteFolders);		
	_addFabModal.open();	
}

function getFavoriteFolders (favs) {
    var folders = [];
    if(favs.items) {
      favs.items.forEach(function(fav) {
        if(fav.items) {
          folders.push({
            id: fav.id,
            text: fav.text
          });
        }
      });
    }    
    return folders;
 }
 
function favoritesUpdated() {	
	console.log('favorites have been updated');
	userUpdatedFavorite = true;
	toast.addToast('favorites have been updated','success', 4000);
	
}


function addFavorite(saveFavorite, favorites) {
	clpSaveFavorite = saveFavorite;
	favoriteFolders = getFavoriteFolders(favorites);
	setFavoriteFolderOptions(favoriteFolders);		
	_addFabModal.open();	
}


/*
function gotoFavorite(url) {
	console.log('gotoFavorite', url)
	console.log(url);
}
*/

/*
function favoritesUpdated(saveFavorites, favorites) {
	console.log('favoritesUpdated saveFavorites', saveFavorites);
	console.log('favoritesUpdated', favorites);
}
*/

function openDebugMenu() {
	console.log('openDebugMenu');
	console.log(arguments);
}


document.addEventListener('menuReceived', function() {
	console.log('menuReceived event Header fetched');
});



document.addEventListener('favoritesReceived', function(e) {
	console.log('favoritesReceived Favorites fetched event' + e.data);
	console.log('favoritesReceived Favorites fetched event.data.status' + e.data.status);
	if (_saveButtonOnFavoritesModalClicked) {		
		_saveButtonOnFavoritesModalClicked = false;
		toast.addToast(_currentFavoriteLabel + ' added to Favorites','success', 4000);		
	}
});




document.addEventListener('productsReceived', function() {
	console.log('productsReceived Products fetched');
});




function okBtnClickedOnFavoritesModal() {
	console.log('ok button clicked on Favorites Modal');
    var name = $(_addFabModal.el).find("[name='favName']").val().trim();
    _currentFavoriteLabel = name;
    if(!name || name === '') {
      var input = new eto.TextInput({ el: document.getElementById('favName') });
      input.setMessage('Please enter a name', 'error');
      $(_addFabModal.el).find("[name='favName']").on('focus', function(){
    	  input.clearMessage();
      });
      return;
    }
    
    var _currUrlNum = window.location.href.lastIndexOf('/');    
    
    var _currUrl = baseUrl + "e2redirect.do?url=" + lastUrl;
    console.log('_currUrl', _currUrl);
    var _parentFolderName;
    var folderId = _selectInput.getValue();
    if (folderId == 0) {
    	var parentFolderName = $('input[name=parentFolderName]').val().trim();        	
    	_parentFolderName = parentFolderName;
    	if(!parentFolderName || parentFolderName === '') {
            var parentFolderInput = new eto.TextInput({ el: document.getElementById('parentFolderName') });
            parentFolderInput.setMessage('Please enter name of new folder', 'error');
            $(_addFabModal.el).find("[name='parentFolderName']").on('focus', function(){
            	parentFolderInput.clearMessage();
            });
            return;
          }        	
    }
    favoriteName = name;
    //e2sc code       
    var data = {};
    data.isAdd = true;
    data.text = name;      
    data.url = _currUrl;
    data.isHome = false;
    data.app = 'scplatform';
    if (folderId == 0) {
    	data.parentFolderName = _parentFolderName;
    }
    else {
    	data.parentFolderId = folderId;
    }
    
    
    clpSaveFavorite(data);        
    _saveButtonOnFavoritesModalClicked = true;
    _addFabModal.close();
}

function favoritesDomModify() {
	_selectInput.on('change:value', function(newValue, oldValue) {
		if (newValue == 0) {
			var parentFolderDiv = document.getElementById("parentFolderName");
			parentFolderDiv.style.display = "block";
		}
		else if (oldValue == 0) {
			var parentFolderDiv = document.getElementById("parentFolderName");
			parentFolderDiv.style.display = "none";
		}
	});

	var favoriteName;
	 
	_addFabModal.on('opened', function(e){			
		$(_addFabModal.el).find("[name='favName']").val(mcmApp.pageItems[mcmApp.pageItems.length-1].name);
	});

	$(_addFabModal.el).keyup(function(e){
		if(e.which === 13) {
	          $(_addFabModal.el).find('#okBtn').trigger('click');
	    }
	});
}
	      
	      