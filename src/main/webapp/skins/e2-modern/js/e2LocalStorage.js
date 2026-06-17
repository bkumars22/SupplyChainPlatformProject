/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
function saveUserData(userDataName, userDataValue) {try {if (typeof(localStorage) != 'undefined' ) {localStorage.setItem(getUserDataKey(userDataName), userDataValue + "");} else {var localStorageElm = document.createElement('link');if(localStorageElm.addBehavior){localStorageElm.style.behavior = 'url(#default#userData)';document.getElementsByTagName('head')[0].appendChild(localStorageElm);localStorageElm.load("localStorage");localStorageElm.setAttribute(userDataName, userDataValue + "");localStorageElm.save("localStorage");}}
} catch (e) {}}
function getUserData(userDataName,defaultValue) {var value = defaultValue;if (typeof(localStorage) != 'undefined' ) {value = localStorage.getItem(getUserDataKey(userDataName));if (!value) value = defaultValue;} else {var localStorageElm = document.createElement('link');if(localStorageElm.addBehavior){localStorageElm.style.behavior = 'url(#default#userData)';document.getElementsByTagName('head')[0].appendChild(localStorageElm);localStorageElm.load("localStorage");value = localStorageElm.getAttribute(userDataName);if (!value) value = defaultValue;}}
return value;}
function getUserDataKey(userDataName) {var url = document.location.href;return url.substring(url.indexOf("//") + 2, url.lastIndexOf("/")) + ":" + userDataName;}
