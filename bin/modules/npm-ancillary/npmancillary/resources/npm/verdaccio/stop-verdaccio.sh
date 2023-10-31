#!/bin/sh

VERDACCIO_BASE_PATH="$(pwd)"
VERDACCIO_PID_FILE="$VERDACCIO_BASE_PATH/verdaccio.pid"

if [ -f "$VERDACCIO_PID_FILE" ]; then
    echo "verdaccio.pid found. Stopping verdaccio..."
    kill -9 `cat $VERDACCIO_BASE_PATH/verdaccio.pid` && rm -f "$VERDACCIO_PID_FILE"
else
    echo "No verdaccio.pid found, no need to stop verdaccio."
fi

# Reset npm registry
npm set registry https://registry.npmjs.org/
