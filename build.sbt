import sbt.Keys.libraryDependencies

lazy val root = (project in file("."))
  .disablePlugins(JUnitXmlReportPlugin) // Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    name := "trader-goods-profiles-api-tests",
    version := "0.1.0",
    scalaVersion := "3.3.4",
    libraryDependencies ++= Seq(
      "org.playframework" %% "play-json" % "3.0.4" // Add Play JSON dependency here
    ) ++ Dependencies.test,
    (Compile / compile) := ((Compile / compile) dependsOn (Compile / scalafmtSbtCheck, Compile / scalafmtCheckAll)).value
  )
