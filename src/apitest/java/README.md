# Test Tier Layout

| Tier | Source root | Profile | File pattern | Meta-annotation |
|---|---|---|---|---|
| Unit | `src/test/java/`   | (default)         | `*Test.java`     | `@UnitTest` (optional) |
| API  | `src/apitest/java/`| `-P api-tests`    | `*ApiTest.java`  | `@ApiTest`  (optional) |
| UI   | `src/uitest/java/` | `-P uitest`       | `*UiTest.java`   | `@UiTest`   (optional) |

## Run

```powershell
./mvnw test                         # unit only — fast, default CI
./mvnw -P api-tests test            # API integration
./mvnw -P uitest test               # Selenium / Cucumber
./mvnw -P all-tests,uitest test     # everything
./mvnw -P uitest test "-Dtest=Commodity*UiTest"   # ad-hoc UI by module
```

## Conventions

* Tier is determined by **source root** — drop the file in the right folder and you are done.
* Class name suffix mirrors the tier (`*Test`, `*ApiTest`, `*UiTest`).
* For a test in an unusual location, add the matching meta-annotation
  (`@UnitTest` / `@ApiTest` / `@UiTest`) from `com.scplatform.pcm.testing`.
* Resources mirror the source roots:
  * `src/test/resources/`   — unit fixtures
  * `src/apitest/resources/` — API fixtures
  * `src/uitest/resources/`  — Selenium config / features / data
    (lives at the framework-required path `com/scplatform/selenium/scplatform/…`)
