# Filters

Filters are applied to the received image by embedded demo client.
Their configuration can be found in settings.properties.
Property `client.imageFilterChain` is a _chain_ or _pipeline_ of filters descriptor in form of _filter chain string_.

## Syntax
Filter chain string syntax is as follows:<br>
It consists of filter descriptors separated by `|` character:<br>
`<filter0> | <filter1> | <filter2> | ..... | <filterN>`<br>
Each filter descriptor consists of filter name and optional parameters:<br>
`<filter_name> <param0> <param1> <param2> ... <paramN>`<br>
Filters are be applied from left to right.

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
        *Examples:*<br>`dot`
* `applyPalette` -- converts an image to a palette. Has 1 parameter.
                * `palette` -- representation of a palette. Can be either an identifier of existing palette (case insensitive), 
                or a color list with color picker algorithm. For more info see Palettes<br>.
                By default is `monochrome`<br>
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
<br>*Examples:* `channelintensity 1.0 0.5 1.2`<br>`channelintensity 1.0 0.5 1.2 0.9`
### Dithering            
All dithering filters have the same `palette` parameter, as `applyPalette`.
* `atkinson` -- Bill Atkinson's dithering (like in an old Macintosh)
* `floydsteinberg` -- Floyd-Steinberg Error-diffusion algorithm
* `jarvisjudiceninke` -- Jarvice, Judice, and Ninke Dithering algorithm.
* `sierra3`           
* `sierra24`
* `stucki` -- Stucki's dithering

### Aliases
* `macintosh` -- the same as `resize 512 342 | Atkinson Monochrome`<br>

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