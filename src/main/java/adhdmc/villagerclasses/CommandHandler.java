package adhdmc.villagerclasses;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class CommandHandler implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player user = null;
        //checking if player, error if not
        if (sender instanceof Player) {
            user = (Player) sender;
        } else {
            sender.sendMessage(ChatColor.RED + "&cYou must be a player to run this command");
            return true;
        }
        //checking for arguments
        if (args.length < 1) {
            user.sendMessage("FILLER FOR VERSION NUMBER AND AUTHOR");
            return true;
        }
        //aaaaaaaaaaaaaaaaaaaaaaa
        if(args.length >= 1) {
            if (args[0].equalsIgnoreCase("toggle")) {
                if (toggleSetting(user)) {
                    user.sendMessage(translateAlternateColorCodes('&', "&aVillager Info Toggled &nON"));
                } else {
                    user.sendMessage(translateAlternateColorCodes('&', "&cVillager Info Toggled &nOFF"));
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("help")) {
                user.sendMessage(ChatColor.AQUA + "• /vill toggle" + ChatColor.GRAY + "\n  Toggle on or off, shift right-click a villager to access it's information!");
                return true;
            }
            user.sendMessage(ChatColor.RED + "No subcommand by that name!");
        }
        return false;
    }
    private boolean toggleSetting(Player p) {
        UUID uuid = p.getUniqueId();
        if (VillagerHandler.villagerCheck.containsKey(uuid)) {
            if (VillagerHandler.villagerCheck.get(uuid)) {
                VillagerHandler.villagerCheck.put(uuid, false);
                return false;
            } else {
                VillagerHandler.villagerCheck.put(uuid, true);
                return true;
            }
        }
        VillagerHandler.villagerCheck.put(p.getUniqueId(), true);
        return true;
    }
}
