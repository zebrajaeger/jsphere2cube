package de.zebrajaeger.sphere2cube.viewer;

public class Pannellum extends Viewer {

    public Pannellum() {
        super();
    }

    @Override
    public String getTemplate() {
        return """
                 <html>
                     <head>
                         <meta charset="utf-8">
                         <meta name="viewport" content="width=device-width, initial-scale=1.0">
                         
                         <meta property="og:title" content="{{description.title}}"/>
                         <meta property="og:type" content="{{description.type}}"/>
                         {{#description.description}}
                         <meta property="og:description" content="{{description.description}}"/>
                         {{/description.description}}
                         <meta property="og:image" content="{{description.preview.path}}"/>
                         <meta property="og:alt" content="{{description.preview.alt}}"/>
                         {{#description.location}}
                         <meta property="og:latitude" content="{{latitude}}">
                         <meta property="og:longitude" content="{{longitude}}">
                         {{/description.location}}
                         
                         <title>{{description.title}}</title>
                         
                         {{#js}}
                         <script type="text/javascript" src="{{.}}"></script>
                         {{/js}}
                         {{#css}}
                         <link rel="stylesheet" href="{{.}}">
                         {{/css}}
                         <style>
                         html, body {
                             margin: 0;
                             height: 100%;
                             background: black;
                         }
                         .panorama {
                             width: calc(100vw - 1px);
                             height: calc(100vh - 1px);
                         }
                         </style>
                     </head>
                     <body>
                    
                     <div id="panorama" class="panorma"></div>
                    
                     <script>
                         function checkSensor() {
                             return new Promise(resolve => {
                                 const sensor = new AbsoluteOrientationSensor();
                                 if (sensor) {
                                     sensor.onreading = () => {
                                         console.log('onReading');
                                         sensor.stop();
                                         resolve(true);
                                     }
                                     sensor.onerror = (event) => {
                                         if (event.error.name === 'NotReadableError') {
                                             console.log("Sensor is not available.");
                                         }
                                         sensor.stop();
                                         resolve(false);
                                     }
                                     sensor.start();
                                 } else {
                                     resolve(false);
                                 }
                             })
                         }
                                
                         (async () => {
                             const av = await checkSensor();
                             const cfg = {
                                 "type": "multires",
                                 "multiRes": {
                                     "basePath": ".",
                                     "path": "/%l/%s%x_%y",
                                     "extension": "{{tileFileType}}",
                                     "tileResolution": {{tileSize}},
                                     "maxLevel": {{levelCount}},
                                     "cubeResolution": {{targetImageSize}},
                                 },
                                 "preview": "{{description.preview.path}}",
                                 "autoLoad": {{autoLoad}},
                                 "minYaw": {{xmin}},
                                 "maxYaw": {{xmax}},
                                 "minPitch": {{ymin}},
                                 "maxPitch": {{ymax}},
                             }
                             cfg['orientationOnByDefault'] = av;
                             if (!av) {
                                 cfg['autoRotate'] = 2;
                             }
                             if(console){
                                 console.log(cfg);
                             }
                             pannellum.viewer('panorama', cfg);
                         })();
                     </script>
                     </body>
                 </html>
                """;
    }
}
