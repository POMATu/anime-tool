Dim WshShell
Set WshShell = WScript.CreateObject("WScript.Shell")
WshShell.CurrentDirectory = ".\bin"
WshShell.Run "cmd /c java -jar AnimeTool.jar > ..\AnimeTool.log", 0, false