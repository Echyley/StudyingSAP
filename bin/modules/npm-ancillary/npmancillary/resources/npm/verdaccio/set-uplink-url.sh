#!/bin/sh

# Script Used to set Verdaccio uplink url and reset
# If 1st input para: url is set with non-empty value, then set verdaccio/config.xml uplink url to input value
# If 1st input para: url is set with empty value, then reset verdaccio/config.xml uplink url to default value: https://registry.npmjs.org/

URL=$1

DEF_URL="https://registry.npmjs.org/"

if [ "${URL}" = "" ] ; then
  echo "Reset uplink url to config.yaml"
  sed -i.bak "s;url: .*;url: ${DEF_URL};g" config.yaml
else
  echo "Set value ${URL} to config.yaml"
  sed -i.bak "s;url: .*;url: ${URL};g" config.yaml
fi
