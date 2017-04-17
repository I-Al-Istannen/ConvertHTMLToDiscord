package me.ialistannen.htmltodiscord;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Contains all valid mappers
 */
public class MapperCollection {

  private Set<Mapper> mapperMap = new HashSet<>();

  /**
   * Adds a mapper, overwriting existing
   *
   * @param mapper The Mapper to add
   */
  public void addMapper(Mapper mapper) {
    mapperMap.add(mapper);
  }

  /**
   * Adds all mappers from the iterable.
   *
   * @param mappers The mappers to add
   * @see #addMapper(Mapper)
   */
  public void addMappers(Iterable<Mapper> mappers) {
    mappers.forEach(this::addMapper);
  }

  /**
   * Adds all mappers from the array.
   *
   * @param mappers The mappers to add
   * @see #addMapper(Mapper)
   */
  public void addMappers(Mapper... mappers) {
    for (Mapper mapper : mappers) {
      addMapper(mapper);
    }
  }

  /**
   * @param element The {@link WrappedElement} to check
   * @return The Mapper for the identifier, if any
   */
  public Optional<Mapper> getMapper(WrappedElement element) {
    return mapperMap.stream().filter(mapper -> mapper.matches(element)).findAny();
  }

  /**
   * @param element The {@link WrappedElement} to check
   * @return True if the identifier is contained in this collection
   */
  public boolean hasMapper(WrappedElement element) {
    return mapperMap.stream().anyMatch(mapper -> mapper.matches(element));
  }
}
