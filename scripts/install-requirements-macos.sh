#!/usr/bin/env bash

# install nix
sh <(curl -L https://nixos.org/nix/install)
. /home/codespace/.nix-profile/etc/profile.d/nix.sh

# install direnv
sudo apt update
sudo apt install direnv
# allow direnv
direnv allow

# install java 17
# this didn't work: sudo apt update && sudo apt install openjdk-17-jdk -y
nix-env -iA nixpkgs.openjdk17

echo 'export PATH="$HOME/.nix-profile/bin:$PATH"' >> ~/.bashrc
source ~/.bashrc

java -version


cd quickstart
make install-daml-sdk

make build

make start