# JVM generated resource tables

The JVM follows the same principle as Android: generate both the resource reference and
its resource table with the same complete identity, then look up that identity exactly.
`sample_account_settings` therefore cannot resolve to `sample_settings`, regardless of
source XML order. String formatting preserves repeated and reordered argument indices.

The existing JVM generator already emitted the OHOS-compatible JSON tables in
`base/element/string.json`, `plural.json` and `color.json`. The runtime now reads those
generated tables instead of original XML. JVM and OHOS continue to share the generators;
their prefixed names and packaged format are unchanged from `0.0.1-neupass.10`.
No additional alias map or inferred prefix is needed. Each dependency JAR's tables are
enumerated through the classloader. A table that appears more than once on the classpath
with identical entries is accepted; the same complete name with different values fails
explicitly instead of depending on classpath order. Different prefixes keep identically
named source resources separate across modules.

The public resource constructors and generated MR getter signatures remain unchanged.
MR bytecode and resource tables packaged by `0.0.1-neupass.10` work with the new runtime
without regenerating or recompiling that consumer. Source-only XML classpaths and manually
invented unprefixed names are not substitutes for the generated resource contract.

Generated color values are ARGB, matching the Android/OHOS output. The shared generator
interprets eight-digit source colors as RGBA and converts them; the former JVM XML reader
incorrectly skipped that conversion. Applications must use the library's source color
format. Files, images and fonts keep their existing loading paths.

Platform source audit: Android still uses generated R IDs, Apple still uses exact bundle
keys, and OHOS still passes the same generated full names to its native resource manager.
The shared OHOS generators and retained OHOS runtime are unchanged; the JVM adapter now
consumes their existing output correctly. No native capability was removed or stubbed.

## Known limitations

These predate the switch to generated tables; reading XML had the same gaps.

- **Light/dark theme colors.** The generator writes themed colors into the `light` and
  `dark` resource directories, but the JVM runtime only reads `base/element/color.json`,
  so a themed color fails with "Color resource not found" on Desktop.
- **Base resources only.** Locale and other qualifier directories are not selected on
  JVM. Plurals support `one` and `other` only, falling back to `other` when `one` is
  absent; `zero`, `two`, `few` and `many` are ignored.
- **Single uber JAR.** Every module packages its tables at the same
  `base/element/*.json` paths. Merging modules into one JAR keeps only one copy of each
  table, so resources from the other modules are lost. Keep modules as separate JARs on
  the classpath.
- **Test coverage.** The JVM tests use hand-written tables and do not exercise the
  generator-to-runtime path end to end. `scripts/VerifyJvmConsumer.java` covers that
  path against a real consumer build.

## Validation

Validation commands (Linux; iOS and OHOS native toolchains unavailable):

```sh
./gradlew :resource-generator:build :resource-core:jvmTest \
  :resource-core:compileDebugKotlinAndroid :resource-compose:compileKotlinJvm \
  :resource-compose:compileDebugKotlinAndroid
```

The JVM tests cover complete-name lookup, missing names, escaping, repeated/reordered
formatting, plural fallback, generated ARGB values, multiple dependency JARs in either
classpath order, the same table appearing twice, conflicting identities and malformed
tables.

### Previously compiled consumer check

`scripts/VerifyJvmConsumer.java` loads an unchanged consumer JAR with a candidate runtime.
It exercises every generated string/plural/color getter, plus explicit expected strings
provided by the caller. Run it with the candidate core JAR, Kotlin stdlib and
kotlinx-serialization JSON/core JARs on `--class-path`:

```sh
java --class-path "$candidate_runtime_classpath" scripts/VerifyJvmConsumer.java \
  "$consumer_jar" example.resources.MR 'sample_settings=Settings'
```

These checks do not claim native iOS/OHOS execution or arbitrary third-party resource
formats.
