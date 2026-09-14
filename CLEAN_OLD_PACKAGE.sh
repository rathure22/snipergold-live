#!/bin/bash
cd ~/snipergold-v9
echo "=== DELETING OLD PACKAGE ==="
rm -rf app/src/main/java/com/rathure22
rm -rf app/src/main/java/com/snipergold/v9/old
rm -f index.html app_icon.png
rm -f app/src/main/res/layout/activity_main.xml
echo "Old files deleted!"
ls -R app/src/main/java/ || true
echo ""
echo "Now unzip final package..."
mkdir -p ~/my_tmp
unzip -o /storage/emulated/0/Download/SniperGold_FINAL_CLEAN_OLD_PACKAGE.zip -d ~/my_tmp
cp -r ~/my_tmp/* .
cp -r ~/my_tmp/.github .
chmod +x gradlew
git add .
git commit -m "FINAL CLEAN - DELETE OLD rathure22 PACKAGE - SINGLE COMPOSE ONLY"
git push
echo "Pushed! Check Actions - should be GREEN with APK!"
