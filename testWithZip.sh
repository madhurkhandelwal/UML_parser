#!/bin/bash

rm test?.zip
rm sequence.zip
rm UML_parser-*-with-dependencies.jar
rm *.png

wget -O sequence.zip "https://www.dropbox.com/s/1myplvt3hefn0tr/sequence.zip?dl=1"
wget -O test1.zip "https://www.dropbox.com/s/b5t1lbivqd070wz/test1.zip?dl=1"
wget -O test2.zip "https://www.dropbox.com/s/g8w9swdp854enpf/test2.zip?dl=1"
wget -O test3.zip "https://www.dropbox.com/s/e5j5v511x56hzw9/test3.zip?dl=1"
wget -O test4.zip "https://www.dropbox.com/s/29pmd24wgwjls79/test4.zip?dl=1"
wget -O test5.zip "https://www.dropbox.com/s/h3adyk76317j7il/test5.zip?dl=1"

./setup.sh

./umlparser sequence.zip sequence.png
echo "--------------------------------------------------------------------------------"
echo "--------------------------------------------------------------------------------"
./umlparser test1.zip test1.png
echo "--------------------------------------------------------------------------------"
echo "--------------------------------------------------------------------------------"
./umlparser test2.zip test2.png
echo "--------------------------------------------------------------------------------"
echo "--------------------------------------------------------------------------------"
./umlparser test3.zip test3.png
echo "--------------------------------------------------------------------------------"
echo "--------------------------------------------------------------------------------"
./umlparser test4.zip test4.png
echo "--------------------------------------------------------------------------------"
echo "--------------------------------------------------------------------------------"
./umlparser test5.zip test5.png

rm UML_parser-*-with-dependencies.jar

rm test?.zip
rm -r test?/

rm sequence.zip
rm -r sequence/

if [ `uname` == "Darwin" ]
then
	open *.png
fi