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
import java.util.Arrays;
import java.util.List;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.ums.service.UMSService;
import com.scplatform.pcm.user.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.scplatform.pcm.role.entity.Role;
import com.scplatform.pcm.role.repository.RoleRepository;
import com.scplatform.pcm.businessEntity.service.BusinessEntityService;
import com.scplatform.pcm.ums.dto.UMSError;
import com.scplatform.pcm.ums.dto.UMSErrorResponse;
import com.scplatform.pcm.ums.dto.UMSException;
import com.scplatform.pcm.ums.dto.UMSResponse;
import com.scplatform.pcm.ums.dto.UMSUser;
import com.scplatform.pcm.ums.dto.UMSUserAccessControl;
import com.scplatform.pcm.ums.dto.UMSUserAccessControlsResponse;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;

/**
 * Controller for finding user information and adding users
 * 
 */
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    
	private final static Logger logger = LogManager.getLogger(UserController.class);

	private final PcmConfigUtil pcmConfigUtil;
	private final UsersRepository usersRepository;
    private final UMSService umsService;
	private final RoleRepository roleRepository;
    private final UserService userService;
	private final BusinessEntityService businessEntityService;

    /**
     * Get the roles for the user identified by userName. Ignoring limit, offset parameters because a user is associated
     * to a single business and has a single role
     * 
     * @param userName
     * @return UMSUserAccessControlsResponse
     */
    @RequestMapping(value = "/{userName:.+}/access_controls", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<UMSResponse> getUserRoles(@PathVariable String userName,
            @RequestParam MultiValueMap<String, String> params) {
        logger.info("Getting roles for userId " + userName);

        HttpStatus httpStatus = HttpStatus.OK;

        UMSUserAccessControlsResponse userAcessControlsResponse = new UMSUserAccessControlsResponse();
        UMSResponse response = userAcessControlsResponse;
        userAcessControlsResponse.setTotalSize(0);

        try {
            Users user = usersRepository.findUserByUserId(userName);
            if (user == null) {
                logger.warn("The user " + userName + " does not exist");
                throw new UMSException();
            } else if (user.getIsEnabled()) {
                // verify the business for the user matches the accessGroups filter
                // if the filter is provided
                boolean businessFound = true;
                String accessGroups = params.getFirst(umsService.ACCESS_GROUPS);
                List<String> accessGroupKeys = new ArrayList<>();
                List<Long> businessEntityKeys = new ArrayList<>();
                if (accessGroups != null) {
                    accessGroupKeys = new ArrayList<>(Arrays.asList(accessGroups.split(",")));
                    for (String accessGroupKey : accessGroupKeys) {
                        try {
                            businessEntityKeys.add(Long.valueOf(accessGroupKey));
                        } catch (NumberFormatException ex) {
                            // do nothing
                        }
                    }
                    if (!businessEntityKeys.contains(user.getBusinessEntity().getBusinessEntityKey())) {
                        businessFound = false;
                        logger.warn("Current business " + user.getBusinessEntity().getBusinessEntityKey()
                                + " not in the accessGroups filter " + accessGroups);
                    }
                }

                if (businessFound) {
                    userAcessControlsResponse.setTotalSize(1);

                    UMSUserAccessControl userAccessControl = new UMSUserAccessControl();
                    userAccessControl.setAccessControlId(user.getRole().getRoleId());
                    userAccessControl.setAccessControlDisplayName(user.getRole().getRoleName());
                    userAccessControl.setAccessGroupId(user.getBusinessEntity().getBusinessEntityKey().toString());
                    userAccessControl.setAccessGroupName(businessEntityService.getAccessGroupDisplayName(user.getBusinessEntity()));
                    userAccessControl.setAccessGroupDescription(user.getBusinessEntity().getBusinessEntityDesc());
                    userAcessControlsResponse.addAccessControl(userAccessControl);
                }
            }
        } catch (UMSException ex) {
            httpStatus = HttpStatus.BAD_REQUEST;
            UMSErrorResponse error = new UMSErrorResponse(UMSError.UserName.getError(userName));
            response = error;
            logger.warn(error.getErrors());
        } catch (Exception ex) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            UMSErrorResponse error = new UMSErrorResponse(ex.getMessage());
            response = error;
            logger.warn(ex);
        }
        return new ResponseEntity<>(response, httpStatus);
    }

    /**
     * Assign a business and a role to a user
     * 
     * @param userName
     * @param accessGroupId
     * @param accessControlId
     * @return UMSErrorResponse
     */
    @RequestMapping(value = "/{userName:.+}/access_groups/{accessGroupId}/access_controls/{accessControlId:.+}", method = RequestMethod.PUT)
    @Transactional
    public @ResponseBody ResponseEntity<UMSResponse> createUserWithCompanyRole(@PathVariable String userName,
            @PathVariable String accessGroupId, @PathVariable String accessControlId) {
        logger.info("Adding company " + accessGroupId + " and role " + accessControlId + " for user with userId "
                + userName);

        UMSResponse response = null;
        UMSErrorResponse error = new UMSErrorResponse();
        HttpStatus httpStatus = HttpStatus.OK;

        try {

            Users user = usersRepository.findByUserId(userName).orElse(null);
            if (user == null) {
                httpStatus = HttpStatus.BAD_REQUEST;
                error.addError("userName");
                error.addError(UMSError.UserName.getError(userName));
                throw new UMSException();
            }

            Role role = roleRepository.findByRoleIdIgnoreCase(accessControlId).orElse(null);
            if (role == null) {
                httpStatus = HttpStatus.BAD_REQUEST;
                error.addError("accessControlId");
                error.addError(UMSError.Role.getError(accessControlId));
                throw new UMSException();
            }
            user.setRole(role);

            Long businessEntityKey = null;
            try {
                businessEntityKey = Long.valueOf(accessGroupId);
            } catch (NumberFormatException ex) {
                httpStatus = HttpStatus.BAD_REQUEST;
                error.addError("accessGroupId");
                error.addError(UMSError.AccessGroup.getError(accessGroupId));
                throw new UMSException();
            }
            BusinessEntity be = businessEntityService.getBusinessEntity(businessEntityKey);
            if (be == null) {
                httpStatus = HttpStatus.BAD_REQUEST;
                error.addError("accessGroupId");
                error.addError(UMSError.AccessGroup.getError(accessGroupId));
                throw new UMSException();
            }
            user.setBusinessEntity(be);

            // activate the user since there is no explicit api to activate the user
            user.setIsEnabled(true);

            usersRepository.save(user);

        } catch (UMSException ex) {
            response = error;
            logger.warn(error.getErrors());
        } catch (Exception ex) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            response = new UMSErrorResponse(ex.getMessage());
            logger.warn(ex);
        }

        return new ResponseEntity<>(response, httpStatus);
    }

    /**
     * Deactivate a user
     * 
     * @param userName
     * @param accessGroupId
     * @param accessControlId
     * @return UMSErrorResponse
     */
    @RequestMapping(value = "/{userName:.+}/access_groups/{accessGroupId}/access_controls/{accessControlId:.+}", method = RequestMethod.DELETE)
    @Transactional
    public @ResponseBody ResponseEntity<UMSResponse> delteCompanyRoleForUser(@PathVariable String userName,
            @PathVariable String accessGroupId, @PathVariable String accessControlId) {
        logger.info("Disabling user " + userName + " with company " + accessGroupId + " and role " + accessControlId);

        UMSResponse response = null;
        UMSErrorResponse error = new UMSErrorResponse();
        HttpStatus httpStatus = HttpStatus.OK;

        try {

            Users user = usersRepository.findByUserId(userName).orElse(null);
            if (user == null) {
                httpStatus = HttpStatus.BAD_REQUEST;
                error.addError("userName");
                error.addError(UMSError.UserName.getError(userName));
                throw new UMSException();
            }

            Role role = roleRepository.findByRoleIdIgnoreCase(accessControlId).orElse(null);
            if (role == null) {
                httpStatus = HttpStatus.BAD_REQUEST;
                error.addError("accessControlId");
                error.addError(UMSError.Role.getError(accessControlId));
                throw new UMSException();
            }

            if (!user.getRole().equals(role)) {
                httpStatus = HttpStatus.BAD_REQUEST;
                error.addError("accessControlId");
                error.addError(UMSError.Role.getError(accessControlId));
                throw new UMSException();
            }

            BusinessEntity be = businessEntityService.getBusinessEntity(Long.valueOf(accessGroupId));
            if (be == null) {
                httpStatus = HttpStatus.BAD_REQUEST;
                error.addError("accessGroupId");
                error.addError(UMSError.AccessGroup.getError(accessGroupId));
                throw new UMSException();
            }

            if (!user.getBusinessEntity().equals(be)) {
                httpStatus = HttpStatus.BAD_REQUEST;
                error.addError("accessGroupId");
                error.addError(UMSError.AccessGroup.getError(accessGroupId));
                throw new UMSException();
            }

            user.setIsEnabled(false);

            usersRepository.save(user);

        } catch (UMSException ex) {
            response = error;
            logger.warn(error.getErrors());
        } catch (Exception ex) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            response = new UMSErrorResponse(ex.getMessage());
            logger.warn(ex);
        }

        return new ResponseEntity<>(response, httpStatus);
    }

    /**
     * Update the user
     * 
     * @param userName
     * @param umsUser
     * @return UMSErrorResponse
     * @throws Exception
     */
    @RequestMapping(value = "/{userName:.+}", method = RequestMethod.PUT, headers = "Accept=application/json,application/xml", produces = "application/json")
    @Transactional
    public @ResponseBody ResponseEntity<UMSResponse> updateUser(@PathVariable String userName,
            @RequestBody UMSUser umsUser) throws Exception {
        UMSResponse response = null;
        UMSErrorResponse error = new UMSErrorResponse();
        HttpStatus httpStatus = HttpStatus.OK;

        try {

            Users user = usersRepository.findUserByUserId(userName);
            if (user == null) {
                httpStatus = HttpStatus.BAD_REQUEST;
                error.addError("userName");
                error.addError(UMSError.UserName.getError(userName));
            }

            userService.mapUserEntityWithUMSUser(userName, umsUser, user, pcmConfigUtil);

            usersRepository.save(user);
        } catch (UMSException ex) {
            response = error;
            logger.warn(error.getErrors());
        } catch (Exception ex) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            response = new UMSErrorResponse(ex.getMessage());
            logger.warn(ex);
        }

        return new ResponseEntity<>(response, httpStatus);
    }
}
