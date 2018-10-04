#!/bin/bash

grails clean-all && grails package-plugin && grails publish-plugin
