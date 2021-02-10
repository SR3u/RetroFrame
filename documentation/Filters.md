# Filters

Filters are applied to the received image by embedded demo client.
Their configuration can be found in `settings.properties`.
Property `client.imageFilterChain` is a _chain_ or _pipeline_ of filters descriptor in form of _filter chain string_.
See [Settings.md](https://github.com/SR3u/gphotorepo/blob/master/documentation/Settings.md) for more info.

## Syntax
Filter chain string syntax is as follows:<br>
It consists of filter descriptors separated by `|` character:<br>
`<filter0> | <filter1> | <filter2> | ..... | <filterN>`<br>
Each filter descriptor consists of filter name and optional parameters:<br>
`<filter_name> <param0> <param1> <param2> ... <paramN>`<br>
Filters are applied from left to right.

## Available filters:
* `identity` -- does nothing, is a default option<br>
        *Examples:*<br>`identity`
* `scale` -- scales image, has two parameters: factorX and factorY.<br>
           * `factorX` is X axis (width) scale factor,<br>
           * `factorY` is Y axis (height) scale factor.<br>
           If no `factorY` is provided, `factorY` is equal to `factorX`.
           If not parameters provided `factorX` and `factorY` are considered to be 1.<br>
           This filter does not change window size, only the image size (in pixels), can be used to optimize your filter chain.<br>
           *Examples:*<br> `scale 0.5`<br> `scale 0.95 1.25`<br>
* `resize` -- resize image to certain width and height in pixels, keeping the proportions.<br>
            Has 2 parameters: `width` and `height`, both in pixels and both are required.<br>
            *Example:* `resize 512 256`<br>
* `OriginalSize` -- resize image back to its original size.<br>Has no parameters.<br>*Example:* `OriginalSize`<br>
* `brightness` -- make images brighter (or darker) with clipping.<br>
            Has 1 parameter: `factor` --brightness multiplier. <br>
            *Examples:*<br>`brightness 0.75`<br>`brightness 1.2`<br>
* `hue` -- changes image HUE channel with round-robin clipping.<br>
            Has 1 parameter: `factor` --hue multiplier. <br>
            *Examples:*<br>`hue 0.75`<br>`hue 1.2`<br>
* `saturation` -- changes image saturation channel with clipping.<br>
            Has 1 parameter: `factor` --hue multiplier. <br>
            *Examples:*<br>`saturation 0.75`<br>`saturation 1.2`<br>                     
* `grayscale` -- convert image to grayscale. Has 3 parameters `grayscale redWeight greenWeight blueWeight`:<br>
            * `redWeight` -- weight of red channel, default: `0.299`<br>
            * `greenWeight` -- weight of green channel, default `0.587`<br>
            * `blieWeight` -- weight of blue channel, default `0.114`<br>
            *Examples:*<br>`grayscale`<br>`grayscale 0.4 0.3 0.2`<br>
* `dot` -- apply a 2x2 channel filtering pattern, has no parameters<br>
        *Examples:*<br>`dot`<br>
* `TrinitronH` -- [Sony Trinitron](https://en.wikipedia.org/wiki/Trinitron) (kinda) with a horizontal grid<br>
* `TrinitronV` -- [Sony Trinitron](https://en.wikipedia.org/wiki/Trinitron) (kinda) with a vertical grid<br>
* `cmyk2x2` -- apply 2x2 channel filtering palette pattern:<br>
              `grayscale` `cyan`<br>
               `magenta` `yellow`<br>
               It has no parameters.
               For more info see [Palettes.md](https://github.com/SR3u/gphotorepo/blob/master/documentation/Palettes.md).<br>
                *Example:* <br>`cmyk2x2`
* `applyPalette` -- converts an image to a palette. Has 1 parameter.
                * `palette` -- representation of a palette. Can be either an identifier of existing palette (case-insensitive), 
                or a color list with color picker algorithm. For more info see [Palettes.md](https://github.com/SR3u/gphotorepo/blob/master/documentation/Palettes.md).<br>
                By default is `monochrome`<br>See [Palettes.md](https://github.com/SR3u/gphotorepo/blob/master/documentation/Palettes.md) for more info.<br>
                *Examples:* <br>`applyPalette CGA`<br>`applypalette #000000 #ffffff #FF0000 #00FF00 #0000FF`<br>
                `applyPalette LUMINANCE #000000 #111111 #222222 #333333 #444444 #555555 #666666 #777777 #888888 #999999 #AAAAAA #BBBBBB #CCCCCC #DDDDDD #EEEEEE #FFFFFF`<br>
### RGB channel filters
* `redchannel` -- removes green and blue channels<br>*Example:* `redchannel`<br>
* `greenchannel` -- removes red and blue channels<br>*Example:* `greenchannel`<br>
* `bluechannel` -- removes red and green channels<br>*Example:* `bluechannel`<br>
* `channelintensity` -- scales channels intensity by `fR` `fG` `fB` `fA` with clipping.<br>Has 4 parameters:
<br>* `fR` -- red channel intensity multiplier
<br>* `fG` -- green channel intensity multiplier
<br>* `fB` -- blue channel intensity multiplier
<br>* `fA` -- alpha channel intensity multiplier
<br>*Examples:* <br>`channelintensity 1.0 0.5 1.2`<br>`channelintensity 1.0 0.5 1.2 0.9`
* `Spectrum` -- spectrum-like effect. Has 2 parameters: `columns` and `rows`.<br>
        * `columns` -- how many columns does the attributes map have. If `rows` <= 0, then it is interpretted as character width and height (*default:* `8`)<br>
        * `rows` -- how many rows does the attributes map have (*default:* `-1`)<br>
        
### Dithering            
All dithering filters have the same `palette` parameter, as `applyPalette`.
* `atkinson` -- Bill Atkinson's dithering (like in an old Macintosh)
* `floydsteinberg` -- Floyd-Steinberg Error-diffusion algorithm
* `jarvisjudiceninke` -- Jarvice, Judice, and Ninke Dithering algorithm.
* `sierra3` -- Sierra 3 dithering           
* `sierra24` -- Sierra 24 dithering
* `stucki` -- Stucki's dithering

### Aliases (Presets)
* `Commodore64` -- Commodore 64 emulation (`resize 320 200 | Atkinson c64`)<br>
* `Commodore64HD` -- Commodore 64 emulation, but without downscaling (`Atkinson c64`)<br>
* `ColorPrinter` -- Lo-Fi color printer emulation (`Atkinson CMYK`)<br>
* `CRT` -- Old Cathode Ray Tube screen (kinda) (`Trinitron`)<br>
* `Drawing`  -- Drawing effect (`applyPalette Crayola`)<br>
* `GameBoy` -> [Original Nintendo GameBoy](https://en.wikipedia.org/wiki/List_of_video_game_console_palettes#Game_Boy) (`size 160 144 | Atkinson GameBoy`)<br>
* `GameBoyHD` -> [Original Nintendo GameBoy](https://en.wikipedia.org/wiki/List_of_video_game_console_palettes#Game_Boy), but without downscaling (`Atkinson GameBoy`)<br>
* `IbmPcCga` -- IBM PC CGA mode emulation (320x200, 4 colors from first palette) (`resize 320 200 | atkinson CGA`)<br>
* `ImPcCgaHD` -- IBM PC CGA mode emulation without downscaling (4 colors from CGA first palette) (`atkinson CGA`)<br>
* `IBM_PC_CGA` -- IBM PC CGA mode emulation (320x200, 4 colors from first palette) (`resize 320 200 | atkinson CGA`)<br>
* `IBM_PC_CGA_HD` -- IBM PC CGA mode emulation without downscaling (4 colors from CGA first palette) (`atkinson CGA`)<br>
* `Macintosh` -- Macintosh Classic emulation (`resize 512 342 | Atkinson Monochrome`)<br>
* `MacintoshHD` -- Macintosh Classic emulation, but without downscaling (`Atkinson Monochrome`)<br>
* `MacintoshClassic` -- Macintosh Classic emulation (`resize 512 342 | Atkinson Monochrome`)<br>
* `MacintoshClassicHD` -- Macintosh Classic emulation, but without downscaling (`Atkinson Monochrome`)<br>
* `Printer` -- Lo-Fi color printer emulation (`Atkinson CMYK`) same as `ColorPrinter`<br>
* `Technicolor` -> [Technicolor process 2 (Two Stripes)](https://en.wikipedia.org/wiki/Technicolor#Process_2) (`applyPalette Technicolor`)<br>
* `Technicolor2` -> [Technicolor process 2 (Two Stripes)](https://en.wikipedia.org/wiki/Technicolor#Process_2) (`applyPalette Technicolor`)<br>
* `Trinitron` -- [Sony Trinitron](https://en.wikipedia.org/wiki/Trinitron) (kinda)<br>
* `TV` -- Old TV (kinda) (`TrinitronH`)<br>  
* `ZxSpectrum` -- ZX Spectrum emulation (`resize 348 256 | atkinson ZXFULL | buffer | Spectrum 32 24`)<br>
* `ZxSpectrumHD` -- ZX Spectrum emulation, but without downscaling and attributes collision (`atkinson ZXFULL`)<br>

## Examples
* `client.imageFilterChain=sierra3 LUMINANCE #000000 #111111 #222222 #333333 #444444 #555555 #666666 #777777 #888888 #999999 #AAAAAA #BBBBBB #CCCCCC #DDDDDD #EEEEEE #FFFFFF | Atkinson monochrome`<br>
* `client.imageFilterChain=Macintosh`<br>
* `client.imageFilterChain=identity`<br>
* `client.imageFilterChain=DOT`<br>
* `client.imageFilterChain=Grayscale`<br>
* `client.imageFilterChain=Scale 0.25 | DOT`<br>
* `client.imageFilterChain=Scale 0.25 | Atkinson monochrome`<br>
* `client.imageFilterChain=resize 512 512`<br>
* `client.imageFilterChain=sierra3 LUMINANCE #000000 #111111 #222222 #333333 #444444 #555555 #666666 #777777 #888888 #999999 #AAAAAA #BBBBBB #CCCCCC #DDDDDD #EEEEEE #FFFFFF | DOT`<br>

## Important notes: 
* `TV`, `Trinitron`, `TrinitronV` and `TrinitronH` may require additional 3x upscaling if applied after low-res filters (`Commodore`, `IbmPcCga`, etc).
Insert `scale 3 3` or `OriginalSize` between it and previous filters for best result or if you experience any weird color issues.<br>
*WARNING:* Don't upscale if you are using *HD* filter.<br>
*Example:* `Commodore64 | scale 3 3 | Trinitron`, `IbmPcCga | OriginalSize | Trinitron`.
* `ZxSpectrum` and `Spectrum` filter is very *very* slow. 

## Aliases
You can add an alias to a filter combination for simpler use in your filter chain.
To do so, just add properties with names like `client.imageFilterChain.alias.<alias_name>` to settings.properties.<br>
*Examples:*<br>
* `client.imageFilterChain.alias.mac=resize 512 342 | Atkinson Monochrome`
* `client.imageFilterChain.alias.atkinsonWebSafe=atkinson websafe`
* `client.imageFilterChain.alias.atkinsonWebSafeLowRes=resize 512 256 | atkinson websafe`
* `client.imageFilterChain.alias.speccy=resize 348 256 | atkinson ZXFULL | buffer | Spectrum 32 24 | scale 3 3 | tv`
* `client.imageFilterChain.alias.c64_tv=Commodore64 | scale 3 3 | tv`
<br>
Then you can refer to them in your filter chain as if they were defined out of the box:
* `client.imageFilterChain=c64_tv`<br>
or
* `client.imageFilterChain=speccy`<br>
or   
* `client.imageFilterChain=mac`<br>

