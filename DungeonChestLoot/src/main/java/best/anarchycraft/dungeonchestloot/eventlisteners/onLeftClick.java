package best.anarchycraft.dungeonchestloot.eventlisteners;
import best.anarchycraft.dungeonchestloot.Dungeonchestloot;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import java.util.ArrayList;
import java.util.List;

public class onLeftClick implements Listener {
    Player sender;
    int mode;
    String type;
    String lvl;
    public onLeftClick(Player sender,int mode, String type, String lvl){
        this.type =type;
        this.lvl = lvl;
        this.sender=sender;
        this.mode=mode; //mode =1 => delete chest, mode = 2 => set chest
    }

    @EventHandler
    public void onLeft(PlayerInteractEvent event){

        Player player =event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.getName().equals(sender.getName())) {

            Block b = event.getClickedBlock();
            assert b != null;

            List<Integer> coords = new ArrayList<>();

            coords.add(b.getX());
            coords.add(b.getY()+1);
            coords.add(b.getZ());

            if (mode==1){

                player.sendMessage("Selected Coords are: X:" + coords.get(0) + " Y:" + coords.get(1) + " Z:" + coords.get(2));
                    if(!Dungeonchestloot.INSTANCE.remChestLoc(coords, type, lvl)){
                        player.sendMessage(""+ChatColor.RED+ ChatColor.ITALIC + "No chest was saved at this location.");
                    }

            }
           else  if(mode == 2) {

                player.sendMessage("Selected Coords are: X:" + coords.get(0) + " Y:" + coords.get(1) + " Z:" + coords.get(2));
                Dungeonchestloot.INSTANCE.setChestLoc(coords, type, lvl);
            }
        }
        Dungeonchestloot.INSTANCE.disableListener();
       }
}
