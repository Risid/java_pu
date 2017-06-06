#!/bin/sh

PROGRAM="PU"
PROGDIR=`dirname $0` 

if [ -z "$JAVACMD" ] ; then 
  if [ -n "$JAVA_HOME"  ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then 
      JAVACMD="$JAVA_HOME/jre/sh/java"
    else
      JAVACMD="$JAVA_HOME/bin/java"
    fi
  else
    JAVACMD=`which java 2> /dev/null`
    if [ -z "$JAVACMD" ] ; then 
      JAVACMD=java
    fi
  fi
fi

$JAVACMD -Xms128m -Xmx1024m -cp "$PROGDIR/$PROGRAM.jar" $PROGRAM $@
