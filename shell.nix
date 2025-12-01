{ pkgs ? import <nixpkgs> {} }:
  let
  runtimeLibs = with pkgs; lib.makeLibraryPath [
  ];
  in
  pkgs.mkShell {
    shellHook = ''
      export JAVA_HOME=${pkgs.zulu21}/
    '';
    # Use pkgs directly for runtime dependencies
    nativeBuildInputs = with pkgs; [
      maven # Build tool
      zulu21 # Java runtime
    ];

}

