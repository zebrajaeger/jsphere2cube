package de.zebrajaeger.sphere2cube.viewer;

import java.util.Map;
import lombok.Getter;
import org.junit.jupiter.api.Test;

class ObjectMapperTest {

  @Getter
  public static class Foo {

    private final Bar bar = new Bar();
    String narf = "Pinky&Brain";
  }

  @Getter
  public static class Bar {

    String hello = "hello";
    String world = "world";
  }

  @Getter
  public static class Basic {

    private final boolean bool = false;
    private final char aChar = 'x';
    private final String string = "yolo";
    private final byte aByte = 1;
    private final short aShort = 2;
    private final int aInt = 3;
    private final long aLong = 4;
    private final float aFloat = 1.1f;
    private final double aDouble = 2.2;
  }

  @Test
  public void basicTest() {
    final ObjectMapper om = new ObjectMapper();
    final Map<String, Object> values = om.map(new Basic());
    System.out.println(values);
  }

  @Test
  public void recursiveTest() {
    final ObjectMapper om = new ObjectMapper();
    final Map<String, Object> values = om.map(new Foo());
    System.out.println(values);
  }
}
