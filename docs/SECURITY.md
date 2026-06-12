# Security: OWASP-focused local guidance

This document explains quick steps taken to enable OWASP-style protections locally and how to run basic dependency scanning.

What I added
- `SecurityConfig` (Spring Security) — adds common security headers (CSP, HSTS, Referrer-Policy, XSS protection, frame options).
- `application-local.properties` — enables content-security-policy for the `local` profile.

How to test locally
1. Run the application with the `local` profile so CSP and headers are applied:

```powershell
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

2. Use a browser devtools or `curl -I http://localhost:8089/scplatform` and check response headers: `Content-Security-Policy`, `Strict-Transport-Security`, `Referrer-Policy`, `X-Frame-Options`.

Dependency scanning (OWASP Dependency-Check)
1. Add the Maven plugin `org.owasp:dependency-check-maven` to CI or run locally via the plugin coordinate. Example command (no POM changes required to run):

```powershell
mvn org.owasp:dependency-check-maven:check -Dformat=ALL
```

2. The report will be produced under `target/dependency-check-report.*` — fix or review any high/critical findings.

Next steps (optional)
- Add `dependency-check-maven` to the `pom.xml` build `plugins` so scans run in CI.
- Integrate SAST (e.g., SonarCloud) and SCA in your pipeline.
- Review CSP in `application-local.properties` and tighten `script-src`/`style-src` origins to match your app assets.
