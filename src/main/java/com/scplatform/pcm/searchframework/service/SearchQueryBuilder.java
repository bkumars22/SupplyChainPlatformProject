/*
 * Copyright (c) 2006 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2006, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.searchframework.service;

import com.scplatform.pcm.authentication.dto.ApplicationContext;
import com.scplatform.pcm.commodityProfile.service.CommodityProfileService;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.searchframework.dto.*;
import com.scplatform.pcm.writter.dto.ExtractWriter;
import jakarta.persistence.EntityManager;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.ParameterMetadata;
import org.hibernate.query.Query;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Generic functions for querys and query filters This class is NOT threadsafe
 */
@Service
@RequiredArgsConstructor
public class SearchQueryBuilder {

    private static final Log logger = LogFactory.getLog(SearchQueryBuilder.class);
    protected static Log perfLogger = LogFactory.getLog("com.scplatform.pcm.PERFORMANCETRACE.SearchQuery");

    private final PcmConfigUtil pcmConfigUtil;
    private final CommodityProfileService commodityProfileService;
    private final EntityManager entityManager;

    private Session session;

	private static final int MAX_PARAM_LIST = 1000;

	protected static final String QUERY_TYPE_SQL = "SQL";
	private final static String SQL_COUNT_QUERY_1 = "SELECT * FROM ( SELECT r.*, ROWNUM RNUM, COUNT(*) OVER () RESULT_COUNT FROM (";

    protected SearchQueryResultCallback callback;

	//private boolean showPerformanceStats =
    private int queryTimeout = -1;
    private boolean readOnlyQuery;

	@PostConstruct
	public void init() {
		this.session = entityManager.unwrap(Session.class);
	}

	public void setCallback(SearchQueryResultCallback callback) {
		this.callback = callback;
	}

	// Set the timeout or set to -1 for no timeout
	public void setQueryTimeout(int timeoutInSeconds) {
		queryTimeout = timeoutInSeconds;
	}

	public GenericResultSet executeQueryWithRowCount(SearchDefinition sd, Collection<SearchParameter> parameters,
                                                     Map<String, Object> presetParameterValues, ApplicationContext ac, int start, int len) throws Error {
		HashMap<String, Object> parameterValues;
		if (presetParameterValues == null) {
			parameterValues = new HashMap<String, Object>();
		} else {
			parameterValues = new HashMap<String, Object>(presetParameterValues);
		}
		
		String profileFilter = "";

		if (commodityProfileService.getCommoditytProfileFilterList().contains(sd.getName())) {
			profileFilter = commodityProfileService.getQuery(sd.getName(), ac.getCurrentUser());
			sd.setCommodityProfileFilter(profileFilter);
		}

		GenericResultSet rs = executeRowAndDataQuery(sd.getSourceQueryType(),sd.getSource() + buildWhereClause(sd, parameters, parameterValues)+ buildGroupByClause(sd) + buildOrderByClause(sd), parameterValues,
				start, len);
		return rs;
	}
	
	public GenericResultSet executeRowAndDataQuery(String queryType,String queryString, Map<String, Object> criteriaValues, int start, int len) {
		Query query = null;
		StringBuilder finalQuery = new StringBuilder(SQL_COUNT_QUERY_1);
		finalQuery.append(queryString);
		finalQuery.append(") R ) WHERE RNUM between ");
		finalQuery.append(start);
		finalQuery.append(" and ");
		finalQuery.append(len);

        if(queryType != null && queryType.equalsIgnoreCase(QUERY_TYPE_SQL)) {
            query = session.createNativeQuery(finalQuery.toString(), Object[].class);
		}else {
			query = session.createQuery(finalQuery.toString());
		}
		query.setReadOnly(pcmConfigUtil.getBoolean("pcm.search.readOnlyQuery", true));
		return executeRowAndData(query, criteriaValues, start, len);
	}
	
	protected GenericResultSet executeRowAndData(Query query, Map<String, Object> criteriaValues, int start, int len)  throws Error {
		bindParameters(query, criteriaValues);		
		if (queryTimeout > 0) {
			query.setTimeout(queryTimeout);
		}
		StopWatch timer = new StopWatch();
		if (pcmConfigUtil.getBoolean("pcm.search.showPerformanceStats", false)) {
			timer.start();
		}

		ScrollableResults sr = null;
		GenericResultSet results = null;
		int cnt = 0;
		try {
			sr = query.scroll();

			if (pcmConfigUtil.getBoolean("pcm.search.showPerformanceStats", false)) {
				timer.split();
				perfLogger.info("Executed in: " + timer + " SQL:" + query.getQueryString());
				timer.unsplit();
			}

			String[] colNames = buildColumnNames(query);
			results = new GenericResultSet(colNames);
			if (callback != null) {
				callback.start(colNames);
			}
			while (sr.next()) {
				Object row = sr.get();
				GenericResultRow grr = new GenericResultRow(row);
				if (callback == null) {
					results.add(grr);
				} else {
					if (callback.onRow(grr) == false) {
						break;
					}
					// Clear the Hibernate 1st level cache after a predefined number of rows
					// to improve performance
					if (cnt % pcmConfigUtil.getInteger("pcm.search.clearCacheCount", 500) == 0) {
						session.flush();
						session.clear();
					}
				}
				cnt++;

				if (pcmConfigUtil.getBoolean("pcm.search.showPerformanceStats", false)) {
					if (cnt % 100 == 0) {
						timer.split();
						perfLogger.info("processed " + cnt + " records in " + timer);
						timer.unsplit();
					}
				}
			}
			if (callback != null) {
				callback.end(colNames);
			}

		} finally {
			if (sr != null) {
				sr.close();
			}
			if (pcmConfigUtil.getBoolean("pcm.search.showPerformanceStats", false)) {
				timer.stop();
			}
		}

		return results;
	}
	
	public long executeRowCountQuery(SearchDefinition sd, Collection<SearchParameter> parameters,
			Map<String, Object> presetParameterValues, ApplicationContext ac) throws Error {
		// Since we just use the values plus whatever is in
		// the parameters, if the preset values are null,
		// create a working copy to use

		HashMap<String, Object> parameterValues;
		if (presetParameterValues == null) {
			parameterValues = new HashMap<String, Object>();
		} else {
			parameterValues = new HashMap<String, Object>(presetParameterValues);
		}
		String source = sd.getSource();

		if (source.startsWith("select distinct")) {
			int indexOfDistinct = source.indexOf("distinct");
			String distinctToForm = source.substring(indexOfDistinct + 8, source.lastIndexOf("from"));
			String distinctColumn = "";
			if (distinctToForm.contains(",")) {
				distinctColumn = source.substring(indexOfDistinct + 8, source.indexOf(",", indexOfDistinct));
			} else {
				for (String s : distinctToForm.split(" ")) {
					if (!s.trim().isEmpty()) {
						distinctColumn = s;
						break;
					}
				}
			}
			if (source.indexOf("select") == 0) {
				source = "from " + source.substring(source.lastIndexOf("from") + 4);
			}
			source = "select count( distinct " + distinctColumn + ") " + source;
		} else {
				if (source.indexOf("select") == 0) {
					source = source.substring(findMainFromIndex(source));
				}
				if (sd.getGroupBy() != null && !sd.getGroupBy().isEmpty()) {
					source = "select coalesce(sum(count(*)/count(*)) , 0) " + source;
				}else {
					source = "select count(*) " + source;
				}
		}

		String profileFilter = "";

		if (commodityProfileService.getCommoditytProfileFilterList().contains(sd.getName())) {
			profileFilter = commodityProfileService.getQuery(sd.getName(), ac.getCurrentUser());
			sd.setCommodityProfileFilter(profileFilter);
		}

		if(callback!=null){
			try {
				Method countQuery = callback.getClass().getMethod("countRow",Query.class);
				/*f(countQuery.getDeclaringClass().equals(BomSearchExtractSSWriter.class)){
					String queryString = sd.getExtractSource() + buildWhereClause(sd, parameters, parameterValues)+ buildGroupByClause(sd);
					Query query = HibernateUtil.currentSession().createQuery(queryString);
					bindParameters(query,parameterValues);
					return callback.countRow(query);
				}*/
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}

		}

		GenericResultSet rs = executeQuery(sd, sd.getSourceQueryType(), source + buildWhereClause(sd, parameters, parameterValues)+ buildGroupByClause(sd),
				parameterValues, -1, -1);
		long c = 0;
		if (rs.getValues().size() == 1) {
			Iterator<GenericResultRow> ritr = rs.getValues().iterator();
			if (ritr.hasNext()) {
				Object re = ritr.next().getObject(0);
				if(re instanceof BigDecimal) {
					BigDecimal b = (BigDecimal)re;
					c= b.longValue();
				}else {
					c = (Long) re;
				}
			}
		} else {
			c = rs.getValues().size();
		}
		return c;
	}
	
	public long executeExtractRowCountQuery(SearchDefinition sd, Collection<SearchParameter> parameters,
			Map<String, Object> presetParameterValues, ApplicationContext ac) throws Error {

		HashMap<String, Object> parameterValues;
		if (presetParameterValues == null) {
			parameterValues = new HashMap<String, Object>();
		} else {
			parameterValues = new HashMap<String, Object>(presetParameterValues);
		}
		String source = sd.getExtractSource();

		if (source.contains("distinct")) {
			int indexOfDistinct = source.indexOf("distinct");
			String distinctToForm = source.substring(indexOfDistinct + 8, source.lastIndexOf("from"));
			String distinctColumn = "";
			if (distinctToForm.contains(",")) {
				distinctColumn = source.substring(indexOfDistinct + 8, source.indexOf(",", indexOfDistinct));
			} else {
				for (String s : distinctToForm.split(" ")) {
					if (!s.trim().isEmpty()) {
						distinctColumn = s;
						break;
					}
				}
			}
			if (source.indexOf("select") == 0) {
				source = "from " + source.substring(source.lastIndexOf("from") + 4);
			}
			source = "select count( distinct " + distinctColumn + ") " + source;
		} else {
				if (source.indexOf("select") == 0) {
					source = source.substring(findMainFromIndex(source));
				}
				if (sd.getGroupBy() != null && !sd.getGroupBy().isEmpty()) {
					source = "select coalesce(sum(count(*)/count(*)) , 0) " + source;
				}else {
					source = "select count(*) " + source;
				}
		}

		String profileFilter = "";

		if (commodityProfileService.getCommoditytProfileFilterList().contains(sd.getName())) {
			profileFilter = commodityProfileService.getQuery(sd.getName(), ac.getCurrentUser());
			sd.setCommodityProfileFilter(profileFilter);
		}

        if(callback!=null){
            try {
                Method countQuery = callback.getClass().getMethod("countRow",Query.class);
                /*if(countQuery.getDeclaringClass().equals(BomSearchExtractSSWriter.class)){
                    String queryString = sd.getExtractSource() + buildWhereClause(sd, parameters, parameterValues)+ buildGroupByClause(sd);
                    Query query = HibernateUtil.currentSession().createQuery(queryString);
                    bindParameters(query,parameterValues);
                    return callback.countRow(query);
                }*/
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

        }

		GenericResultSet rs = executeQuery(sd, sd.getExtractQueryType(), source + buildWhereClause(sd, parameters, parameterValues)+ buildGroupByClause(sd),
				parameterValues, -1, -1);
		long c = 0;
		if (rs.getValues().size() == 1) {
			Iterator<GenericResultRow> ritr = rs.getValues().iterator();
			if (ritr.hasNext()) {
				Object re = ritr.next().getObject(0);
				if(re instanceof BigDecimal) {
					BigDecimal b = (BigDecimal)re;
					c= b.longValue();
				}else {
					c = (Long) re;
				}
			}
		} else {
			c = rs.getValues().size();
		}
		return c;
	}
	
	public void executeExtractRowCountDataQuerySQL(SearchDefinition sd, Collection<SearchParameter> parameters,
                                                   Map<String, Object> presetParameterValues, ApplicationContext ac, HttpServletResponse response, int maxRow, ExtractWriter extractWriter, Map<String,Object> extractProps, String fileName) throws Error, Exception {

		HashMap<String, Object> parameterValues;
		if (presetParameterValues == null) {
			parameterValues = new HashMap<String, Object>();
		} else {
			parameterValues = new HashMap<String, Object>(presetParameterValues);
		}
		String source = sd.getExtractSource();
		
		Query query = null;
		StringBuilder finalQuery = new StringBuilder(SQL_COUNT_QUERY_1);
		finalQuery.append(sd.getExtractSource()+buildWhereClause(sd, parameters, parameterValues)+ buildOrderByClause(sd));
		finalQuery.append(") R )");
		
		query = session.createNativeQuery(finalQuery.toString());
		query.setReadOnly(pcmConfigUtil.getBoolean("pcm.search.readOnlyQuery", true));
		executeExtractSQL(query, parameterValues,response,maxRow,extractWriter,extractProps,fileName);
	}

	public GenericResultSet executeQuery(SearchDefinition sd, Collection<SearchParameter> parameters,
			Map<String, Object> presetParameterValues, int start, int len) {
		// Since we just use the values plus whatever is in
		// the parameters, if the preset values are null,
		// create a working copy to use, otherwise copy the values in.
		HashMap<String, Object> parameterValues;
		if (presetParameterValues == null) {
			parameterValues = new HashMap<String, Object>();
		} else {
			parameterValues = new HashMap<String, Object>(presetParameterValues);
		}

		return executeQuery(sd, sd.getSourceQueryType(),
				sd.getSource() + buildWhereClause(sd, parameters, parameterValues) + buildGroupByClause(sd)+buildOrderByClause(sd), parameterValues, start, len);
	}

	public GenericResultSet executeUserQuery(String selectClause, SearchDefinition sd,
			Collection<SearchParameter> parameters, Map<String, Object> presetParameterValues, int start, int len) {
		// Since we just use the values plus whatever is in
		// the parameters, if the preset values are null,
		// create a working copy to use, otherwise copy the values in.
		HashMap<String, Object> parameterValues;
		if (presetParameterValues == null) {
			parameterValues = new HashMap<String, Object>();
		} else {
			parameterValues = new HashMap<String, Object>(presetParameterValues);
		}

		return executeQuery(sd, sd.getExtractQueryType(),
				selectClause + buildWhereClause(sd, parameters, parameterValues) +buildGroupByClause(sd)+ buildOrderByClause(sd), parameterValues, start, len);
	}

	protected String buildOrderByClause(SearchDefinition sd) {
		StringBuilder order = new StringBuilder(100);

		Iterator<String> orderBy = sd.getOrderByFields().iterator();
		while (orderBy.hasNext()) {

			String field = orderBy.next();
			SearchDefinition.Order sort = sd.getOrderBy(field);
			if (sort != SearchDefinition.Order.NOTSET) {
				String[] fieldParts = StringUtils.split(field, ",");
				for (String part : fieldParts) {
					order.append((order.length() == 0) ? " order by " : " , ");
					order.append(part).append(" ");
					order.append(sort.name());
				}
			}
		}
		return order.toString();
	}

	protected String buildGroupByClause(SearchDefinition sd) {
		String group = " ";
		if(sd.getGroupBy()!=null && !sd.getGroupBy().isEmpty()) {
			group = " group by "+ sd.getGroupBy();
		}
		return group;
	}
	 
	protected String buildWhereClause(SearchDefinition sd, Collection<SearchParameter> parameters,
			Map<String, Object> parameterValues) {
		StringBuilder where = new StringBuilder(1000);
		// Process the filters
		Iterator<Map.Entry<String, SearchExpression>> fItr = sd.getExpressions().entrySet().iterator();
		while (fItr.hasNext()) {
			Map.Entry<String, SearchExpression> entry = fItr.next();
			if (entry.getValue().getOperator() == SearchExpression.OperatorType.FIXED || parameterValues.containsKey(entry.getKey())) {
				where.append((where.length() == 0) ? " where " : " and ");
				where.append(entry.getValue().getExpression());
			}

		}

		Iterator<SearchParameter> itr = parameters.iterator();
		while (itr.hasNext()) {
			SearchParameter sp = itr.next();
			if (sd.getName().equals("SearchDefItemOnly") || sd.getName().equals("SearchDefItem")) {
				if (sp.getName().equalsIgnoreCase("eolType") && sp.getValue() == null) {
					String work = " im.eolType is null ";
					where.append((where.length() == 0) ? " where " : " and ");
					where.append(work);
				}
			}
			if (sp.hasValue()) {
				String work = sp.getSearchExpression().getExpression();
				// Skip parameters with empty expressions (handled externally by Action/Controller classes)
				if (StringUtils.isBlank(work)) {
					continue;
				}
				where.append((where.length() == 0) ? " where " : " and ");
				DefaultExpressions de = sp.getDefaultExpressions();
				boolean isDefaultExpression = false;
				if (de != null) {
					Map<String, String> exprs = de.getDefaultExprs();
					String rawValue = (String) sp.getValueForSQL();
					if (rawValue != null && exprs.containsKey(rawValue)) {
						work = exprs.get(rawValue);
						where.append(work);
						isDefaultExpression = true;
					} else if (rawValue != null && rawValue.length() > 2) {
						String op = rawValue.substring(0, 2);
						if (op.equals("!=")) {
							String searchTerm = rawValue.substring(2, rawValue.length());
							work = exprs.get(op);
							where.append(work);
							parameterValues.put(sp.getName(), searchTerm);
							isDefaultExpression = true;
						}
					}
				}

				if(!isDefaultExpression) {
					work = StringUtils.replace(work, "{VALUE}", sp.getValue().toString());
					where.append(work);
					// where.append(sp.getSearchExpression().getExpression());
					if ("itemState".equals(sp.getName())) {
						if(work.contains(":itemStateBool")) {
							Object value = ((List<Object>)sp.getValue()).get(0);
							parameterValues.put("itemStateBool", value);
						}
						if(work.contains(":itemStateStr")) {
							Object value = ((List<Object>)sp.getValue()).get(1);
							parameterValues.put("itemStateStr", value);
						}
					} else {
						if(sp instanceof SearchParameterSelect && ((SearchParameterSelect) sp).getMultiValue()) {
							String rawValue = sp.getValueForSQL().toString();
							String[] values = rawValue.split("\\|");
							IntStream.range(0,values.length).forEach(i -> parameterValues.put(sp.getName()+i, values[i]));
						}else {
							parameterValues.put(sp.getName(), processParameterValue(sp));
						}
					}
				}
			}
		}

		String profileFilter = "";

		if (commodityProfileService.getCommoditytProfileFilterList().contains(sd.getName())) {
			if (sd.getCommodityProfileFilter() != null) {
				profileFilter = sd.getCommodityProfileFilter();
			}
		}

		if (where.toString().isEmpty() && !profileFilter.isEmpty()) {
			profileFilter = " where "
					+ profileFilter.substring(profileFilter.toLowerCase().indexOf("and") + 3, profileFilter.length());
		}

		return where.toString() + profileFilter;
	}

	protected Object processParameterValue(SearchParameter sp) {
		Object rawValue = sp.getValueForSQL();
		if (rawValue instanceof String) {
			String value = (String) rawValue;
			value = StringUtils.strip(value);
			if (value.startsWith("{") && value.endsWith("}")) {
				String temp = StringUtils.substringBetween(value, "{", "}");
				return temp.split(",");
			}
			// If LIKE or ILIKE we will default a ending wildcard
			// as well as make the conversions of * to %
			if (sp.getMatchType() == SearchParameter.MatchType.LIKE
					|| sp.getMatchType() == SearchParameter.MatchType.ILIKE) {
				value = value.replace('*', '%');
				if (value.startsWith("\"") && value.endsWith("\"")) {
					value = StringUtils.removeStart(value, "\"");
					value = StringUtils.removeEnd(value, "\"");
				} else if (value.contains("%") == false) {
					value += "%";
				}
				return value;
			}
		}
		return rawValue;
	}

	public GenericResultSet executeQuery(SearchDefinition sd, String queryType, String queryString,
			Map<String, Object> criteriaValues, int start, int len) {
		Query query = null;
		if (queryType != null && queryType.equalsIgnoreCase(QUERY_TYPE_SQL)) {
			query = session.createNativeQuery(queryString);
		} else {
			query = session.createQuery(queryString);
		}

		if ("SearchDefCostRecord".equals(sd.getName())) {
			if (queryString.startsWith("select count(*)"))
				query.addQueryHint("result_cache");
			else {
				query.addQueryHint("opt_param('optimizer_index_cost_adj',2)");
			}
		}

		query.setReadOnly(pcmConfigUtil.getBoolean("pcm.search.readOnlyQuery", true));
		return execute(query, criteriaValues, start, len);
	}
	
	public GenericResultSet executeQuery(String queryString, Map<String, Object> criteriaValues, int start, int len) {
		Query query = session.createQuery(queryString);
		query.setReadOnly(pcmConfigUtil.getBoolean("pcm.search.readOnlyQuery", true));
		return execute(query, criteriaValues, start, len);
	}

	protected void executeExtractSQL(Query query, Map<String, Object> criteriaValues, HttpServletResponse response, int maxRow, ExtractWriter extractWriter, Map<String,Object> extractProps, String fileName)  throws Error, Exception {
		bindParameters(query, criteriaValues);
		
		boolean firstCall = true;
		
		if (queryTimeout > 0) {
			query.setTimeout(queryTimeout);
		}
		StopWatch timer = new StopWatch();
		if (pcmConfigUtil.getBoolean("pcm.search.showPerformanceStats", false)) {
			timer.start();
		}
		
		ScrollableResults sr = null;
		GenericResultSet results = null;
		int cnt = 0;
		try {
			sr = query.scroll();

			if (pcmConfigUtil.getBoolean("pcm.search.showPerformanceStats", false)) {
				timer.split();
				perfLogger.info("Executed in: " + timer + " SQL:" + query.getQueryString());
				timer.unsplit();
			}

			String[] colNames = buildColumnNames(query);
			results = new GenericResultSet(colNames);
			if (callback != null) {
				callback.start(colNames);
			}
			while (sr.next()) {
				Object row = sr.get();
				GenericResultRow grr = new GenericResultRow(row);
				if(firstCall) {
					long count = 0;
					firstCall = false;
					if(grr.getValues().size()>0) {
						Object countValue = grr.getObject(grr.getValues().size()-1);
						if(countValue instanceof BigDecimal) {
							BigDecimal b = (BigDecimal)countValue;
							count = b.longValue();
						}else {
							count = (Long) countValue;
						}
					}
					if(count > maxRow) {
						throw new Exception("MAX_ROW_EXCEED_ERROR");
					}
					response.reset();
					response.setDateHeader("Expires", 0);
					response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0, no-store");
					response.setHeader("Pragma", "public");
					response.setCharacterEncoding("UTF-8");
					response.setContentType("application/force-download");
					response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
					extractWriter.setStream(response.getOutputStream(), "UTF-8");
				}
				if (callback.onRow(grr) == false) {
					break;
				}
				if (cnt % pcmConfigUtil.getInteger("pcm.search.clearCacheCount", 500) == 0) {
					session.flush();
					session.clear();
				}
				cnt++;
				if (pcmConfigUtil.getBoolean("pcm.search.showPerformanceStats", false)) {
					if (cnt % 100 == 0) {
						timer.split();
						perfLogger.info("processed " + cnt + " records in " + timer);
						timer.unsplit();
					}
				}
			}
			if (callback != null) {
				callback.end(colNames);
			}
		} finally {
			if (sr != null) {
				sr.close();
			}
			if (pcmConfigUtil.getBoolean("pcm.search.showPerformanceStats", false)) {
				timer.stop();
			}
		}
	}
	
	protected GenericResultSet execute(Query query, Map<String, Object> criteriaValues, int start, int len)  throws Error {
		bindParameters(query, criteriaValues);

		if (start > -1) {
			query.setFirstResult(start);
		}
		if (len > -1) {
			query.setMaxResults(len);
			query.setFetchSize(len);
		}
		if (queryTimeout > 0) {
			query.setTimeout(queryTimeout);
		}
		StopWatch timer = new StopWatch();
		if (pcmConfigUtil.getBoolean("pcm.search.showPerformanceStats", false)) {
			timer.start();
		}
		/**
		 * We se a list to get around the ORA-01791 issue. When you do an iterator
		 * hibernate only selects the key with the order by which Oracle does not like.
		 **/
		ScrollableResults sr = null;
		GenericResultSet results = null;
		int cnt = 0;
		try {
			sr = query.setCacheable(false).scroll(ScrollMode.FORWARD_ONLY);

			if (pcmConfigUtil.getBoolean("pcm.search.showPerformanceStats", false)) {
				timer.split();
				perfLogger.info("Executed in: " + timer + " SQL:" + query.getQueryString());
				timer.unsplit();
			}

			String[] colNames = buildColumnNames(query);
			results = new GenericResultSet(colNames);
			if (callback != null) {
				callback.start(colNames);
			}
			while (sr.next()) {
				Object row = sr.get();
				GenericResultRow grr = new GenericResultRow(row);
				if (callback == null) {
					results.add(grr);
				} else {
					if (callback.onRow(grr) == false) {
						break;
					}
					// Clear the Hibernate 1st level cache after a predefined number of rows
					// to improve performance
					if (cnt % pcmConfigUtil.getInteger("pcm.search.clearCacheCount", 500) == 0) {
						session.flush();
						session.clear();
					}
				}
				cnt++;

				if (pcmConfigUtil.getBoolean("pcm.search.showPerformanceStats", false)) {
					if (cnt % 100 == 0) {
						timer.split();
						perfLogger.info("processed " + cnt + " records in " + timer);
						timer.unsplit();
					}
				}
			}
			if (callback != null) {
				callback.end(colNames);
			}

		} finally {
			if (sr != null) {
				sr.close();
			}
			if (pcmConfigUtil.getBoolean("pcm.search.showPerformanceStats", false)) {
				timer.stop();
			}
		}

		return results;
	}

	protected void bindParameters(Query query, Map<String, Object> criteriaValues) throws Error{
        ParameterMetadata parameterMetadata = query.getParameterMetadata();
		for (var paramName : parameterMetadata.getParameters()) {
			Object values = criteriaValues.get(paramName.getName());
			if (values == null) {
				query.setParameter(paramName, values);
			} else {
				if (values instanceof Object[]) {
					int numValues = ((Object[]) values).length;
					if (numValues > MAX_PARAM_LIST) {
						throw new Error(
								"You may not have more than " + MAX_PARAM_LIST + " items in a search parameter array");
					}
					if (numValues > 0) {
						Object object0 = ((Object[]) values)[0];
						if (object0 instanceof Long) {
							query.setParameterList(paramName, (Object[]) values, Long.class);
						} else if (object0 instanceof Date) {
							query.setParameterList(paramName, (Object[]) values, Timestamp.class);
						} else {
							query.setParameterList(paramName, (Object[]) values, String.class);
						}
					}
				} else if (values instanceof Collection) {
					int numValues = ((Collection) values).size();
					if (numValues > MAX_PARAM_LIST) {
						throw new Error("You may not have more than " + MAX_PARAM_LIST
								+ " items in a search parameter list" + " (" + numValues + ")");
					}
					if (numValues > 0) {
						Object object0 = ((Collection) values).toArray()[0];
						if (object0 instanceof Long) {
							query.setParameterList(paramName, (Collection<?>) values, Long.class);
						} else if (object0 instanceof Date) {
							query.setParameterList(paramName, (Collection<?>) values, Date.class);
						} else if (object0 instanceof Boolean) {
							query.setParameterList(paramName, (Collection<?>) values, Boolean.class);
						} else {
							query.setParameterList(paramName, (Collection<?>) values, String.class);
						}
					}
				} else if (values instanceof Date) {
					query.setParameter(paramName, values);
				} else {
					query.setParameter(paramName.getName(), values);
				}
			}
		}
	}

	public static String[] buildColumnNames(Query query) {
		if((query instanceof NativeQuery<?>)) {
			return new String[] {"0"};
		}else {
			try {
				Class<?> queryClass = query.getClass();

				try {
					Method method = queryClass.getMethod("getSelectedAliases");
					String[] aliases = (String[]) method.invoke(query);
					if (aliases != null && aliases.length > 0) {
						return aliases;
					}
				} catch (NoSuchMethodException e) {
					logger.debug("getSelectedAliases not available");
				}
				
				// Fallback: Try to inspect the query's selection list
				try {
					// Access the query's internal query spec
					java.lang.reflect.Field selectionField = queryClass.getDeclaredField("querySpec");
					if (selectionField != null) {
						selectionField.setAccessible(true);
						Object querySpec = selectionField.get(query);
						
						// Get the select list from querySpec
						java.lang.reflect.Field selectListField = querySpec.getClass().getDeclaredField("selectList");
						if (selectListField != null) {
							selectListField.setAccessible(true);
							List<?> selectList = (List<?>) selectListField.get(querySpec);
							
							if (selectList != null && selectList.size() > 0) {
								String[] columnNames = new String[selectList.size()];
								for (int i = 0; i < selectList.size(); i++) {
									Object item = selectList.get(i);
									columnNames[i] = "col_" + i;
									
									// Try to get a meaningful name if available
									try {
										java.lang.reflect.Field aliasField = item.getClass().getDeclaredField("alias");
										if (aliasField != null) {
											aliasField.setAccessible(true);
											String alias = (String) aliasField.get(item);
											if (alias != null && !alias.isEmpty()) {
												columnNames[i] = alias;
											}
										}
									} catch (Exception e) {
										// Use default name
									}
								}
								return columnNames;
							}
						}
					}
				} catch (Exception e) {
					logger.debug("Could not access querySpec: {}", e);
				}
				
				// Final fallback: Return generic column names based on result count
				logger.debug("Using fallback generic column names");
				return new String[] {"col_0"};
				
			} catch (Exception e) {
				logger.warn("Error building column names: {}", e);
			}
		}
        return null;
	}

	public String getQueryString(String selectClause, SearchDefinition sd, Collection<SearchParameter> parameters,
			Map<String, Object> parameterValues) {
		return selectClause + buildWhereClause(sd, parameters, parameterValues) + buildGroupByClause(sd)
				+ buildOrderByClause(sd);
	}

	public Query getQuery(SearchDefinition sd, Collection<SearchParameter> parameters,
			Map<String, Object> presetParameterValues) throws Error {
		HashMap<String, Object> parameterValues;
		if (presetParameterValues == null) {
			parameterValues = new HashMap<String, Object>();
		} else {
			parameterValues = new HashMap<String, Object>(presetParameterValues);
		}

		String queryString = sd.getSource() + buildWhereClause(sd, parameters, parameterValues)
			+  buildGroupByClause(sd) + buildOrderByClause(sd);

		Query query = session.createQuery(queryString);
		bindParameters(query, parameterValues);
		return query;
	}

	/**
	 * @return the readOnlyQuery
	 */
	public boolean isReadOnlyQuery() {
		return pcmConfigUtil.getBoolean("pcm.search.readOnlyQuery", true);
	}

	/**
	 * @param readOnlyQuery
	 *            the readOnlyQuery to set
	 */
	public void setReadOnlyQuery(boolean readOnlyQuery) {
		this.readOnlyQuery = readOnlyQuery;
	}
	
	public GenericResultSet executeExtractQuery(SearchDefinition sd, Collection<SearchParameter> parameters,
			Map<String, Object> presetParameterValues, int start, int len) {
		// Since we just use the values plus whatever is in
		// the parameters, if the preset values are null,
		// create a working copy to use, otherwise copy the values in.
		HashMap<String, Object> parameterValues;
		if (presetParameterValues == null) {
			parameterValues = new HashMap<String, Object>();
		} else {
			parameterValues = new HashMap<String, Object>(presetParameterValues);
		}

		return executeQuery(sd, sd.getSourceQueryType(), sd.getExtractSource()
				+ buildWhereClause(sd, parameters, parameterValues) + buildGroupByClause(sd) + buildOrderByClause(sd),
				parameterValues, start, len);
	}

    // For TAM Download only - not used for regular search results
    public long getQueryCount (String source, SearchDefinition sd, Collection<SearchParameter> parameters,
                           Map<String, Object> presetParameterValues, String groupBy, String noSupplierHQLQuery) {
        HashMap<String, Object> parameterValues;
        if (presetParameterValues == null) {
            parameterValues = new HashMap<String, Object>();
        } else {
            parameterValues = new HashMap<String, Object>(presetParameterValues);
        }

        String queryString =
                source + buildWhereClause(sd, parameters, parameterValues, noSupplierHQLQuery) + groupBy;

        Query query = session.createQuery(queryString);
        bindParameters(query, parameterValues);
        return query.uniqueResult() != null ? (Long) query.uniqueResult() : 0L;
    }

    protected String buildWhereClause (SearchDefinition sd, Collection<SearchParameter> parameters,
                                       Map<String, Object> parameterValues, String noSupplierHQLQuery) {
        StringBuilder where = new StringBuilder(1000);
        // Process the filters
        Iterator<Map.Entry<String, SearchExpression>> fItr = sd.getExpressions().entrySet().iterator();
        while (fItr.hasNext()) {
            Map.Entry<String, SearchExpression> entry = fItr.next();
            if (entry.getValue().getOperator() == SearchExpression.OperatorType.FIXED
                    || parameterValues.containsKey(entry.getKey())) {
                where.append((where.length() == 0) ? " where " : " and ");
                where.append(entry.getValue().getExpression());
            }

        }

        Iterator<SearchParameter> itr = parameters.iterator();
        while (itr.hasNext()) {
            SearchParameter sp = itr.next();
            if (sp.hasValue()) {
                if (sp.getName().equals("noSupplier") && noSupplierHQLQuery != null) {
                    where.append((where.length() == 0) ? " where " : " and ");
                    String work = noSupplierHQLQuery;
                    work = StringUtils.replace(work, "{VALUE}", sp.getValue().toString());
                    where.append(work);
                    parameterValues.put(sp.getName(), processParameterValue(sp));
                } else {
                    where.append((where.length() == 0) ? " where " : " and ");
                    String work = sp.getSearchExpression().getExpression();
                    work = StringUtils.replace(work, "{VALUE}", sp.getValue().toString());
                    where.append(work);
                    parameterValues.put(sp.getName(), processParameterValue(sp));
                }
            }
        }

        return where.toString();
    }

	/**
     * Finds the index of the main FROM clause in the query,
     * skipping any "from" keywords inside parenthesized subqueries.
     */
    private int findMainFromIndex(String source) {
        int depth = 0;
        String lower = source.toLowerCase();
        for (int i = 0; i < lower.length(); i++) {
            char c = lower.charAt(i);
            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
            } else if (depth == 0 && lower.startsWith("from ", i)) {
                // Ensure this "from" is not at the very start (i.e., it's the main FROM after SELECT projections)
                if (i > 0) {
                    return i;
                }
            }
        }
        // Fallback: use original behavior
        return source.indexOf("from ");
    }
}