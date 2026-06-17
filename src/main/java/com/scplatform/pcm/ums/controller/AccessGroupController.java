

/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ums.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.businessEntity.repository.BusinessEntityRepository;
import com.scplatform.pcm.businessEntity.service.BusinessEntityService;
import com.scplatform.pcm.role.repository.RoleRepository;
import com.scplatform.pcm.role.service.RoleService;
import com.scplatform.pcm.ums.service.UMSService;
import com.scplatform.pcm.user.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.scplatform.pcm.role.entity.Role;
import com.scplatform.pcm.ums.dto.UMSAccessControl;
import com.scplatform.pcm.ums.dto.UMSAccessControlsResponse;
import com.scplatform.pcm.ums.dto.UMSAccessGroup;
import com.scplatform.pcm.ums.dto.UMSAccessGroupResponse;
import com.scplatform.pcm.ums.dto.UMSError;
import com.scplatform.pcm.ums.dto.UMSErrorResponse;
import com.scplatform.pcm.ums.dto.UMSResponse;
import com.scplatform.pcm.ums.dto.UMSUserResponse;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.user.entity.Users;
import lombok.RequiredArgsConstructor;

/**
 * Controller for querying businesses
 * 
 */
@Controller
@RequestMapping("/access_groups")
@RequiredArgsConstructor
public class AccessGroupController {

	private final BusinessEntityService businessEntityService;
	private final UserService userService;
    private final UMSService umsService;
    private final BusinessEntityRepository businessEntityRepository;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
	private final static Logger logger = LogManager.getLogger(AccessGroupController.class);

	@Autowired
	private PcmConfigUtil pcmConfigUtil;

    /**
     * Get all the businesses
     * 
     * If accessGroups are included in the filter, then only get the businesses where the business Id equals the
     * accessGroup
     * 
     * @return UMSResponse
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<UMSResponse> getAcessGroups(@RequestParam MultiValueMap<String, String> params) {
        logger.info("Getting all companies");

        HttpStatus httpStatus = HttpStatus.OK;

        UMSAccessGroupResponse accessGroupResponse = new UMSAccessGroupResponse();
        UMSResponse response = accessGroupResponse;

        try {
            boolean includeAll = pcmConfigUtil.getBoolean("pcm.ums.accessgroups.includeAll", false);
            if (params.getFirst(umsService.INCLUDE_ALL) != null) {
                includeAll = Boolean.valueOf(params.getFirst(umsService.INCLUDE_ALL));
            }

            String accessGroups = params.getFirst(umsService.ACCESS_GROUPS);
            List<String> keys = null;
            if (accessGroups != null) {
                keys = new ArrayList<String>(Arrays.asList(accessGroups.split(",")));
            }

            Map<String, Object> results = businessEntityService.findBusinessesByKeys(includeAll, keys, params);
            List<BusinessEntity> businesses = (List<BusinessEntity>) results.get("resultList");
            accessGroupResponse.setTotalSize((Integer) results.get("count"));
            for (BusinessEntity be : businesses) {
                UMSAccessGroup accessGroup = new UMSAccessGroup();
                accessGroup.setAccessGroupId(be.getBusinessEntityKey().toString());
                accessGroup.setAccessGroupDisplayName(businessEntityService.getAccessGroupDisplayName(be));
                accessGroup.setAccessGroupDescription(be.getBusinessEntityDesc());
                accessGroupResponse.addAccessGroup(accessGroup);
            }
        } catch (Exception ex) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            response = new UMSErrorResponse(ex.getMessage());
            logger.warn(ex);
        }

        return new ResponseEntity<UMSResponse>(response, httpStatus);
    }

    /**
     * Get the business identified by accessGroupId
     * 
     * @param accessGroupId
     * @return UMSResponse
     */
    @RequestMapping(value = "/{accessGroupId}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getAccessGroup(@PathVariable String accessGroupId) {
        logger.info("Getting company for companyId " + accessGroupId);

        HttpStatus httpStatus = HttpStatus.OK;

        UMSResponse response = null;

        try {
            BusinessEntity be = businessEntityService.getBusinessEntity(Long.valueOf(accessGroupId));
            if (be != null) {
                UMSAccessGroup accessGroup = new UMSAccessGroup();
                accessGroup.setAccessGroupId(be.getBusinessEntityKey().toString());
                accessGroup.setAccessGroupDisplayName(businessEntityService.getAccessGroupDisplayName(be));
                accessGroup.setAccessGroupDescription(be.getBusinessEntityDesc());
                return new ResponseEntity<UMSAccessGroup>(accessGroup,httpStatus);
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
                UMSErrorResponse error = new UMSErrorResponse(UMSError.AccessGroup.getError(accessGroupId));
                response = error;
                logger.warn(error.getErrors());
            }
        } catch (Exception ex) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            response = new UMSErrorResponse(ex.getMessage());
            logger.warn(ex);
        }

        return new ResponseEntity<UMSResponse>(response, httpStatus);
    }

    /**
     * Get the users for the business identified by groupName
     * 
     * @param accessGroupId
     * @param params
     * @return UMSResponse
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/{accessGroupId}/access_controls", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<UMSResponse> getAccessControls(@PathVariable String accessGroupId,
            @RequestParam MultiValueMap<String, String> params) {
        logger.info("Getting users for accessGroupId " + accessGroupId);

        HttpStatus httpStatus = HttpStatus.OK;

        UMSAccessControlsResponse accessControlsResponse = new UMSAccessControlsResponse();
        UMSResponse response = accessControlsResponse;

        try {
            BusinessEntity be = businessEntityService.getBusinessEntity(Long.valueOf(accessGroupId));
            if (be != null) {
                String searchText = params.getFirst(umsService.SEARCH_TEXT);

                // pass in the be so we can filter the ADMIN role if the business type is
                // not OPERATOR or ENTERPRISE
                Map<String, Object> results = roleService.findRolesByIdOrName(be, searchText, params);
                List<Role> roles = (List<Role>) results.get(umsService.RESULT_LIST);

                accessControlsResponse.setTotalSize((Integer) results.get(umsService.COUNT));
                for (Role role : roles) {
                    UMSAccessControl accessControl = new UMSAccessControl();
                    accessControl.setAccessControlId(role.getRoleId());
                    accessControl.setAccessControlDisplayName(role.getRoleName());
                    accessControl.setAccessControlDescription(role.getRoleName());
                    accessControlsResponse.addAccessControl(accessControl);
                }
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
                UMSErrorResponse error = new UMSErrorResponse(UMSError.AccessGroup.getError(accessGroupId));
                response = error;
                logger.warn(error.getErrors());
            }
        } catch (Exception ex) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            response = new UMSErrorResponse(ex.getMessage());
            logger.warn(ex);
        }

        return new ResponseEntity<UMSResponse>(response, httpStatus);
    }

    /**
     * Get the users for the business identified by accessGroupId
     * 
     * @param accessGroupId
     * @param params
     * @return UMSResponse
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/{accessGroupId}/users", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<UMSResponse> getAccessGroupUsers(@PathVariable String accessGroupId,
            @RequestParam MultiValueMap<String, String> params) {
        logger.info("Getting users for accessGroupId " + accessGroupId);

        HttpStatus httpStatus = HttpStatus.OK;

        UMSUserResponse usersResponse = new UMSUserResponse();
        UMSResponse response = usersResponse;

        try {
            BusinessEntity be = businessEntityService.getBusinessEntity(Long.valueOf(accessGroupId));
            if (be != null) {
                String searchText = params.getFirst(umsService.SEARCH_TEXT);
                Boolean activeOnly = Boolean.valueOf(params.getFirst(umsService.ACTIVE_ONLY));

                Map<String, Object> results = userService.findUsersForBusinessByKey(Long.valueOf(accessGroupId),
                        searchText, activeOnly, params);
                List<Users> users = (List<Users>) results.get(umsService.RESULT_LIST);

                usersResponse.setTotalSize((Integer) results.get(umsService.COUNT));
                for (Users user : users) {
                    usersResponse.addUser(user.getUserId());
                }
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
                UMSErrorResponse error = new UMSErrorResponse(UMSError.AccessGroup.getError(accessGroupId));
                response = error;
                logger.warn(error.getErrors());
            }
        } catch (Exception ex) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            UMSErrorResponse error = new UMSErrorResponse(ex.getMessage());
            response = error;
            logger.warn(ex);
        }

        return new ResponseEntity<UMSResponse>(response, httpStatus);
    }
}
