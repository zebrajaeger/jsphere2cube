package de.zebrajaeger.sphere2cube.viewer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ObjectMapper {

  public Map<String, Object> map(Object o) {
    Map<String, Object> result = new HashMap<>();

    for (Method m : o.getClass().getMethods()) {
      String name = m.getName();
      if (name.length() > 2 && name.startsWith("is") && Character.isUpperCase(name.charAt(2))) {
        name = name.substring(2, 3).toLowerCase() + name.substring(3);
        try {
          result.put(name, m.invoke(o));
        } catch (IllegalAccessException | InvocationTargetException e) {
          throw new RuntimeException(e);
        }
      }

      if (name.length() > 3 && name.startsWith("get") && Character.isUpperCase(name.charAt(3))) {
        if("getClass".equals(name)){
          continue;
        }
        name = name.substring(3, 4).toLowerCase() + name.substring(4);
        try {
          Object value = m.invoke(o);
          if (value == null) {
            result.put(name, null);
          } else if (Character.class.equals(value.getClass())) {
            result.put(name, value);
          } else if (String.class.equals(value.getClass())) {
            result.put(name, value);
          } else if (Boolean.class.equals(value.getClass())) {
            result.put(name, value);
          } else if (Byte.class.equals(value.getClass())) {
            result.put(name, value);
          } else if (Short.class.equals(value.getClass())) {
            result.put(name, value);
          } else if (Integer.class.equals(value.getClass())) {
            result.put(name, value);
          } else if (Long.class.equals(value.getClass())) {
            result.put(name, value);
          } else if (Float.class.equals(value.getClass())) {
            result.put(name, value);
          } else if (Double.class.equals(value.getClass())) {
            result.put(name, value);
          } else {
            result.put(name, map(value));
          }
        } catch (IllegalAccessException | InvocationTargetException e) {
          throw new RuntimeException(e);
        }
      }
    }

    return result;
  }

}
