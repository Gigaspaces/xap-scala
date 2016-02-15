export JSHOMEDIR=`dirname $0`/../..
. `dirname $0`/../../bin/setenv.sh

REPL_CLASSPATH="-cp $GS_JARS:$SPRING_JARS:$SIGAR_JARS:"${XAP_HOME}"/lib/platform/scala/lib/*:"${XAP_HOME}"/lib/platform/scala/*"
REPL_JAVA_OPTS="$JAVA_OPTIONS $XAP_OPTIONS"
REPL_MAIN_CLASS="org.openspaces.scala.repl.GigaSpacesScalaRepl"
REPL_COMPILER_OPTS="-usejavacp -Yrepl-sync"
REPL_COMMAND="$JAVACMD $REPL_JAVA_OPTS $REPL_CLASSPATH $REPL_MAIN_CLASS $REPL_COMPILER_OPTS"

$REPL_COMMAND
