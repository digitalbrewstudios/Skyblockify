{
	inputs = {
		nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
		flake-parts = { url = "github:hercules-ci/flake-parts"; inputs.nixpkgs.follows = "nixpkgs"; };
		flake-compat = { url = "github:edolstra/flake-compat"; flake = false; };
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
				jdk = pkgs.zulu8;
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
						(gradle_7.override {
							java = jdk;
						})
					];

					JAVA_HOME = jdk.home;
				};
			};
		};
}
