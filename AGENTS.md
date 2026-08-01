# KmmResource Maintenance Rules

## Purpose

This repository is the resource foundation for NEUPass on four platform families:

- JVM Desktop;
- Android;
- iOS;
- OpenHarmony (`ohosArm64`).

It is derived from Tencent KuiklyBase `KmmResource`. Preserve upstream history, authorship, copyright notices, and licenses.

## Platform parity (MUST)

- Every public resource type and generator capability **MUST** remain behaviorally aligned across JVM, Android, iOS, and OHOS.
- The OHOS build target may remain commented out while its custom Kotlin/KNOI toolchain is unavailable, but its generator, runtime, Compose bridge, native bridge, and packaging code **MUST** be updated with every relevant change.
- Disabled or untested OHOS code **MUST NOT** be deleted or replaced with empty stubs.
- Adding a resource category, qualifier, localization rule, lookup behavior, or Compose accessor **MUST** include JVM Desktop and OHOS implementations in the same change.

## Desktop support

- JVM Desktop is a first-class target, not a fallback.
- The Gradle generator **MUST** generate JVM `actual MR` declarations and package generated resources into the JVM classpath.
- Desktop runtime support **MUST** cover strings, formatted strings, plurals, colors, images, fonts, files, and assets.
- Compose accessors **MUST** provide equivalent string, color, image/painter, and font behavior where the platform supports it.

## NEUPass compatibility

- Android resource names and visual assets are authoritative during NEUPass migration. Do not silently rename, recolor, replace, or omit resources.
- Generator output must remain type-safe and usable directly from common Compose UI.
- Platform-specific source sets should contain only platform loading/bridging; resource declarations and UI-facing APIs belong in common code.
- Avoid introducing application-specific resource names or NEUPass business logic into this library.

## Toolchains and validation

- Enabled targets use standard Kotlin 2.1.20 unless a deliberate migration is documented.
- Compile the generator, resource-core JVM/Android, and resource-compose JVM/Android before pushing.
- Validate iOS on macOS when available.
- OHOS changes require a manual source audit while its target is disabled; explicitly document untested native changes.
- Publish annotated tags such as `0.0.1-neupass.N`; NEUPass pins tags and must not depend on an unversioned branch.

## Upstream maintenance

- Keep commits relevant to the original KmmResource subtree when syncing upstream.
- Review all generator backends and runtime actuals during upstream merges.
- Never regress Desktop or retained OHOS support while adopting upstream Android/iOS changes.
