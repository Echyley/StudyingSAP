if ($null -eq $env:ANT_MEM_OPTS) {
    $env:ANT_MEM_OPTS="-Xmx2g"
}

$env:ANT_OPTS="$env:ANT_MEM_OPTS -Dfile.encoding=UTF-8 -Dpolyglot.js.nashorn-compat=true -Dpolyglot.engine.WarnInterpreterOnly=false --add-exports java.xml/com.sun.org.apache.xpath.internal=ALL-UNNAMED --add-exports java.xml/com.sun.org.apache.xpath.internal.objects=ALL-UNNAMED"
$env:ANT_HOME="$pwd\apache-ant"
$env:Path="$env:ANT_HOME\bin;$env:Path"
echo "Setting ant home to: $env:ANT_HOME"
echo "Setting ant opts to: $env:ANT_OPTS"
ant -version
