package net.minecraft.src;

public class EntityRegistryInitializer {
    public static void init() {
        EntityRegistry.register("zombie", EntityZombie::new);
        EntityRegistry.register("skeleton", EntitySkeleton::new);
        EntityRegistry.register("spider", EntitySpider::new);
        EntityRegistry.register("creeper", EntityCreeper::new);
        EntityRegistry.register("ghast", EntityGhast::new);
        EntityRegistry.register("pigzombie", EntityPigZombie::new);
        EntityRegistry.register("slime", EntitySlime::new);
        EntityRegistry.register("cow", EntityCow::new);
        EntityRegistry.register("pig", EntityPig::new);
        EntityRegistry.register("chicken", EntityChicken::new);
    }
}
