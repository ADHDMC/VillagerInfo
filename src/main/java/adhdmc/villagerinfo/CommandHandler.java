package adhdmc.villagerinfo;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static net.md_5.bungee.api.ChatColor.GREEN;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class CommandHandler implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player user = null;
        //checking if player, error if not
        if (sender instanceof Player) {
            user = (Player) sender;
        } else {
            if(args[0].equalsIgnoreCase("reload")) {
                VillagerInfo.plugin.reloadConfig();
                MessageHandler.loadConfigMsgs();
                sender.sendMessage(MessageHandler.configReload);
                return true;
            } else {
            sender.sendMessage(ChatColor.RED + "&cYou must be a player to run this command");
            return true;
            }
        }

        //checking for arguments
        if (args.length == 0) {
            user.sendMessage(MessageHandler.prefix + ChatColor.translateAlternateColorCodes('&', "\n&aAuthor: &6Illogicalsong\n&aVersion:&7 BETA\n&aSpecial Thanks to Peashooter101"));
            return true;
        }

        //aaaaaaaaaaaaaaaaaaaaaaa
        if(args.length == 1) {
            if (args[0].equalsIgnoreCase("toggle")) {
                if(user.hasPermission("villagerinfo.toggle")) {
                    if (toggleSetting(user)) {
                        user.sendMessage(MessageHandler.toggleOn);
                    } else {
                        user.sendMessage(MessageHandler.toggleOff);
                    }
                } else {
                    user.sendMessage(MessageHandler.noPermission);
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("help")) {
                if(user.hasPermission("villagerinfo.use")) {
                    user.sendMessage(ChatColor.AQUA + "• /vill toggle" + ChatColor.GRAY + "\n  Toggle on or off, then shift right-click a villager to access it's information!");
                } else {
                    user.sendMessage(MessageHandler.noPermission);
                }
                return true;
            }
            if(args[0].equalsIgnoreCase("reload")){
                if(user.hasPermission("villagerinfo.reload")){
                    VillagerInfo.plugin.reloadConfig();
                    MessageHandler.loadConfigMsgs();
                    user.sendMessage(MessageHandler.configReload);
                } else {
                    user.sendMessage(MessageHandler.noPermission);
                }
                return true;
            }
            user.sendMessage(MessageHandler.noCommand);
            return true;
        }
        //why ppl tryina put too many words tho
        if(args.length > 1){
            user.sendMessage(MessageHandler.noCommand);
            return true;
        }
        return true;
    }
    //Toggle
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
        VillagerHandler.villagerCheck.put(p.getUniqueId(), false);
        return false;
    }
}
