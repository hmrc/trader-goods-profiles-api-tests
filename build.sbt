import sbt.Keys.libraryDependencies

lazy val root = (project in file("."))
  .disablePlugins(JUnitXmlReportPlugin) // Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    name := "trader-goods-profiles-api-tests",
    version := "0.1.0",
    scalaVersion := "2.13.12",
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-json" % "2.9.3" // Add Play JSON dependency here
    ) ++ Dependencies.test,
    (Compile / compile) := ((Compile / compile) dependsOn (Compile / scalafmtSbtCheck, Compile / scalafmtCheckAll)).value
  )
