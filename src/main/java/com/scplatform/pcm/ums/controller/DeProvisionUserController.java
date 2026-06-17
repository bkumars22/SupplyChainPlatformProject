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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import lombok.RequiredArgsConstructor;

import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.user.repository.UsersRepository;
import com.scplatform.pcm.ums.dto.UMSError;
import com.scplatform.pcm.ums.dto.UMSErrorResponse;
import com.scplatform.pcm.ums.dto.UMSException;
import com.scplatform.pcm.ums.dto.UMSResponse;

/**
 * Controller for adding users
 * 
 */
@Controller
@RequestMapping("/deprovision_user")
@RequiredArgsConstructor
public class DeProvisionUserController {
    
	private final static Logger logger = LogManager.getLogger(DeProvisionUserController.class);
	
	private final UsersRepository usersRepository;

    /**
     * Create the user
     * 
     * @param userName
     * @param umsUser
     * @return UMSErrorResponse
     */
    @RequestMapping(value = "{userName:.+}", method = RequestMethod.POST)
    @Transactional
    public @ResponseBody ResponseEntity<UMSResponse> deactivateUser(@PathVariable String userName) {
        logger.info("Deactivating user with userId " + userName);
        
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
}
