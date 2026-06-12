/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
﻿import React, { useState, useEffect } from 'react';
import { View, Text, FlatList, TouchableOpacity, TextInput, StyleSheet, ActivityIndicator } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import api from '../api/client';

export default function SuppliersScreen() {
  const [suppliers, setSuppliers] = useState([]);
  const [search, setSearch]       = useState('');
  const [loading, setLoading]     = useState(false);
  const navigation = useNavigation();

  useEffect(() => { fetchSuppliers(); }, [search]);

  const fetchSuppliers = async () => {
    setLoading(true);
    try {
      const res = await api.get('/api/suppliers', { params: { search } });
      setSuppliers(res.data.data);
    } catch (err) { console.error(err); }
    finally { setLoading(false); }
  };

  const scoreColor = (score) => score >= 80 ? '#2e7d32' : score >= 60 ? '#f57f17' : '#c62828';

  const renderItem = ({ item: s }) => (
    React.createElement(TouchableOpacity,
      { style: [styles.card, s.atRisk && styles.atRisk], onPress: () => navigation.navigate('SupplierDetail', { supplierId: s.supplierId }) },
      React.createElement(View, { style: styles.row },
        React.createElement(View, { style: { flex: 1 } },
          React.createElement(Text, { style: styles.name }, s.supplierName),
          React.createElement(Text, { style: styles.sub }, s.supplierId + ' Â· ' + s.country)
        ),
        React.createElement(Text, { style: [styles.score, { color: scoreColor(s.compositeScore) }] }, s.compositeScore?.toFixed(1))
      ),
      React.createElement(View, { style: styles.scoreRow },
        React.createElement(Text, { style: styles.scoreLabel }, 'OTD: ' + s.otdScore?.toFixed(1) + '%'),
        React.createElement(Text, { style: styles.scoreLabel }, 'Quality: ' + s.qualityScore?.toFixed(1)),
        React.createElement(Text, { style: styles.scoreLabel }, 'Deliveries: ' + s.totalDeliveries)
      ),
      s.atRisk ? React.createElement(Text, { style: styles.riskBadge }, 'AT RISK â€” OTD below 70%') : null
    )
  );

  return (
    React.createElement(View, { style: styles.container },
      React.createElement(TextInput, { style: styles.search, placeholder: 'Search suppliers...', value: search, onChangeText: setSearch }),
      loading
        ? React.createElement(ActivityIndicator, { style: { margin: 20 }, color: '#1a237e' })
        : React.createElement(FlatList, {
            data: suppliers,
            keyExtractor: s => s.supplierId,
            renderItem: renderItem,
            ListEmptyComponent: React.createElement(Text, { style: styles.empty }, 'No suppliers found')
          })
    )
  );
}

const styles = StyleSheet.create({
  container:  { flex: 1, backgroundColor: '#f0f2f5', padding: 12 },
  search:     { backgroundColor: '#fff', borderRadius: 8, padding: 10, marginBottom: 12, fontSize: 14, borderWidth: 1, borderColor: '#e0e0e0' },
  card:       { backgroundColor: '#fff', borderRadius: 12, padding: 14, marginBottom: 10, elevation: 2 },
  atRisk:     { borderLeftWidth: 4, borderLeftColor: '#c62828' },
  row:        { flexDirection: 'row', alignItems: 'center', marginBottom: 8 },
  name:       { fontSize: 15, fontWeight: '700', color: '#1a237e' },
  sub:        { fontSize: 12, color: '#888', marginTop: 2 },
  score:      { fontSize: 28, fontWeight: '800' },
  scoreRow:   { flexDirection: 'row', gap: 14, flexWrap: 'wrap' },
  scoreLabel: { fontSize: 12, color: '#666' },
  riskBadge:  { marginTop: 8, backgroundColor: '#ffebee', color: '#c62828', padding: 4, borderRadius: 6, fontSize: 11, fontWeight: '700', textAlign: 'center' },
  empty:      { textAlign: 'center', color: '#aaa', marginTop: 40 },
});