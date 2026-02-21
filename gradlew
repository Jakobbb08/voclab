#!/usr/bin/env sh

# Gradle wrapper script for Unix.

# Set defaults
GRADLE_VERSION=7.0
BASE_DIR=$(cd "$(dirname "$0")/.."; pwd)

# The directory the wrapper jar is in
WRAPPER_JAR_DIR="${BASE_DIR}/gradle/wrapper"
WRAPPER_JAR_NAME="gradle-wrapper.jar"

# Validate that the expected jar exists
if [ ! -f "${WRAPPER_JAR_DIR}/${WRAPPER_JAR_NAME}" ]; then
  echo "Error: expected jar not found: ${WRAPPER_JAR_DIR}/${WRAPPER_JAR_NAME}"
  exit 1
fi

exec java -jar "${WRAPPER_JAR_DIR}/${WRAPPER_JAR_NAME}" "$@"