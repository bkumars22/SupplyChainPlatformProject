/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.steps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.test.selenium.api.e2na.b2bc.B2BClient;
import com.test.selenium.api.e2na.b2bc.B2BClientData;
import com.test.selenium.api.e2na.directoryAdapter.DirectoryAdapter;
import com.test.selenium.api.e2na.directoryAdapter.DirectoryAdapter.DirectoryAdapterUploadException;
import com.test.selenium.api.e2na.directoryAdapter.DirectoryAdapter.VALIDATION;
import com.test.selenium.api.e2na.directoryAdapter.DirectoryAdapterMessage;
import com.test.selenium.api.e2na.login.LoginE2NA;
import com.test.selenium.api.e2na.login.LoginE2Net;
import com.test.selenium.api.e2na.navigation.ConsoleThemeHelper;
import com.test.selenium.api.e2na.transaction.TransactionData;
import com.test.selenium.api.e2na.transaction.TransactionSearch;
import com.test.selenium.api.e2na.transaction.Transactions;
import com.test.selenium.common.Configuration;
import com.test.selenium.common.JLog;
import com.test.selenium.common.Prop;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.steps.Users;
import com.test.selenium.common.users.User;
import com.test.selenium.scplatform.cucumber.Preprocessing;
import com.test.selenium.scplatform.navigation.SCPlatformNavigation;
import com.test.selenium.scplatform.ui.main.upload.UploadController;
import com.test.selenium.scplatform.ui.main.upload.UploadModel;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Then;

/**
 * DOCUMENTATION: http://confluence.dev.scplatform.local/display/QA/Gherkin+Step+Definitions+-+Message+Upload
 *
 * @author dgenrich
 *
 */
public class MessageUploads {
  private static List<TransactionData> transactionInfo;

  @Before
  public void beforeMethod(Scenario scenario){
    JLog.setScenarioForCucumber(scenario);
    JLog.resetErrorCount();
  }

  private void checkForErrors()	{
    if (JLog.getErrorCount() > 0){
      JLog.fail(JLog.getErrorCount() + " errors occurred in the test.  Check log.", TakeScreenshot.True);
    }
  }

  @Then("I upload messages using (UI|DirectoryAdaptor)")
  public void upload_message_using(String uploadMethod, List<TransactionData> transactionDetails) throws Throwable	{
    transactionInfo = new ArrayList<>();
    transactionInfo.addAll(Preprocessing.process(transactionDetails));

    if (uploadMethod.equals("DirectoryAdaptor"))	{
      uploadDirectoryAdaptor();
    } else if (uploadMethod.equals("B2BC"))	{
      JLog.fail("B2BC Upload type is not supported yet", TakeScreenshot.False);
      uploadB2BC();
    } else if (uploadMethod.equals("UI"))	{
      uploadUI();
    } else	{
      JLog.fail("Unknown upload method: " + uploadMethod, TakeScreenshot.False);
    }
    checkForErrors();
  }

  private void uploadB2BC() {
    Prop prop = Prop.getInstance();

    for (int i = 0; i < transactionInfo.size(); i++){
      B2BClient b2bClient = new B2BClient();
      B2BClientData b2bcClientData = b2bClient.getB2BClientData(transactionInfo.get(i).getConnectionName());

      JLog.section(String.format("Upload using B2B Client (%s): %s", b2bcClientData.getConnectionName(), transactionInfo.get(i).getMessage()));

      String uploadFile = getSavedMessage(transactionInfo.get(i));

      try {
        Transactions transaction = new Transactions();

        try {
          prop.get().setProperty("stack.b2b", b2bcClientData.getDeployOnBox());
          prop.save();

          transaction.uploadFileToHost (uploadFile, b2bcClientData.getOutbox());

          String transactionKey = buildTransactionRecord(uploadFile, transactionInfo.get(i));
          if (checkTransactionViaDB(transactionKey))	{
            verifyTransactionInE2Net(uploadFile, transactionInfo.get(i), transactionKey);
            verifyTransactionInE2NA(uploadFile, transactionInfo.get(i), transactionKey);
          }

        } catch (IOException e) {
          JLog.error("Could not B2B upload the file "+uploadFile, e);
        }

      } catch (Exception e) {
        JLog.error(e);
      }
    }
  }

  private boolean checkTransactionViaDB(String transactionKey){
    boolean success = false;
    TransactionData data = new TransactionData();
    data = data.load(transactionKey);
    String transactionID = "";

    try {
      transactionID = DirectoryAdapter.getTransactionID(data.businessID);
      if (transactionID != null){
        JLog.write ("Transaction ID: " + transactionID);
        DirectoryAdapter.checkSCPMForMessageFailure(transactionID);
        success = true;
      }
    } catch (DirectoryAdapterUploadException e) {
      JLog.error(e);
    }

    if (!success){
      JLog.error("Unable to find message in database: " + transactionID);
    }
    return success;
  }

  private boolean verifyTransactionInE2NA(String uploadedFile, TransactionData transactionData, String transactionKey)	{
    ConsoleThemeHelper.setE2NAStack();
    User user = Users.get(transactionData.getLoginUserKeyForE2NA());
    LoginE2NA login = new LoginE2NA();
    login.login(user);

    boolean success = verifyTransaction(uploadedFile, transactionKey);
    login.logout();
    return success;
  }

  private boolean verifyTransactionInE2Net(String uploadedFile, TransactionData transactionData, String transactionKey)	{
    ConsoleThemeHelper.setE2NetStack();
    User user = Users.get(transactionData.getLoginUserKeyForE2Net());
    LoginE2Net login = new LoginE2Net();
    login.login(user);

    boolean success = verifyTransaction(uploadedFile, transactionKey);
    login.logout();
    return success;
  }

  private boolean verifyTransaction(String uploadedFile, String transactionKey)	{
    // 	Login to Console as the bootstrap super user
    TransactionSearch search = new TransactionSearch();

    return search.workflow_TransactionSearchAndValidate(transactionKey);
  }

  private String buildTransactionRecord(String uploadedFile, TransactionData transactionData) {
    String saveAsKey = "transaction";

    File businessIDFile = new File (uploadedFile);
    String ext = "." + FilenameUtils.getExtension(uploadedFile);
    String businessID = businessIDFile.getName().replace(ext, "");

    transactionData.setBusinessID(businessID);
    transactionData.save(saveAsKey, transactionData);

    return saveAsKey;
  }

  private void uploadDirectoryAdaptor() {

    for (int i = 0; i < transactionInfo.size(); i++){
      JLog.section("Upload using Directory Adaptor: " + transactionInfo.get(i).getMessage());

      String uploadFile = getSavedMessage(transactionInfo.get(i));
      DirectoryAdapterMessage message = new DirectoryAdapterMessage();
      message.setFilename(FilenameUtils.getName(uploadFile));

      DirectoryAdapter.setRemoveExtForBusinessID(REMOVE_EXT_FOR_BUSINESS_ID());
      DirectoryAdapter.setDirectory(FilenameUtils.getFullPath(uploadFile));
      if (StringUtils.isNoneEmpty(transactionInfo.get(i).getOverrideBusinessID()))	{
        DirectoryAdapter.setOverrideBusinessID(transactionInfo.get(i).getOverrideBusinessID());
      } else	{
        DirectoryAdapter.setOverrideBusinessID(null);
      }

      if (StringUtils.isNoneEmpty(transactionInfo.get(i).getState()))	{
        DirectoryAdapter.setExpectedState(transactionInfo.get(i).getState());
      } else	{
        DirectoryAdapter.setExpectedState(null);
      }

      DirectoryAdapter.uploadMessage(message, getValidation(transactionInfo.get(i)));
    }
  }

  private String getSavedMessage(TransactionData transactionData)	{
    String uploadFile = Configuration.getRuntime(transactionData.getSavedMessageKey());
    String directory = FilenameUtils.getFullPath(uploadFile);
    String fileName = FilenameUtils.getName(uploadFile);
    String[] fileParts = fileName.split("_");
    String ext = FilenameUtils.getExtension(fileName);
    String extNew = ext;

    boolean changedName = false;
    if (StringUtils.isNotBlank(transactionData.getFromName()))	{
      if (!transactionData.getFromName().equals(fileParts[0]))	{
        changedName = true;
        fileParts[0] = transactionData.getFromName();
      }
    }
    if (StringUtils.isNotBlank(transactionData.getToName()))	{
      if (!transactionData.getToName().equals(fileParts[1]))	{
        changedName = true;
        fileParts[1] = transactionData.getToName();
      }
    }
    if (StringUtils.isNotBlank(transactionData.getFileExtension()))	{
      if (!transactionData.getFileExtension().equals(ext))	{
        changedName = true;
        extNew = transactionData.getFileExtension();
      }
    }

    if (changedName){
      fileName = StringUtils.join(fileParts, "_");
      fileName = uploadFile.replace("." + ext, "." + extNew);
      uploadFile = directory + fileName;
    }
    return uploadFile;
  }


  private boolean REMOVE_EXT_FOR_BUSINESS_ID()	{
    boolean removeExt = true;

    if (Configuration.propertyContainsKey("directory.adaptor.REMOVE_EXT_FOR_BUSINESS_ID"))	{
      String value = Configuration.getProperty("directory.adaptor.REMOVE_EXT_FOR_BUSINESS_ID", "true");
      if (StringUtils.isNotBlank(value))	{
        removeExt = value.toLowerCase().equals("true");
      }
    }

    return removeExt;
  }


  public VALIDATION getValidation(TransactionData transactionData)	{
    VALIDATION validation = VALIDATION.SKIP_E2SC;

    if (transactionData.getSkipE2NAValidation().toLowerCase().equals("true") && (transactionData.getSkipE2SCValidation().toLowerCase().equals("true")))	{
      validation = VALIDATION.SKIP_ALL;
    } else if ((transactionData.getSkipE2NAValidation().toLowerCase().equals("true"))){
      validation = VALIDATION.SKIP_ALL;
    } else if ((transactionData.getSkipE2SCValidation().toLowerCase().equals("true")))	{
      validation = VALIDATION.SKIP_E2SC;
    }

    return validation;
  }


  private void uploadUI() throws Exception {
    SCPlatformNavigation nav = new SCPlatformNavigation();

    for (int i = 0; i < transactionInfo.size(); i++){
      JLog.section("Upload using UI: " + transactionInfo.get(i).getMessage());

      String uploadFile = getSavedMessage(transactionInfo.get(i));

      nav.Upload();

      UploadModel uploadModel = new UploadModel();
      uploadModel.setMessageType(transactionInfo.get(i).getMessage());
      uploadModel.setUploadFile(uploadFile);


      UploadController uploadController = new UploadController();
      if (StringUtils.isNotBlank(transactionInfo.get(i).getState()))	{
        uploadController.setExpectedStatus(transactionInfo.get(i).getState());
      }
      uploadController.setModel(uploadModel);
      //uploadController.upload();
    }
  }



}
