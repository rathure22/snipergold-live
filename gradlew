#!/usr/bin/env sh
# Dummy wrapper that uses system gradle if wrapper jar missing - will be replaced by action
exec gradle "$@"
