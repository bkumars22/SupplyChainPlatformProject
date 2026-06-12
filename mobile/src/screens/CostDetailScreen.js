/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
﻿import React, { useState, useEffect } from 'react';
import { View, Text, ScrollView, TouchableOpacity, TextInput, StyleSheet, ActivityIndicator, Alert } from 'react-native';
import { useRoute, useNavigation } from '@react-navigation/native';
import api from '../api/client';

const STATUS_COLORS = {
  DRAFT:            { bg: '#e3f2fd', color: '#1565c0' },
  PENDING_APPROVAL: { bg: '#fff8e1', color: '#f57f17' },
  APPROVED:         { bg: '#e8f5e9', color: '#2e7d32' },
  REJECTED:         { bg: '#ffebee', color: '#c62828' },
};

export default function CostDetailScreen() {
  const route = useRoute();
  const navigation = useNavigation();
  const { id } = route.params;
  const [record, setRecord]             = useState(null);
  const [loading, setLoading]           = useState(true);
  const [actionLoading, setAction]      = useState(false);
  const [rejectReason, setRejectReason] = useState('');
  const [showReject, setShowReject]     = useState(false);

  useEffect(() => { fetchRecord(); }, []);

  const fetchRecord = async () => {
    try {
      const res = await api.get('/api/costs/' + id);
      setRecord(res.data.data);
    } catch (err) {
      Alert.alert('Error', 'Failed to load cost record');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async () => {
    setAction(true);
    try {
      await api.put('/api/costs/' + id + '/submit');
      await fetchRecord();
      Alert.alert('Success', 'Submitted for approval!');
    } catch (err) {
      Alert.alert('Error', (err.response && err.response.data && err.response.data.message) || 'Submit failed');
    } finally { setAction(false); }
  };

  const handleApprove = async () => {
    setAction(true);
    try {
      await api.put('/api/costs/' + id + '/approve');
      await fetchRecord();
      Alert.alert('Approved', 'Cost record approved and item cost updated!');
    } catch (err) {
      Alert.alert('Error', (err.response && err.response.data && err.response.data.message) || 'Approve failed');
    } finally { setAction(false); }
  };

  const handleReject = async () => {
    if (!rejectReason.trim()) { Alert.alert('Error', 'Rejection reason is required'); return; }
    setAction(true);
    try {
      await api.put('/api/costs/' + id + '/reject', { reason: rejectReason });
      await fetchRecord();
      setShowReject(false);
      Alert.alert('Rejected', 'Cost record rejected.');
    } catch (err) {
      Alert.alert('Error', (err.response && err.response.data && err.response.data.message) || 'Reject failed');
    } finally { setAction(false); }
  };

  if (loading) return React.createElement(ActivityIndicator, { style: { marginTop: 40 }, size: 'large', color: '#1a237e' });
  if (!record) return React.createElement(Text, { style: { margin: 20 } }, 'Record not found');

  const sc = STATUS_COLORS[record.status] || {};
  const changeColor = record.changePercent > 0 ? '#c62828' : '#2e7d32';
  const changeSign  = record.changePercent > 0 ? '+' : '';

  return (
    React.createElement(ScrollView, { style: styles.container },
      React.createElement(View, { style: styles.header },
        React.createElement(Text, { style: styles.title }, 'Cost Record #' + record.id),
        React.createElement(Text, { style: [styles.badge, { backgroundColor: sc.bg, color: sc.color }] }, (record.status || '').replace('_', ' '))
      ),
      React.createElement(View, { style: styles.card },
        React.createElement(Text, { style: styles.sectionTitle }, 'Item'),
        React.createElement(Text, { style: styles.row }, 'Code: ' + (record.item ? record.item.itemCode : '--')),
        React.createElement(Text, { style: styles.row }, 'Description: ' + (record.item ? record.item.description : '--')),
        React.createElement(Text, { style: styles.row }, 'Version: v' + record.versionNumber)
      ),
      React.createElement(View, { style: styles.card },
        React.createElement(Text, { style: styles.sectionTitle }, 'Cost Change'),
        React.createElement(Text, { style: styles.row }, 'Previous: $' + (record.previousCost ? record.previousCost.toFixed(4) : '--')),
        React.createElement(Text, { style: styles.row }, 'Proposed: $' + (record.proposedCost ? record.proposedCost.toFixed(4) : '--')),
        React.createElement(Text, { style: [styles.row, { color: changeColor, fontWeight: '700' }] }, 'Change: ' + changeSign + (record.changePercent ? record.changePercent.toFixed(2) : '0') + '%')
      ),
      React.createElement(View, { style: styles.card },
        React.createElement(Text, { style: styles.sectionTitle }, 'Justification'),
        React.createElement(Text, { style: { color: '#555', lineHeight: 22, marginTop: 6 } }, record.justification),
        record.rejectionReason ? React.createElement(View, { style: styles.rejectBox },
          React.createElement(Text, { style: styles.rejectText }, 'Rejected: ' + record.rejectionReason)
        ) : null
      ),
      record.status === 'DRAFT' ? React.createElement(TouchableOpacity, { style: styles.btnPrimary, onPress: handleSubmit, disabled: actionLoading },
        React.createElement(Text, { style: styles.btnText }, actionLoading ? 'Submitting...' : 'Submit for Approval')
      ) : null,
      record.status === 'PENDING_APPROVAL' ? React.createElement(View, { style: { flexDirection: 'row', gap: 10 } },
        React.createElement(TouchableOpacity, { style: [styles.btnPrimary, { flex: 1, backgroundColor: '#2e7d32' }], onPress: handleApprove, disabled: actionLoading },
          React.createElement(Text, { style: styles.btnText }, 'Approve')
        ),
        React.createElement(TouchableOpacity, { style: [styles.btnPrimary, { flex: 1, backgroundColor: '#c62828' }], onPress: () => setShowReject(true), disabled: actionLoading },
          React.createElement(Text, { style: styles.btnText }, 'Reject')
        )
      ) : null,
      showReject ? React.createElement(View, { style: styles.card },
        React.createElement(Text, { style: styles.sectionTitle }, 'Rejection Reason *'),
        React.createElement(TextInput, { style: styles.textarea, multiline: true, placeholder: 'Enter reason...', value: rejectReason, onChangeText: setRejectReason }),
        React.createElement(TouchableOpacity, { style: [styles.btnPrimary, { backgroundColor: '#c62828' }], onPress: handleReject },
          React.createElement(Text, { style: styles.btnText }, 'Confirm Reject')
        )
      ) : null
    )
  );
}

const styles = StyleSheet.create({
  container:    { flex: 1, backgroundColor: '#f0f2f5', padding: 12 },
  header:       { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 },
  title:        { fontSize: 18, fontWeight: '800', color: '#1a237e' },
  badge:        { fontSize: 11, fontWeight: '700', padding: 4, paddingHorizontal: 10, borderRadius: 12 },
  card:         { backgroundColor: '#fff', borderRadius: 12, padding: 16, marginBottom: 12, elevation: 2 },
  sectionTitle: { fontSize: 15, fontWeight: '700', color: '#1a237e', marginBottom: 8 },
  row:          { fontSize: 13, color: '#555', marginBottom: 4 },
  rejectBox:    { backgroundColor: '#ffebee', borderRadius: 8, padding: 10, marginTop: 10 },
  rejectText:   { color: '#c62828', fontSize: 13 },
  btnPrimary:   { backgroundColor: '#1a237e', borderRadius: 10, padding: 14, alignItems: 'center', marginBottom: 10 },
  btnText:      { color: '#fff', fontWeight: '700', fontSize: 14 },
  textarea:     { borderWidth: 1, borderColor: '#ddd', borderRadius: 8, padding: 10, minHeight: 80, fontSize: 13, marginBottom: 10 },
});