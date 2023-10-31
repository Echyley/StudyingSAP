echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" >.classpath
echo "<classpath>" >>.classpath
echo "	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>" >>.classpath
echo "	<classpathentry kind=\"output\" path=\"eclipsebin/notused\"/>" >>.classpath
echo "" >>.classpath
echo "	<classpathentry exported=\"false\" kind=\"src\" path=\"/platform\" />" >>.classpath

for file in lib/*
do
echo "	<classpathentry exported=\"false\" kind=\"lib\" path=\"web/webroot/WEB-INF/$file\" />" >>.classpath
done

echo "	<classpathentry exported=\"true\" kind=\"lib\" path=\"resources\" />" >>.classpath
echo "	<classpathentry exported=\"true\" output=\"eclipsebin/classes\" kind=\"src\" path=\"src\" />" >>.classpath
echo "	<classpathentry exported=\"false\" output=\"eclipsebin/web/classes\" kind=\"src\" path=\"web/src\" />" >>.classpath
echo "	<classpathentry exported=\"true\" output=\"eclipsebin/classes\" kind=\"src\" path=\"testsrc\" />" >>.classpath
echo "	<classpathentry exported=\"true\" output=\"eclipsebin/classes\" kind=\"src\" path=\"gensrc\" />" >>.classpath
echo "	<classpathentry exported=\"true\" kind=\"src\" path=\"/acceleratorservices\" />" >>.classpath
echo "	<classpathentry exported=\"true\" kind=\"src\" path=\"/xyformsservices\" />" >>.classpath
echo "				" >>.classpath
echo "</classpath>" >>.classpath

mv .classpath ../../../
