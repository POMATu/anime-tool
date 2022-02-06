chcp 65001
cd Output
rmdir /S /Q AnimeTool
del /S /Q {app}
del /F /Q *.iss
del /F /Q *.zip
..\innounp -b -x AnimeToolInstaller-win64.exe
ren {app} AnimeTool
"C:\Program Files\7-Zip\7z.exe" a -mx9 AnimeToolPortable-win64.zip AnimeTool