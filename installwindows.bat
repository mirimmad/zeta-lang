if not exist %SystemDrive%\zeta mkdir %SystemDrive%\zeta
xcopy /s/h/k/f/c/y "out\*.*" %SystemDrive%\zeta
xcopy /s/h/k/f/c/y zeta.bat %SystemDrive%\zeta\zeta.bat*
if exist %SystemDrive%\zeta setx path "%SystemDrive%\zeta;%PATH%"