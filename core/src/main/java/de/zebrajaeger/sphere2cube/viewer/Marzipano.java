package de.zebrajaeger.sphere2cube.viewer;

public class Marzipano {


    public String getTemplate() {
        return """
                // {f} : tile face (one of b, d, f, l, r, u)
                // {z} : tile level index (0 is the smallest level)
                // {x} : tile horizontal index
                // {y} : tile vertical index
                                
                module.exports = {
                    createHtml: createHtml
                }
                                
                function createHtml(config, data) {
                    return `
                <html>
                <head>
                    <title>{{title}}}</title>
                    <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/marzipano@0.9.1/dist/marzipano.min.js"></script>    \s
                    <link rel="stylesheet" href="https://www.marzipano.net/demos/sample-tour/style.css">
                </head>
                <body>
                    <div id="pano" class="pano"></div>
                    <script>
                        let panoElement = document.getElementById('pano');
                        let viewer = new Marzipano.Viewer(panoElement)
                        let geometry = new Marzipano.CubeGeometry({{levels}});
                        let source = Marzipano.ImageUrlSource.fromString('',{cubeMapPreviewUrl:'${config.previewFlatPath}'});
                        source._sourceFromTile = (tile)=>{
                            return {url:\\`\\${tile.z + 1}/\\${tile.face}\\${tile.y}_\\${tile.x}.${config.tileFileType}\\`};
                        }
                        let view = new Marzipano.RectilinearView();
                        let scene = viewer.createScene({source, geometry, view});
                        scene.switchTo();
                    </script>
                </body>
                </html>
                `}
                """;
    }
}
