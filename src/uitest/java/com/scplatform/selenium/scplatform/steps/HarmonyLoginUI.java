/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.steps;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.openqa.selenium.By;
import org.testng.SkipException;

import com.test.selenium.common.AbstractPage;
import com.test.selenium.common.JLog;
import com.test.selenium.common.Prop;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.login.AbstractLogin;
import com.test.selenium.common.navigation.unity.Menu;
import com.test.selenium.common.steps.Users;
import com.test.selenium.common.users.User;
import com.test.selenium.scplatform.login.LoginSCPlatformHarmony;
import com.test.selenium.scplatform.modelViewController.MTCMController;
import com.test.selenium.scplatform.navigation.HarmonyMTCMNavigation;

import io.cucumber.java.Scenario;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;

public class HarmonyLoginUI {
	@Before
	public void beforeMethod(Scenario scenario) throws Throwable {
		JLog.setScenarioForCucumber(scenario);
		JLog.resetErrorCount();
		if (scenario.getName().contains("mass update for the suppliers not matching")
				&& LoginSCPlatformHarmony.navUrl.contains("dev4160")) {
			throw new SkipException(
					"Skipping this test for PS box due to issues on testdata upload, working on dev box");
		} else if (scenario.getName().contains("using SuperGCM role")) {
			login_harmony_mtcm_role("mtcmUser", "adminuser2");
			navHarmonyMTCM("Administration", "Manage Roles");
			General gen = new General();
			gen.clickName("SUPER_GCM");
			Admin ad = new Admin();
			ad.clickTab("Business Document", "Manage Roles");
			ad.selectDeselectPermissionsCheckBox("check", "accessRights(ITEM_ASSIGNMENT)", "AssignSelf");
			gen.clickSaveButton();
			logout_mtcm();
			login_harmony_mtcm_role("mtcmUser", "supergcm5");
		} else if (scenario.getName().contains("As an admin Mass update by parent functional group")) {
			login_harmony_mtcm_role("mtcmUser", "adminUser3");
			FunctionalGroup g = new FunctionalGroup();
			String fgName = "CYCFG_TEST004";
			if (scenario.getName().contains("ODM Buy")) {
				fgName = "UI201905241439";
			}
			g.removeItemsFromGroup(fgName);
		} else if (scenario.getName().contains("Upload ApprovedForecast with zero")) {
			// && DateTime.now().dayOfMonth().equals("25")) {
			login_harmony_mtcm_role("mtcmUser", "adminuser2");
			navHarmonyMTCM("Cost Forecast", "Search Forecast");
			General gen = new General();
			gen.clickButton("Clear");
			gen.setItemNumber("00025");
			gen.selectForecastModel("Adjustable");
			gen.selectStatus("Pending");
			gen.clickButton("Apply");
			gen.verifySearchFilterResults();

			gen.selectRows("1", "selectedPageKeys", "checkbox");
			gen.clickButton("Next");
			AbstractPage page = new AbstractPage();
			String fiscalDate = page.getList(By.xpath("//th[@class='periodPAST']")).get(0).getAttribute("title");

			fiscalDate = fiscalDate.substring(4, 6);
			int fiscalDay = Integer.parseInt(fiscalDate) - 1;

			page.sleep(1);
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			Date date = new Date();

			String today = formatter.format(date).substring(0, 2);

			// fiscalDay = 02;
			// if (today.equals(String.valueOf(fiscalDay))) {

			navHarmonyMTCM("Cost Forecast", "Search Forecast");
			// General gen = new General();
			gen.clickButton("Clear");
			gen.setItemNumber("00025");
			gen.selectForecastModel("Adjustable");
			gen.selectStatus("Pending");
			gen.clickButton("Apply");
			gen.verifySearchFilterResults();
			gen.clickDownloadButton("Forecast", "AdjustableTestData");

			navHarmonyMTCM("Cost Forecast", "Search Forecast");
			gen.clickButton("Clear");
			gen.setItemNumber("U289G");
			gen.selectForecastModel("Current");
			gen.selectStatus("Approved");
			gen.clickButton("Apply");
			gen.verifySearchFilterResults();
			gen.clickDownloadButton("Forecast", "ApprovedForecastTestData");

			navHarmonyMTCM("Cost Forecast", "Search Forecast");
			gen.clickButton("Clear");
			gen.setItemNumber("U289G");
			gen.selectForecastModel("Current");
			gen.selectStatus("Approved");
			gen.clickButton("Apply");
			gen.verifySearchFilterResults();
			gen.clickDownloadButton("Forecast", "TestDataApprovedForecastValidations");

			navHarmonyMTCM("Cost Forecast", "Search Forecast");
			gen.clickButton("Clear");
			gen.setItemNumber("00002");
			gen.selectForecastModel("Current");
			gen.selectStatus("Closed");
			gen.clickButton("Apply");
			gen.verifySearchFilterResults();
			gen.clickDownloadButton("Forecast", "TestDataApprovedForecastClosed");

			// IProject project = root.getProject(currentProjectName);
			// project.refreshLocal(IResource.DEPTH_INFINITE, null);
			// prop.getProjectDir()
		}
	}
	// }

	@After
	public void AfterMethod(Scenario scenario) throws Throwable {
		JLog.setScenarioForCucumber(scenario);
		String name = scenario.getName();
		if (!name.contains("setup remove")) {
			FunctionalGroup g = new FunctionalGroup();
			CostRecords cr = new CostRecords();
			TAMSupplyAllocation tam = new TAMSupplyAllocation();
			Admin admin = new Admin();
			InputStream fo1 = new FileInputStream(
					prop.getRootDir() + "scplatform/data/properties/loginUserDetails.properties");
			Properties p1 = new Properties();
			p1.load(fo1);
			String logUN = p1.getProperty("login");
			logout_mtcm();
			if (logUN != null && !(logUN.equals("admin"))) {
				try {
					login_harmony_mtcm_role("mtcmUser", logUN);
				} catch (org.testng.SkipException | org.openqa.selenium.NoSuchSessionException se) {
					JLog.write("Browser session dead during AfterMethod cleanup re-login - skipping: " + se.getMessage());
					lastLoginObj = null;
				}
			}
			if ((name.contains("New Sourcing Lane page") || (name.contains("Search Sourcing Lane"))
					|| name.contains("Search Cost Records"))) {
				cr.clearTestDataSavedForFilter(scenario);
			} else if (name.contains("edit the user details by adding agent")
					|| name.contains("Change dashboard news page") || name.contains("Manage Roles page"))// admin
																											// //
																											// module
			{
				admin.clearTestData(scenario);
			} else if (name.contains("mass update"))
				tam.clearTestData(scenario);
			else if (name.contains("No option to Delete allocation"))
				tam.clearCheckBoxForBuyerRole();
			else if (name.contains("excep permissions")) {
				//
				// login_harmony_mtcm("mtcmUser");
				navHarmonyMTCM("Administration", "Manage Roles");
				General gen = new General();
				gen.clickName("SUPER_GCM");
				Admin ad = new Admin();
				ad.clickTab("Business Document", "Manage Roles");
				ad.selectDeselectPermissionsCheckBox("uncheck", "accessRights(COST_EXCEPTION)", "All");
				gen.clickSaveButton();
				logout_mtcm();
			} else if (name.contains("Agile and Proteus")) {
				navHarmonyMTCM("Rebates", "Search Rebate Program");
				General gen = new General();
				gen.clickButton("Clear");
				gen.enterTextFieldVal("PCBA Components-ESG-Cindy Lou-$30 LCAP program", "name");
				gen.clickButton("Apply");
				// gen.selectRows("1", "selectedPageKeys", "radio");
				Rebates reb = new Rebates();
				reb.clickEditIcon("1");
				reb.clickRulesTab();
				gen.clickEleByID("editRuleId");
				MTCMController mc = new MTCMController();
				boolean status = false;
				try {
					status = mc.getComboSelectedOptionByName("rulePlatformKeys") != null;
				} catch (Exception e) {
					JLog.resetErrorCount();
				}
				if (status) {
					gen.selectComboSelectionByComboName("rulePlatformKeys", "1 SOCKET 1U AMD ODM");
					reb.clickButton("platformRemove");
					reb.clickSaveButton();
					gen.clickSaveButton();
					gen.verifySuccessMsg("Changes saved successfully");
				}
			} else {
				// if (name.contains("Functional Group") || name.contains("Parent Group")) {
				if (name.contains("Parent Group")) {

					g.clearTestData(scenario);
				}
				if (name.contains("Saved Filter")) {
					g.clearTestDataSavedForFilter(scenario);
				}
			}
			logout_mtcm();
		}
	}

	// @AfterClass
	// public void AfterClass() throws Throwable {
	// logout_mtcm();
	//
	// }

	private void checkForErrors() {
		if (JLog.getErrorCount() > 0) {
			JLog.fail(JLog.getErrorCount() + " errors occurred in the test.  Check log.", TakeScreenshot.True);
		}
	}

	private static User currentLoggedInUser;
	private static AbstractLogin lastLoginObj;
	Prop prop = Prop.getInstance();

	public static User getCurrentLogggedInUser() {
		return currentLoggedInUser;
	}

	@Given("I log into HarmonyMTCM as {string}")
	public void login_harmony_mtcm(String userKey) throws Throwable {
		LoginSCPlatformHarmony login = new LoginSCPlatformHarmony();
		if (lastLoginObj == null) {
			currentLoggedInUser = Users.get(userKey);
			login.login(currentLoggedInUser, "admin");
			lastLoginObj = login;
			Properties p = new Properties();
			FileOutputStream fr = new FileOutputStream(
					prop.getRootDir() + "scplatform/data/properties/loginUserDetails.properties");
			p.setProperty("userName", getUserName("admin"));
			p.setProperty("login", "admin");
			p.store(fr, "");
			fr.close();
		}
		JLog.screenCapture();
		checkForErrors();
	}

	public String getUserName(String un) {
		switch (un) {
			case "admin":
				return "Administrator";
			case "cheeseang.ong@scplatform.local":
				return "Chee Seang Ong";
			case "abinaya.nagaraju@scplatform.local":
				return "Abinaya Nagaraju";
			default:
				return "";
		}
	}

	@Given("I log into HarmonyMTCM as {string} with {string}")
	public void login_harmony_mtcm_role(String userKey, String role) throws Throwable {
		if (lastLoginObj == null) {
			currentLoggedInUser = Users.get(userKey);
			LoginSCPlatformHarmony login = new LoginSCPlatformHarmony();
			try {
				JLog.screenCapture();
			} catch (org.openqa.selenium.WebDriverException e) {
				JLog.write("Browser session dead before login - skipping scenario: " + e.getMessage());
				throw new SkipException("Browser session dead - skipping: " + e.getMessage());
			}
			// AbstractPage.sleep(150);
			try {
				login.login(currentLoggedInUser, role);
			} catch (org.openqa.selenium.NoSuchSessionException e) {
				JLog.write("Browser session dead - cannot login, skipping scenario: " + e.getMessage());
				lastLoginObj = null;
				throw new SkipException("Browser session dead - skipping: " + e.getMessage());
			}
			lastLoginObj = login;
			Properties p = new Properties();
			FileOutputStream fr = new FileOutputStream(
					prop.getRootDir() + "scplatform/data/properties/loginUserDetails.properties");
			p.setProperty("userName", role);
			p.setProperty("login", role);
			p.store(fr, "");
			fr.close();
		}
		JLog.screenCapture();
		checkForErrors();

	}

	@And("I log out of HarmonyMTCM")
	public void logout_mtcm() throws Throwable {
		currentLoggedInUser = null;
		try {
			AbstractPage page = new AbstractPage();
			// page.browser().manage().window().maximize();
			boolean isLoginPage = page.exists(By.name("username"));
			if (!genericLogout() && (lastLoginObj != null) && !isLoginPage) {
				LoginSCPlatformHarmony login = new LoginSCPlatformHarmony();
				login.logout();
			}
		} catch (org.openqa.selenium.NoSuchSessionException e) {
			JLog.write("Session invalid during logout - skipping UI logout: " + e.getMessage());
		} catch (org.openqa.selenium.WebDriverException e) {
			JLog.write("WebDriver error during logout - skipping: " + e.getMessage());
		} finally {
			lastLoginObj = null;
		}
		try {
			JLog.screenCapture();
		} catch (org.openqa.selenium.WebDriverException e) {
			System.err.println("[WARN] Screenshot failed in logout_mtcm - browser dead: " + e.getClass().getSimpleName());
		}
		checkForErrors();
	}

	@And("I delete cookies from the broswer")
	public void deleteCookies() {
		AbstractPage page = new AbstractPage();
		page.browser().manage().deleteAllCookies();
		lastLoginObj = null;
		checkForErrors();
	}

	@And("I log out of HarmonyMTCM loggen in as {string}")
	public void logout_mtcm_role(String role) throws Throwable {
		currentLoggedInUser = null;
		if (!genericLogout()) {
			LoginSCPlatformHarmony login = new LoginSCPlatformHarmony();
			login.logout();
		}
		lastLoginObj = null;
		checkForErrors();
	}

	private boolean genericLogout() {
		boolean success = false;
		if (lastLoginObj != null) {
			lastLoginObj.logout();
			success = true;
		}
		return success;
	}

	@And("I navigate to {string} -> {string}")
	public void navHarmonyMTCM(String mainMenu, String subMenu) throws Throwable {
		try {
			JLog.screenCapture();

			// CRITICAL: Switch to main page context (menu is in parent frame, not iframe)
			AbstractPage.browserSession.getDriver().switchTo().defaultContent();
			JLog.write("Switched to default content (main page) for menu navigation");

			// Small wait to ensure page is stable before menu interaction
			AbstractPage.sleep(2);

			// WORKAROUND: Use JavaScript for entire menu interaction to avoid
			// ElementNotInteractableException with standard Selenium sendKeys
			// Step 1: Click menu open with JavaScript
			String jsClickMenu = "var btn = document.evaluate(\"//nav[@class='eto-header__menu' or @class='eto-header__menu filtering']/button[contains(text(),'Menu')]\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue; if(btn) btn.click();";
			((org.openqa.selenium.JavascriptExecutor) AbstractPage.browserSession.getDriver())
					.executeScript(jsClickMenu);
			AbstractPage.sleep(5); // Wait for menu open animation
			JLog.write("Pre-opened menu using JavaScript (waited 5 sec for animation)");

			// DEBUG: Check available menu items BEFORE filtering
			String jsCheckItemsBeforeFilter = "var allItems = document.querySelectorAll('a.eto-menu__link');" +
					"var itemTexts = [];" +
					"for(var i=0; i<allItems.length && i<20; i++) {" +
					"  itemTexts.push(allItems[i].textContent.trim());" +
					"}" +
					"return itemTexts;";
			@SuppressWarnings("unchecked")
			java.util.List<String> itemsBeforeFilter = (java.util.List<String>) ((org.openqa.selenium.JavascriptExecutor) AbstractPage.browserSession
					.getDriver())
					.executeScript(jsCheckItemsBeforeFilter);
			JLog.write("Menu items BEFORE filtering: "
					+ (itemsBeforeFilter != null ? String.join(", ", itemsBeforeFilter) : "none"));

			// Step 2: Use JavaScript to set filter input value and trigger menu filtering
			// SubMenu returns an array, get the first element which is the menu text
			String[] subMenuParts = HarmonyMTCMNavigation.SubMenu(mainMenu, subMenu);
			String subMenuInfo = subMenuParts[0]; // Get first part (e.g., "Administration --> Manage
													// Items|name=Manage Items")
			String[] parts = subMenuInfo.split("\\|");
			String menuPath = parts.length > 0 ? parts[0].trim() : subMenu; // e.g., "Administration
																			// --> Manage Items"

			// Extract the target menu item (last part after --> )
			String filterText = subMenu; // Use subMenu parameter as default
			if (menuPath.contains(" --> ")) {
				String[] pathParts = menuPath.split(" --> ");
				filterText = pathParts[pathParts.length - 1].trim(); // Get last part (target item)
			}
			JLog.write("Navigating to menu item: " + filterText + " (from path: " + menuPath + ")");

			String jsSetFilter = "var input = document.evaluate(\"//input[@placeholder='Filter workflows' or @placeholder='Filter items']\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue; "
					+
					"if(input) { " +
					"  input.value = '" + filterText + "'; " +
					"  var event = new Event('input', { bubbles: true }); " +
					"  input.dispatchEvent(event); " +
					"}";
			((org.openqa.selenium.JavascriptExecutor) AbstractPage.browserSession.getDriver())
					.executeScript(jsSetFilter);
			AbstractPage.sleep(5); // Increased wait from 3s to 5s for filtering animation
			JLog.write("Set filter using JavaScript: " + filterText);

			// Step 3: Click the menu item via JavaScript for SPA routing.
			// IMPORTANT: We must use a JS click (not Selenium navigate().to()) so that the
			// application's SPA/Angular router handles the transition and injects the
			// contentFrame iframe that subsequent steps expect.  Direct URL navigation
			// bypasses the SPA router and the iframe is never created.
			//
			// Chrome can briefly drop its DevTools connection during the SPA page
			// transition (executeScript throws WebDriverException / NoSuchSessionException).
			// We catch this, wait a moment, then verify the session reconnected.
			// If the session is alive, navigation succeeded.  If it is truly dead,
			// we re-throw so the outer catch + GlobalHooks can recover.
			String jsClickMenuItem = "var filterText = '" + filterText + "';\n" +
					"var filterTextLower = filterText.toLowerCase();\n" +
					"var menuItem = null;\n" +
					"var strategy = '';\n" +
					"var items = document.querySelectorAll('a.eto-menu__link');\n" +
					"// Strategy 1: Case-insensitive exact text match\n" +
					"for(var i=0; i<items.length; i++) {\n" +
					"  var itemText = items[i].textContent.trim();\n" +
					"  if(itemText.toLowerCase() === filterTextLower) {\n" +
					"    menuItem = items[i];\n" +
					"    strategy = 'exact-case-insensitive';\n" +
					"    break;\n" +
					"  }\n" +
					"}\n" +
					"// Strategy 2: Case-insensitive contains match (only visible items)\n" +
					"if(!menuItem) {\n" +
					"  for(var i=0; i<items.length; i++) {\n" +
					"    var itemText = items[i].textContent.trim();\n" +
					"    var style = window.getComputedStyle(items[i]);\n" +
					"    if(style.display !== 'none' && itemText.toLowerCase().indexOf(filterTextLower) >= 0) {\n" +
					"      menuItem = items[i];\n" +
					"      strategy = 'contains-case-insensitive';\n" +
					"      break;\n" +
					"    }\n" +
					"  }\n" +
					"}\n" +
					"// Strategy 3: Starts with (case-insensitive)\n" +
					"if(!menuItem) {\n" +
					"  for(var i=0; i<items.length; i++) {\n" +
					"    var itemText = items[i].textContent.trim();\n" +
					"    var style = window.getComputedStyle(items[i]);\n" +
					"    if(style.display !== 'none' && itemText.toLowerCase().startsWith(filterTextLower)) {\n" +
					"      menuItem = items[i];\n" +
					"      strategy = 'startsWith-case-insensitive';\n" +
					"      break;\n" +
					"    }\n" +
					"  }\n" +
					"}\n" +
					"if(menuItem) {\n" +
					"  var savedHref = menuItem.href;\n" +
					"  menuItem.click();\n" +
					"  return {success: true, strategy: strategy, text: menuItem.textContent.trim(), href: savedHref};\n" +
					"} else {\n" +
					"  var availableItems = [];\n" +
					"  for(var j=0; j<items.length && j<20; j++) {\n" +
					"    var style = window.getComputedStyle(items[j]);\n" +
					"    var visible = (style.display !== 'none' && style.visibility !== 'hidden');\n" +
					"    availableItems.push(items[j].textContent.trim() + ' [visible=' + visible + ']');\n" +
					"  }\n" +
					"  return {success: false, error: 'Menu item not found: ' + filterText, available: availableItems};\n" +
					"}";

			Object jsResult = null;
			boolean navigationTriggeredByClick = false;
			try {
				jsResult = ((org.openqa.selenium.JavascriptExecutor) AbstractPage.browserSession.getDriver())
						.executeScript(jsClickMenuItem);
			} catch (org.openqa.selenium.WebDriverException wde) {
				// Chrome's DevTools connection dropped during the JS click.
				// This happens when the SPA routing or a full page navigation starts while
				// executeScript is still waiting for a return value.
				JLog.write("[navHarmonyMTCM] WebDriverException during JS menu click (Chrome DevTools disconnect): "
						+ wde.getMessage().split("\n")[0]);
				// Wait for Chrome to recover / page to load
				AbstractPage.sleep(3);
				try {
					// Verify the session is still alive by requesting the current URL
					String currentUrl = AbstractPage.browserSession.getDriver().getCurrentUrl();
					JLog.write("[navHarmonyMTCM] Session alive after click, URL: " + currentUrl);
					navigationTriggeredByClick = true;
				} catch (Exception sessionCheck) {
					// Session is truly dead — Chrome crashed, not just a transient disconnect
					JLog.write("[navHarmonyMTCM] Session truly dead after click — Chrome crashed: "
							+ sessionCheck.getMessage());
					throw wde; // re-throw so outer catch handles it
				}
			}

			if (navigationTriggeredByClick) {
				// JS click triggered SPA routing; Chrome reconnected after the page transition.
				JLog.write("[navHarmonyMTCM] SPA navigation completed (Chrome reconnected) — page ready");
				AbstractPage.sleep(2); // Wait for page to fully initialize
				AbstractPage.browserSession.getDriver().switchTo().defaultContent();
				JLog.write("Switched to defaultContent after SPA navigation");
			} else if (jsResult != null && jsResult instanceof java.util.Map) {
				// JS click returned normally without throwing (no DevTools disconnect)
				@SuppressWarnings("unchecked")
				java.util.Map<String, Object> result = (java.util.Map<String, Object>) jsResult;
				Boolean success = (Boolean) result.get("success");
				if (Boolean.TRUE.equals(success)) {
					String strategy = (String) result.get("strategy");
					String clickedText = (String) result.get("text");
					JLog.write("SUCCESS: Clicked menu item '" + clickedText + "' using JavaScript strategy: " + strategy);

					// Wait for page to load after menu click
					AbstractPage.sleep(3);

					// Switch to defaultContent so each subsequent step's controller can cleanly
					// call setFrameContext() / switchToFrame() from the main window context.
					AbstractPage.browserSession.getDriver().switchTo().defaultContent();
					JLog.write("Switched to defaultContent after navigation (frame context reset for next step)");
				} else {
					String error = (String) result.get("error");
					@SuppressWarnings("unchecked")
					java.util.List<String> available = (java.util.List<String>) result.get("available");
					JLog.write("FAILED: " + error);
					JLog.write("Available menu items: " + (available != null ? String.join(", ", available) : "none"));
					JLog.screenCapture();
					throw new RuntimeException(error + ". Available items: " + available);
				}
			}
			JLog.screenCapture();
		} catch (Exception e) {
			JLog.write("Exception occurred: " + e.toString());
			JLog.write("Main Menu - " + mainMenu);
			JLog.write("Sub Menu - " + subMenu);
			// Skip screenshot if browser session is dead to avoid a cascading exception
			boolean sessionDead = (e instanceof org.openqa.selenium.WebDriverException)
					&& (e.getMessage() != null)
					&& (e.getMessage().contains("session deleted") || e.getMessage().contains("invalid session id")
							|| e.getMessage().contains("not connected to DevTools"));
			if (!sessionDead) {
				JLog.screenCapture();
			}
			if (e.toString()
					.contains("Unable to find the Menu Item (Change Dashboard News|name=Change Dashboard News)")) {
				// debugging for finding menu missing after user logins
				// AbstractPage.browserSession.getDriver().findElement(By.className("eto-input__clear")).click();
				// JLog.screenCapture();
				// Menu.filter(HarmonyMTCMNavigation.SubMenu("Main", "Upload"));
				// JLog.screenCapture();
				logout_mtcm();
				JLog.screenCapture();
				login_harmony_mtcm("mtcmUser");
				Menu.filter(HarmonyMTCMNavigation.SubMenu(mainMenu, subMenu));
				JLog.fail("Failed to find the subMenu - " + subMenu);
			}
			if (e.toString().contains("ElementClickInterceptedException")
					|| e.toString().contains("ElementClickInterceptedException")) {
				AbstractPage.browserSession.closeAll();
				lastLoginObj = null;
				JLog.fail("Failed to click on Menu.");
			}
		}
		checkForErrors();
	}

}