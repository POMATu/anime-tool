;This file will be executed next to the application bundle image
;I.e. current directory will contain folder AnimeTool with application files
[Setup]
AppId={{app}}
AppName=AnimeTool
AppVersion=Rolling
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
OutputBaseFilename=AnimeTool
Compression=lzma
SolidCompression=yes
PrivilegesRequired=lowest
SetupIconFile=AnimeTool\AnimeTool.ico
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
Source: "AnimeTool\AnimeTool.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "AnimeTool\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
;please place MPV nightly build to c:\mpv so its included into the bundle
Source: "C:\mpv\*"; DestDir: "{app}\app"; Flags: ignoreversion recursesubdirs createallsubdirs


[Icons]
Name: "{group}\AnimeTool"; Filename: "{app}\AnimeTool.exe"; IconFilename: "{app}\AnimeTool.ico"; Check: returnTrue()
Name: "{commondesktop}\AnimeTool"; Filename: "{app}\AnimeTool.exe";  IconFilename: "{app}\AnimeTool.ico"; Check: returnTrue()


[Run]
Filename: "{app}\AnimeTool.exe"; Parameters: "-Xappcds:generatecache"; Check: returnFalse()
Filename: "{app}\AnimeTool.exe"; Description: "{cm:LaunchProgram,AnimeTool}"; Flags: nowait postinstall skipifsilent; Check: returnTrue()
Filename: "{app}\AnimeTool.exe"; Parameters: "-install -svcName ""AnimeTool"" -svcDesc ""AnimeTool - an mpv frontend for watching chunked titles from different sources"" -mainExe ""AnimeTool.exe""  "; Check: returnFalse()

[UninstallRun]
Filename: "{app}\AnimeTool.exe "; Parameters: "-uninstall -svcName AnimeTool -stopOnUninstall"; Check: returnFalse()

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
