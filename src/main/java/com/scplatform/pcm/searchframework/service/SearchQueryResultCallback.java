/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.service;

import com.scplatform.pcm.searchframework.dto.GenericResultRow;
import org.hibernate.query.Query;

public interface SearchQueryResultCallback {
		public void start(String[] columnNames);

		public boolean onRow(GenericResultRow row);

		public void end(String[] columnNames);

        default long countRow(Query query){return 0;};
	}