Migrating from sbt 1.x
======================

Changing `build.sbt` DSL to Scala 3.x
-------------------------------------

As a reminder, users can build either Scala 2.x or Scala 3.x programs using either sbt 1.x or sbt 2.x. However, the Scala that underlies the `build.sbt` DSL is determined by the sbt version. In sbt 2.0, we are migrating to Scala 3.x.

This means that if you implement custom tasks or sbt plugins for sbt 2.x, it must be done using Scala 3.x. See [Scala 3.x incompatibility table][scala-incompatibility-table] for the list of potential migration points.

Bare settings changes
---------------------

```scala
version := "0.1.0"
scalaVersion := "{{scala3_example_version}}"
```

_Bare settings_, like the example above, are settings written directly in `build.sbt` without `settings(...)`. In sbt 1.x bare settings were project settings that applied only to the root subproject. In sbt 2.x, the bare settings in `build.sbt` are common settings that are injected to **all subprojects**.

### Migrating ThisBuild

In sbt 2.x, bare settings settings should no longer be scoped to `ThisBuild`. One benefit of the new _common settings_ over `ThisBuild` is that it would act in a more predictable delegation. These settings are inserted between plugins settings and those defined in `settings(...)`, meaning they can be used to define settings like `Compile / scalacOptions`, which was not possible with `ThisBuild`.

Changes to `%%`
---------------

In sbt 2.x, `ModuleID`'s `%%` operator has become platform-aware. For JVM subprojects, `%%` works as before, encoding Scala suffix (for example `_3`) on Maven repositories.

### Migrating `%%%` operator

When Scala.JS or Scala Native becomes available on sbt 2.x, `%%` will encode both the Scala version (such as `_3`) and the platform suffix (`_sjs1` etc). As a result, `%%%` can be replaced with `%%`:

```scala
libraryDependencies += "org.scala-js" %% "scalajs-dom" % "2.8.0"
```

Use `.platform(Platform.jvm)` in case where JVM libraries are needed.


  [scala-incompatibility-table]: https://docs.scala-lang.org/scala3/guides/migration/incompatibility-table.html