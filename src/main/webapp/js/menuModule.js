/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
var MenuModule = (function() {
	// 'use strict';
	// placeholder for cached DOM elements
	var DOM = {};

	/* =================== private methods ================= */
	// cache DOM elements
	function cacheDom() {
		// Cache DOM elements
		// DOM.$someElement = $('#some-element');
	}
	// bind events
	function bindEvents() {
		// DOM.$someElement.click(handleClick);
	}
	// handle click events
	function handleClick(e) {
		// render(); // etc
	}
	// render DOM
	function render() {
		// DOM.$someElement
		// .html('<p>Yeah!</p>');
	}

	/* =================== public methods ================== */
	// main init method
	var headerDataStr;
	var favoritesWrapStr;
	var _environment;
	var _userId;
	var _userName;
	var _roleName;
	var _headerData;
	var _header;
	var _addFavModal;

	function init(menuObject) {
		headerDataStr = menuObject.headerDataStr;
		favoritesWrapStr = menuObject.favoritesWrapStr;
		_environment = menuObject.environment;
		_userId = menuObject.userId;
		_userName = menuObject.userName;
		_roleName = menuObject.roleName;
		_headerData = menuObject.headerData;
		_header = menuObject.header;
		updateBannerType(_environment, _headerData);
		updateUserInfo(_userId, _userName, _roleName, _headerData);
		updateUserDropDown(_headerData);
		if (favoritesWrapStr) {
			var _favoritesWrap = JSON.parse(favoritesWrapStr);
			if (_favoritesWrap && _favoritesWrap.favorites) {
				_headerData.favorites = _favoritesWrap.favorites;
			}
		}
		createHeadObject(_headerData);
		_addFavModal = new eto.Modal({
			el : document.querySelector('#add-fav-modal')
		});
	}
	// Menu Begin

	function updateUserInfo(_userId, _userName, _roleName, headerData) {
		headerData.userId = _userId;
		headerData.userName = _userName;
		headerData.roleName = _roleName;
		return headerData;
	}

	function updateBannerType(environment, headerData) {
		headerData.bannerType = environment;
		return headerData;
	}

	// Check why aboutWindow is not declared as local variable
	function openAboutPage() {
		console.log('I am in open about page');
		var url = '../common/about_e2modern.jsp';
		aboutWindow = window.open(url, 'aboutPage',
				'height=320,width=504,location=yes,resizable=yes');
		if (aboutWindow == null) {
			showPopupBlockerMessage();
			return;
		}
		aboutWindow.focus();
	}

	function logout() {
		console.log('logging out');
		window.location.href = "logout.do";
	}

	function updateUserDropDown(headerData) {
		headerData.dropdownsA = [];
		headerData.dropdownsA.push({
			url : 'javascript:MenuModule.openAboutPage()',
			title : 'About',
			icon : 'info_outline',
			text : 'About'
		});
		headerData.dropdownsB = [];
		headerData.dropdownsB.push({
			url : 'javascript:MenuModule.logout()',
			title : '',
			icon : 'exit_to_app',
			text : 'Exit'
		});
		return headerData;
	}

	function onMenuItemClicked(instance, event) {
		event.preventDefault();
		if (event.target.href) {
			_header.closeMenu();
			document.getElementById('contentFrame').setAttribute("src",
					event.target.href);
		}
	}

	function onAddFavoriteClicked(instance, event) {
		_addFavModal.open();
		$('#errorpopUp').html("");
		document.getElementById('favName').value = '';
	}

	function onFavoriteItemClicked(instance, event) {
		document.getElementById('contentFrame').setAttribute("src",
				event.target.href);
	}

	function onFavoritesUpdated(instance, fav) {
		var wrap = {};
		wrap.favorites = fav;
		$.ajax({
			type : "POST",
			url : "mcm/api/save_favorites",
			contentType:"application/json; charset=utf-8",
			dataType : "json",
			data : JSON.stringify(wrap),
			success : function(response) {
			}
		});
	}

	function onNotificationsClicked(instance, fav) {
		document.getElementById('contentFrame').setAttribute("src",
				'AlertSearch.do');
	}

	function onMenuOpened(instance, fav) {
		$('.eto-header__menu-filter .eto-input__field-container input').focus();
	}

	function createHeadObject(headerData) {
		var headerDiv = document.createElement('div');
		headerDiv.setAttribute("id", "header-example");
		document.getElementById('header-parent').appendChild(headerDiv);

		_header = new eto.Header({
			el : document.querySelector('#header-example'),
			systemMessage : {},
			banner : {
				type : headerData.bannerType
			},
			logo : {
				url : 'skins/e2-modern/images/main_logo_277x65.png',
				title : 'Kumar Learning Project'
			},
			products : {
				activeTitle : 'Multi-tier Cost Manager',
				items : [ {
					url : '#',
					title : 'MTCM',
					text : 'Multi-tier Cost Manager'
				} ]
			},
			search : {
				isExpanded : false,
				placeholder : 'Search',
				title : 'Search for stuff'
			},
			help : {
				title : 'Help',
				url : 'javascript:goShowContentPageHelp()'
			},
			notification : {
				type : 'info',
				count : 2,
				url : '#',
				title : 'Get notified'
			},
			user : {
				title : 'User',
				username : headerData.userId,
				role : headerData.roleName,
				showRole : true,
				image : 'skins/e2-modern/images/account_circle.svg',
				name : headerData.userName,
				menu : [ headerData.dropdownsA, headerData.dropdownsB ]
			},
			menu : {
				filter : {
					placeholder : 'Filter items'
				},
				text : 'Menu',
				items : headerData.menu
			},
			favorites : {
				headingText : 'Add to Favorites',
				headingTitle : 'Add a favorite',
				headingIcon : 'star',
				title : 'Favorites',
				text : 'Favorites',
				moreTitle : 'More',
				moreText : 'More',
				items : headerData.favorites
			}
		});

		_header.on('menuItemClicked', onMenuItemClicked);
		_header.on('addFavoriteClicked', onAddFavoriteClicked);
		_header.on('favoriteItemClicked', onFavoriteItemClicked);
		_header.on('favoritesUpdated', onFavoritesUpdated);
		_header.on('notificationsClicked', onNotificationsClicked);
		_header.on('menuOpened', onMenuOpened);

		hideSearchAndAlert();

		/*
		 * $('.eto-header__menu').hover( function() { _header.openMenu(); },
		 * function() { _header.closeMenu(); });
		 */

		/*
		 * $('.eto-header__menu').mouseover(function() { _header.openMenu(); });
		 * 
		 * $(document).mouseup( function(e) { var container =
		 * $('.eto-header__menu'); // if the target of the click isn't the
		 * header container nor // a descendant of it if
		 * (!container.is(e.target) && container.has(e.target).length === 0) {
		 * _header.closeMenu(); } });
		 * 
		 * $('.eto-header__menu-dropdown .eto-menu__group .eto-menu__item
		 * button') .mouseover(function() { $(this).trigger('click'); });
		 */
	}

	function hideSearchAndAlert() {
		var elemsHeaderSearch = document
				.getElementsByClassName('eto-header__search');
		elemsHeaderSearch[0].style.display = 'none';
		var notificationDiv = document
				.getElementsByClassName('eto-header__notifications');
		notificationDiv[0].style.display = 'none';
	}

	function saveFavClicked(e) {
		console.log('save favorite clicked');
		var name = $("#favName").val().trim();
		var error = " ";
		if (name == "") {
			error = '<div class="eto-messageblock" data-message-type="error" id="name_not_empty">'
					+ '<div class="eto-messageblock__body">Please enter name.</div>'
					+ '<a href="javascript:void(0)" role="button" class="eto-messageblock__close"></a> </div>';
			$('#errorpopUp').html(error);
			new eto.MessageBlock({
				el : document.querySelector('#name_not_empty')
			});
			$("#name_not_empty").css({
				'display' : ''
			});
			return;
		}
		$('#errorpopUp').html(error);
		//var cf = frames['contentFrame'];
		var srcLink = lastUrl;
		//srcLink = srcLink.substr(srcLink.lastIndexOf('/') + 1);
		var favoriteName = document.getElementById('favName').value;
		var myData = {
			favName : favoriteName,
			url : srcLink
		};
		_addFavModal.close();
		$.ajax({
			type : "POST",
			url : "mcm/api/add_fav_link",
			contentType : "application/json; charset=utf-8",
			dataType : "json",
			data : JSON.stringify(myData),
			success : function(response) {
				_header.remove();
				delete _header;
				_headerData.favorites = response.favorites;
				createHeadObject(_headerData);
			}
		});
	}

	// Menu end

	/* =============== export public methods =============== */
	return {
		init : init,
		saveFavClicked : saveFavClicked,
		openAboutPage : openAboutPage,
		logout : logout,
		updateUserInfo : updateUserInfo,
		hideSearchAndAlert : hideSearchAndAlert,
		updateBannerType : updateBannerType
	};
}());
