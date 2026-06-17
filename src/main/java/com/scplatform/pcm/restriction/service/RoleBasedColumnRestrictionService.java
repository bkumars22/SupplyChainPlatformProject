/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.restriction.service;

import com.scplatform.pcm.restriction.dto.*;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Spring singleton service for managing role-based column restrictions
 * Automatically managed as singleton by Spring (@Service annotation)
 */
@Service
public class RoleBasedColumnRestrictionService {

    private static final Logger logger = LogManager.getLogger(RoleBasedColumnRestrictionService.class);

    private Restriction restriction = null;

    /**
     * Constructor - Spring will create only ONE instance of this service
     * Initializes the restriction configuration on first instantiation
     */
    public RoleBasedColumnRestrictionService() {
        initializeConfiguration();
    }

    /**
     * Initialize configuration from XML file
     */
    private void initializeConfiguration() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Restriction.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("config/RoleBasedColumnRestriction.xml")) {
                restriction = (Restriction) jaxbUnmarshaller.unmarshal(inputStream);
            }
        } catch (Exception e) {
            logger.error("Error initializing RoleBasedColumnRestrictionService: ", e);
        }
    }

    /**
     * Get the parsed restriction configuration
     * @return Restriction configuration object
     */
    public Restriction getRestrictionConfiguration() {
        if (restriction == null) {
            initializeConfiguration();
        }
        return restriction;
    }

    /**
     * Get list of restricted columns for a given screen and role
     * @param screenName the name of the screen
     * @param roleID the role ID
     * @return list of restricted column names
     */
    public List<String> getRestrictedColumnList(String screenName, String roleID) {
        List<String> restrictedColumnList = new ArrayList<>();
        List<Screen> screenList = getRestrictionConfiguration().getScreen();
        
        nextScreen:
        for (Screen screen : screenList) {
            if (screen.getName().equals(screenName)) {
                List<Condition> conditionList = screen.getCondition();
                
                nextCondition:
                for (Condition condition : conditionList) {
                    IfExist ifCheck = condition.getIfExist();
                    List<Element> ifElement = ifCheck.getElement();
                    boolean valueMatchFlag = false;
                    
                    for (Element elementIf : ifElement) {
                        if (elementIf.getName().equals("role")) {
                            List<String> roleIDList = Arrays.asList(elementIf.getValue().split(","));
                            valueMatchFlag = roleIDList.contains(roleID);
                        } else {
                            continue nextCondition;
                        }
                    }
                    
                    if (valueMatchFlag) {
                        Require requireCheck = condition.getRequire();
                        List<Element> elementRequire = requireCheck.getElement();
                        
                        for (Element elementReq : elementRequire) {
                            if (elementReq.getName().equals("restrictedColumns")) {
                                String[] columnArray = elementReq.getValue().split(",");
                                for (String columnName : columnArray) {
                                    restrictedColumnList.add(columnName.trim());
                                }
                            }
                        }
                        break nextScreen;
                    }
                }
            }
        }
        return restrictedColumnList;
    }
}
