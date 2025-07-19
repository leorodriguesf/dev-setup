#!/usr/bin/env bash

set -e


ascii_art='
       __                                     __                  
  ____/ /  ___  _   __          _____  ___   / /_  __  __    ____ 
 / __  /  / _ \| | / /         / ___/ / _ \ / __/ / / / /   / __ \
/ /_/ /  /  __/| |/ /         (__  ) /  __// /_  / /_/ /   / /_/ /
\__,_/   \___/ |___/         /____/  \___/ \__/  \__,_/   / .___/ 
                                                         /_/      
'

cd ~

echo -e "$ascii_art"


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

DEV_SETUP_REF=${DEV_SETUP_REF:-"main"}

if [[ $DEV_SETUP_REF != "main" ]]; then
    git checkout "$DEV_SETUP_REF"
fi

echo "Installation starting..."

source ~/.dev-setup/install $OS

cd - >/dev/null