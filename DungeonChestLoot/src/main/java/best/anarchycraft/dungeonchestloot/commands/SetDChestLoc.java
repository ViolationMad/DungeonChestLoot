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

        int mode = 0;
        String temp1 = "ß", temp2 = "0";

        if(!(sender instanceof Player)){
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED +"This command can only be used in game.");
            return true;
        }

        Player player = (Player) sender;



        if (player.hasPermission("best.anarchycraft.dungeonchestloot.chests")){
            if (checkArgsSyntax(args, player)) {
                if (args[0].equals("set")){
                    mode = 2;
                    temp1 = " Add ";
                    temp2 = "random ";
                }
                if (args[0].equals("rem")){
                    mode = 1;
                    temp1 = " Remove ";
                    temp2 = "random ";
                }
                if (args[0].equals("setf")){
                    mode = 3;
                    temp1 = " Add ";
                    temp2 = "fixed ";
                }
                if (args[0].equals("remf")){
                    mode = 4;
                    temp1 = " Remove ";
                    temp2 = "fixed ";
                }

                player.sendMessage("" + ChatColor.AQUA + ChatColor.ITALIC + "Change to config:" + ChatColor.GREEN + ChatColor.ITALIC + temp1 + temp2 + ChatColor.AQUA + ChatColor.ITALIC + "chest.");
                player.sendMessage("" + ChatColor.BLUE + ChatColor.ITALIC + "Click below the desired coordinates.");
                Dungeonchestloot.INSTANCE.enableListener(player, mode, args[1], args[2]);
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

        if (args.length  < 3){
            player.sendMessage(ChatColor.AQUA+ player.getName()+"!"+ChatColor.RED+ ChatColor.ITALIC+ " Invalid use of arguments.");
            printSyntax(player);
            return false;
        }
        if ( (args[0].equals("set") || args[0].equals("rem") || args[0].equals("setf") || args[0].equals("remf")) && (args[1].equals("serpent") || args[1].equals("glacial") || args[1].equals("inferno") || args[1].equals("twilight")) && (args[2].equals("lvl1") || args[2].equals("lvl2") || args[2].equals("lvl3") || args[2].equals("lvl4") ||args[2].equals("lvl5"))){
            return true;
        }
        else{
            player.sendMessage(ChatColor.AQUA+ player.getName()+"!"+ChatColor.RED+ ChatColor.ITALIC+ " Invalid use of arguments.");
            printSyntax(player);
            return false;
        }
    }

    public void printSyntax(Player player){
        player.sendMessage(ChatColor.RED+ "DCL:"+ChatColor.GRAY+ ChatColor.ITALIC+ " Syntax: /DCL [mode: (set, rem, setf, remf)][type: (serpent, glacial, inferno, twilight)] [level: (lvl1-lvl5])");
    }
}
