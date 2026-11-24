@echo off
cd /d "%~dp0"
if exist out rmdir /s /q out
mkdir out
dir /s /b src\main\java\*.java > out\fontes.txt
javac -encoding UTF-8 -d out @out\fontes.txt || exit /b 1
java -cp out br.ucsal.monitorauc.App %*
