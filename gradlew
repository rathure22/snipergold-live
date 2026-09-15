#!/bin/sh
#
# Copyright © 2015-2021 the original authors.
#
##############################################################################
#
#   Gradle start up script for POSIX sh and bash
#
##############################################################################
#
APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`
APP_HOME=`cd "${0%/*}" >/dev/null; cd .. ; pwd -P`
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
exec java -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
