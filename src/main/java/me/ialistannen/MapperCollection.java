package me.ialistannen;

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
     * @param element The {@link WrappedElement} to check
     *
     * @return The Mapper for the identifier, if any
     */
    public Optional<Mapper> getMapper(WrappedElement element) {
        return mapperMap.stream().filter(mapper -> mapper.matches(element)).findAny();
    }

    /**
     * @param element The {@link WrappedElement} to check
     *
     * @return True if the identifier is contained in this collection
     */
    public boolean hasMapper(WrappedElement element) {
        return mapperMap.stream().anyMatch(mapper -> mapper.matches(element));
    }
}
