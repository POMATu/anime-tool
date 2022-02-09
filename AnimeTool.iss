;This file will be executed next to the application bundle image
;I.e. current directory will contain folder AnimeTool with application files
[Setup]
AppId={{app}}
AppName=AnimeTool
AppVersion=AnimeTool
AppVerName=AnimeTool 
AppPublisher=pomatu
AppComments=AnimeTool
AppCopyright=Copyleft (C) 2022
;AppPublisherURL=http://java.com/
;AppSupportURL=http://java.com/
;AppUpdatesURL=http://java.com/
DefaultDirName={userpf}\AnimeTool
DisableStartupPrompt=Yes
DisableDirPage=Yes
DisableProgramGroupPage=Yes
DisableReadyPage=Yes
DisableFinishedPage=No
DisableWelcomePage=No
DefaultGroupName=AnimeTool
;Optional License
LicenseFile=
;(Windows 2000/XP/Server 2003 are no longer supported.)
MinVersion=6.0
OutputBaseFilename=AnimeToolInstaller-win64
Compression=lzma
SolidCompression=yes
PrivilegesRequired=lowest
SetupIconFile=AnimeTool.ico
UninstallDisplayIcon={app}\AnimeTool.ico
UninstallDisplayName=AnimeTool
WizardImageStretch=Yes
WizardSmallImageFile=AnimeTool-setup-icon.bmp   
ArchitecturesInstallIn64BitMode=x64


[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[InstallDelete]
Type: filesandordirs; Name: "{app}"

[Files]
Source: "C:\openjre11\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "out\artifacts\AnimeTool_jar\AnimeTool.jar"; DestDir: "{app}\bin"; Flags: ignoreversion
Source: "AnimeTool.vbs"; DestDir: "{app}"; Flags: ignoreversion
Source: "AnimeTool.cmd"; DestDir: "{app}"; Flags: ignoreversion
Source: "AnimeTool-debug.cmd"; DestDir: "{app}"; Flags: ignoreversion
Source: "license.txt"; DestDir: "{app}"; Flags: ignoreversion
;please place MPV nightly build to c:\mpv so its included into the bundle
Source: "C:\mpv\*"; DestDir: "{app}\bin"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "AnimeTool.ico"; DestDir: "{app}"; Flags: ignoreversion

[Icons]
Name: "{group}\AnimeTool"; Filename: "{app}\AnimeTool.vbs"; IconFilename: "{app}\AnimeTool.ico"; Check: returnTrue()
Name: "{userdesktop}\AnimeTool"; Filename: "{app}\AnimeTool.vbs";  IconFilename: "{app}\AnimeTool.ico"; Check: returnTrue()
Name: "{app}\AnimeTool"; Filename: "{app}\AnimeTool.vbs"; IconFilename: "{app}\AnimeTool.ico"; Check: returnTrue()


[Run]
;Filename: "{app}\AnimeTool.vbs"; Parameters: ""; Check: returnFalse()
Filename: "{app}\AnimeTool.vbs"; Description: "{cm:LaunchProgram,AnimeTool}"; Flags: nowait shellexec postinstall skipifsilent; Check: returnTrue()
;Filename: "{app}\AnimeTool.vbs"; Parameters: "-install -svcName ""AnimeTool"" -svcDesc ""AnimeTool - an mpv frontend for watching chunked titles from different sources"" -mainExe ""AnimeTool.exe""  "; Check: returnFalse()

[UninstallRun]
;Filename: "{app}\AnimeTool.exe "; Parameters: "-uninstall -svcName AnimeTool -stopOnUninstall"; Check: returnFalse()

[Code]
function returnTrue(): Boolean;
begin
  Result := True;
end;

function returnFalse(): Boolean;
begin
  Result := False;
end;

function InitializeSetup(): Boolean;
begin
// Possible future improvements:
//   if version less or same => just launch app
//   if upgrade => check if same app is running and wait for it to exit
//   Add pack200/unpack200 support? 
  Result := True;
end;  
