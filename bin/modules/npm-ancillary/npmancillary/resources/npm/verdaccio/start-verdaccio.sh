#!/bin/sh

# Script that run verdaccio in background process with a given configuration.
# Verdaccio is used to cache npm node_modules for offline usage.
# See https://github.com/verdaccio/verdaccio

CONFIG=$1
DEBUG=$2

if [ "${CONFIG}" = "" ] ; then
    echo "CONFIG must be set."
    echo "Example (no debug): ./verdaccio.sh path/to/config.yaml"
    echo "Example (debug enabled): ./verdaccio.sh path/to/config.yaml true"
    exit -1
fi

VERDACCIO_BASE_PATH="$(pwd)"
VERDACCIO_BIN_PATH="$VERDACCIO_BASE_PATH/verdaccio-lib/bin/verdaccio"
VERDACCIO_LOG="$VERDACCIO_BASE_PATH/verdaccio.log"
NPM_MODULE_HOME="${WORKSPACE}/hybris/bin/ext-content/npmancillary"

# Stop verdaccio if already running
if [ -f "$VERDACCIO_PID_FILE" ]; then
    sh "$VERDACCIO_BASE_PATH/stop-verdaccio.sh"
fi

# Remove old log file
if [ -f "$VERDACCIO_LOG" ]; then
    rm -f "$VERDACCIO_LOG"
fi

echo "Set npm registry to verdaccio default value: http://localhost:4873/"
npm set registry http://localhost:4873/

echo "Running verdaccio with config file: ${CONFIG} - DEBUG mode is set to: ${DEBUG}"

# Execute verdaccio in background process
if [ "${DEBUG}" = "true" ] ; then
    NODE_DEBUG=request nohup "$VERDACCIO_BIN_PATH" -c "$CONFIG" > "$VERDACCIO_LOG" 2>&1 &
else
    nohup "$VERDACCIO_BIN_PATH" -c "$CONFIG" > "$VERDACCIO_LOG" 2>&1 &
fi

# Save verdaccio pid
echo $! > "$VERDACCIO_BASE_PATH/verdaccio.pid"
