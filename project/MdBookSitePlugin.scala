import sbt.*
import Keys.*
import com.typesafe.sbt.site.SitePlugin.autoImport.siteSubdirName
import com.typesafe.sbt.site.SitePlugin
import com.typesafe.sbt.site.util.SiteHelpers
import scala.annotation.nowarn

object MdBookSitePlugin extends AutoPlugin {
  override def requires = SitePlugin
  override def trigger = noTrigger
  override def projectSettings = mdbookSettings(Compile)

  object autoImport {
    val MdBook = config("mdbook")
    val mdbookBuild = taskKey[File]("")
    val mdbookDirectory = settingKey[File]("Directory where docs are located")
  }
  import autoImport.*

  @nowarn
  def mdbookSettings(config: Configuration): Seq[Setting[_]] =
    inConfig(if (config == Compile) MdBook else config)(
      List(
        siteSubdirName := "",
        mdbookDirectory := baseDirectory.value,
        config / mdbookBuild := {
          import scala.sys.process.*
          val dir = mdbookDirectory.value
          IO.createDirectory(dir / "book")
          Process(List("mdbook", "build", "-d", "book/en"), cwd = dir).!
          Process(List("script/build.sh", "ja"), cwd = dir).!
          Process(List("script/build.sh", "zh-cn"), cwd = dir).!
          val out = dir / "book"
          out
        },
      )
    ) ++
      SiteHelpers.watchSettings(ThisScope.in(config, mdbookBuild.key)) ++
      SiteHelpers.addMappingsToSiteDir(
        (config / mdbookBuild)
          .map(SiteHelpers.selectSubpaths(_, AllPassFilter)),
        (if (config == Compile) MdBook else config) / siteSubdirName
      )
}
