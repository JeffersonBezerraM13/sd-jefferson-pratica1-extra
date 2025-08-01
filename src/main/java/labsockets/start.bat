@echo off
title Chat TCP Multi-Usuario

:: Function to start a new terminal window with specified command
:startTerminal
start "%~1" cmd /k "%~2"
exit /b

:: Main script
echo Iniciando servidor e 2 clientes...

:: Start the server in first terminal
call :startTerminal "Servidor Chat" "java -cp bin labsockets.MultiUserChatServer & pause"

:: Wait a moment for server to start
timeout /t 3 >nul

:: Start first client
call :startTerminal "Cliente 1" "java -cp bin labsockets.MultiUserChatClient & pause"

:: Start second client
call :startTerminal "Cliente 2" "java -cp bin labsockets.MultiUserChatClient & pause"

echo Todos os terminais foram iniciados.
pause
