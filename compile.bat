@echo off
if not exist out mkdir out
javac -encoding UTF-8 -cp "lib/*" -d out -sourcepath src src\campusskillswap\Main.java
if %errorlevel% neq 0 (echo COMPILE FAILED) else (echo Compiled successfully. Now run run.bat)
pause
