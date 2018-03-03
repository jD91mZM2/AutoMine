package one.krake.automine;

import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.InvalidBlockStateException;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.IClientCommand;

public class MainCommand extends CommandBase implements IClientCommand {
    public static Block material;

    public String getName() { return "automine"; }
    public String getUsage(ICommandSender sender) { return "/automine [material]"; }
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String cmd) { return true; }
    public int getRequiredPermissionLevel() { return 0; }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayerSP)) {
            throw new PlayerNotFoundException("sender is not a player");
        }
        if (args.length == 0) {
            material = null;
        } else {
            Block b = getBlockByText(sender, args[0]);
            if (b == null) {
                throw new InvalidBlockStateException();
            }
            material = b;
        }
    }
}
