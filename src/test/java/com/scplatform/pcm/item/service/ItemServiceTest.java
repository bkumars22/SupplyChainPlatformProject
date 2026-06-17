/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.item.service;

import com.scplatform.pcm.SpringContextHolder;
import com.scplatform.pcm.aml.entity.Aml;
import com.scplatform.pcm.assignment.entity.ItemAssignment;
import com.scplatform.pcm.avl.entity.Avl;
import com.scplatform.pcm.avl.repository.AvlRepository;
import com.scplatform.pcm.bom.entity.Bom;
import com.scplatform.pcm.bom.entity.BomLine;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.common.entity.Attribute;
import com.scplatform.pcm.common.entity.FlexAttributeDefn;
import com.scplatform.pcm.common.entity.FlexAttributeManager;
import com.scplatform.pcm.common.entity.FlexAttributesDefn;
import com.scplatform.pcm.common.entity.VersionRevision;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.functionalGroup.entity.FunctionalGroup;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.item.entity.ItemAlternate;
import com.scplatform.pcm.item.entity.ItemCategory;
import com.scplatform.pcm.item.repository.ItemRepository;
import com.scplatform.pcm.platform.entity.Platform;
import com.scplatform.pcm.util.message.SCPlatformMessages;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    private ItemRepository itemRepository;
    private AvlRepository avlRepository;
    private ItemService service;

    private MockedStatic<SpringContextHolder> contextHolderStatic;
    private SCPlatformMessages prevInstance;

    @BeforeEach
    void setUp() throws Exception {
        itemRepository = mock(ItemRepository.class);
        avlRepository = mock(AvlRepository.class);
        service = new ItemService(itemRepository, avlRepository);

        // Stub SpringContextHolder so FlexAttributeManager.ITEM.getFlexAttributeDefinitionList()
        // returns an empty list (configFile null -> defns null -> emptyList).
        PcmConfigUtil cfg = mock(PcmConfigUtil.class);
        when(cfg.getString(anyString())).thenReturn(null);
        contextHolderStatic = mockStatic(SpringContextHolder.class);
        contextHolderStatic.when(() -> SpringContextHolder.getBean(PcmConfigUtil.class)).thenReturn(cfg);

        // Save & install SCPlatformMessages.INSTANCE mock
        prevInstance = SCPlatformMessages.INSTANCE;
        SCPlatformMessages msgs = mock(SCPlatformMessages.class);
        when(msgs.getAuditMessage(eq("audit.itemTitle"), any(Object[].class), any())).thenReturn("AUDIT_TITLE");
        Field f = SCPlatformMessages.class.getDeclaredField("INSTANCE");
        f.setAccessible(true);
        f.set(null, msgs);
    }

    @AfterEach
    void tearDown() throws Exception {
        contextHolderStatic.close();
        Field f = SCPlatformMessages.class.getDeclaredField("INSTANCE");
        f.setAccessible(true);
        f.set(null, prevInstance);
    }

    // ----- getItem / findItemByNumber / findItemByNaturalKey -----

    @Test
    void testGetItem_PresentAndAbsent() {
        Item item = mock(Item.class);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        assertSame(item, service.getItem(1L));

        when(itemRepository.findById(2L)).thenReturn(Optional.empty());
        assertNull(service.getItem(2L));
    }

    @Test
    void testFindItemByNumber_PresentAndAbsent() {
        BusinessEntity be = mock(BusinessEntity.class);
        Item item = mock(Item.class);
        when(itemRepository.findByItemNumberAndBusinessEntity("PN", "ID", "I", be))
                .thenReturn(Optional.of(item));
        assertSame(item, service.findItemByNumber("PN", "ID", "I", be));

        when(itemRepository.findByItemNumberAndBusinessEntity("PN2", null, null, be))
                .thenReturn(Optional.empty());
        assertNull(service.findItemByNumber("PN2", null, null, be));
    }

    @Test
    void testFindItemByNaturalKey_PresentAndAbsent() {
        BusinessEntity be = mock(BusinessEntity.class);
        Item item = mock(Item.class);
        when(itemRepository.findByItemNumberAndItemIdAndVersionAndRevisionAndItemTypeAndBusinessEntity(
                "PN", "ID", "v", "r", "I", be)).thenReturn(Optional.of(item));
        assertSame(item, service.findItemByNaturalKey("PN", "ID", "v", "r", "I", be));

        when(itemRepository.findByItemNumberAndItemIdAndVersionAndRevisionAndItemTypeAndBusinessEntity(
                "PN", null, null, null, null, be)).thenReturn(Optional.empty());
        assertNull(service.findItemByNaturalKey("PN", null, null, null, null, be));
    }

    // ----- autoCreateAvl: 4 branches -----

    @Test
    void testAutoCreateAvl_NullItem() {
        BusinessEntity supplier = mock(BusinessEntity.class);
        assertNull(service.autoCreateAvl(null, supplier, false));
    }

    @Test
    void testAutoCreateAvl_NullSupplier() {
        Item item = mock(Item.class);
        assertNull(service.autoCreateAvl(item, null, false));
    }

    @Test
    void testAutoCreateAvl_SupplierAlreadyOnAvl() {
        Item item = mock(Item.class);
        BusinessEntity supplier = mock(BusinessEntity.class);
        when(avlRepository.findByItemAndSupplier(item, supplier))
                .thenReturn(List.of(mock(Avl.class)));

        assertNull(service.autoCreateAvl(item, supplier, true));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void testAutoCreateAvl_ExistingSupplierItem_NoCheck() {
        Item item = mock(Item.class);
        BusinessEntity supplier = mock(BusinessEntity.class);
        Item supplierItem = mock(Item.class);
        when(item.getItemNumber()).thenReturn("PN");
        when(itemRepository.findByItemNumberAndBusinessEntity("PN", null, Item.SUP_ITEM, supplier))
                .thenReturn(Optional.of(supplierItem));

        Avl created = service.autoCreateAvl(item, supplier, false);

        assertNotNull(created);
        assertSame(item, created.getItem());
        assertSame(supplier, created.getSupplier());
        assertSame(supplierItem, created.getSupplierItem());
        assertEquals("AutoCreated", created.getDescription());
        assertTrue(created.getCurrentFlag());
        verify(item).addAvl(created);
        verify(itemRepository).save(item);
        verify(itemRepository, never()).save(argThat(it -> it != item));
    }

    @Test
    void testAutoCreateAvl_NewSupplierItem_CreatedWhenNotFound() {
        Item item = mock(Item.class);
        BusinessEntity supplier = mock(BusinessEntity.class);
        when(item.getItemNumber()).thenReturn("PN");
        when(item.getDescription()).thenReturn("desc");
        when(item.getItemId()).thenReturn("ID");
        when(item.getRevision()).thenReturn("r");
        when(item.getVersion()).thenReturn("v");
        when(item.getDataSource()).thenReturn(null); // -> "B2B"
        when(itemRepository.findByItemNumberAndBusinessEntity("PN", null, Item.SUP_ITEM, supplier))
                .thenReturn(Optional.empty());
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        Avl created = service.autoCreateAvl(item, supplier, false);

        assertNotNull(created);
        assertSame(item, created.getItem());
        // saved supplier item with B2B fallback
        verify(itemRepository).save(argThat(it -> it != null && "B2B".equals(((Item) it).getDataSource())));
    }

    @Test
    void testAutoCreateAvl_NewSupplierItem_PreservesDataSource() {
        Item item = mock(Item.class);
        BusinessEntity supplier = mock(BusinessEntity.class);
        when(item.getItemNumber()).thenReturn("PN");
        when(item.getDescription()).thenReturn("desc");
        when(item.getItemId()).thenReturn("ID");
        when(item.getRevision()).thenReturn("r");
        when(item.getVersion()).thenReturn("v");
        when(item.getDataSource()).thenReturn("MANUAL");
        when(itemRepository.findByItemNumberAndBusinessEntity("PN", null, Item.SUP_ITEM, supplier))
                .thenReturn(Optional.empty());
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        Avl created = service.autoCreateAvl(item, supplier, false);

        assertNotNull(created);
        verify(itemRepository).save(argThat(it -> it != null && it != item && "MANUAL".equals(((Item) it).getDataSource())));
    }

    @Test
    void testAutoCreateAvl_CheckFlagButNoExistingAvl_StillCreates() {
        Item item = mock(Item.class);
        BusinessEntity supplier = mock(BusinessEntity.class);
        Item supplierItem = mock(Item.class);
        when(avlRepository.findByItemAndSupplier(item, supplier)).thenReturn(Collections.emptyList());
        when(item.getItemNumber()).thenReturn("PN");
        when(itemRepository.findByItemNumberAndBusinessEntity("PN", null, Item.SUP_ITEM, supplier))
                .thenReturn(Optional.of(supplierItem));

        Avl created = service.autoCreateAvl(item, supplier, true);
        assertNotNull(created);
    }

    // ----- getAuditTitle -----

    @Test
    void testGetAuditTitle_Null() {
        assertEquals("", service.getAuditTitle(null));
    }

    @Test
    void testGetAuditTitle_WithAndWithoutBusinessEntity() {
        Item item = mock(Item.class);
        when(item.getItemNumber()).thenReturn("PN");
        when(item.getBusinessEntity()).thenReturn(null);
        assertEquals("AUDIT_TITLE", service.getAuditTitle(item));

        BusinessEntity be = mock(BusinessEntity.class);
        when(be.getBusinessEntityName()).thenReturn("BE-NAME");
        when(item.getBusinessEntity()).thenReturn(be);
        assertEquals("AUDIT_TITLE", service.getAuditTitle(item));
    }

    // ----- getItemNaturalKeyAsJSON -----

    @Test
    void testGetItemNaturalKeyAsJSON_Null() {
        assertNull(service.getItemNaturalKeyAsJSON(null));
    }

    @Test
    void testGetItemNaturalKeyAsJSON_WithAndWithoutBusinessEntity() {
        Item item = mock(Item.class);
        when(item.getItemKey()).thenReturn(1L);
        when(item.getItemNumber()).thenReturn("PN");
        when(item.getDescription()).thenReturn("desc");
        VersionRevision vr = mock(VersionRevision.class);
        when(vr.getRevision()).thenReturn("r1");
        when(vr.getVersion()).thenReturn("v1");
        when(item.getItemVersion()).thenReturn(vr);
        when(item.getBusinessEntity()).thenReturn(null);

        ObjectNode node = service.getItemNaturalKeyAsJSON(item);
        assertNotNull(node);
        assertEquals(1L, node.get("itemKey").asLong());
        assertEquals("PN", node.get("itemIdentifier").asText());
        assertEquals("desc", node.get("description").asText());
        assertTrue(node.get("business").isNull());
        assertEquals("r1", node.get("revision").asText());
        assertEquals("v1", node.get("version").asText());

        BusinessEntity be = mock(BusinessEntity.class);
        ObjectNode beJson = new ObjectMapper().createObjectNode().put("be", "yes");
        when(be.getNaturalKeyAsJSON()).thenReturn(beJson);
        when(item.getBusinessEntity()).thenReturn(be);
        node = service.getItemNaturalKeyAsJSON(item);
        assertEquals(beJson, node.get("business"));
    }

    // ----- getInlineItemNaturalKeyAsJSON: cover EOL branches and helpers -----

    private Item buildFullItem(Boolean eol, String eolType, BusinessEntity be) {
        Item item = mock(Item.class);
        when(item.getItemKey()).thenReturn(1L);
        when(item.getItemNumber()).thenReturn("PN");
        when(item.getDescription()).thenReturn("desc");
        when(item.getItemType()).thenReturn("I");
        when(item.getBusinessEntity()).thenReturn(be);
        when(item.getManagedFlag()).thenReturn("Y");
        VersionRevision vr = mock(VersionRevision.class);
        when(vr.getRevision()).thenReturn("r");
        when(vr.getVersion()).thenReturn("v");
        when(item.getItemVersion()).thenReturn(vr);
        when(item.getIsTopLevel()).thenReturn(true);
        when(item.getItemClassification()).thenReturn("CLS");
        when(item.getProductFamily()).thenReturn("FAM");
        when(item.getLifeCycleTypeCode()).thenReturn("LC");
        when(item.getLifeCycleTypeCodeOther()).thenReturn("LCO");
        when(item.getProductUomCode()).thenReturn("EA");
        when(item.getInventory()).thenReturn(java.math.BigDecimal.TEN);
        when(item.getEol()).thenReturn(eol);
        when(item.getEolType()).thenReturn(eolType);
        when(item.getInsertDate()).thenReturn(new Date());
        when(item.getUpdateDate()).thenReturn(new Date());
        when(item.getDataSource()).thenReturn("DS");
        when(item.getGroupMembers()).thenReturn(Collections.emptySet());
        when(item.getCategories()).thenReturn(Collections.emptySet());
        when(item.getFunctionalGroups()).thenReturn(Collections.emptySet());
        when(item.getAssignments()).thenReturn(Collections.emptySet());
        when(item.getPlatforms()).thenReturn(Collections.emptySet());
        when(item.getAttributes()).thenReturn(Collections.emptyList());
        when(item.getAmls()).thenReturn(Collections.emptySet());
        when(item.getAvls()).thenReturn(Collections.emptySet());
        when(item.getAlternates()).thenReturn(Collections.emptySet());
        when(item.getWhereUsedSet()).thenReturn(Collections.emptySet());
        return item;
    }

    @Test
    void testGetInlineItemNaturalKeyAsJSON_Null() throws Exception {
        assertNull(service.getInlineItemNaturalKeyAsJSON(null));
    }

    @Test
    void testGetInlineItemNaturalKeyAsJSON_EolNull_ActiveStatus() throws Exception {
        Item item = buildFullItem(null, null, null);
        ObjectNode n = service.getInlineItemNaturalKeyAsJSON(item);
        assertEquals("ACTIVE", n.get("eolState").asText());
        assertTrue(n.get("business").isNull());
    }

    @Test
    void testGetInlineItemNaturalKeyAsJSON_EolFalse_ActiveStatus() throws Exception {
        BusinessEntity be = mock(BusinessEntity.class);
        when(be.getNaturalKeyAsJSON()).thenReturn(new ObjectMapper().createObjectNode());
        Item item = buildFullItem(Boolean.FALSE, null, be);
        ObjectNode n = service.getInlineItemNaturalKeyAsJSON(item);
        assertEquals("ACTIVE", n.get("eolState").asText());
    }

    @Test
    void testGetInlineItemNaturalKeyAsJSON_EolTrue_NoType_Inactive() throws Exception {
        Item item = buildFullItem(Boolean.TRUE, null, null);
        ObjectNode n = service.getInlineItemNaturalKeyAsJSON(item);
        assertEquals("INACTIVE", n.get("eolState").asText());
    }

    @Test
    void testGetInlineItemNaturalKeyAsJSON_EolTrue_EmptyType_Inactive() throws Exception {
        Item item = buildFullItem(Boolean.TRUE, "", null);
        ObjectNode n = service.getInlineItemNaturalKeyAsJSON(item);
        assertEquals("INACTIVE", n.get("eolState").asText());
    }

    @Test
    void testGetInlineItemNaturalKeyAsJSON_EolTrue_WithType() throws Exception {
        Item item = buildFullItem(Boolean.TRUE, "OBSOLETE", null);
        ObjectNode n = service.getInlineItemNaturalKeyAsJSON(item);
        assertEquals("OBSOLETE", n.get("eolState").asText());
    }

    @Test
    void testGetInlineItemNaturalKeyAsJSON_PopulatesAllArrays() throws Exception {
        Item item = buildFullItem(Boolean.FALSE, null, null);

        // categories
        ItemCategory cat = mock(ItemCategory.class);
        when(cat.getCategoriesNaturalKeyAsJSON()).thenReturn(new ObjectMapper().createObjectNode().put("c", 1));
        when(item.getCategories()).thenReturn(new LinkedHashSet<>(List.of(cat)));

        // functional groups
        FunctionalGroup fg = mock(FunctionalGroup.class);
        when(fg.getFgNaturalKeyAsJSON()).thenReturn(new ObjectMapper().createObjectNode());
        when(item.getFunctionalGroups()).thenReturn(new LinkedHashSet<>(List.of(fg)));

        // assignments
        ItemAssignment ia = mock(ItemAssignment.class);
        when(ia.getAssignmentsNaturalKeyAsJSON()).thenReturn(new ObjectMapper().createObjectNode());
        when(item.getAssignments()).thenReturn(new LinkedHashSet<>(List.of(ia)));

        // platforms
        Platform pf = mock(Platform.class);
        when(pf.getPlatformsNaturalKeyAsJSON()).thenReturn(new ObjectMapper().createObjectNode());
        when(item.getPlatforms()).thenReturn(new LinkedHashSet<>(List.of(pf)));

        // attributes
        Attribute attr = mock(Attribute.class);
        when(attr.getAttrName()).thenReturn("Z_attr");
        when(attr.getAttributesNaturalKeyAsJSON()).thenReturn(new ObjectMapper().createObjectNode());
        Attribute attr2 = mock(Attribute.class);
        when(attr2.getAttrName()).thenReturn("A_attr");
        when(attr2.getAttributesNaturalKeyAsJSON()).thenReturn(new ObjectMapper().createObjectNode());
        when(item.getAttributes()).thenReturn(List.of(attr, attr2));

        // amls / avls / alternates / whereUsed
        Aml aml = mock(Aml.class);
        when(aml.getAmlsNaturalKeyAsJSON()).thenReturn(new ObjectMapper().createObjectNode());
        when(item.getAmls()).thenReturn(new LinkedHashSet<>(List.of(aml)));
        Avl avl = mock(Avl.class);
        when(avl.getAvlsNaturalKeyAsJSON()).thenReturn(new ObjectMapper().createObjectNode());
        when(item.getAvls()).thenReturn(new LinkedHashSet<>(List.of(avl)));
        ItemAlternate alt = mock(ItemAlternate.class);
        when(alt.getAlternatesNaturalKeyAsJSON()).thenReturn(new ObjectMapper().createObjectNode());
        when(item.getAlternates()).thenReturn(new LinkedHashSet<>(List.of(alt)));

        BomLine bl = mock(BomLine.class);
        Item bli = mock(Item.class);
        BusinessEntity blBe = mock(BusinessEntity.class);
        when(blBe.getBusinessEntityName()).thenReturn("BE");
        when(bli.getBusinessEntity()).thenReturn(blBe);
        when(bl.getItem()).thenReturn(bli);
        Bom bom = mock(Bom.class);
        when(bom.getBomName()).thenReturn("bom");
        when(bom.getStatus()).thenReturn("OK");
        when(bom.getBomDesc()).thenReturn("BD");
        when(bl.getBom()).thenReturn(bom);
        when(item.getWhereUsedSet()).thenReturn(new LinkedHashSet<>(List.of(bl)));

        ObjectNode n = service.getInlineItemNaturalKeyAsJSON(item);
        assertEquals(1, n.get("categories").size());
        assertEquals(1, n.get("functionalGroups").size());
        assertEquals(1, n.get("assignment").size());
        assertEquals(1, n.get("platforms").size());
        assertEquals(2, n.get("attributes").size());
        assertEquals(1, n.get("amls").size());
        assertEquals(1, n.get("avls").size());
        assertEquals(1, n.get("alternates").size());
        assertEquals(1, n.get("whereUsed").size());
    }

    // ----- getManufacturers / getSuppliers -----

    @Test
    void testGetManufacturers_NullAndPopulated() {
        assertTrue(service.getManufacturers(null).isEmpty());

        Item item = mock(Item.class);
        BusinessEntity m1 = mock(BusinessEntity.class);
        BusinessEntity m2 = mock(BusinessEntity.class);
        Aml a1 = mock(Aml.class); when(a1.getMfg()).thenReturn(m1);
        Aml a2 = mock(Aml.class); when(a2.getMfg()).thenReturn(m2);
        Aml a3 = mock(Aml.class); when(a3.getMfg()).thenReturn(m1);
        when(item.getAmls()).thenReturn(new LinkedHashSet<>(List.of(a1, a2, a3)));

        Set<BusinessEntity> result = service.getManufacturers(item);
        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(m1, m2)));
    }

    @Test
    void testGetSuppliers_NullAndPopulated() {
        assertTrue(service.getSuppliers(null).isEmpty());

        Item item = mock(Item.class);
        BusinessEntity s1 = mock(BusinessEntity.class);
        when(s1.getBusinessEntityName()).thenReturn("Beta");
        BusinessEntity s2 = mock(BusinessEntity.class);
        when(s2.getBusinessEntityName()).thenReturn("Alpha");
        Avl v1 = mock(Avl.class); when(v1.getSupplier()).thenReturn(s1);
        Avl v2 = mock(Avl.class); when(v2.getSupplier()).thenReturn(s2);
        when(item.getAvls()).thenReturn(new LinkedHashSet<>(List.of(v1, v2)));

        Set<BusinessEntity> result = service.getSuppliers(item);
        assertEquals(2, result.size());
        // sorted by name -> Alpha first
        assertEquals("Alpha", result.iterator().next().getBusinessEntityName());
    }

    // ----- getDerivedManagedFlag -----

    @Test
    void testGetDerivedManagedFlag_Null() {
        assertNull(service.getDerivedManagedFlag(null));
    }

    @Test
    void testGetDerivedManagedFlag_FromItem() {
        Item item = mock(Item.class);
        when(item.getManagedFlag()).thenReturn("Y");
        assertEquals("Y", service.getDerivedManagedFlag(item));
    }

    @Test
    void testGetDerivedManagedFlag_FromCategory() {
        Item item = mock(Item.class);
        when(item.getManagedFlag()).thenReturn(null);
        ItemCategory c1 = mock(ItemCategory.class); when(c1.getManagedFlag()).thenReturn(null);
        ItemCategory c2 = mock(ItemCategory.class); when(c2.getManagedFlag()).thenReturn("N");
        when(item.getCategories()).thenReturn(new LinkedHashSet<>(List.of(c1, c2)));
        assertEquals("N", service.getDerivedManagedFlag(item));
    }

    @Test
    void testGetDerivedManagedFlag_NoneFound() {
        Item item = mock(Item.class);
        when(item.getManagedFlag()).thenReturn(null);
        ItemCategory c1 = mock(ItemCategory.class); when(c1.getManagedFlag()).thenReturn(null);
        when(item.getCategories()).thenReturn(new LinkedHashSet<>(List.of(c1)));
        assertNull(service.getDerivedManagedFlag(item));
    }

    // ----- getItemCategoryNames -----

    @Test
    void testGetItemCategoryNames_Null() {
        assertEquals("", service.getItemCategoryNames(null));
    }

    @Test
    void testGetItemCategoryNames_Multi() {
        Item item = mock(Item.class);
        ItemCategory c1 = mock(ItemCategory.class); when(c1.getCategoryName()).thenReturn("A");
        ItemCategory c2 = mock(ItemCategory.class); when(c2.getCategoryName()).thenReturn("B");
        when(item.getCategories()).thenReturn(new LinkedHashSet<>(List.of(c1, c2)));
        assertEquals("A, B", service.getItemCategoryNames(item));
    }

    @Test
    void testGetItemCategoryNames_Empty() {
        Item item = mock(Item.class);
        when(item.getCategories()).thenReturn(Collections.emptySet());
        assertEquals("", service.getItemCategoryNames(item));
    }

    // ----- determineItemType -----

    @Test
    void testDetermineItemType_Mfg() {
        BusinessEntity be = mock(BusinessEntity.class);
        when(be.getBusinessEntityTypeKey()).thenReturn(BusinessEntity.MFG_TYPE);
        Item item = mock(Item.class);
        assertEquals(Item.MFG_ITEM, ItemService.determineItemType(be, item));
    }

    @Test
    void testDetermineItemType_SupplierPhantom() {
        BusinessEntity be = mock(BusinessEntity.class);
        when(be.getBusinessEntityTypeKey()).thenReturn(BusinessEntity.SUPPLIER_TYPE);
        Item item = mock(Item.class);
        when(item.getItemType()).thenReturn(Item.PHANTOM_ITEM);
        assertEquals(Item.PHANTOM_ITEM, ItemService.determineItemType(be, item));
    }

    @Test
    void testDetermineItemType_SupplierStandard() {
        BusinessEntity be = mock(BusinessEntity.class);
        when(be.getBusinessEntityTypeKey()).thenReturn(BusinessEntity.SUPPLIER_TYPE);
        Item item = mock(Item.class);
        when(item.getItemType()).thenReturn("X");
        assertEquals(Item.SUP_ITEM, ItemService.determineItemType(be, item));
    }

    @Test
    void testDetermineItemType_Default() {
        BusinessEntity be = mock(BusinessEntity.class);
        when(be.getBusinessEntityTypeKey()).thenReturn(99L);
        Item item = mock(Item.class);
        assertEquals(Item.ITEM, ItemService.determineItemType(be, item));
    }

    // ----- getFlexAttributeDefinitions -----

    @Test
    void testGetFlexAttributeDefinitions() {
        // With our SpringContextHolder mock returning a config with no path, list should be empty
        List<FlexAttributeDefn> defns = service.getFlexAttributeDefinitions();
        assertNotNull(defns);
        assertTrue(defns.isEmpty());
    }

    // ----- countDistinctDataSourceByType / countItemByDataSource / findDistinctDataSources -----

    @Test
    void testCountDistinctDataSourceByType_NullReturnsZero() {
        assertEquals(0L, service.countDistinctDataSourceByType(null));
        verifyNoInteractions(itemRepository);
    }

    @Test
    void testCountDistinctDataSourceByType_Delegates() {
        when(itemRepository.countDistinctDataSourceByType("X")).thenReturn(7L);
        assertEquals(7L, service.countDistinctDataSourceByType("X"));
    }

    @Test
    void testCountItemByDataSource_NullReturnsZero() {
        assertEquals(0L, service.countItemByDataSource(null));
    }

    @Test
    void testCountItemByDataSource_Delegates() {
        when(itemRepository.countItemByDataSource("Y")).thenReturn(42L);
        assertEquals(42L, service.countItemByDataSource("Y"));
    }

    @Test
    void testFindDistinctDataSources_NullReturnsEmpty() {
        assertTrue(service.findDistinctDataSources(null).isEmpty());
    }

    @Test
    void testFindDistinctDataSources_Delegates() {
        when(itemRepository.findDistinctDataSources("Z")).thenReturn(List.of("A", "B"));
        assertEquals(List.of("A", "B"), service.findDistinctDataSources("Z"));
    }

    @Test
    void testGetInlineItemNaturalKeyAsJSON_FlexAttributesPopulated() throws Exception {
        Item item = buildFullItem(Boolean.FALSE, null, null);
        when(item.getFlexAttribute("stringAttribute1")).thenReturn("flexValue");

        FlexAttributeDefn defn = mock(FlexAttributeDefn.class);
        when(defn.getName()).thenReturn("flexName");
        when(defn.getAssociatedAttribute()).thenReturn("stringAttribute1");

        ItemService spy = spy(service);
        doReturn(List.of(defn)).when(spy).getFlexAttributeDefinitions();

        ObjectNode n = spy.getInlineItemNaturalKeyAsJSON(item);
        assertEquals(1, n.get("flex").size());
        assertEquals("flexValue", n.get("flex").get(0).get("flexName").asText());
    }
}
