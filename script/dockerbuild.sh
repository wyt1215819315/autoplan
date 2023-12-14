#!/bin/bash

basedir=$(cd `dirname $0`; pwd)
cd $basedir
cd ../
docker build -t autoplan .