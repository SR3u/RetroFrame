# Settings
Settings are stored in `settings.properties` file in working directory. It is a [standard Java properties](https://en.wikipedia.org/wiki/.properties) file.<br>
Here is a reference to its keys and values:<br>
* `java.awt.headless` -- overrides system variable, needs to be false for gui startup (*default:* `true`)
* `client.enable` -- enable client gui startup, is overriden by java.awt.headless (*default:* `false`)
* `client.fullscreen` -- launch client window in full screen (*default:* `false`)
* `client.refreshDelay` -- how many milliseconds each photo is shown (*default:* `60000`)
* `client.window.transparent` -- try to make client window transparent (*default:* `false`)
* `client.window.transparent.controls` -- try still have client window controls if it is transparent (*default:* `true`)
* `client.imageFilterChain` -- filters applied to the image before it is shown (*default:* `identity`). See [Filters.md](https://github.com/SR3u/gphotorepo/blob/master/documentation/Filters.md) for more info.
* `server.port` -- port server will listen (*default:* `4242`)
* `media.databasePath` -- local database cache (*default:* `mediaItems.db`)
* `media.backup` -- perform backup of cached media (*default:* `false`)
* `media.backupPath` -- path to backup media items (*default:* `null`)
* `media.albumName` -- album to take photos from (*default:* empty string). If `media.albumName` is empty or album is absent, then it defaults to whole Google Photo library.
