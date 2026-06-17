/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.functionalGroup.controller;

import com.scplatform.pcm.functionalGroup.dto.FunctionalGroupForm;
import com.scplatform.pcm.searchframework.dto.SearchDefinition;
import com.scplatform.pcm.searchframework.dto.SearchExpression;
import com.scplatform.pcm.searchframework.dto.SearchParameter;
import com.scplatform.pcm.searchframework.exception.SearchFormException;
import com.scplatform.pcm.searchframework.service.SearchService;
import com.scplatform.pcm.tam.service.TAMAllocationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@Log4j2
@RequiredArgsConstructor
public class FunctionalGroupController {

    private final SearchService searchService;
    private final TAMAllocationService tamAllocationService;

    private static final String VIEW_FUNCTIONAL_GROUP_SEARCH = "/tam/functionalGroupManage";


    @RequestMapping("/manageFunctionalGroup")
    public String init(FunctionalGroupForm form, HttpServletRequest request, HttpServletResponse response,
                       Model model) throws Exception {
        Properties properties = new Properties();
        properties.put("definition", "SearchDefFunctionalGroup.xml");
        model.addAttribute("functionalGroupForm", form);
        searchService.init(properties, form, request, response);
        return VIEW_FUNCTIONAL_GROUP_SEARCH;
    }

    @RequestMapping("/searchManageFunctionalGroup")
    public String search(FunctionalGroupForm functionalGroupForm, HttpServletRequest request, HttpServletResponse response,
                         Model model) throws Exception {
        Properties properties = new Properties();
        functionalGroupForm = searchService.mergeRequestWithCachedForm(functionalGroupForm, request);
        model.addAttribute("functionalGroupForm", functionalGroupForm);

        SearchParameter hasSpecifiedParam = functionalGroupForm.getSearchParameter("hasSpecifiedFg");
        SearchParameter hasItemTypeParam = functionalGroupForm.getSearchParameter("itemType");
        SearchParameter parentItemParam = functionalGroupForm.getSearchParameter("parentItemNumbers");
        SearchParameter ODMPartParam = functionalGroupForm.getSearchParameter("ODMPartItemNumbers");
        SearchParameter itemStateParam = functionalGroupForm.getSearchParameter("itemState");
        Object itemStateParamValue = itemStateParam.getValue();
        Object hasSpecifiedValue = (String) hasSpecifiedParam.getValue();
        hasSpecifiedParam.setValue(null);
        SearchDefinition sd = functionalGroupForm.getSearchDefinition();
        Boolean parameterQueryOveridden = false;
        String itemNumberRealExpression = null;
        String itemNumbersRealExpression = null;
        Boolean itemTypeFixedHasValue = Boolean.FALSE;
        if(hasSpecifiedValue != null && hasSpecifiedValue.equals("yes")) {
            parameterQueryOveridden = true;
            SearchParameter itemNumberParam = functionalGroupForm.getSearchParameter("itemNumber");
            if(itemNumberParam.hasValue()) {
                SearchExpression se = itemNumberParam.getSearchExpression();
                itemNumberRealExpression = se.getExpression();
                se.setExpression(sd.getExpressions().get("includeFGItem").getExpression());
            }
            SearchParameter itemNumbersParam = functionalGroupForm.getSearchParameter("itemNumbers");
            if(itemNumbersParam.hasValue()) {
                SearchExpression se = itemNumbersParam.getSearchExpression();
                itemNumbersRealExpression = se.getExpression();
                se.setExpression(sd.getExpressions().get("includeFGItems").getExpression());
            }
        }


        if(parentItemParam.hasValue()) {
            sd.setSource(sd.getSource().replace("{0}", " inner join fg.parentItem parentItem"));
        }
        else
        {
            sd.setSource(sd.getSource().replace("{0}", ""));
        }

        if(ODMPartParam.hasValue()) {
            sd.setSource(sd.getSource().replace("{1}", " inner join fg.ODMPart ODMPart"));
        }
        else {
            sd.setSource(sd.getSource().replace("{1}", ""));
        }

        if(hasItemTypeParam.hasValue()) {
            itemTypeFixedHasValue = Boolean.TRUE;
            sd.getExpressions().get("itemTypeFilter").setOperator(SearchExpression.OperatorType.EQ);
        }

        itemEOLQueryBuilder(functionalGroupForm, sd);

        searchService.search(properties, functionalGroupForm, request, response);
        hasSpecifiedParam.setValue(hasSpecifiedValue);
        itemStateParam.setValue(itemStateParamValue);
        if(parameterQueryOveridden) {
            if(itemNumberRealExpression != null) {
                SearchParameter itemNumberParam = functionalGroupForm.getSearchParameter("itemNumber");
                itemNumberParam.getSearchExpression().setExpression(itemNumberRealExpression);
            }
            if(itemNumbersRealExpression != null) {
                SearchParameter itemNumbersParam = functionalGroupForm.getSearchParameter("itemNumbers");
                itemNumbersParam.getSearchExpression().setExpression(itemNumbersRealExpression);
            }
        }
        if(itemTypeFixedHasValue) {
            sd.getExpressions().get("itemTypeFilter").setOperator(SearchExpression.OperatorType.FIXED);
        }
        return VIEW_FUNCTIONAL_GROUP_SEARCH;
    }

    /**
     * REST API to check if TAM allocation data exists for a functional group.
     *
     * @param functionalGroupId the functional group ID to check
     * @return true if TAM allocation data exists with allocation > 0, false otherwise
     */
    @GetMapping("/mcm/api/checkTAMExistByFunctionalGroup")
    @ResponseBody
    public boolean checkTAMAllocationExist(@RequestParam("functionalGroupId") Long functionalGroupId) {
        log.debug("Checking TAM allocation existence for functional group ID: {}", functionalGroupId);
        try {
            boolean exists = tamAllocationService.checkIfTAMExistsForFunctionalGroup(functionalGroupId);
            log.debug("TAM allocation exist check result for FG ID {}: {}", functionalGroupId, exists);
            return exists;
        } catch (Exception e) {
            log.error("Error checking TAM allocation existence for functional group ID: {}", functionalGroupId, e);
            throw new RuntimeException("Failed to check TAM allocation existence", e);
        }
    }

    private void itemEOLQueryBuilder(FunctionalGroupForm functionalGroupForm, SearchDefinition sd) throws SearchFormException {
        SearchParameter itemStateParam = functionalGroupForm.getSearchParameter("itemState");
        if (itemStateParam.hasValue()) {
            StringBuilder eolQueryBuilder = new StringBuilder();
            Object paramValuesObj = itemStateParam.getValue();
            String[] paramValues = null;
            if (paramValuesObj instanceof Object[]) {
                Object[] paramValuesObj_arr = (Object[]) paramValuesObj;
                paramValues = new String[paramValuesObj_arr.length];
                for (int i = 0; i < paramValuesObj_arr.length; i++) {
                    paramValues[i] = (String) paramValuesObj_arr[i];
                }
            }
            Map<String, Boolean> setQueryTypeMap = new HashMap<>();
            String[] possibleQueries = {"itemEOLNULLCheck", "itemEOLCheck", "itemEOLTypeCheck"};
            Arrays.stream(possibleQueries).forEach(queryType -> setQueryTypeMap.put(queryType, Boolean.FALSE));
            eolQueryBuilder.append("(");
            List<Boolean> itemStateBool = new ArrayList<>();
            List<Object> itemStateFinalVal = new ArrayList<>();
            for (int i = 0; i < paramValues.length; i++) {
                if ("IS_NULL".equals(paramValues[i])) {
                    paramValues[i] = "";
                    setQueryTypeMap.put("itemEOLNULLCheck", Boolean.TRUE);
                }
                if ("ACTIVE".equals(paramValues[i]) || "INACTIVE".equals(paramValues[i])) {
                    Boolean isActive = ("ACTIVE".equals(paramValues[i])) ? true : false;
                    itemStateBool.add(!isActive);
                    paramValues[i] = "";
                    setQueryTypeMap.put("itemEOLCheck", Boolean.TRUE);
                } else if ("PRODUCTION_EOL".equals(paramValues[i]) || "SERVICE_EOL".equals(paramValues[i])) {
                    paramValues[i] = paramValues[i].replace('_',' ');
                    setQueryTypeMap.put("itemEOLTypeCheck", Boolean.TRUE);
                }
            }
            for (Map.Entry<String, Boolean> entry : setQueryTypeMap.entrySet()) {
                if (entry.getValue()) {
                    if (!eolQueryBuilder.toString().equals("("))
                        eolQueryBuilder.append(" or ");
                    eolQueryBuilder.append(sd.getExpressions().get(entry.getKey()).getExpression());
                }
            }
            eolQueryBuilder.append(")");
            SearchExpression se = itemStateParam.getSearchExpression();
            se.setExpression(eolQueryBuilder.toString());
            eolQueryBuilder.setLength(0);
            List<String> eolTypeList = Arrays.stream(paramValues).filter(paramValue -> !paramValue.isEmpty()).collect(Collectors.toList());
            itemStateFinalVal.add(itemStateBool);
            itemStateFinalVal.add(eolTypeList);
            itemStateFinalVal.add(paramValuesObj);
            itemStateParam.setValue(itemStateFinalVal);
        }
    }

}
