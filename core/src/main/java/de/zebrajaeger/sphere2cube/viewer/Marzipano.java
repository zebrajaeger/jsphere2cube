package de.zebrajaeger.sphere2cube.viewer;

public class Marzipano extends Viewer {

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
                    <title>{{htmlTitle}}}</title>
                    <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/marzipano@0.9.1/dist/marzipano.min.js"></script>
                    <link rel="stylesheet" href="https://www.marzipano.net/demos/sample-tour/style.css">
                </head>
                <body>
                    <div id="pano" class="pano"></div>
                    <script>
                        let panoElement = document.getElementById('pano');
                        let viewer = new Marzipano.Viewer(panoElement)
                        let geometry = new Marzipano.CubeGeometry({{levels}});
                        let source = Marzipano.ImageUrlSource.fromString('',{cubeMapPreviewUrl:'{{previewPath}}'});
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
