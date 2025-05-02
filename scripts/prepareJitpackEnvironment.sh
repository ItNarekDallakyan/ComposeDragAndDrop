#!/bin/bash
# This script prepares the JitPack environment for publishing

# Create empty GPG key for signing
mkdir -p ~/.gradle
echo "signing.keyId=AA8AABB1" > ~/.gradle/gradle.properties
echo "signing.password=jitpack" >> ~/.gradle/gradle.properties
echo "signing.secretKeyRingFile=~/.gnupg/secring.gpg" >> ~/.gradle/gradle.properties

# Set dummy Maven Central credentials for the plugin
echo "mavenCentralUsername=jitpack" >> ~/.gradle/gradle.properties
echo "mavenCentralPassword=jitpack" >> ~/.gradle/gradle.properties

# Make GPG folder
mkdir -p ~/.gnupg
# Generate a dummy GPG key for JitPack
echo "Key-Type: RSA
Key-Length: 2048
Subkey-Type: RSA
Subkey-Length: 2048
Name-Real: JitPack
Name-Email: jitpack@jitpack.io
Expire-Date: 0
Passphrase: jitpack
%no-protection
%commit" > ~/.gnupg/gen-key-script
gpg --batch --gen-key ~/.gnupg/gen-key-script