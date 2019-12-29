#!/bin/bash
mkdir out
javac -cp ./src -d ./out ./src/com/secant/c0compiler/console/Main.java
jar cfm cc0.jar ./meta-inf/manifest.mf -C ./out .
rm -rf ./out
chmod +x ./c0-vm-cpp
echo '#!/bin/bash'>./cc0
echo 'if [ "$1" = "-s" ]; then'>>./cc0
echo '    if [ "$4" != "" ]; then'>>./cc0
echo '        java -jar cc0.jar -s $2>$4'>>./cc0
echo '        cat $4'>>./cc0
echo '    else'>>./cc0
echo '        java -jar cc0.jar -s $2'>>./cc0
echo '    fi'>>./cc0
echo 'elif [ "$1" = "-c" ]; then'>>./cc0
echo '    if [ "$4" != "" ]; then'>>./cc0
echo '        java -jar cc0.jar -s $2>_temp.s'>>./cc0
echo '        cat _temp.s'>>./cc0
echo '        ./c0-vm-cpp -a _temp.s $4'>>./cc0
echo '        rm -f _temp.s'>>./cc0
echo '    else'>>./cc0
echo '        java -jar cc0.jar $1 $2 $3 $4'>>./cc0
echo '    fi'>>./cc0
echo 'else'>>./cc0
echo '    java -jar cc0.jar $1 $2 $3 $4'>>./cc0
echo 'fi'>>./cc0
chmod +x ./cc0
