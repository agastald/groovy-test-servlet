#!/bin/bash

FILE=GroovyTestServletGrailsPlugin.groovy
VERSION=2.2.0
sed -e "s:    def version = \(.*\)$:    def version = '$VERSION':" $FILE > $FILE.tmp
diff -q $FILE $FILE.tmp || mv $FILE.tmp $FILE
. ~/.bashrc
sdk u grails 2.5.6
grails clean && grails package-plugin && grails publish-plugin
