#!/usr/bin/env sh
# FIXED TO 8.4 - SAME AS GREEN #17 - NEVER USE SYSTEM GRADLE 9.7.1
set -e
echo "Using Gradle Wrapper 8.4 - NOT 9.7.1"
if [ ! -f gradle/wrapper/gradle-wrapper.jar ]; then
  echo "Downloading gradle-wrapper.jar 8.4..."
  mkdir -p gradle/wrapper
  curl -L -o gradle/wrapper/gradle-wrapper.jar https://github.com/gradle/gradle/raw/v8.4.0/gradle/wrapper/gradle-wrapper.jar || wget -O gradle/wrapper/gradle-wrapper.jar https://github.com/gradle/gradle/raw/v8.4.0/gradle/wrapper/gradle-wrapper.jar || true
fi
ls -lh gradle/wrapper/gradle-wrapper.jar || true
exec java -jar gradle/wrapper/gradle-wrapper.jar "$@"
