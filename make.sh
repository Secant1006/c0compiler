#!/bin/bash
mkdir out
javac -cp ./src -d ./out ./src/com/secant/c0compiler/console/Main.java
jar cfm cc0.jar ./meta-inf/manifest.mf -C ./out .
rm -rf ./out
echo '#!/bin/bash'>./cc0
echo 'java -jar cc0.jar $1 $2 $3 $4'>>./cc0
chmod +x ./cc0
