#!/bin/sh
OWN_NAME=setantenv.sh
BASENAME="$(basename -- "$0")"

SOURCED=0
if [ -n "$ZSH_EVAL_CONTEXT" ]; then
    [[ $ZSH_EVAL_CONTEXT =~ :file$ ]] && SOURCED=1
elif [ -n "$BASH_VERSION" ]; then
    [[ $0 != $BASH_SOURCE ]] && SOURCED=1
elif [ "$OWN_NAME" != "$BASENAME" ]; then
    SOURCED=1
fi

if [ "$SOURCED" -ne 1 ]; then
    echo "* Please call as '. ./$OWN_NAME', not './$OWN_NAME' !!!---"
    echo "* Also please DO NOT set back the executable attribute"
    echo "* On this file. It was cleared on purpose."

    chmod -x ./$OWN_NAME
    exit
fi

export PLATFORM_HOME
PLATFORM_HOME=$(pwd)

if [ -z "${ANT_MEM_OPTS}" ]; then
  MEM_OPTS='-Xmx2G'
else
  MEM_OPTS="${ANT_MEM_OPTS}"
fi
export ANT_OPTS="$MEM_OPTS -Dfile.encoding=UTF-8 -Dpolyglot.js.nashorn-compat=true -Dpolyglot.engine.WarnInterpreterOnly=false \
--add-exports java.xml/com.sun.org.apache.xpath.internal=ALL-UNNAMED \
--add-exports java.xml/com.sun.org.apache.xpath.internal.objects=ALL-UNNAMED"
export ANT_HOME="$PLATFORM_HOME/apache-ant"
chmod +x "$ANT_HOME/bin/ant"
chmod +x "$PLATFORM_HOME/license.sh"
case "$PATH" in
    *$ANT_HOME/bin:*) ;;
    *) export PATH=$ANT_HOME/bin:$PATH ;;
esac

ant -version
echo "ant home: ${ANT_HOME}"
echo "ant opts: ${ANT_OPTS}"
