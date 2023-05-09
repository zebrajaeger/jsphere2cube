# cube2sphere

Converts a spherical panorama image into a cube map faces/tile tree and creates .html file to render it within browsers.

Directly supported viewers (html generation):
- [Pannellum](https://pannellum.org/)
- [Marzipano](https://www.marzipano.net/)


clone this repo

### Preconditions 

- [java](https://openjdk.java.net/) 8 or higher
- [maven](https://maven.apache.org/)
- [ [git](https://git-scm.com/downloads) ]

## Compile / install application

- Clone the project with git (or download as archive and extract).
- Execute in this directory (this means: the directory of this readme file):
  

    mvn clean install

## Run application:

### jar (single)

From core/target directory take the jar and make it available in your path. Then run (version number may different):

    java -jar sphere2cube-java-core-0.0.1-SNAPSHOT-exec.jar [parameters]

## Config file

Default name is "sphere2cube.json"

Example (at least change source.path for input file, everything else may be ok):
```json
{
  "debug": false,
  "source": {
    "path": "/home/l/prj/panos/IMG_1766_S(168.00x25.63(8.99)).psb"
  },
  "target": "./target",
  "preview": {
    "cubemap": {
      "enabled": true,
      "edge": 200,
      "target": "preview_cube.jpg"
    },
    "equirectangular": {
      "enabled": true,
      "edge": 800,
      "target": "preview_equirectangular.jpg"
    },
    "scaled": {
      "enabled": true,
      "edge": 800,
      "target": "preview_scaled.jpg"
    }
  },
  "cubemap": {
    "tiles": {
      "enabled": true,
      "tileEdge": 512,
      "target": "{{levelCount}}/{{faceNameShortLowerCase}}{{xIndex}}_{{yIndex}}.png"
    },
    "faces": {
      "enabled": false,
      "target": "{{faceNameLowerCase}}_{{levelCount}}.png"
    }
  },
  "viewer": {
    "pannellum": {
      "enabled": true,
      "title": "cube2sphere - Pannellum",
      "target": "index.p.html",
      "js": [
        "https://cdn.jsdelivr.net/npm/pannellum@2.5.6/build/pannellum.js"
      ],
      "css": [
        "https://cdn.jsdelivr.net/npm/pannellum@2.5.6/build/pannellum.css"
      ]
    },
    "marzipano": {
      "enabled": true,
      "title": "cube2sphere - Marzipano",
      "target": "index.m.html",
      "js": [
        "https://cdn.jsdelivr.net/npm/marzipano@0.9.1/dist/marzipano.min.js"
      ],
      "css": [
        "https://www.marzipano.net/demos/sample-tour/style.css"
      ]
    }
  },
  "archive": {
    "enabled": true,
    "target": "pano.zip"
  }
}
```
## Links

- <https://stackoverflow.com/questions/29678510/convert-21-equirectangular-panorama-to-cube-map/29681646#29681646>
- <https://github.com/jaxry/panorama-to-cubemap/blob/gh-pages/convert.js>

## TODO

### HIGH (required)
- [X] More differentiated logging 
- [X] Progress bar~~
- [X] Timer~~
- [X] Marzipano viewer~~  
- [X] read embedded xml of autopano giga~~
- [X] restrict view angle dependent from source image bounds~~
- [X] Add css and js from viewers instead link to CDN~~
- [X] zip file packing (multithreaded)~~

### MID (nice to have)
- [ ] maven plugin
- [X] self containing application
- [ ] Populate README
- [X] Panorama description file

### LOW (later or never...)
- [ ] output image in tiles / dump to HDD
- [ ] input image in tiles / dump to HDD
