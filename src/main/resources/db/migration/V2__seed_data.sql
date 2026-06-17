-- ============================================================
-- Flyway Migration V2 — Seed Data for Production
-- ============================================================

-- Roles
INSERT INTO PCM_ROLE (ROLE_ID, ROLE_NAME) VALUES
  ('ADMIN',     'Administrator'),
  ('BUS_ADMIN', 'Business Administrator'),
  ('GUEST',     'Guest User')
ON CONFLICT (ROLE_ID) DO NOTHING;

-- Default admin user (password: change on first login)
INSERT INTO PCM_USER (USER_ID, USER_NAME, EMAIL_ID, IS_ENABLED, ROLE_KEY)
SELECT 'admin', 'System Administrator', 'admin@company.com', TRUE, ROLE_KEY
FROM PCM_ROLE WHERE ROLE_ID = 'ADMIN'
ON CONFLICT (USER_ID) DO NOTHING;

-- Sample items
INSERT INTO ITEM_MASTER (ITEM_KEY, ITEM_NAME, UNIT_COST, CATEGORY_CODE, UOM) VALUES
  ('COMP-001', 'Microcontroller STM32', 12.5000, 'ELECTRONICS', 'EACH'),
  ('COMP-002', 'Capacitor 100uF',        0.0850, 'ELECTRONICS', 'EACH'),
  ('COMP-003', 'Steel Housing Grade A',  8.2500, 'MECHANICAL',  'EACH'),
  ('RAW-001',  'Aluminium Sheet 2mm',    3.4000, 'RAW_MATERIAL','KG'),
  ('FG-001',   'Widget Assembly v2',    45.0000, 'FINISHED_GOOD','EACH')
ON CONFLICT (ITEM_KEY) DO NOTHING;
