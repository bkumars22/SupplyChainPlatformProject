/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
﻿export const SECURITY_CONFIG = {
  JWT: { STORAGE_KEY: 'jwt_token', USER_KEY: 'user_name', ROLE_KEY: 'user_role' },
  API: { BASE_URL: 'http://172.30.208.1:8089/supchain', AI_URL: 'http://172.30.208.1:8001', TIMEOUT: 15000 },
  FEATURES: { BIOMETRIC_LOGIN: true, ENCRYPTED_STORAGE: true, AUTO_LOGOUT_MINUTES: 30, MAX_LOGIN_ATTEMPTS: 5 },
  ROLES: {
    Administrator: ['dashboard','suppliers','alerts','ai','costs','bom','users'],
    Manager:       ['dashboard','suppliers','alerts','ai','costs','bom'],
    Analyst:       ['dashboard','suppliers','alerts','ai'],
    Viewer:        ['dashboard','suppliers'],
  },
};
export const secureRequest = async (endpoint, options = {}) => {
  const AsyncStorage = require('@react-native-async-storage/async-storage').default;
  const token = await AsyncStorage.getItem(SECURITY_CONFIG.JWT.STORAGE_KEY);
  const headers = { 'Content-Type': 'application/json', ...(token ? { Authorization: 'Bearer ' + token } : {}), ...options.headers };
  const response = await fetch(SECURITY_CONFIG.API.BASE_URL + endpoint, { ...options, headers });
  if (response.status === 401) {
    await AsyncStorage.multiRemove([SECURITY_CONFIG.JWT.STORAGE_KEY, SECURITY_CONFIG.JWT.USER_KEY]);
    throw new Error('SESSION_EXPIRED');
  }
  return response;
};
