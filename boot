#!/usr/bin/env bash

cd ~

set -e

OS_TYPE=$(uname -s)

case "$OS_TYPE" in
Darwin) 
    OS=macos
    OS_NAME="macOS"
    ;;
*)
    echo "OS $OS_TYPE not yet supported. Stopping"
    exit 1
    ;;
esac

echo "=> $OS_NAME detected"

echo -e "\nInstalling $OS_NAME dependencies"

case "$OS" in
macos)
    # Keep sudo alive
    sudo -v

    # Prevent the system from sleeping while the script is running
    caffeinate -s -u -w $$ &

    if [ ! -f /opt/homebrew/bin/brew ]; then
        NONINTERACTIVE=1 /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
    fi

    if ! command -v brew &>/dev/null; then
        eval "$(/opt/homebrew/bin/brew shellenv)"
    fi

    if ! command -v python3 &>/dev/null; then
        brew install python3
    fi

    if ! command -v ansible &>/dev/null; then
        brew install ansible
    fi
    ;;
*)
    echo "OS $OS_TYPE not yet supported. Stopping"
    exit 1
    ;;
esac

echo "$OS dependencies installed"

echo "Cloning repository..."

rm -rf ~/.dev-setup

git clone https://github.com/leorodriguesf/dev-setup.git ~/.dev-setup >/dev/null

cd ~/.dev-setup

echo "Installation starting..."

source ~/.dev-setup/install

cd - >/dev/null