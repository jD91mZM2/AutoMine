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

        int minDist = Integer.MAX_VALUE;
        BlockPos minPos = null;
        Integer minX = null, minZ = null;

        int reach = (int) mc.playerController.getBlockReachDistance();

        for (int relX = -reach; relX <= reach; ++relX) {
            for (int relY = -reach; relY <= reach; ++relY) {
                for (int relZ = -reach; relZ <= reach; ++relZ) {
                    int dist = Math.abs(relX) + Math.abs(relY) + Math.abs(relZ);
                    if (dist > reach) {
                        continue;
                    }
                    int posX = ((int) mc.player.posX) + relX;
                    int posY = ((int) mc.player.posY) + relY;
                    int posZ = ((int) mc.player.posZ) + relZ;

                    BlockPos pos = new BlockPos(posX, posY, posZ);
                    Block block = mc.world.getBlockState(pos).getBlock();
                    if (Block.isEqualTo(block, MainCommand.material)) {
                        if (dist < minDist) {
                            minDist = dist;
                            minPos = pos;
                            minX = ((Integer) (relX)).compareTo(0);
                            minZ = ((Integer) (relZ)).compareTo(0);
                        }
                    }
                }
            }
        }

        if (minPos == null || minX == null || minZ == null) {
            return;
        }

        EnumFacing facing = EnumFacing.getDirectionFromEntityLiving(minPos, mc.player);
        mc.playerController.onPlayerDamageBlock(minPos, facing);
    }
}
