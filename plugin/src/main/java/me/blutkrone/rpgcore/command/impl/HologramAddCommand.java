package me.blutkrone.rpgcore.command.impl;

import me.blutkrone.rpgcore.RPGCore;
import me.blutkrone.rpgcore.command.AbstractCommand;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HologramAddCommand extends AbstractCommand {
    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("rpg.admin") && sender instanceof Player;
    }

    @Override
    public BaseComponent[] getHelpText() {
        return TextComponent.fromLegacyText("<lc_text> <*~x> <*~y> <*~z> §fCreate a hologram (use language identifier.)");
    }

    @Override
    public void invoke(CommandSender sender, String... args) {
        if (args.length >= 2) {
            Location loc = ((Player) sender).getLocation();
            String lc_text = args[1];

            if (args.length >= 3) {
                boolean relative = args[2].startsWith("~");
                double number = Double.valueOf(args[2].replace("~", ""));
                loc.setX((relative ? loc.getX() : 0d) + number);
            }
            if (args.length >= 4) {
                boolean relative = args[3].startsWith("~");
                double number = Double.valueOf(args[3].replace("~", ""));
                loc.setY((relative ? loc.getY() : 0d) + number);
            }
            if (args.length >= 5) {
                boolean relative = args[4].startsWith("~");
                double number = Double.valueOf(args[4].replace("~", ""));
                loc.setZ((relative ? loc.getZ() : 0d) + number);
            }

            // create a hologram
            RPGCore.inst().getHologramManager().createHologram(loc, lc_text);
            // inform about success
            String msg = RPGCore.inst().getLanguageManager().getTranslation("command_success");
            sender.sendMessage(msg);
        }
    }

    @Override
    public List<String> suggest(CommandSender sender, String... args) {
        return null;
    }
}
