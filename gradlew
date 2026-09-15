#!/usr/bin/env sh
# FIXED - uses classpath method like original Android gradlew - works even without manifest
set -e
APP_HOME=`cd "$(dirname "$0")" && pwd -P`
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
exec java -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
