/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.item.entity;

import com.scplatform.pcm.aml.entity.Aml;
import com.scplatform.pcm.aml.entity.AmlId;
import com.scplatform.pcm.assignment.entity.ItemAssignment;
import com.scplatform.pcm.avl.entity.Avl;
import com.scplatform.pcm.bom.entity.Bom;
import com.scplatform.pcm.bom.entity.BomLine;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.common.entity.Attribute;
import com.scplatform.pcm.common.entity.VersionRevision;
import com.scplatform.pcm.contact.entity.Contact;
import com.scplatform.pcm.functionalGroup.entity.FunctionalGroup;
import com.scplatform.pcm.platform.entity.Platform;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemTest {

    @Test
    void testDefaultConstructorState() {
        Item item = new Item();
        assertEquals(Item.ITEM, item.getItemType());
        assertNotNull(item.getItemVersion());
        assertNotNull(item.getContainsBoms());
        assertNotNull(item.getWhereUsedSet());
        assertNotNull(item.getAttributes());
        assertNotNull(item.getAmls());
        assertNotNull(item.getAvls());
        assertNotNull(item.getCategories());
        assertNotNull(item.getPlatforms());
        assertNotNull(item.getGroupMembers());
        assertNotNull(item.getFunctionalGroups());
        assertNotNull(item.getAssignments());
        assertNotNull(item.getAlternates());
    }

    @Test
    void testKeyConstructor() {
        Item item = new Item(42L);
        assertEquals(42L, item.getItemKey());
    }

    @Test
    void testAllSimpleSettersAndGetters() {
        Item item = new Item();
        item.setItemKey(1L);
        item.setItemNumber("PN");
        item.setItemId("ID");
        item.setItemType("M");
        item.setItemExternalId("EXT");
        item.setLifeCycleTypeCode("LC");
        item.setLifeCycleTypeCodeOther("LCO");
        item.setItemClassification("CLS");
        item.setProductFamily("FAM");
        item.setMakeBuy("MB");
        item.setMakeBuyOther("MBO");
        item.setOwnerName("OWN");
        item.setDescription("desc");
        item.setIsTopLevel(Boolean.TRUE);
        item.setCollaboration(Boolean.FALSE);
        item.setItemPartType("PT");
        item.setProductUomCode("EA");
        item.setManagedFlag("y"); // upper-cased
        item.setSerialNumberRequired(Boolean.TRUE);
        item.setCertRequired(Boolean.FALSE);
        item.setInventory(BigDecimal.ONE);
        item.setEol(Boolean.TRUE);
        item.setEolType("OBSOLETE");
        Date d = new Date();
        item.setEolLastChanged(d);

        BusinessEntity be = mock(BusinessEntity.class);
        item.setBusinessEntity(be);
        Contact contact = mock(Contact.class);
        item.setContact(contact);

        VersionRevision vr = new VersionRevision("r", "v");
        item.setItemVersion(vr);

        assertEquals(1L, item.getItemKey());
        assertEquals("PN", item.getItemNumber());
        assertEquals("ID", item.getItemId());
        assertEquals("M", item.getItemType());
        assertEquals("EXT", item.getItemExternalId());
        assertEquals("LC", item.getLifeCycleTypeCode());
        assertEquals("LCO", item.getLifeCycleTypeCodeOther());
        assertEquals("CLS", item.getItemClassification());
        assertEquals("FAM", item.getProductFamily());
        assertEquals("MB", item.getMakeBuy());
        assertEquals("MBO", item.getMakeBuyOther());
        assertEquals("OWN", item.getOwnerName());
        assertEquals("desc", item.getDescription());
        assertEquals(Boolean.TRUE, item.getIsTopLevel());
        assertEquals(Boolean.FALSE, item.getCollaboration());
        assertEquals("PT", item.getItemPartType());
        assertEquals("EA", item.getProductUomCode());
        assertEquals("Y", item.getManagedFlag());
        assertEquals(Boolean.TRUE, item.getSerialNumberRequired());
        assertEquals(Boolean.FALSE, item.getCertRequired());
        assertEquals(BigDecimal.ONE, item.getInventory());
        assertEquals(Boolean.TRUE, item.getEol());
        assertEquals("OBSOLETE", item.getEolType());
        assertEquals(d, item.getEolLastChanged());
        assertSame(be, item.getBusinessEntity());
        assertSame(contact, item.getContact());
        assertSame(vr, item.getItemVersion());
        assertEquals("r", item.getRevision());
        assertEquals("v", item.getVersion());
    }

    @Test
    void testSettersForCollections() {
        Item item = new Item();
        Set<Bom> boms = new HashSet<>(); item.setContainsBoms(boms);
        Set<BomLine> wu = new HashSet<>(); item.setWhereUsedSet(wu);
        List<Attribute> attrs = new ArrayList<>(); item.setAttributes(attrs);
        Set<ItemCategory> cats = new HashSet<>(); item.setCategories(cats);
        Set<Platform> pfs = new HashSet<>(); item.setPlatforms(pfs);
        Set<Item> gm = new HashSet<>(); item.setGroupMembers(gm);
        Set<FunctionalGroup> fgs = new HashSet<>(); item.setFunctionalGroups(fgs);

        assertSame(boms, item.getContainsBoms());
        assertSame(wu, item.getWhereUsedSet());
        assertSame(attrs, item.getAttributes());
        assertSame(cats, item.getCategories());
        assertSame(pfs, item.getPlatforms());
        assertSame(gm, item.getGroupMembers());
        assertSame(fgs, item.getFunctionalGroups());
    }

    // ----- AML helpers -----

    @Test
    void testAddAndGetAndRemoveAml() {
        Item item = new Item();
        Aml aml = mock(Aml.class);
        AmlId id = mock(AmlId.class);
        when(aml.getAmlId()).thenReturn(id);

        item.addAml(aml);
        assertTrue(item.getAmls().contains(aml));
        assertSame(aml, item.getAml(id));

        AmlId other = mock(AmlId.class);
        assertNull(item.getAml(other));

        item.removeAml(aml);
        assertFalse(item.getAmls().contains(aml));
    }

    @Test
    void testGetManufacturers() {
        Item item = new Item();
        BusinessEntity m1 = mock(BusinessEntity.class);
        Aml aml = mock(Aml.class);
        AmlId id = mock(AmlId.class);
        when(aml.getAmlId()).thenReturn(id);
        when(aml.getMfg()).thenReturn(m1);
        item.addAml(aml);
        Set<BusinessEntity> mfgs = item.getManufacturers();
        assertEquals(1, mfgs.size());
        assertTrue(mfgs.contains(m1));
    }

    // ----- AVL helpers -----

    @Test
    void testAddAndRemoveAvl_AndGetAvl_AndForKey() {
        Item parent = new Item();
        Avl avl = mock(Avl.class);
        Item supplierItem = new Item();
        when(avl.getItem()).thenReturn(parent);
        when(avl.getSupplierItem()).thenReturn(supplierItem);
        when(avl.getSupplierItemKey()).thenReturn(7L);

        parent.addAvl(avl);
        assertTrue(parent.getAvls().contains(avl));
        assertSame(avl, parent.getAvl(parent, supplierItem));
        assertNull(parent.getAvl(parent, new Item()));
        assertSame(avl, parent.getAvlForSupplierItemKey(7L));
        assertNull(parent.getAvlForSupplierItemKey(99L));

        parent.removeAvl(avl);
        assertFalse(parent.getAvls().contains(avl));
    }

    // ----- Category / Platform / GroupMember helpers -----

    @Test
    void testCategoryPlatformGroupMemberMutators() {
        Item item = new Item();
        ItemCategory c = new ItemCategory(1L);
        item.addCategory(c);
        assertTrue(item.getCategories().contains(c));

        Platform p = mock(Platform.class);
        item.addPlatform(p);
        assertTrue(item.getPlatforms().contains(p));
        item.removePlatform(p);
        assertFalse(item.getPlatforms().contains(p));

        item.addPlatform(p);
        item.removeAllPlatforms();
        assertTrue(item.getPlatforms().isEmpty());

        Item member = new Item();
        member.setItemNumber("M1");
        member.setItemType(Item.ITEM);
        item.addGroupMember(member);
        assertTrue(item.getGroupMembers().contains(member));
        item.removeGroupMember(member);
        assertFalse(item.getGroupMembers().contains(member));
    }

    // ----- Attributes -----

    @Test
    void testAddAttribute_AddsOnce() {
        Item item = new Item();
        Attribute a = mock(Attribute.class);
        assertTrue(item.addAttribute(a));
        assertFalse(item.addAttribute(a));
        assertEquals(1, item.getAttributes().size());
    }

    // ----- Alternates -----

    @Test
    void testAddAndGetAlternateItem() {
        Item parent = new Item();
        Item alt = new Item();
        alt.setItemNumber("ALT");
        alt.setItemType(Item.ITEM);
        ItemAlternate ia = new ItemAlternate();
        ia.setAlternateItem(alt);

        assertTrue(parent.addAlternateItem(ia));
        assertSame(parent, ia.getItem());
        assertSame(ia, parent.getAlternateItem(alt));
        assertNull(parent.getAlternateItem(new Item()));
    }

    // ----- equals / hashCode / compareTo / toString -----

    private Item buildKeyed(String number, String id, String type, VersionRevision vr, BusinessEntity be) {
        Item it = new Item();
        it.setItemNumber(number);
        it.setItemId(id);
        it.setItemType(type);
        it.setItemVersion(vr);
        it.setBusinessEntity(be);
        return it;
    }

    @Test
    void testEqualsAndHashCode() {
        BusinessEntity be = mock(BusinessEntity.class);
        VersionRevision vr = new VersionRevision("r", "v");
        Item a = buildKeyed("PN", "ID", Item.ITEM, vr, be);
        Item b = buildKeyed("PN", "ID", Item.ITEM, vr, be);
        Item c = buildKeyed("PN2", "ID", Item.ITEM, vr, be);

        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, "string");
    }

    @Test
    void testCompareTo() {
        BusinessEntity be = mock(BusinessEntity.class);
        VersionRevision vr = new VersionRevision("r", "v");
        Item a = buildKeyed("AAA", "ID", Item.ITEM, vr, be);
        Item b = buildKeyed("BBB", "ID", Item.ITEM, vr, be);
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
        Item ac = buildKeyed("AAA", "ID", Item.ITEM, vr, be);
        assertEquals(0, a.compareTo(ac));
    }

    @Test
    void testToString_WithAndWithoutNullables() {
        Item item = new Item();
        item.setItemKey(5L);
        item.setItemNumber("PN");
        // null id and null description
        String s1 = item.toString();
        assertTrue(s1.contains("5"));
        assertTrue(s1.contains("PN"));

        item.setItemId("ID");
        item.setDescription("d");
        String s2 = item.toString();
        assertTrue(s2.contains("ID"));
        assertTrue(s2.contains("d"));
    }

    // ----- getEolState -----

    @Test
    void testGetEolState_ActiveWhenNullOrFalse() {
        Item item = new Item();
        assertEquals("ACTIVE", item.getEolState());
        item.setEol(Boolean.FALSE);
        assertEquals("ACTIVE", item.getEolState());
    }

    @Test
    void testGetEolState_InactiveWhenEolTrueWithoutType() {
        Item item = new Item();
        item.setEol(Boolean.TRUE);
        assertEquals("INACTIVE", item.getEolState());
        item.setEolType("");
        assertEquals("INACTIVE", item.getEolState());
    }

    @Test
    void testGetEolState_TypeWhenSet() {
        Item item = new Item();
        item.setEol(Boolean.TRUE);
        item.setEolType("OBSOLETE");
        assertEquals("OBSOLETE", item.getEolState());
    }

    // ----- getAttribute -----

    @Test
    void testGetAttribute_ReturnsNullWhenEmpty() {
        Item item = new Item();
        assertNull(item.getAttribute("anyName"));
    }

    @Test
    void testGetAttribute_StringTypeStripsNewlines() {
        Item item = new Item();
        com.scplatform.pcm.common.entity.Attribute attr = new com.scplatform.pcm.common.entity.Attribute();
        attr.setAttrName("name");
        attr.setAttrType(com.scplatform.pcm.common.entity.AttributeType.STRING);
        attr.setAttrValue("hello\r\nworld\nfoo");
        item.addAttribute(attr);
        Object value = item.getAttribute("name");
        assertEquals("helloworldfoo", value);
    }

    @Test
    void testGetAttribute_NonStringReturnsRawValue() {
        Item item = new Item();
        com.scplatform.pcm.common.entity.Attribute attr = new com.scplatform.pcm.common.entity.Attribute();
        attr.setAttrName("name");
        attr.setAttrType(com.scplatform.pcm.common.entity.AttributeType.INTEGER);
        attr.setAttrValue("42");
        item.addAttribute(attr);
        Object value = item.getAttribute("name");
        assertEquals(42, value);
    }
}
