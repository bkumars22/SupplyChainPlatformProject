/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.administration.manageRoles;

import com.test.selenium.common.modelViewController.model.Model;

/**
 * Navigation: Administration -> Manage Roles
 *
 * @author dgenrich
 */
public class ManageRolesModel extends Model {

    private static final long serialVersionUID = 1L;

    private String roleID;

    /**
     * @return the roleID
     */
    public String getRoleID() {
        return roleID;
    }

    /**
     * @param roleID
     *            the roleID to set
     */
    public void setRoleID(String roleID) {
        this.roleID = roleID;
    }

}
