Cross building setup
====================

This page covers cross building setup. See [Cross building](../concepts/cross-building.html) for general explanation.

Using cross-built libraries 
---------------------------

To use a library built against multiple versions of Scala, double the first `%` in a ModuleID to be `%%`. This tells sbt that it should append the current version of Scala being used to build the library to the dependency’s name. For example:

```scala
libraryDependencies += "org.typelevel" %% "cats-effect" % "3.5.4"
```

A nearly equivalent, manual alternative for a fixed version of Scala is:

```scala
libraryDependencies += "org.typelevel" % "cats-effect_3" % "3.5.4"
```

### Scala 3 specific cross-versions 

If you are developing an application in Scala 3, you can use Scala 2.13 libraries:

```scala
("a" % "b" % "1.0").cross(CrossVersion.for3Use2_13)
```

This is equivalent to using `%%` except it resolves the `_2.13` variant of the library  when `scalaVersion` is 3.x.y.

Conversely we have `CrossVersion.for2_13Use3` to use the `_3` variant of the library when `scalaVersion` is 2.13.x:

```scala
("a" % "b" % "1.0").cross(CrossVersion.for2_13Use3)
```

```admonish warning
**Warning for library authors:** It is generally not safe to publish a Scala 3 library that depends on a Scala 2.13 library or vice-versa. Doing so could introduce two versions of the same library like `scala-xml_2.13` and `scala-xml_3` on the end users' classpath.
```

### More about using cross-built libraries

You can have fine-grained control over the behavior for different Scala versions by using the `cross` method on `ModuleID` These are equivalent:

```scala
"a" % "b" % "1.0"
("a" % "b" % "1.0").cross(CrossVersion.disabled)
```

These are equivalent:

```scala
"a" %% "b" % "1.0"
("a" % "b" % "1.0").cross(CrossVersion.binary)
```

This overrides the defaults to always use the full Scala version instead of the binary Scala version:

```scala
("a" % "b" % "1.0").cross(CrossVersion.full)
```

`CrossVersion.patch` sits between `CrossVersion.binary` and `CrossVersion.full` in that it strips off any trailing `-bin-...` suffix which is used to distinguish variant but binary compatible Scala toolchain builds.

```scala
("a" % "b" % "1.0").cross(CrossVersion.patch)
```

`CrossVersion.constant` fixes a constant value:

```scala
("a" % "b" % "1.0").cross(CrossVersion.constant("2.9.1"))
```

It is equivalent to:

```scala
"a" % "b_2.9.1" % "1.0"
```

Project matrix
--------------

sbt 2.x introduces project matrix, which enables cross building to happen in parallel.

```scala
organization := "com.example"
scalaVersion := "{{scala3_example_version}}"
version      := "0.1.0-SNAPSHOT"

lazy val core = (projectMatrix in file("core"))
  .settings(
    name := "core"
  )
  .jvmPlatform(scalaVersions = Seq("{{scala3_example_version}}", "{{scala2_13_example_version}}"))
```

Publishing convention
---------------------

We use the Scala ABI (application binary interface) version as suffix to denote which version of Scala was used to compile a library. For example, the artifact name `cats-effect_2.13` means Scala 2.13.x was used. `cats-effect_3` means Scala 3.x was used. This fairly simple approach allows interoperability with users of Maven, Ant and other build tools. For pre-prelease versions of Scala, such as 2.13.0-RC1, full version will be considered the ABI version.

`crossVersion` setting can be used to override the publishing convention:

- `CrossVersion.disabled` (no suffix)
- `CrossVersion.binary` (`_<scala-abi-version>`)
- `CrossVersion.full` (`_<scala-version>`)

The default is either `CrossVersion.binary` or `CrossVersion.disabled` depending on the value of `crossPaths`. Because (unlike Scala library) Scala compiler is not forward compatible among the patch releases, compiler plugins should use `CrossVersion.full`.
