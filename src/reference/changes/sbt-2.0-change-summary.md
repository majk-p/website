sbt 2.0 changes (draft)
=======================

```admonish warning
This is a draft documentation of sbt 2.x that is yet to be released.
While the general concept translates to sbt 1.x,
details of both 2.x and this doc are subject to change.
```

- sbt 2.x uses Scala 3.x for build definitions and plugins (Both sbt 1.x and 2.x are capable of building Scala 2.x and 3.x) by [@eed3si9n][@eed3si9n], [@adpi2][@adpi2], and others.
- Bare settings are added to all subprojects, as opposed to just the root subproject, and thus replacing the role that `ThisBuild` has played. [#6746][6746] by [@eed3si9n][@eed3si9n]
- sbt 2.x adds `platform` setting so `ModuleID`'s `%%` operator can cross build on JVM as well as JS and Native, as opposed to `%%%` operator that was created in a plugin to workaround this issue. [#6746][6746] by [@eed3si9n][@eed3si9n]
- Local/remote hybrid cache system. sbt 2.x implements cached task, which can automatically cache the task results to local disk and Bazel-compatible remote cache. [#7464][7464] / [#7525][7525] by [@eed3si9n][@eed3si9n]

Previously on sbt
-----------------

See also:

- [sbt 1.0 changes](https://www.scala-sbt.org/1.x/docs/sbt-1.0-Release-Notes.html)

  [6746]: https://github.com/sbt/sbt/pull/6746
  [7464]: https://github.com/sbt/sbt/pull/7464
  [7525]: https://github.com/sbt/sbt/pull/7525
  [@eed3si9n]: https://github.com/eed3si9n
  [@adpi2]: https://github.com/adpi2
