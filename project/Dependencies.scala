import sbt.*

object Dependencies {

  val test: Seq[ModuleID] = Seq(
    "com.typesafe"         % "config"                  % "1.4.3"  % Test,
    "com.typesafe.play"   %% "play-ahc-ws-standalone"  % "2.2.5"  % Test,
    "com.typesafe.play"   %% "play-ws-standalone-json" % "2.2.5"  % Test,
    "com.vladsch.flexmark" % "flexmark-all"            % "0.64.8" % Test,
    "org.scalatest"       %% "scalatest"               % "3.2.18" % Test,
    "org.slf4j"            % "slf4j-simple"            % "2.0.9"  % Test,
    "com.typesafe.play"   %% "play-ws-standalone-json" % "2.1.2"  % Test,
    "io.circe"            %% "circe-core"              % "0.14.1" % Test,
    "io.circe"            %% "circe-parser"            % "0.14.1" % Test,
    // Other dependencies
    "com.typesafe.play"   %% "play-ws"                 % "2.8.8"  % Test,
    "io.rest-assured"      % "rest-assured"            % "4.4.0"  % Test,
    "commons-codec"        % "commons-codec"           % "1.15"   % Test
  )

}
