/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
﻿import React, { useState, useEffect } from 'react';
import { View, Text, FlatList, TouchableOpacity, TextInput, StyleSheet, ActivityIndicator } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import api from '../api/client';

const STATUS_COLORS = {
  DRAFT:            { bg: '#e3f2fd', color: '#1565c0' },
  PENDING_APPROVAL: { bg: '#fff8e1', color: '#f57f17' },
  APPROVED:         { bg: '#e8f5e9', color: '#2e7d32' },
  REJECTED:         { bg: '#ffebee', color: '#c62828' },
};

export default function CostRecordsScreen() {
  const [records, setRecords]   = useState([]);
  const [search, setSearch]     = useState('');
  const [page, setPage]         = useState(0);
  const [loading, setLoading]   = useState(false);
  const [hasMore, setHasMore]   = useState(true);
  const navigation = useNavigation();

  useEffect(() => { fetchRecords(0, true); }, [search]);

  const fetchRecords = async (pageNum = 0, reset = false) => {
    if (loading) return;
    setLoading(true);
    try {
      const res = await api.get('/api/costs', { params: { search, page: pageNum, size: 20 } });
      const newData = res.data.data.content;
      setRecords(reset ? newData : [...records, ...newData]);
      setHasMore(!res.data.data.last);
      setPage(pageNum);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const renderItem = ({ item: r }) => {
    const sc = STATUS_COLORS[r.status] || {};
    const changeColor = r.changePercent > 0 ? '#c62828' : '#2e7d32';
    const changeSign  = r.changePercent > 0 ? '+' : '';
    return (
      React.createElement(TouchableOpacity, { style: styles.card, onPress: () => navigation.navigate('CostDetail', { id: r.id }) },
        React.createElement(View, { style: styles.cardHeader },
          React.createElement(Text, { style: styles.itemCode }, (r.item ? r.item.itemCode : '') + ' v' + r.versionNumber),
          React.createElement(Text, { style: [styles.badge, { backgroundColor: sc.bg, color: sc.color }] }, (r.status || '').replace('_', ' '))
        ),
        React.createElement(View, { style: styles.costRow },
          React.createElement(Text, { style: styles.label }, 'Prev: $' + (r.previousCost ? r.previousCost.toFixed(2) : '--')),
          React.createElement(Text, { style: styles.label }, 'New: $' + (r.proposedCost ? r.proposedCost.toFixed(2) : '--')),
          React.createElement(Text, { style: [styles.change, { color: changeColor }] }, changeSign + (r.changePercent ? r.changePercent.toFixed(2) : '0') + '%')
        ),
        React.createElement(Text, { style: styles.justification, numberOfLines: 2 }, r.justification)
      )
    );
  };

  return (
    React.createElement(View, { style: styles.container },
      React.createElement(TextInput, { style: styles.search, placeholder: 'Search by item or justification...', value: search, onChangeText: t => setSearch(t) }),
      React.createElement(FlatList, {
        data: records,
        keyExtractor: r => r.id.toString(),
        renderItem: renderItem,
        onEndReached: () => hasMore && fetchRecords(page + 1),
        onEndReachedThreshold: 0.3,
        ListFooterComponent: loading ? React.createElement(ActivityIndicator, { style: { margin: 20 } }) : null,
        ListEmptyComponent: !loading ? React.createElement(Text, { style: styles.empty }, 'No cost records found') : null
      })
    )
  );
}

const styles = StyleSheet.create({
  container:     { flex: 1, backgroundColor: '#f0f2f5', padding: 12 },
  search:        { backgroundColor: '#fff', borderRadius: 8, padding: 10, marginBottom: 12, fontSize: 14, borderWidth: 1, borderColor: '#e0e0e0' },
  card:          { backgroundColor: '#fff', borderRadius: 12, padding: 14, marginBottom: 10, shadowColor: '#000', shadowOffset: { width: 0, height: 1 }, shadowOpacity: 0.07, elevation: 2 },
  cardHeader:    { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 },
  itemCode:      { fontWeight: '700', fontSize: 14, color: '#1a237e' },
  badge:         { fontSize: 10, fontWeight: '700', padding: 3, paddingHorizontal: 8, borderRadius: 10 },
  costRow:       { flexDirection: 'row', gap: 12, marginBottom: 6, flexWrap: 'wrap' },
  label:         { fontSize: 12, color: '#888' },
  change:        { fontSize: 13, fontWeight: '700' },
  justification: { fontSize: 12, color: '#666', fontStyle: 'italic' },
  empty:         { textAlign: 'center', color: '#aaa', marginTop: 40, fontSize: 14 },
});