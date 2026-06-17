/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ums.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.scplatform.pcm.accessControl.service.AccessControlService;
import com.scplatform.pcm.authentication.service.AppContextService;
import com.scplatform.pcm.launchpad.dto.Item;
import com.scplatform.pcm.workflow.dto.Header;
import com.scplatform.pcm.workflow.dto.Menu;
import com.scplatform.pcm.workflow.dto.Children;
import com.scplatform.pcm.util.common.SCPlatformConstant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.scplatform.pcm.role.entity.Role;
import com.scplatform.pcm.authentication.dto.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * 
 * @author averma
 *
 */
@Service
public class MenuItemGen {

    private final static Logger logger = LogManager.getLogger(MenuItemGen.class);

    private final AppContextService appContextService;

	private final Map<Role, Header> headerMapByRole = new HashMap<>();
	public final static String BASE_URL = "https://" + System.getProperty("e2.webproxy.dnsname") + System.getProperty("scplatform.junction") +  "/scplatform/";

    public MenuItemGen(AppContextService appContextService) {
        this.appContextService = appContextService;
    }

    public List<Item> generateMenuItems(ApplicationContext ac, String requestApp) throws IOException {
		logger.info("BASE_URL=" + BASE_URL);
		Header header = headerMapByRole.get(ac.getCurrentRole());
		if (header == null) {
			header = appContextService.getAccessableWorkflowsMenu(ac);
			headerMapByRole.put(ac.getCurrentRole(), header);
		}
		
		List<List<Menu>> listOfItemList = header.getMenu();		
		List<Item> launchpadItems = new ArrayList<>();
		for (List<Menu> items : listOfItemList) {
			for (Menu menu : items) {
				Item item = new Item();
				item.setApp("scplatform");
				item.setLabel(menu.getName());
				item.setName(menu.getName());
				item.setTitle(menu.getTitle());				
				item.setChildren(this.getItemChildren(menu, requestApp));
				launchpadItems.add(item);
			}
		}
		
		List<Item>rootItems = new ArrayList<>();
		Item rootItem = new Item();
		rootItem.setApp("scplatform");
		rootItem.setLabel("Multi Tier Cost Manager");
		rootItem.setName("scplatformRoot");
		rootItem.setTitle("Multi Tier Cost Manager");
		List<List<Item>> listOfChildItemLists = new ArrayList<>();
		listOfChildItemLists.add(launchpadItems);		
		rootItem.setChildren(listOfChildItemLists);
		rootItems.add(rootItem);		
		return rootItems;		
	}

    private List<List<Item>> getItemChildren (Menu menu, String requestedApp) {
      List<List<Item>> itemLists = new ArrayList<List<Item>>();
      List<List<Children>> childLists = menu.getChildren();
      itemLists = processListChild(childLists, requestedApp);
      return itemLists;
    }

    private List<List<Item>> processListChild (List<List<Children>> childLists,
        String requestedApp) {
      List<List<Item>> itemLists = new ArrayList<List<Item>>();
      List<Item> items = null;
      for (List<Children> childList : childLists) {
        items = getChildrenItems(childList, requestedApp);
        itemLists.add(items);
      }
      return itemLists;
    }

    private List<Item> getChildrenItems (List<Children> childList, String requestedApp) {
      List<Item> items = new ArrayList<Item>();
      for (Children child : childList) {
        if (child.getUrl() != null) {
          Item item = new Item();
          item.setApp("scplatform");
          item.setLabel(child.getName());
          item.setName(child.getName());
          item.setTitle(child.getTitle());
          if ("scplatform".equalsIgnoreCase(requestedApp)) {
            item.setUrl(BASE_URL + "e2redirect?url=" + child.getUrl());
            if (child.getUrl().contains("?")) {
              item.setUrl(BASE_URL + child.getUrl());
            }
          } else {
            item.setUrl(BASE_URL + "e2redirect?url=" + child.getUrl());
          }
          items.add(item);
        } else {
          List<List<Children>> newChildLists = child.getChildren();
          List<List<Item>> newItemLists = new ArrayList<List<Item>>();
          newItemLists = processListChild(newChildLists, requestedApp);
          Item newItem = new Item();
          newItem.setApp("scplatform");
          newItem.setLabel(child.getName());
          newItem.setName(child.getName());
          newItem.setTitle(child.getTitle());
          newItem.setChildren(newItemLists);
          items.add(newItem);
        }
      }
      return items;
    }

}
