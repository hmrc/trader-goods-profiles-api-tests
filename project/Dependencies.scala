import sbt.*

object Dependencies {

  val test: Seq[ModuleID] = Seq(
    "org.scalatestplus.play" %% "scalatestplus-play"      % "7.0.1"    % Test,
    "org.scalatest"          %% "scalatest"               % "3.2.19"   % Test,
    "com.vladsch.flexmark"    % "flexmark-all"            % "0.64.8"   % Test,
    "com.typesafe"            % "config"                  % "1.4.3"    % Test,
    "org.playframework"      %% "play-ahc-ws-standalone"  % "3.0.6"    % Test,
    "org.slf4j"               % "slf4j-simple"            % "2.0.16"   % Test,
    "org.playframework"      %% "play-ws-standalone-json" % "3.1.0-M4" % Test,
    "io.circe"               %% "circe-core"              % "0.14.10"  % Test,
    "io.circe"               %% "circe-parser"            % "0.14.10"  % Test,
    // Other dependencies
    "org.playframework"      %% "play-ws"                 % "3.0.6"    % Test,
    "io.rest-assured"         % "rest-assured"            % "5.5.0"    % Test,
    "commons-codec"           % "commons-codec"           % "1.18.0"   % Test
  )
}
