#!/bin/bash

if [ "${TRAVIS_BRANCH}" = "master" ]; then
    echo "Building master"
    ./gradlew release -Prelease.useAutomaticVersion=true
else
    echo "Building branch"
    ./gradlew check
fi