#!/bin/bash
# CLEAN REPOSITORY SCRIPT - RUN THIS IN TERMUX
echo "=== CLEANING REPOSITORY ==="
rm -rf app/src/main/java/com/rathure22
rm -rf app/src/main/java/com/rathure
rm -rf app/src/main/java/com/rathure22_bak
rm -rf app/build
rm -rf .gradle
git rm -rf app/src/main/java/com/rathure22 2>/dev/null || true
git rm -rf app/src/main/java/com/rathure 2>/dev/null || true
git add -A
echo "=== AFTER CLEAN ==="
ls -R app/src/main/java/ || true
echo "Repository cleaned! Only com.snipergold.v9 should remain"
