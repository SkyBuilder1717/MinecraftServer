package net.minecraft.src;

import java.util.*;
import java.util.function.*;

public class EntityRegistry {
    private static final HashMap<String, Function<WorldServer, Entity>> entities = new HashMap<>();

    public static void register(String name, java.util.function.Function<WorldServer, Entity> creator) {
        entities.put(name.toLowerCase(), creator);
    }

    public static Entity create(String name, WorldServer world) {
        java.util.function.Function<WorldServer, Entity> func = entities.get(name.toLowerCase());
        if(func != null) {
            return func.apply(world);
        }
        return null;
    }

    public static Set<String> getAvailableEntities() {
        return entities.keySet();
    }
}

