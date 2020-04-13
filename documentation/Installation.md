# Installation

## Linux, macOS and other UNIX-like platforms

### Pre-requisites

* jdk11 (or later)
* maven

### Installation

* Create script `update.sh` in the desired installation directory with following contents:<br>
```bash
#!/bin/bash
DIR=$(cd -P -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd -P)
cd "$DIR"
git clone https://github.com/SR3u/RetroFrame.git
cd ./RetroFrame
mkdir ./credentials
git fetch
git pull origin master
mvn package
rm -f ./mediaItems.db
```
* Add execution flag to this script `chmod +x ./update.sh`
* Run `update.sh` script
* Get Google Photo Api key :
    * Go to [https://developers.google.com/photos/library/guides/get-started-java#enable-the-api](https://developers.google.com/photos/library/guides/get-started-java#enable-the-api) 
    * Follow the steps provided there. (Create a new project)
    * If asked something like 'Where are you calling from?' choose the 'Desktop app' option ("Configure your OAuth client" step)
    * When the key is created ("You're all set!" step) download teh client configuration click on 'DOWNLOAD CLIENT CONFIGURATION' button
    * You should end up with a file named `credentials.json`
    * Put `credentials.json` to `/path/to/Photo/Frame/Installation/RetroFrame/credentials/` (here `<......>/credentials/` is a folder, so final file path should be `/path/to/Photo/Frame/Installation/RetroFrame/credentials/credentials.json`)
* Create script named start.sh in the desired installation directory with following contents:<br>
```bash
#!/bin/bash
DIR=$(cd -P -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd -P)
cd "$DIR"
cd ./RetroFrame
java -jar ./target/photoframe-1.0-SNAPSHOT.jar -gui
```
* Add execution flag to this script `chmod +x ./start.sh`
<Br>Then ther is two options:
    1) CLI setup:
        * Launch photoframe via `start.sh` and follow teh command line instructions to grant access to your Google Photo library
    2) GUI setup
        * Launch photoframe via `start.sh` and wait until `settings.properties` file appears in `RetroFrame` folder.
        * Edit `settings.properties` in any text editor:
            * replace `client.enable=fakse` with `client.enable=true`
            * replace `java.awt.headless=true` with `java.awt.headless=false`
            * save file
        * Restart photoframe via `start.sh` and follow the instructions in browser window, that will pop up.

## Configuration
See [Settings.md](https://github.com/SR3u/RetroFrame/blob/master/documentation/Settings.md) for configuration reference.
