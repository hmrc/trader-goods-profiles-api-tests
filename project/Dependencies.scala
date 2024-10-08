import sbt._

object Dependencies {

  val test: Seq[ModuleID] = Seq(
    "org.scalatestplus.play" %% "scalatestplus-play"      % "7.0.1"  % Test,
    "org.scalatest"          %% "scalatest"               % "3.2.18" % Test,
    "com.vladsch.flexmark"    % "flexmark-all"            % "0.64.8" % Test,
    "com.typesafe"            % "config"                  % "1.4.3"  % Test,
    "org.playframework"      %% "play-ahc-ws-standalone"  % "3.0.2"  % Test,
    "org.slf4j"               % "slf4j-simple"            % "2.0.13" % Test,
    "org.playframework"      %% "play-ws-standalone-json" % "3.0.3"  % Test,
    "io.circe"               %% "circe-core"              % "0.14.7" % Test,
    "io.circe"               %% "circe-parser"            % "0.14.7" % Test,
    // Other dependencies
    "org.playframework"      %% "play-ws"                 % "3.0.2"  % Test,
    "io.rest-assured"         % "rest-assured"            % "5.4.0"  % Test,
    "commons-codec"           % "commons-codec"           % "1.17.0" % Test
  )
}
