/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
﻿import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { Text } from 'react-native';

import LoginScreen          from './screens/LoginScreen';
import DashboardScreen      from './screens/DashboardScreen';
import AlertsScreen         from './screens/AlertsScreen';
import BomScreen            from './screens/BomScreen';
import CostRecordsScreen    from './screens/CostRecordsScreen';
import CostDetailScreen     from './screens/CostDetailScreen';
import SuppliersScreen      from './screens/SuppliersScreen';
import SupplierDetailScreen from './screens/SupplierDetailScreen';
import AiInsightsScreen     from './screens/AiInsightsScreen';
import UsersScreen          from './screens/UsersScreen';

const Stack = createStackNavigator();
const Tab   = createBottomTabNavigator();

function MainTabs() {
  return (
    <Tab.Navigator screenOptions={{ headerShown: false, tabBarActiveTintColor: '#2563eb', tabBarInactiveTintColor: '#94a3b8' }}>
      <Tab.Screen name="Dashboard"  component={DashboardScreen}  options={{ tabBarLabel:'Home',      tabBarIcon:({color})=><Text style={{color,fontSize:18}}>ðŸ </Text> }} />
      <Tab.Screen name="Alerts"     component={AlertsScreen}     options={{ tabBarLabel:'Alerts',    tabBarIcon:({color})=><Text style={{color,fontSize:18}}>ðŸ””</Text> }} />
      <Tab.Screen name="Suppliers"  component={SuppliersScreen}  options={{ tabBarLabel:'Suppliers', tabBarIcon:({color})=><Text style={{color,fontSize:18}}>ðŸ­</Text> }} />
      <Tab.Screen name="AiInsights" component={AiInsightsScreen} options={{ tabBarLabel:'AI',        tabBarIcon:({color})=><Text style={{color,fontSize:18}}>ðŸ¤–</Text> }} />
    </Tab.Navigator>
  );
}

export default function AppNavigator() {
  return (
    <NavigationContainer>
      <Stack.Navigator screenOptions={{ headerShown: false }} initialRouteName="Login">
        <Stack.Screen name="Login"           component={LoginScreen} />
        <Stack.Screen name="Main"            component={MainTabs} />
        <Stack.Screen name="CostRecords"     component={CostRecordsScreen}    options={{ headerShown:true, title:'Cost Records' }} />
        <Stack.Screen name="CostDetail"      component={CostDetailScreen}     options={{ headerShown:true, title:'Cost Detail' }} />
        <Stack.Screen name="SupplierDetail"  component={SupplierDetailScreen} options={{ headerShown:true, title:'Supplier Detail' }} />
        <Stack.Screen name="Bom"             component={BomScreen}            options={{ headerShown:true, title:'Bill of Materials' }} />
        <Stack.Screen name="Users"           component={UsersScreen}          options={{ headerShown:true, title:'Users' }} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}
