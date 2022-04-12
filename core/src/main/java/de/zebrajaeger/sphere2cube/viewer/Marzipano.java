package de.zebrajaeger.sphere2cube.viewer;

public class Marzipano extends Viewer {

    public Marzipano() {
        super();
    }

    /**
     * https://www.marzipano.net/docs.html
     */
    @Override
    public String getTemplate() {
        // {f} : tile face (one of b, d, f, l, r, u)
        // {z} : tile level index (0 is the smallest level)
        // {x} : tile horizontal index
        // {y} : tile vertical index
        return """
                <html>
                <head>
                     <meta charset="utf-8">
                    
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
                </head>
                <body>
                    <div id="pano" class="pano"></div>
                    <script>
                        let panoElement = document.getElementById('pano');
                        let viewer = new Marzipano.Viewer(panoElement)
                        let geometry = new Marzipano.CubeGeometry({{levels}});
                        let source = Marzipano.ImageUrlSource.fromString('',{cubeMapPreviewUrl:'{{description.preview.path}}'});
                        source._sourceFromTile = (tile)=>{
                            return {url:`${tile.z + 1}/${tile.face}${tile.x}_${tile.y}.{{tileFileType}}`};
                        }
                        let view = new Marzipano.RectilinearView();
                        let scene = viewer.createScene({source, geometry, view});
                        scene.switchTo();
                    </script>
                </body>
                </html>
                """;
    }
}
