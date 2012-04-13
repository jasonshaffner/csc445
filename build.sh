#!/bin/bash

CLASSPATH="."
BUILDDIR="../classes/"

cd src/
echo "Building..."
javac -cp $CLASSPATH -d $BUILDDIR $1 server/*.java 
javac -cp $CLASSPATH -d $BUILDDIR $1 database/*.java 
javac -cp $CLASSPATH -d $BUILDDIR $1 client/*.java 
javac -cp $CLASSPATH -d $BUILDDIR $1 GUI/*.java 
