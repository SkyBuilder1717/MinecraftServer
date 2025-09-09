package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.util.*;
import net.minecraft.server.MinecraftServer;

public class EntityTracker
{

    public EntityTracker(MinecraftServer minecraftserver)
    {
        field_911_a = new HashSet<>();
        field_910_b = new MCHashTable();
        mcServer = minecraftserver;
        field_912_d = minecraftserver.configManager.func_640_a();
    }

    public void func_611_a(Entity entity)
    {
        if (entity instanceof EntityPlayerMP)
        {
            func_6187_a(entity, 512, 2);
            EntityPlayerMP newPlayer = (EntityPlayerMP) entity;
            boolean newIsVanished = mcServer.configManager.isVanished(newPlayer);

            for (Iterator it = field_911_a.iterator(); it.hasNext();)
            {
                EntityTrackerEntry entry = (EntityTrackerEntry)it.next();
                Entity tracked = entry.field_909_a;

                if (tracked == newPlayer) continue;

                if (tracked instanceof EntityPlayerMP)
                {
                    EntityPlayerMP trackedPlayer = (EntityPlayerMP) tracked;
                    boolean trackedIsVanished = mcServer.configManager.isVanished(trackedPlayer);

                    if (trackedIsVanished)
                    {
                        if (newIsVanished)
                        {
                            entry.func_606_a(newPlayer);
                        }
                    } else
                    {
                        entry.func_606_a(newPlayer);
                    }
                } else
                {
                    entry.func_606_a(newPlayer);
                }
            }

            List<EntityPlayerMP> visiblePlayers = new ArrayList();
            for (EntityPlayerMP o : mcServer.worldMngr.playerEntities)
            {
                if (!(o instanceof EntityPlayerMP)) continue;
                EntityPlayerMP player = o;
                if (player == newPlayer) continue;

                if (newIsVanished)
                {
                    if (mcServer.configManager.isVanished(player))
                    {
                        visiblePlayers.add(player);
                    }
                } else
                {
                    visiblePlayers.add(player);
                }
            }

            EntityTrackerEntry newEntry = (EntityTrackerEntry)field_910_b.lookup(newPlayer.field_331_c);
            if (newEntry != null)
            {
                newEntry.func_601_b(visiblePlayers);
            }
        }
        else
        {
            if(entity instanceof EntityFish)
            {
                func_6186_a(entity, 64, 5, true);
            } else
            if(entity instanceof EntityArrow)
            {
                func_6186_a(entity, 64, 5, true);
            } else
            if(entity instanceof EntitySnowball)
            {
                func_6186_a(entity, 64, 5, true);
            } else
            if(entity instanceof EntityItem)
            {
                func_6186_a(entity, 64, 20, true);
            } else
            if(entity instanceof EntityMinecart)
            {
                func_6186_a(entity, 160, 5, true);
            } else
            if(entity instanceof EntityBoat)
            {
                func_6186_a(entity, 160, 5, true);
            } else
            if(entity instanceof IAnimals)
            {
                func_6187_a(entity, 160, 3);
            } else
            if(entity instanceof EntityTNTPrimed)
            {
                func_6186_a(entity, 160, 10, true);
            }
        }
    }

    public void func_6187_a(Entity entity, int i, int j)
    {
        func_6186_a(entity, i, j, false);
    }

    public void func_6186_a(Entity entity, int i, int j, boolean flag)
    {
        if(i > field_912_d)
        {
            i = field_912_d;
        }
        if(field_910_b.containsItem(entity.field_331_c))
        {
            throw new IllegalStateException("Entity is already tracked!");
        } else
        {
            EntityTrackerEntry entitytrackerentry = new EntityTrackerEntry(entity, i, j, flag);
            field_911_a.add(entitytrackerentry);
            field_910_b.addKey(entity.field_331_c, entitytrackerentry);

            List visiblePlayerEntities = new ArrayList();
            boolean trackedIsPlayer = entity instanceof EntityPlayerMP;
            boolean trackedIsVanished = false;
            if (trackedIsPlayer) trackedIsVanished = mcServer.configManager.isVanished((EntityPlayerMP)entity);

            for (EntityPlayerMP o : mcServer.worldMngr.playerEntities)
            {
                if (o == null) continue;
                EntityPlayerMP player = o;
                if (player == entity) continue;

                if (trackedIsPlayer)
                {
                    if (trackedIsVanished)
                    {
                        if (mcServer.configManager.isVanished(player))
                        {
                            visiblePlayerEntities.add(player);
                        }
                    } else
                    {
                        visiblePlayerEntities.add(player);
                    }
                } else
                {
                    visiblePlayerEntities.add(player);
                }
            }

            entitytrackerentry.func_601_b(visiblePlayerEntities);
            return;
        }
    }


    public void func_610_b(Entity entity)
    {
        if(entity instanceof EntityPlayerMP)
        {
            EntityPlayerMP entityplayermp = (EntityPlayerMP) entity;
            EntityTrackerEntry entitytrackerentry1;
            for(Iterator iterator = field_911_a.iterator(); iterator.hasNext(); entitytrackerentry1.func_12019_a(entityplayermp))
            {
                entitytrackerentry1 = (EntityTrackerEntry)iterator.next();
            }

        }
        EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry)field_910_b.removeObject(entity.field_331_c);
        if(entitytrackerentry != null)
        {
            field_911_a.remove(entitytrackerentry);
            entitytrackerentry.func_604_a();
        }
    }

    public void func_607_a()
    {
        ArrayList arraylist = new ArrayList();
        Iterator iterator = field_911_a.iterator();
        while(iterator.hasNext())
        {
            EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry)iterator.next();

            List visiblePlayers = new ArrayList();
            Entity tracked = entitytrackerentry.field_909_a;
            boolean trackedIsPlayer = tracked instanceof EntityPlayerMP;
            boolean trackedIsVanished = false;
            if (trackedIsPlayer) trackedIsVanished = mcServer.configManager.isVanished((EntityPlayerMP)tracked);

            for (EntityPlayerMP o : mcServer.worldMngr.playerEntities)
            {
                if (o == null) continue;
                EntityPlayerMP player = o;
                if (player == tracked) continue;

                if (trackedIsPlayer)
                {
                    if (trackedIsVanished)
                    {
                        if (mcServer.configManager.isVanished(player))
                        {
                            visiblePlayers.add(player);
                        }
                    } else
                    {
                        visiblePlayers.add(player);
                    }
                } else
                {
                    visiblePlayers.add(player);
                }
            }

            entitytrackerentry.func_605_a(visiblePlayers);

            if(entitytrackerentry.field_900_j && (entitytrackerentry.field_909_a instanceof EntityPlayerMP))
            {
                arraylist.add((EntityPlayerMP)entitytrackerentry.field_909_a);
            }
        }

        label0:
        for(int i = 0; i < arraylist.size(); i++)
        {
            EntityPlayerMP entityplayermp = (EntityPlayerMP) arraylist.get(i);
            Iterator iterator1 = field_911_a.iterator();
            do
            {
                if(!iterator1.hasNext())
                {
                    continue label0;
                }
                EntityTrackerEntry entitytrackerentry1 = (EntityTrackerEntry)iterator1.next();
                if(entitytrackerentry1.field_909_a == entityplayermp)
                {
                    continue;
                }

                if(entitytrackerentry1.field_909_a instanceof EntityPlayerMP)
                {
                    EntityPlayerMP trackedPlayer = (EntityPlayerMP) entitytrackerentry1.field_909_a;
                    boolean trackedIsVanished = mcServer.configManager.isVanished(trackedPlayer);

                    if(trackedIsVanished)
                    {
                        if(mcServer.configManager.isVanished(entityplayermp))
                        {
                            entitytrackerentry1.func_606_a(entityplayermp);
                        }
                    } else
                    {
                        entitytrackerentry1.func_606_a(entityplayermp);
                    }
                } else
                {
                    entitytrackerentry1.func_606_a(entityplayermp);
                }
            } while(true);
        }
    }

    public void func_12021_a(Entity entity, Packet packet)
    {
        EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry)field_910_b.lookup(entity.field_331_c);
        if(entitytrackerentry != null)
        {
            entitytrackerentry.func_603_a(packet);
        }
    }

    public void func_609_a(Entity entity, Packet packet)
    {
        EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry)field_910_b.lookup(entity.field_331_c);
        if(entitytrackerentry != null)
        {
            entitytrackerentry.func_12018_b(packet);
        }
    }

    public void func_9238_a(EntityPlayerMP entityplayermp)
    {
        EntityTrackerEntry entitytrackerentry;
        for(Iterator iterator = field_911_a.iterator(); iterator.hasNext(); entitytrackerentry.func_9219_b(entityplayermp))
        {
            entitytrackerentry = (EntityTrackerEntry)iterator.next();
        }

    }

    private Set<EntityTrackerEntry> field_911_a;
    private MCHashTable field_910_b;
    private MinecraftServer mcServer;
    private int field_912_d;
}
