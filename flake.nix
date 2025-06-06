    {
	inputs = {
		nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
		flake-parts.url = "github:hercules-ci/flake-parts";
		flake-compat.url = "https://flakehub.com/f/edolstra/flake-compat/1.tar.gz";
	};

	outputs = inputs@{ nixpkgs, flake-parts, ... }: 
		flake-parts.lib.mkFlake {inherit inputs;} {
			systems = nixpkgs.lib.systems.flakeExposed;
			perSystem = {
				lib,
				pkgs,
				system,
				config,
				...
			}:
			let
				jdk = pkgs.zulu;
			in
			{
			    formatter = pkgs.nixpkgs-rfc-fmt;
				_module.args.pkgs = import nixpkgs {
					inherit system;
					config.allowUnsupportedSystem = true;
				};
				devShells.default = pkgs.mkShell {
					nativeBuildInputs = with pkgs; [
						jdk
						(kotlin.override {
							jre = jdk;
						})
						(gradle_8.override {
							java = jdk;
						})
					];

					JAVA_HOME = jdk.home;
					LD_LIBRARY_PATH = if pkgs.stdenv.isLinux then lib.makeLibraryPath [
							pkgs.glfw3-minecraft
							pkgs.openal
							pkgs.flite
							pkgs.libglvnd
							pkgs.pulseaudio
							pkgs.udev
					] else [
					    pkgs.glfw3-minecraft
					    pkgs.openal
					];
				};
			};
		};
}
