sbt 2.0 changes (draft)
=======================

```admonish warning
This is a draft documentation of sbt 2.x that is yet to be released.
While the general concept translates to sbt 1.x,
details of both 2.x and this doc are subject to change.
```

Changes with compatibility implications
---------------------------------------

See also [Migrating from sbt 1.x](./migrating-from-sbt-1.x.md).

- sbt 2.x uses Scala 3.x for build definitions and plugins (Both sbt 1.x and 2.x are capable of building Scala 2.x and 3.x) by [@eed3si9n][@eed3si9n], [@adpi2][@adpi2], and others.
- Bare settings are added to all subprojects, as opposed to just the root subproject, and thus replacing the role that `ThisBuild` has played. by [@eed3si9n][@eed3si9n] in [#6746][6746]
- `test` task is changed to be incremental test that can cache test results. Use `testFull` for full test by [@eed3si9n][@eed3si9n] in [#7686][7686] 
- sbt 2.x plugins are published with `_sbt2_3` suffix by [@eed3si9n][@eed3si9n] in [#7671][7671]
- sbt 2.x adds `platform` setting so `ModuleID`'s `%%` operator can cross build on JVM as well as JS and Native, as opposed to `%%%` operator that was created in a plugin to workaround this issue, by [@eed3si9n][@eed3si9n] in [#6746][6746]
- Dropped `useCoursier` setting so Coursier cannot be opted out, by [@eed3si9n][@eed3si9n] in [#7712][7712]

### Dropped dreprecations

- sbt 0.13 style shell syntax by [@eed3si9n][@eed3si9n] in [#7700][7700]

Features
--------

- Project matrix, which was available via plugin in sbt 1.x, is in-sourced.
- Local/remote hybrid cache system. sbt 2.x implements cached task, which can automatically cache the task results to local disk and Bazel-compatible remote cache. by [@eed3si9n][@eed3si9n] in [#7464][7464] / [#7525][7525]

Previously on sbt
-----------------

See also:

- [sbt 1.0 changes](https://www.scala-sbt.org/1.x/docs/sbt-1.0-Release-Notes.html)

  [6746]: https://github.com/sbt/sbt/pull/6746
  [7464]: https://github.com/sbt/sbt/pull/7464
  [7525]: https://github.com/sbt/sbt/pull/7525
  [7671]: https://github.com/sbt/sbt/pull/7671
  [7686]: https://github.com/sbt/sbt/pull/7686
  [7700]: https://github.com/sbt/sbt/pull/7700
  [7712]: https://github.com/sbt/sbt/pull/7712
  [@eed3si9n]: https://github.com/eed3si9n
  [@adpi2]: https://github.com/adpi2
