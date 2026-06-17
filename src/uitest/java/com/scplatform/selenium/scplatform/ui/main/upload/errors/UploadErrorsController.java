/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.main.upload.errors;

import java.util.List;

import com.test.selenium.common.JLog;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.modelViewController.SCPlatformController;
import com.test.selenium.scplatform.ui.main.manageUploadJobs.loadJob.LoadJobDetailsPage;

public class UploadErrorsController extends SCPlatformController {
    private UploadErrorsPage page;

    public UploadErrorsController() {
        super();
        page = new UploadErrorsPage();
    }

    @Override
    public PageImpl getView() {
        return new LoadJobDetailsPage();
    }

    public boolean printAndVerify(String msgType, String msg) {
        List<UploadErrorsModel> tableData = page.parseResults();
        Boolean status = false;
        JLog.section("Load Job Errors");
        for (UploadErrorsModel model : tableData) {
            write(model);
            String s = model.getMessage();
            if ((msgType.equals("error") || (msgType.contains("VALIDATION_ERROR")))
                    && model.getMessage().contains(msg)) {
                status = true;
            }
        }
        return status;
    }

    private void write(UploadErrorsModel model) {

        JLog.write(String.format("%s = %s", model.getDisplayName("type"), model.getType()));
        JLog.write(String.format("%s = %s", model.getDisplayName("message"), model.getMessage()));
        JLog.write(String.format("%s = %s", model.getDisplayName("location"), model.getLocation()));
        JLog.blankLine();
    }
}
