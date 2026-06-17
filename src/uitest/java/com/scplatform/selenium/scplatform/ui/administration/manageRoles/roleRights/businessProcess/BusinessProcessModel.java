/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.administration.manageRoles.roleRights.businessProcess;

import com.test.selenium.common.modelViewController.model.Model;
import com.google.common.base.Preconditions;

/**
 * Navigation: Administration -> Manage Roles
 *
 * @author dgenrich
 */
public class BusinessProcessModel extends Model {

    // TODO: Add in all details for each section

    private static final long serialVersionUID = 1L;

    private String sourcing_setAll;
    private String bom_setAll;
    private String forecastADJ_setAll;
    private String forecast_setAll;
    private String rebate_setAll;

    public String getSourcing_setAll() {
        return sourcing_setAll;
    }

    public void setSourcing_setAll(String sourcing_setAll) {
        checkParameter("sourcing_setAll", sourcing_setAll);
        this.sourcing_setAll = sourcing_setAll.toLowerCase();
    }

    public String getBom_setAll() {
        return bom_setAll;
    }

    public void setBom_setAll(String bom_setAll) {
        checkParameter("bom_setAll", bom_setAll);
        this.bom_setAll = bom_setAll.toLowerCase();
    }

    public String getForecastADJ_setAll() {
        return forecastADJ_setAll;
    }

    public void setForecastADJ_setAll(String forecastADJ_setAll) {
        checkParameter("forecastADJ_setAll", forecastADJ_setAll);
        this.forecastADJ_setAll = forecastADJ_setAll.toLowerCase();
    }

    public String getForecast_setAll() {
        return forecast_setAll;
    }

    public void setForecast_setAll(String forecast_setAll) {
        checkParameter("forecast_setAll", forecast_setAll);
        this.forecast_setAll = forecast_setAll.toLowerCase();
    }

    public String getRebate_setAll() {
        return rebate_setAll;
    }

    public void setRebate_setAll(String rebate_setAll) {
        checkParameter("rebate_setAll", rebate_setAll);
        this.rebate_setAll = rebate_setAll.toLowerCase();
    }

    private void checkParameter(String subVaraible, String subValue) {
        Preconditions.checkArgument(
                (("true".equals(subValue.toLowerCase())) || ("false".equals(subValue.toLowerCase()))),
                "Invalid value for %s (%s).  %s can only be 'true' or 'false'", subVaraible, subValue, subVaraible);
    }

}
