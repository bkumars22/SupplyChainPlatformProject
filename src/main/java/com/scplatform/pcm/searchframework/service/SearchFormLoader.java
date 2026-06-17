/*
 * Copyright (c) 2007 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2007, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.searchframework.service;


import com.scplatform.pcm.SpringContextHolder;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.searchframework.dto.DefaultExpressions;
import com.scplatform.pcm.searchframework.dto.SearchDefinition;
import com.scplatform.pcm.searchframework.dto.SearchExpression;
import com.scplatform.pcm.searchframework.dto.SearchParameter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchFormLoader extends DefaultHandler {

    private static final String DATE_ELEMENT = "Date";
    private static final String EXPRESSION_ELEMENT = "Expression";
    private static final String EXPR_ELEMENT = "Expr";
    private static final String FILTER_EXPRESSION_ELEMENT = "FilterExpression";
    private static final String LIST_VALUE_ELEMENT = "ListValue";
    private static final String LIST_ELEMENT = "List";
    private static final String TEXT_ELEMENT = "Text";
    private static final String MULTITEXT_ELEMENT = "MultiText";
    private static final String ORDER_BY_ELEMENT = "OrderBy";
    private static final String SOURCE_ELEMENT = "Source";
    private static final String EXTRACT_SOURCE_ELEMENT = "ExtractSource";
    private static final String PROPERTY_ELEMENT = "Property";
    private static final String GROUP_BY_ELEMENT = "GroupBy";

    private static final String TRANSFORM_ATTR = "transformDefinition";
    private static final String FINDER_NAME_ATTR = "finderName";
    private static final String POPUP_FINDER_NAME_ATTR = "popupFinderName";
    private static final String EXTRACT_WRITER_ATTR = "extractWriterClass";
    private static final String EXTRACT_WRITER_PROPS_ATTR = "extractWriterProps";
    private static final String EXTRACT_TEMPLATE_CONFIG_ATTR = "extractTemplateConfig";
    private static final String EXTRACT_TEMPLATE_ATTR = "extractTemplate";
    private static final String EXTRACT_TYPE_ATTR = "extractType";

    private static final String VALUE_ATTR = "value";
    private static final String LABEL_ATTR = "label";
    private static final String MULTI_SELECT_ATTR = "multiSelect";
    private static final String MULTI_VALUE_ATTR = "multiValue";
    private static final String LABEL_KEY_ATTR = "labelKey";
    private static final String NAME_ATTR = "name";
    private static final String ROW_ATTR = "rows";
    private static final String DELIMITER_ATTR = "delimiter";
    private static final String FIELD_NAME_ATTR = "fieldName";
    private static final String MATCH_TYPE_ATTR = "matchType";
    private static final String EXP_TYPE_ATTR = "type";
    private static final String INITIALIZER_ATTR = "initializer";
    private static final String INITIALIZER_PARAM_ATTR = "initializerData";
    private static final String COLUMM_NAME_ATTR = "colummName";
    private static final String DEFAULT_ATTR = "default";
    private static final String REQUIRED_ATTR = "required";
    private static final String DATA_TYPE = "dataType";
    private static final String DATA_FORMAT = "dataFormat";
    private static final String KEY_ATTR = "keys";
    private static final String QUERY_TYPE_ATTR = "queryType";
    private static final String COUNT_ALLIAS = "countAllias";
    private static final String COUNT_COLUMN = "countColumn";

    private static final String CHECK_IF_TRUE_ATTR = "checkIfTrue";

    private static Log logger = LogFactory.getLog(SearchFormLoader.class);

    private SearchDefinition searchDefinition;
    private List<SearchParameter> searchParameters;
    private StringBuffer elementBody = null;
    private StringBuffer Groupy = null;
    private SearchParameter currentParameter;
    private String currentFilterName = null;
    private String currentFilterType = null;
    private String currentDataType = null;
    private String currentDefaultExprName = null;
    private String currentDefaultExprValue = null;
    private Boolean ifTrue = true;

    private final PcmConfigUtil pcmConfigUtil;

    public SearchDefinition getSearchDefinition() {
        return searchDefinition;
    }

    public List<SearchParameter> getSearchParameters() {
        return searchParameters;
    }

    public void parse(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser;
        parser = factory.newSAXParser();
        parser.parse(inputStream, this);
    }

    public void startDocument() throws SAXException {
        searchDefinition = new SearchDefinition();
        searchParameters = new ArrayList<SearchParameter>();

        super.startDocument();
    }

    public void startElement(String uri, String localName, String qName, Attributes attr) throws SAXException {
        if (TEXT_ELEMENT.equals(qName) || MULTITEXT_ELEMENT.equals(qName) || DATE_ELEMENT.equals(qName)
                || LIST_ELEMENT.equals(qName)) {
            String ifTrueVal = attr.getValue(CHECK_IF_TRUE_ATTR);
            if (!StringUtils.isEmpty(ifTrueVal)) {
                ifTrue = false;
            }
        }

        if (ifTrue) {
            if ("SearchFormDefinition".equals(qName)) {
                searchDefinition.setName(attr.getValue(NAME_ATTR));
            } else if (SOURCE_ELEMENT.equals(qName)) {
                elementBody = new StringBuffer(500);
                searchDefinition.setKeys(attr.getValue(KEY_ATTR));
                searchDefinition.setSourceTransform(StringUtils.trimToNull(attr.getValue(TRANSFORM_ATTR)));
                searchDefinition.setSourceQueryType(StringUtils.trimToNull(attr.getValue(QUERY_TYPE_ATTR)));
            } else if (EXTRACT_SOURCE_ELEMENT.equals(qName)) {
                elementBody = new StringBuffer(500);
                searchDefinition.setExtractWriterClass(StringUtils.trimToNull(attr.getValue(EXTRACT_WRITER_ATTR)));
                searchDefinition.setExtractWriterProp(StringUtils.trimToNull(attr.getValue(EXTRACT_WRITER_PROPS_ATTR)));
                searchDefinition.setExtractWriterTransform(StringUtils.trimToNull(attr.getValue(TRANSFORM_ATTR)));
                searchDefinition.setExtractTemplateConfig(StringUtils.trimToNull(attr.getValue(EXTRACT_TEMPLATE_CONFIG_ATTR)));
                searchDefinition.setExtractTemplate(StringUtils.trimToNull(attr.getValue(EXTRACT_TEMPLATE_ATTR)));
                searchDefinition.setExtractType(StringUtils.trimToNull(attr.getValue(EXTRACT_TYPE_ATTR)));
                searchDefinition.setExtractQueryType(StringUtils.trimToNull(attr.getValue(QUERY_TYPE_ATTR)));
            } else if (ORDER_BY_ELEMENT.equals(qName)) {
                searchDefinition.addOrderBy(attr.getValue(FIELD_NAME_ATTR), attr.getValue(COLUMM_NAME_ATTR));
                searchDefinition.setOrderBy(attr.getValue(FIELD_NAME_ATTR), attr.getValue(DEFAULT_ATTR));
            } 
            else if (GROUP_BY_ELEMENT.equals(qName)) {
            	elementBody = new StringBuffer(500);
            }else if (TEXT_ELEMENT.equals(qName)) {
                currentParameter = new SearchParameterText(attr.getValue(NAME_ATTR), attr.getValue(LABEL_KEY_ATTR));
                currentParameter.setFinderName(attr.getValue(FINDER_NAME_ATTR));
                setCommon(currentParameter, attr);
            } else if (MULTITEXT_ELEMENT.equals(qName)) {
                currentParameter = new SearchParameterMultiText(attr.getValue(NAME_ATTR), attr.getValue(LABEL_KEY_ATTR), pcmConfigUtil.getString("pcm.web.search.multiValueDelimiter", ","));
                currentParameter.setFinderName(attr.getValue(FINDER_NAME_ATTR));
                currentParameter.setPopupFinderName(attr.getValue(POPUP_FINDER_NAME_ATTR));
                ((SearchParameterMultiText) currentParameter).setRows(getIntegerAttr(attr, ROW_ATTR));
                ((SearchParameterMultiText) currentParameter).setDelimiter(attr.getValue(this.DELIMITER_ATTR));
                setCommon(currentParameter, attr);
            }

            else if (DATE_ELEMENT.equals(qName)) {
                currentParameter = new SearchParameterDate(attr.getValue(NAME_ATTR), attr.getValue(LABEL_KEY_ATTR));
                currentParameter.setFinderName(attr.getValue(FINDER_NAME_ATTR));
                setCommon(currentParameter, attr);
            }

            else if (LIST_ELEMENT.equals(qName)) {
                currentParameter = new SearchParameterSelect(attr.getValue(NAME_ATTR), attr.getValue(LABEL_KEY_ATTR));
                ((SearchParameterSelect) currentParameter).setMultiSelect(getBooleanAttr(attr, MULTI_SELECT_ATTR));
                ((SearchParameterSelect) currentParameter).setMultiValue(getBooleanAttr(attr, MULTI_VALUE_ATTR));
                setCommon(currentParameter, attr);
            } else if (LIST_VALUE_ELEMENT.equals(qName)) {
                if (ifTrue) {
                    ((SearchParameterSelect) currentParameter).addSelectValue(attr.getValue(LABEL_ATTR),
                            attr.getValue(VALUE_ATTR));
                }
            } else if (EXPRESSION_ELEMENT.equals(qName)) {
                if (ifTrue) {
                    elementBody = new StringBuffer(100);
                }
			} else if (EXPR_ELEMENT.equals(qName)) {
				if (ifTrue) {
					elementBody = new StringBuffer(100);
					currentDefaultExprName = attr.getValue(NAME_ATTR);
				}
			} else if (FILTER_EXPRESSION_ELEMENT.equals(qName)) {
                currentFilterName = attr.getValue(NAME_ATTR);
                currentFilterType = attr.getValue(EXP_TYPE_ATTR);
                currentDataType = attr.getValue(DATA_TYPE);
                elementBody = new StringBuffer(100);
            } else if (PROPERTY_ELEMENT.equals(qName)) {
                currentParameter.setProperty(attr.getValue(NAME_ATTR), attr.getValue(VALUE_ATTR));

            }
        }

    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (SOURCE_ELEMENT.equals(qName)) {
            searchDefinition.setSource(StringUtils.trimToNull(elementBody.toString()));
            elementBody = null;
        } else if (EXTRACT_SOURCE_ELEMENT.equals(qName)) {
            searchDefinition.setExtractSource(StringUtils.trimToNull(elementBody.toString()));
            elementBody = null;
        } else if (FILTER_EXPRESSION_ELEMENT.equals(qName)) {
            if (elementBody == null) {
                throw new SAXException("Expression element has no body");
            }
            SearchExpression se = searchDefinition.addExpression(currentFilterName,
                    StringUtils.trimToEmpty(elementBody.toString()));
            se.setDataType(currentDataType);
            se.setOperator(findOperatorType(StringUtils.trimToNull(currentFilterType)));
        } else if (EXPRESSION_ELEMENT.equals(qName)) {
            if (ifTrue) {
                if (elementBody == null) {
                    throw new SAXException("Expression element has no body");
                }

                if (currentParameter == null) {
                    elementBody = null;
                    throw new SAXException("Expression element used outside of TEXT|DATE|LIST");
                } else {
                    currentParameter.setSearchExpression(new SearchExpression(StringUtils.trimToEmpty(elementBody
                            .toString())));
                    elementBody = null;
                }
            }
		} else if (EXPR_ELEMENT.equals(qName)) {
			if (ifTrue) {
				if (elementBody == null) {
					throw new SAXException("Expr element has no body");
				}

				if (currentParameter == null) {
					elementBody = null;
					throw new SAXException("Expr element used outside of TEXT|DATE|LIST");
				} else {
					DefaultExpressions de = currentParameter.getDefaultExpressions();
					if (de == null) {
						de = new DefaultExpressions();
					}
					currentDefaultExprValue = StringUtils.trimToEmpty(elementBody.toString());
					de.setDefaultExpr(currentDefaultExprName, currentDefaultExprValue);
					currentParameter.setDefaultExpressions(de);
					elementBody = null;
				}
			}
        } else if (TEXT_ELEMENT.equals(qName) || MULTITEXT_ELEMENT.equals(qName) || DATE_ELEMENT.equals(qName)
                || LIST_ELEMENT.equals(qName)) {
            if (ifTrue) {
                searchParameters.add(currentParameter);
                currentParameter = null;
            }
            ifTrue = true;
        } else if (GROUP_BY_ELEMENT.equals(qName)) {
            searchDefinition.setGroupBy(StringUtils.trimToNull(elementBody.toString()));
            elementBody = null;
        } 

    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        if (elementBody != null) {
            elementBody.append(ch, start, length);
        }
    }

    private boolean getBooleanAttr(Attributes attr, String name) {
        String value = attr.getValue(name);
        return BooleanUtils.toBoolean(value);
    }

    private int getIntegerAttr(Attributes attr, String name) {
        String value = attr.getValue(name);
        return NumberUtils.toInt(value, 1);
    }

    private void setCommon(SearchParameter parameter, Attributes attr) {
        parameter.setMatchType(findMatchType(StringUtils.trimToNull(attr.getValue(MATCH_TYPE_ATTR))));
        String className = attr.getValue(INITIALIZER_ATTR);
        if (StringUtils.isBlank(className) == false) {
            SearchParameterInitializer initializer = null;
            try {
                Class<?> c = Class.forName(className);
                try {
                    initializer = (SearchParameterInitializer) SpringContextHolder.getBean(c);
                } catch (Exception springEx) {
                    logger.debug("Bean not found in Spring context, trying reflection: " + className);
                    initializer = (SearchParameterInitializer) c.getDeclaredConstructor().newInstance();
                }
            } catch (ClassNotFoundException e) {
                logger.error("Unable to load initializer class: " + className, e);
            } catch (Exception e) {
                logger.error("Unable to instantiate initializer: " + className, e);
            }

            if (initializer != null) {
                String initParamData = StringUtils.trimToNull(attr.getValue(INITIALIZER_PARAM_ATTR));
                initializer.setInitialData(initParamData);
                parameter.setInitializer(initializer);
            }
        }
        parameter.setDataType(StringUtils.trimToNull(attr.getValue(DATA_TYPE)));
        parameter.setDataFormat(StringUtils.trimToNull(attr.getValue(DATA_FORMAT)));
        parameter.setRequired(BooleanUtils.toBoolean(attr.getValue(REQUIRED_ATTR)));
    }

    private SearchParameter.MatchType findMatchType(String name) {
        try {
            if (name != null) {
                return SearchParameter.MatchType.valueOf(name);
            } else {
                return SearchParameter.MatchType.EXACT;
            }
        } catch (Exception e) {
            logger.warn("Invalid match type:" + name, e);
        }
        return null;
    }

    private SearchExpression.OperatorType findOperatorType(String name) {
        try {
            if (name != null) {
                return SearchExpression.OperatorType.valueOf(name);
            }
        } catch (Exception e) {
            logger.warn("Invalid operator type:" + name, e);
        }
        return null;
    }
}
