/*
 * Copyright (c) 2014 E2open Inc. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2014, by E2open Inc. All rights reserved.
 */

package com.scplatform.pcm.ums.controller;

import java.util.ArrayList;
import java.util.List;

import com.scplatform.pcm.ums.service.UMSService;
import com.scplatform.pcm.user.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.transaction.annotation.Transactional;

import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.role.entity.Role;
import com.scplatform.pcm.user.repository.UsersRepository;
import com.scplatform.pcm.role.repository.RoleRepository;
import com.scplatform.pcm.ums.dto.UMSErrorResponse;
import com.scplatform.pcm.ums.dto.UMSException;
import com.scplatform.pcm.ums.dto.UMSProvisionUserErrorResponse;
import com.scplatform.pcm.ums.dto.UMSResponse;
import com.scplatform.pcm.ums.dto.UMSUser;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.businessEntity.service.BusinessEntityService;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import lombok.RequiredArgsConstructor;

/**
 * Controller for adding users
 * 
 */
@Controller
@RequestMapping("/provision_user")
@RequiredArgsConstructor
public class ProvisionUserController {
    
	private final static Logger logger = LogManager.getLogger(ProvisionUserController.class);

    private final PcmConfigUtil pcmConfigUtil;
    private final UsersRepository usersRepository;
    private final RoleRepository roleRepository;
    private final BusinessEntityService businessEntityService;
    private final UMSService umsService;
    private final UserService userService;

    /**
     * Create the user
     * 
     * @param userName
     * @param umsUser
     * @return UMSErrorResponse
     */
    @RequestMapping(value = "{userName:.+}", method = RequestMethod.POST, headers = "content-type=application/json")
    @Transactional
    public @ResponseBody ResponseEntity<UMSResponse> createUser(@PathVariable String userName,
            @RequestBody UMSUser umsUser) {
        logger.info("Creating or updating user with userId " + userName);

        UMSResponse response = null;
        UMSProvisionUserErrorResponse provErrors = new UMSProvisionUserErrorResponse();
        HttpStatus httpStatus = HttpStatus.OK;

        try {

            Users user = usersRepository.findByUserId(userName).orElse(null);

            // create a new user if we are adding or replacing
            if (user == null) {

                user = new Users();
                user.setUserId(userName);

                // by default disable the user
                user.setIsEnabled(false);
            }

            String roleId = null;
            List<String> aclErrorStrings = new ArrayList<>();

            // default the role to viewer because a user
            // cannot be created without a role
            if (user.getRole() == null) {
                roleId = pcmConfigUtil.getString("pcm.user.provision.default.role", "VIEWER");
            }

            if (umsUser.getAccessControls() != null && !umsUser.getAccessControls().isEmpty()) {
                roleId = umsUser.getAccessControls().get(0).getAccessControlId();
            }

            // Set the role if we are creating a user or if a new role is specified
            if (roleId != null) {
                Role role = roleRepository.findByRoleIdIgnoreCase(roleId).orElse(null);
                if (role != null) {
                    user.setRole(role);
                } else {
                    httpStatus = HttpStatus.BAD_REQUEST;
                    aclErrorStrings.add(umsService.ACCESS_CONTROL_ID);
                }
            }

            // Set the business
            String accessGroupId = null;
            if (umsUser.getAccessControls() != null && !umsUser.getAccessControls().isEmpty()) {
                accessGroupId = umsUser.getAccessControls().get(0).getAccessGroupId();
            }
            if (accessGroupId != null) {
                try {
                    Long businessEntityKey = Long.valueOf(accessGroupId);
                    BusinessEntity be = businessEntityService.getBusinessEntity(businessEntityKey);
                    if (be != null) {
                        user.setBusinessEntity(be);
                    } else {
                        httpStatus = HttpStatus.BAD_REQUEST;
                        aclErrorStrings.add(umsService.ACCESS_GROUP_ID);
                    }
                } catch (NumberFormatException ex) {
                    httpStatus = HttpStatus.BAD_REQUEST;
                    aclErrorStrings.add(umsService.ACCESS_GROUP_ID);
                }
            }

            if (!aclErrorStrings.isEmpty()) {
                provErrors.getErrors().addAccessControl(roleId, accessGroupId, aclErrorStrings);
                throw new UMSException();
            }

            userService.mapUserEntityWithUMSUser(userName, umsUser, user, pcmConfigUtil);

            user.setIsEnabled(umsUser.isStatus());

            usersRepository.save(user);

        } catch (UMSException ex) {
            response = provErrors;
            logger.warn(provErrors.getErrors());
        } catch (Exception ex) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            response = new UMSErrorResponse(ex.getMessage());
            logger.warn(ex);
        }

        // send a response
        return new ResponseEntity<>(response, httpStatus);
    }
}