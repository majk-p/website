Migrating from sbt 1.x
======================

Changing `build.sbt` DSL to Scala 3.x
-------------------------------------

As a reminder, users can build either Scala 2.x or Scala 3.x programs using either sbt 1.x or sbt 2.x. However, the Scala that underlies the `build.sbt` DSL is determined by the sbt version. In sbt 2.0, we are migrating to Scala 3.x.

This means that if you implement custom tasks or sbt plugins for sbt 2.x, it must be done using Scala 3.x. See [Scala 3.x incompatibility table][scala-incompatibility-table] and [Scala 2 with -Xsource:3][tooling-scala2-xsource3].

```scala
// This works on Scala 2.12.20 under -Xsource:3
import sbt.{ given, * }
```

Bare settings changes
---------------------

```scala
version := "0.1.0"
scalaVersion := "{{scala3_example_version}}"
```

_Bare settings_, like the example above, are settings written directly in `build.sbt` without `settings(...)`.

```admonish warning
In sbt 1.x bare settings were project settings that applied only to the root subproject. In sbt 2.x, the bare settings in `build.sbt` are common settings that are injected to **all subprojects**.
```

```scala
name := "root" // every subprojects will be named root!
publish / skip := true
```

### Migrating ThisBuild

In sbt 2.x, bare settings settings should no longer be scoped to `ThisBuild`. One benefit of the new _common settings_ over `ThisBuild` is that it would act in a more predictable delegation. These settings are inserted between plugins settings and those defined in `settings(...)`, meaning they can be used to define settings like `Compile / scalacOptions`, which was not possible with `ThisBuild`.

Cross building sbt plugins
--------------------------

In sbt 2.x, if you cross build an sbt plugin with Scala 3.x and 2.12.x, it will automatically cross build against sbt 1.x and sbt 2.x:

```scala
// using sbt 2.x
lazy val plugin = (projectMatrix in file("plugin"))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-vimquit",
  )
  .jvmPlatform(scalaVersions = Seq("3.3.3", "2.12.20"))
```

If you use `projectMatrix`, make sure to move the plugin to a subdirectory like `plugin/`. Otherwise, the synthetic root project will also pick up the `src/`.

### Cross building sbt plugin with sbt 1.x

Use sbt 1.10.2 or later, if you want to cross build using sbt 1.x.

```scala
// using sbt 1.x
lazy val scala212 = "2.12.20"
lazy val scala3 = "3.3.4"
ThisBuild / crossScalaVersions := Seq(scala212, scala3)

lazy val plugin = (project in file("plugin"))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-vimquit",
    (pluginCrossBuild / sbtVersion) := {
      scalaBinaryVersion.value match {
        case "2.12" => "1.5.8"
        case _      => "{{sbt_version}}"
      }
    },
  )
```

Migrating to slash syntax
-------------------------

sbt 1.x supported both the sbt 0.13 style syntax and the slash syntax. sbt 2.x removes the support for the sbt 0.13 syntax, so use the slash syntax for both sbt shell and in `build.sbt`:

```scala
<project-id> / Config / intask / key
```

For example, `test:compile` will no longer work on the shell. Use `Test/compile` instead. See [syntactic Scalafix rule for unified slash syntax][syntactic-scalafix-rule-for-unified-slash-syntax] for semi-automated migration of `build.sbt` files.

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
  [syntactic-scalafix-rule-for-unified-slash-syntax]: https://eed3si9n.com/syntactic-scalafix-rule-for-unified-slash-syntax/
  [tooling-scala2-xsource3]: https://docs.scala-lang.org/scala3/guides/migration/tooling-scala2-xsource3.html
