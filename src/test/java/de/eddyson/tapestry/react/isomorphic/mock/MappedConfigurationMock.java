package de.eddyson.tapestry.react.isomorphic.mock;

import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry5.ioc.MappedConfiguration;

public class MappedConfigurationMock implements MappedConfiguration<String, Object> {
  
  private final Map<String, Object> map = new HashMap<String, Object>();


  public MappedConfigurationMock() {
    super();
  }
  
  public Object get(String key) {
    return this.map.get(key);
  }
  

  @Override
  public void add(String key, Object value) {
    this.map.put(key, value);
  }

  @Override
  public void override(String key, Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addInstance(String key, Class<? extends Object> clazz) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void overrideInstance(String key, Class<? extends Object> clazz) {
    throw new UnsupportedOperationException();
  }

}
