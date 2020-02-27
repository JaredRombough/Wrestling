call mvn clean package
md ".\dist\Open Wrestling"
xcopy ".\target\openwrestling-1.0-SNAPSHOT.jar" ".\dist\Open Wrestling" /y
rename  ".\dist\Open Wrestling\openwrestling-1.0-SNAPSHOT.jar" "Open Wrestling.jar"
xcopy ".\src\Open Wrestling.bat" ".\dist\Open Wrestling" /y
7z a -tzip .\dist\OpenWrestling.zip ".\dist\Open Wrestling"