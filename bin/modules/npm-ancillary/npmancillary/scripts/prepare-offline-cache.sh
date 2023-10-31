#!/bin/sh
# Prepare Offline NPM Ancillary Module
# Supports: Linux or Mac
# 1. Start verdaccio.
# 2. Build smartedittools: this will use verdaccio and generate .cache folder in npm-ancillary.
# 3. Stop verdaccio.
OS_NAME="$(uname -s)"

#Input parameters includes all the mandatory extension path
PROJECT_ID=$1
NPM_MODULE_HOME=$2
UPLINK_URL=$3
SMARTEDIT_TOOLS_HOME=$4
SMARTEDIT_EXTENSION_HOME=$5
CMSSMARTEDIT_EXTENSION_HOME=$6
P_SMARTEDIT_EXTENSION_HOME=$7
P_SEARCH_SMARTEDIT_EXTENSION_HOME=$8
M_SMARTEDIT_EXTENSION_HOME=$9

SMARTEDIT_SCRIPTS_HOME="${SMARTEDIT_TOOLS_HOME}/apps/smartedit-scripts/scripts"
NPM_RESOURCE_HOME="${NPM_MODULE_HOME}/npmancillary/resources/npm"


if [ "${OS_NAME}" = "Darwin" ] ; then
    NODE_HOME="${NPM_RESOURCE_HOME}/node/node-v16.20.0-darwin-x64/bin"
    sh "${NPM_RESOURCE_HOME}/repairnpm.sh" "darwin"
elif [ "${OS_NAME}" = "Linux" ] ; then
    NODE_HOME="${NPM_RESOURCE_HOME}/node/node-v16.20.0-linux-x64/bin"
    sh "${NPM_RESOURCE_HOME}/repairnpm.sh" "linux"
fi
echo "Updating PATH for node binary to $NODE_HOME"
export PATH="$NODE_HOME:$PATH"

echo "Starting verdaccio"
rm -rf ~/.pnpm-store && rm -rf ~/.rush/
npm cache clear --force
cd "${NPM_RESOURCE_HOME}/verdaccio"

echo "UPLINK_URL: $UPLINK_URL "
if [ "$UPLINK_URL" != "" ] ; then
  echo "set-uplink-url.sh $UPLINK_URL "
  sh set-uplink-url.sh "$UPLINK_URL"
fi
sh start-verdaccio.sh config.yaml true

cd "${NPM_RESOURCE_HOME}/verdaccio"
# link downstream smartedit extension
if [ "${PROJECT_ID}" = "smartedit" ] ; then
    node "${SMARTEDIT_SCRIPTS_HOME}/link-smartedit-project.js" "$SMARTEDIT_EXTENSION_HOME,$CMSSMARTEDIT_EXTENSION_HOME" "$SMARTEDIT_TOOLS_HOME" true
fi

if [ "${PROJECT_ID}" = "personalization" ] ; then
    node "${SMARTEDIT_SCRIPTS_HOME}/link-smartedit-project.js" "$SMARTEDIT_EXTENSION_HOME,$CMSSMARTEDIT_EXTENSION_HOME,$P_SMARTEDIT_EXTENSION_HOME" "$SMARTEDIT_TOOLS_HOME" true
fi

if [ "${PROJECT_ID}" = "personalization-cds-integration" ] ; then
    node "${SMARTEDIT_SCRIPTS_HOME}/link-smartedit-project.js" "$SMARTEDIT_EXTENSION_HOME,$CMSSMARTEDIT_EXTENSION_HOME,$P_SMARTEDIT_EXTENSION_HOME" "$SMARTEDIT_TOOLS_HOME" true
fi

if [ "${PROJECT_ID}" = "merchandising" ] ; then
    node "${SMARTEDIT_SCRIPTS_HOME}/link-smartedit-project.js" "$SMARTEDIT_EXTENSION_HOME,$CMSSMARTEDIT_EXTENSION_HOME,$M_SMARTEDIT_EXTENSION_HOME" "$SMARTEDIT_TOOLS_HOME" true
fi

if [ "${PROJECT_ID}" = "personalizationsearch" ] ; then
    echo "P_SEARCH_SMARTEDIT_EXTENSION_HOME:$P_SEARCH_SMARTEDIT_EXTENSION_HOME"
    node "${SMARTEDIT_SCRIPTS_HOME}/link-smartedit-project.js" "$SMARTEDIT_EXTENSION_HOME,$CMSSMARTEDIT_EXTENSION_HOME,$P_SMARTEDIT_EXTENSION_HOME,$P_SEARCH_SMARTEDIT_EXTENSION_HOME" "$SMARTEDIT_TOOLS_HOME" true
fi


if [ "${PROJECT_ID}" = "offline-linux-npm-ancillary" ] ; then
    echo "P_SEARCH_SMARTEDIT_EXTENSION_HOME:$P_SEARCH_SMARTEDIT_EXTENSION_HOME"
    node "${SMARTEDIT_SCRIPTS_HOME}/link-smartedit-project.js" "$SMARTEDIT_EXTENSION_HOME,$CMSSMARTEDIT_EXTENSION_HOME,$P_SMARTEDIT_EXTENSION_HOME,$P_SEARCH_SMARTEDIT_EXTENSION_HOME,$M_SMARTEDIT_EXTENSION_HOME" "$SMARTEDIT_TOOLS_HOME" true
fi

cd "$SMARTEDIT_TOOLS_HOME"
echo "rush update at: $SMARTEDIT_TOOLS_HOME"
node ./common/scripts/install-run-rush.js update --bypass-policy

cd "${NPM_RESOURCE_HOME}/verdaccio"

if [ "$UPLINK_URL" != "" ] ; then
  sh set-uplink-url.sh 
fi
sh stop-verdaccio.sh config.yaml true
