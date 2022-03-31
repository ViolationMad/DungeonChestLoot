package best.anarchycraft.dungeonchestloot.commands;
import best.anarchycraft.dungeonchestloot.Dungeonchestloot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetDChestLoc implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player)){
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED +"This command can only be used in game.");
            return true;
        }
        Player player = (Player) sender;

        if (player.hasPermission("best.anarchycraft.dungeonchestloot.chests")){
            if (checkArgsSyntax(args, player)) {
                player.sendMessage("" + ChatColor.AQUA + ChatColor.ITALIC + "Performing this will" + ChatColor.GREEN + ChatColor.ITALIC +" add " + ChatColor.AQUA + ChatColor.ITALIC + "the clicked block to the config.");
                player.sendMessage("" + ChatColor.BLUE + ChatColor.ITALIC + "Click on the Block above which the chest will be placed later.");
                Dungeonchestloot.INSTANCE.enableListener(player, 2, args[0], args[1]);
                return true;
            }
        }
        else{
            player.sendMessage(ChatColor.AQUA+ player.getName()+"!"+ChatColor.RED+ ChatColor.ITALIC+ " You do not have permission to use this command.");
            return true;
        }
        return true;
    }

    public boolean checkArgsSyntax(String[] args, Player player){

        if (args.length == 0 || args.length == 1){
            player.sendMessage(ChatColor.AQUA+ player.getName()+"!"+ChatColor.RED+ ChatColor.ITALIC+ " Invalid use of arguments.");
            player.sendMessage(ChatColor.AQUA+ player.getName()+"!"+ChatColor.GRAY+ ChatColor.ITALIC+ " Syntax: /\"command\" [type: serpent, glacial, inferno, twilight] [level: lvl1-lvl5].");
            return false;
        }
        if ((args[0].equals("serpent") || args[0].equals("glacial") || args[0].equals("inferno") || args[0].equals("twilight")) && (args[1].equals("lvl1") || args[1].equals("lvl2") || args[1].equals("lvl3") || args[1].equals("lvl4") ||args[1].equals("lvl5"))){
            return true;
        }
        else{
            player.sendMessage(ChatColor.AQUA+ player.getName()+"!"+ChatColor.RED+ ChatColor.ITALIC+ " Invalid use of arguments.");
            player.sendMessage(ChatColor.AQUA+ player.getName()+"!"+ChatColor.GRAY+ ChatColor.ITALIC+ " Syntax: /\"command\" [type: serpent, glacial, inferno, twilight] [level: lvl1-lvl5].");
            return false;
        }
    }
}
