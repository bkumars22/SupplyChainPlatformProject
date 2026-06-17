/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
(function() {
    var baseUrl = 'mcm/api/';

    function ajaxCall(method, url, data, success, error) {
        var ajaxData = data;
        var contentType = undefined;
        if (method !== 'GET' && data) {
            ajaxData = JSON.stringify(data);
            contentType = 'application/json';
        }
        $.ajax({
            url: baseUrl + url,
            type: method,
            data: ajaxData,
            contentType: contentType,
            success: success,
            error: error
        });
    }

    window.apiService = {
        get: function(url, data, success, error) {
            ajaxCall('GET', url, data, success, error);
        },
        post: function(url, data, success, error) {
            ajaxCall('POST', url, data, success, error);
        },
        delete: function(url, data, success, error) {
            ajaxCall('DELETE', url, data, success, error);
        },

        /**
         * Async GET request using Fetch API
         * Guaranteed asynchronous execution with Promise-based handling
         *
         * @param {string} url - The API endpoint (without baseUrl prefix)
         * @param {object} params - Query parameters as an object
         * @returns {Promise} - Promise that resolves with response data or rejects with error
         */
        getAsync: function(url, params) {
            var queryString = '';
            if (params) {
                queryString = '?' + Object.keys(params)
                    .map(function(key) {
                        return encodeURIComponent(key) + '=' + encodeURIComponent(params[key]);
                    })
                    .join('&');
            }

            return fetch(baseUrl + url + queryString, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            .then(function(response) {
                if (!response.ok) {
                    throw new Error('HTTP error, status = ' + response.status);
                }
                return response.json();
            });
        },

        /**
         * Async POST request using Fetch API
         * Guaranteed asynchronous execution with Promise-based handling
         *
         * @param {string} url - The API endpoint (without baseUrl prefix)
         * @param {object} data - Request body data
         * @returns {Promise} - Promise that resolves with response data or rejects with error
         */
        postAsync: function(url, data) {
            return fetch(baseUrl + url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            })
            .then(function(response) {
                if (!response.ok) {
                    throw new Error('HTTP error, status = ' + response.status);
                }
                return response.json();
            });
        },

        /**
         * Async DELETE request using Fetch API
         * Guaranteed asynchronous execution with Promise-based handling
         *
         * @param {string} url - The API endpoint (without baseUrl prefix)
         * @param {object} params - Query parameters as an object
         * @returns {Promise} - Promise that resolves with response data or rejects with error
         */
        deleteAsync: function(url, params) {
            var queryString = '';
            if (params) {
                queryString = '?' + Object.keys(params)
                    .map(function(key) {
                        return encodeURIComponent(key) + '=' + encodeURIComponent(params[key]);
                    })
                    .join('&');
            }

            return fetch(baseUrl + url + queryString, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            .then(function(response) {
                if (!response.ok) {
                    throw new Error('HTTP error, status = ' + response.status);
                }
                return response.json();
            });
        }
    };
})();
