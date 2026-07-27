# Local Oracle Database Setup for LearningProject

## What this app expects

The UI application is configured to use Oracle.

Source: `src/main/resources/application.properties`

```properties
spring.datasource.url=${DB_URL:jdbc:oracle:thin:@localhost:1521/XE}
spring.datasource.username=${DB_USERNAME:kumar}
spring.datasource.password=${DB_PASSWORD:kumar}
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.OracleDialect
```

That means:
- `DB_URL` can override the JDBC URL
- `DB_USERNAME` can override the database user
- `DB_PASSWORD` must be provided at runtime (environment variable or local override)

## Available schema scripts

The project already contains Oracle schema DDL under:
- `src/main/resources/config/deploy/schema/oracle/`

The primary schema files are:
- `create_mcm_model.sql`
- `create_mcm_views.sql`
- `create_margin_model.sql`
- `create_audit_model.sql`
- `create_best_bom_model.sql`
- `create_bom_auto_approve_model.sql`
- `create_forecast_auto_approve_model.sql`

Additional optional schema files used by the deploy scripts:
- `create_common_extract_model.sql`
- `create_collab_model.sql`
- `create_bom_extract_model.sql`
- `quartz_tables_oracle.sql`
- `bootstrap.sql`
- `roles.sql`

## Deploy helper

There is an Ant deployment file with a `schemacreate` target that executes the schema scripts via `sqlplus`:
- `src/main/resources/config/deploy/deploy.xml`

This target runs the same Oracle DDL files used by the project.

## Recommended local setup

### Option 1: Local Oracle XE / Docker

1. Install Docker on Windows.
2. Start a local Oracle XE container, for example:

```powershell
docker run -d --name oracle-xe -p 1521:1521 -e ORACLE_PASSWORD=Oracle123 -e ORACLE_ALLOW_REMOTE=true gvenzl/oracle-xe:18.4.0
```

3. Create the application schema and user from SQL*Plus or Oracle SQL Developer.

For example, connect with `sqlplus`:

```powershell
sqlplus sys/Oracle123@localhost:1521/XE as sysdba
```

4. Create the application schema/user:

```sql
CREATE USER scplatform IDENTIFIED BY scplatform;
GRANT CONNECT, RESOURCE, CREATE SESSION, CREATE TABLE, CREATE VIEW, CREATE SEQUENCE, CREATE PROCEDURE, CREATE TRIGGER, CREATE TYPE TO scplatform;
```

5. Run the schema scripts as that user:

```powershell
sqlplus test/test@localhost:1521/XE @src/main/resources/config/deploy/schema/oracle/create_mcm_model.sql
sqlplus test/test@localhost:1521/XE @src/main/resources/config/deploy/schema/oracle/create_mcm_views.sql
sqlplus test/test@localhost:1521/XE @src/main/resources/config/deploy/schema/oracle/create_margin_model.sql
sqlplus test/test@localhost:1521/XE @src/main/resources/config/deploy/schema/oracle/create_audit_model.sql
sqlplus test/test@localhost:1521/XE @src/main/resources/config/deploy/schema/oracle/create_best_bom_model.sql
sqlplus test/test@localhost:1521/XE @src/main/resources/config/deploy/schema/oracle/create_bom_auto_approve_model.sql
sqlplus test/test@localhost:1521/XE @src/main/resources/config/deploy/schema/oracle/create_forecast_auto_approve_model.sql
```

### Option 2: Use the Ant deploy target

In the project root, run:

```powershell
mvn -f src/main/resources/config/deploy/pom.xml -DskipTests clean install
```

Or use Ant directly if available, by passing the correct properties for `scplatform.db.user`, `scplatform.db.user.password`, `e2.db.app.tnsname`, and `e2.db.type`.

## Run the application with the local DB

Set environment variables before starting the Spring Boot app:

```powershell
$env:DB_URL = 'jdbc:oracle:thin:@localhost:1521/XE'
$env:DB_USERNAME = 'kumar'
$env:DB_PASSWORD = 'kumar'
```

Then start the app as usual.

## Important notes

- The application is Oracle-specific. It is not configured to use H2 or PostgreSQL.
- If the UI fails on startup, the first issue will usually be the DB connection or missing schema.
- The schema DDL already exists in the repository, so setting up a working local Oracle instance is the missing piece.

## After any bulk data load — recalculate supplier tiers

`SupplierProfile.tier` is not automatically derived from delivery history —
it's only set once (by seed data, or by `SupplierRatingService.addRating`'s
quality/delivery/responsiveness formula). The live supplier scorecard's
`otdScore`/`atRisk` fields, by contrast, are computed fresh from real
`SUPPLIER_DELIVERY` rows on every read. A bulk load of PO/delivery records
(CSV import, PO-receipt integration, etc.) can silently leave a supplier's
stored tier arbitrarily far out of sync with its actual on-time delivery
performance — see BB-SUP-03.

**After loading delivery/PO data in bulk, always call:**

```
POST /api/suppliers/recalculate-tiers
```

This recomputes every supplier's OTD score from `SUPPLIER_DELIVERY` and
updates `tier` accordingly (PREFERRED >=95%, APPROVED >=85%, CONDITIONAL
>=70%, PROBATION <70%). Suppliers with zero delivery history are left
untouched — there's no OTD evidence to derive a tier from.

## Where to start

If you want a quick local DB, the fastest path is:
1. Start Oracle XE in Docker.
2. Create user/schema `test`.
3. Run the Oracle SQL files from `src/main/resources/config/deploy/schema/oracle/`.
4. Point `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` to that local instance.
