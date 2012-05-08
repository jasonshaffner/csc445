#!/bin/bash

CLASSPATH="."
BUILDDIR="../classes/"
mkdir classes/
cd src/
echo "Building..."
javac -cp $CLASSPATH -d $BUILDDIR $1 server/*.java 
javac -cp $CLASSPATH -d $BUILDDIR $1 database/*.java 
javac -cp $CLASSPATH -d $BUILDDIR $1 client/*.java 
javac -cp $CLASSPATH -d $BUILDDIR $1 GUI/*.java 

# Failover additions
#mkdir ../classes/server/failover
#mkdir ../classes/server/failover/packets
javac -cp $CLASSPATH"/server/" -d $BUILDDIR"server/" $1 server/failover/*.java
