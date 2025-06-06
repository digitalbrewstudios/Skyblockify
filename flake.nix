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
				};
			};
		};
}
