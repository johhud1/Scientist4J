# cobbled together with gemini and https://discourse.nixos.org/t/any-libgdx-project-does-not-work-in-nixos/62674/9
{ pkgs ? import <nixpkgs> {} }:
  let
  runtimeLibs = with pkgs; lib.makeLibraryPath [
  ];
  in
  pkgs.mkShell {
    # NOTE: scala metals vscode extension does not play nice with graalvm :( 
    # so set JAVA_HOME to zulu java 17 home.. there's probably a better way to do this
    # export LD_LIBRARY_PATH=${runtimeLibs}:$LD_LIBRARY_PATH
    #      export GLFW=${pkgs.glfw}/
#      export GLFW2=${pkgs.glfw2}/
    #  export GLFW3=${pkgs.glfw3}/
    shellHook = ''
      export JAVA_HOME=${pkgs.zulu21}/
    '';
    # Use pkgs directly for runtime dependencies
    nativeBuildInputs = with pkgs; [
      maven # Build tool
      zulu21 # Java runtime
    ];

}

