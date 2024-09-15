Caching
=======

sbt 2.0 introduces hybrid local/remote cache system, which can cache the task results to local disk and Bazel-compatible remote cache. Throughout sbt releases it has implemented various caches, like `update` cache, incremental compilation, but sbt 2.x's cache is a significant step change for a few reasons:

1. **Automatic**. sbt 2.x cache automates the caching by embedding itself into the task macro unlike sbt 1.x wherein the plugin author called the cache functions manually in the task implementation.
2. **Machine-wide**. sbt 2.x disk cache is shared among all builds on a machine.
3. **Remote-ready**. In sbt 2.x, the cache storage is configured separately such that all cacheable tasks are automatically remote-cache-ready.

Basics of caching
-----------------

The basic idea is treat as if the build process is a pure function that takes input `(A1, A2, A3, ...)` and return some outputs `(R1, List(O1, O2, O3, ...))`. For example, we can take a list of source files, Scala version, and produce a `*.jar` file at the end. If the assumption holds, then for the same inputs, we can memorize the ouput JAR for everyone. We are interested in this technique because using the memorized output JAR would be faster than performing the actual task like Scala compilation etc.

### Hermetic build

As a mental model of the _build as a pure function_, build engineers sometimes use the term _hermetic build_, which is a build that takes place in a shipping container in a dessert with no clocks or the Internet. If we can produce a JAR file from that state, then the JAR file should be safe to be shared by any machine. Why did I mention the clock? It's because a JAR file could capture the timestamp, and thus produce slightly different JARs each time. To avoid this, hermetic build tools overwrite the timestamp to a fixed date 2010-01-01 regardless of when the build took place.

A build that ends up capturing ephemeral inputs, are said to _break the hermeticity_ or _non-hermetic_. Another common way the hermeticity is broken is capturing absolute paths as either input or output. Sometimes the path gets embedded into the JAR via a macro, you might not know until you inspect the bytecode.

Automatic caching
-----------------

Here's a demonstration of the automatic caching:

```scala
import sbt.util.CacheImplicits.given

val someKey = taskKey[String]("something")

someKey := (Def.cachedTask {
  name.value + version.value + "!"
}).value
```

The task will be automatically cached based on the two settings `name` and `version`. The first time we run the task it will be executed onsite, but the second time onward, it will use the disk cache:

```
sbt:demo> show someKey
[info] demo0.1.0-SNAPSHOT!
[success] elapsed time: 0 s, cache 0%, 1 onsite task
sbt:demo> show someKey
[info] demo0.1.0-SNAPSHOT!
[success] elapsed time: 0 s, cache 100%, 1 disk cache hit
```

Remote caching
--------------

You can optionally extend the build to use remote cache in addition to the local disk cache. Remote caching could improve build performance by allowing multiple machines to share build artifacts and outputs.

Imagine you have a dozen people in your project or a company. Each morning, you will `git pull` the changes the dozen people made, and you need to build their code. If you have a successful project, the code size will only get bigger over time, and the % of the time you spend building someone else's in your day increases. This becomes the limiting factor of your team size and code size. Remote caching reverses this tide by CI systems hydrate the cache and you can download the artifacts and task outputs.

sbt 2.x implements Bazel-compatible gRPC interface, which works with number of backend both open source and commercial.
