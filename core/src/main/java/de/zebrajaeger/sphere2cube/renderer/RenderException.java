package de.zebrajaeger.sphere2cube.renderer;

public class RenderException extends Exception {

  public RenderException() {
  }

  public RenderException(String message) {
    super(message);
  }

  public RenderException(String message, Throwable cause) {
    super(message, cause);
  }

  public RenderException(Throwable cause) {
    super(cause);
  }

  public RenderException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
