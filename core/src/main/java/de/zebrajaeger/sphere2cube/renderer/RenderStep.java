package de.zebrajaeger.sphere2cube.renderer;

public interface RenderStep {

  boolean isEnabled(RenderContext renderContext);

  void render(RenderContext renderContext) throws RenderException;
}
