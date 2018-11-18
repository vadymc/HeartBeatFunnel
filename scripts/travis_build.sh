#!/bin/bash

if [ "${TRAVIS_PULL_REQUEST}" = "master" ]; then
    echo "Building master"
    ./gradlew release -Prelease.useAutomaticVersion=true
else
    echo "Building branch"
    ./gradlew check
fi