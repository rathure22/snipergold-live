
JETPACK COMPOSE CANVAS - REQUIRED UG UNSAON PAGHIMO GIT REPOSITORY SA GITHUB

1. REQUIRED PARA MAGHIMO:
- Android Studio Hedgehog o mas bag-o (naay Compose support)
- JDK 17 (Temurin)
- Android SDK 34, build-tools 34.0.0, platform-tools
- Kotlin 1.9.22
- Gradle 8.2.2
- Internet para sa dependencies (Compose BOM 2024.02.00, OkHttp)

2. UNSAON PAGHIMO GIT REPO + GITHUB:

Step 1: Sa Termux / PC
cd ~/snipergold-v9
git init (kung wala pa)
git add .
git commit -m "Initial Jetpack Compose Canvas - SINGLE FRAMEWORK"

Step 2: Maghimo bag-ong repository sa GitHub.com
- Adto sa github.com -> New repository
- Pangalan: snipergold-compose-canvas
- Ayaw i-check ang README (kay naa na kay local)
- Create repository

Step 3: I-connect local to GitHub
git remote add origin https://github.com/rathure22/snipergold-compose-canvas.git
git branch -M main
git push -u origin main

Kung naa nay daan nga repo:
git remote -v (check)
git remote remove origin (kung gusto ilisan)
git remote add origin https://github.com/rathure22/snipergold-v9.git
git push

Step 4: GitHub Actions automatic mag-build ug APK!
Adto sa Actions tab -> GREEN -> Download APK sa Artifacts

3. Ngano Jetpack Compose Canvas ang pinaka dali?
- 100% Kotlin lang, walay XML layout!
- Canvas API built-in, dali mag-draw guide lines
- Modern, declarative, dili na findViewById
- Google recommended, mas paspas kaysa MPAndroidChart
- Single file ra MainActivity.kt, tanan naa didto!

4. Unsaon pag-apply aning package:
cd ~/snipergold-v9
rm -rf .github app/build.gradle app/src/main/java/com/snipergold/v9 app/src/main/AndroidManifest.xml
unzip -o /storage/emulated/0/Download/SniperGold_JetpackCompose_Canvas.zip -d /tmp/compose
cp -r /tmp/compose/* .
cp -r /tmp/compose/.github .
chmod +x gradlew
git add .
git commit -m "JETPACK COMPOSE CANVAS - SINGLE FRAMEWORK MODERN DALI"
git push

DONE! GREEN BUILD!
