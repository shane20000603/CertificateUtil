#!/bin/bash

str=`find ./ -name '*.crt'`
file=" $str "
for i in $file
do
mv $i ${i%crt}cer
done