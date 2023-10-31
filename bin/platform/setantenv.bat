@echo off

setlocal
If "%ANT_MEM_OPTS%"=="" set ANT_MEM_OPTS=-Xmx2G
endlocal & (
set ANT_OPTS=%ANT_MEM_OPTS% -Dfile.encoding=UTF-8 -Dpolyglot.js.nashorn-compat=true -Dpolyglot.engine.WarnInterpreterOnly=false --add-exports java.xml/com.sun.org.apache.xpath.internal=ALL-UNNAMED --add-exports java.xml/com.sun.org.apache.xpath.internal.objects=ALL-UNNAMED
)
set ANT_HOME=%~dp0apache-ant
set PATH=%ANT_HOME%\bin;%PATH%
rem deleting CLASSPATH as a workaround for PLA-8702
set CLASSPATH=

echo ant home: %ANT_HOME%
echo ant opts: %ANT_OPTS%
ant -version
