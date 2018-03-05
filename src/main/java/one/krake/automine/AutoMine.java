package one.krake.automine;

import static net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import one.krake.automine.MainCommand;
import org.apache.logging.log4j.Logger;

@Mod(modid = AutoMine.MODID, name = AutoMine.NAME, version = AutoMine.VERSION)
public class AutoMine {
    public static final String MODID = "automine";
    public static final String NAME = "AutoMine";
    public static final String VERSION = "0.1.0";

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new MainCommand());
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent event) {
        if (MainCommand.material == null) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.world == null) {
            return;
        }

        double minDist = Integer.MAX_VALUE;
        BlockPos minPos = null;
        EnumFacing minFacing = null;
        Integer minX = null, minZ = null;

        int reach = (int) mc.playerController.getBlockReachDistance();

        BlockPos playerPos = mc.player.getPosition();

        for (int relX = -reach; relX <= reach; ++relX) {
            for (int relY = -reach; relY <= reach; ++relY) {
                for (int relZ = -reach; relZ <= reach; ++relZ) {
                    BlockPos pos = playerPos.add(relX, relY, relZ);

                    EnumFacing facing = EnumFacing.getDirectionFromEntityLiving(pos, mc.player);
                    double dist = mc.player.getPosition().distanceSq(pos.offset(facing));
                    // Minecraft uses euclidean distance
                    // distanceSq means it returns the result without sqrting it first
                    if (dist > reach*reach) {
                        continue;
                    }

                    Block block = mc.world.getBlockState(pos).getBlock();
                    if (Block.isEqualTo(block, MainCommand.material)) {
                        if (dist < minDist) {
                            minDist = dist;
                            minFacing = facing;
                            minPos = pos;
                        }
                    }
                }
            }
        }

        if (minPos == null) {
            return;
        }

        mc.playerController.onPlayerDamageBlock(minPos, minFacing);
    }
}
