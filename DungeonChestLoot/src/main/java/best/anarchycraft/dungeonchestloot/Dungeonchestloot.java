package best.anarchycraft.dungeonchestloot;
import best.anarchycraft.dungeonchestloot.commands.RemDChestLoc;
import best.anarchycraft.dungeonchestloot.commands.RollTheDice;
import best.anarchycraft.dungeonchestloot.commands.SetDChestLoc;
import best.anarchycraft.dungeonchestloot.eventlisteners.onLeftClick;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;

public final class Dungeonchestloot extends JavaPlugin implements Listener {

    public static String PREFIX = "§aDCL: ";

    public static Dungeonchestloot INSTANCE;

    public Dungeonchestloot(){
        INSTANCE = this;
    }

    @Override
    public void onEnable() {

        printC("§3--Started--");
        this.registerCommands();
        this.saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    //------------------------------------------------------------------------------------------------------

    private void registerCommands(){

        Objects.requireNonNull(Bukkit.getPluginCommand("setDChestLoc")).setExecutor(new SetDChestLoc());
        Objects.requireNonNull(Bukkit.getPluginCommand("remDChestLoc")).setExecutor(new RemDChestLoc());
        Objects.requireNonNull(Bukkit.getPluginCommand("roll")).setExecutor(new RollTheDice());

    }
    public void printC(String text){
        Bukkit.getConsoleSender().sendMessage(PREFIX + text);
    }
    public void enableListener(Player player, int mode, String type, String lvl){getServer().getPluginManager().registerEvents(new onLeftClick(player,mode, type, lvl), this);}
    public void disableListener(){HandlerList.unregisterAll();
    }



//----------------------------------------------------------------------------------------------------------------

    public int[] createRandomIntArrayNoDuplicates(Integer length){

        int[] ranArray = new int[length];

        for (int i = 0; i < length; i++){
            if (i==0){
                ranArray[i] = new Random().nextInt(length);
            }
            else{
                ranArray[i] = new Random().nextInt(length);
                for (int j = 0; j < i; j++){
                    if (ranArray[i]==ranArray[j]){
                        i = i-1;
                        break;
                    }
                }
            }
        }
        return ranArray;
    }

//---------------------------------------------------------------------------------------------------------------

    public void placeChest(String type, String lvl){

        int chestnumberall = getchestnum(type, lvl);
        int chestnumberallowed = this.getConfig().getInt("chests."+ type + "." + lvl+ ".numallw");
        int[] temp = createRandomIntArrayNoDuplicates(getchestnum(type, lvl));
        Location[] loc = new Location[getchestnum(type, lvl)];
        List<List<Integer>> coord_list = configLoctoList(type, lvl);

        for(int i=0; i<getchestnum(type, lvl); i++){
            assert coord_list != null;
            loc[i]=new Location(Bukkit.getWorld("world"),coord_list.get(i).get(0),coord_list.get(i).get(1),coord_list.get(i).get(2));
        }

        for (int i = 0; i < chestnumberall; i++){
            loc[i].getBlock().setType(Material.AIR);
        }
        for (int i = 0; i < chestnumberallowed; i++){
            loc[temp[i]].getBlock().setType(Material.CHEST);
            putLoot(loc[temp[i]], type, lvl);
        }
    }

    public void putLoot(Location loc, String type, String lvl){

        int amount;
        double p;
        int[] temp = createRandomIntArrayNoDuplicates(27);
        Inventory inventory = ((Chest) loc.getBlock().getState()).getInventory();
        List<String> configItems = this.getConfig().getStringList("loottable."+type+"."+lvl+".item");
        List<Double> configAmount = this.getConfig().getDoubleList("loottable."+type+"."+lvl+".amount");
        int itemnum = configItems.size();

        for (int i = 0; i < itemnum; i++) {

            amount = 0;
            p = configAmount.get(i) / ((float)64 * getConfig().getInt("chests."+ type + "." + lvl+ ".numallw"));
            for (int l = 0; l < 64; l++){
                if ( p > new Random().nextDouble()){
                    amount = amount +1;
                }
                if (amount > 64) amount = 64;
            }

            ItemStack itemStack = new ItemStack(Objects.requireNonNull(Material.getMaterial((configItems.get(i)).toUpperCase())), amount);
            ItemMeta itemMeta = getEnchants(itemStack);
            itemStack.setItemMeta(itemMeta);
            inventory.setItem(temp[i], itemStack);
        }
    }

    // This function is called everytime an item is about to be placed in a chest and changes the ItemMeta according to the config

    public ItemMeta getEnchants (ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        int num = this.getConfig().getInt("loottable.enchants.number");
        List<String> configItem = this.getConfig().getStringList("loottable.enchants.item");
        List<String> configDisplayname = this.getConfig().getStringList("loottable.enchants.displayname");
        List<String> configNcolor = this.getConfig().getStringList("loottable.enchants.ncolor");
        List<String> configLore = this.getConfig().getStringList("loottable.enchants.lore");
        List<String> configLore2 = this.getConfig().getStringList("loottable.enchants.lore2");
        List<String> configLcolor = this.getConfig().getStringList("loottable.enchants.lcolor");
        List<String> configLcolor2 = this.getConfig().getStringList("loottable.enchants.lcolor2");
        List<String> configEnchant = this.getConfig().getStringList("loottable.enchants.enchant");
        List<Integer> configLvl = this.getConfig().getIntegerList("loottable.enchants.lvl");

        for (int i = 0; i < num; i++) {

            if (itemStack.getType() == Material.getMaterial(configItem.get(i).toUpperCase())){

                NamespacedKey key = NamespacedKey.minecraft(configEnchant.get(i));
                Enchantment enchant = Enchantment.getByKey(key);
                assert enchant != null;

                itemMeta.addEnchant(enchant, configLvl.get(i), true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.valueOf(configLcolor.get(i)) + configLore.get(i));

                // This is just a temporary fix for the config mess rn
                if (!configLore2.get(i).equals("0")){
                    if (configLore2.get(i).equals("Opens Legendary Chest")){
                        lore.add(""+ChatColor.valueOf(configLcolor2.get(i))+ ChatColor.ITALIC + configLore2.get(i));
                    }
                    else {
                        lore.add(ChatColor.valueOf(configLcolor2.get(i)) + configLore2.get(i));
                    }
                }
                itemMeta.setLore(lore);

                // This is just a temporary fix for the config mess rn
                if (configDisplayname.get(i).equals("Legendary Key")){
                    itemMeta.setDisplayName("" +ChatColor.valueOf(configNcolor.get(i))+ ChatColor.BOLD + configDisplayname.get(i));
                }
                else{
                    itemMeta.setDisplayName(ChatColor.valueOf(configNcolor.get(i)) + configDisplayname.get(i));
                }
            }
        }
        return itemMeta;
    }

//------------------------------------------------------------------------------------------------------------------

    // Everything below are helper functions for config  access

    public int getchestnum(String type, String lvl){
        return this.getConfig().getInt("chests."+ type + "." + lvl+ ".numall");
    }

    public boolean incrementchestnum( String type, String lvl){
        int chest_num=this.getchestnum(type, lvl);
        this.getConfig().set("chests."+ type + "." + lvl+ ".numall", chest_num+1);
        this.saveConfig();
        return true;
    }

    public boolean decrementchestnum( String type, String lvl){
        int num=this.getchestnum(type, lvl);
        if (num == 0) return false;
        this.getConfig().set("chests."+ type + "." + lvl+ ".numall", num-1);
        this.saveConfig();
        return true;
    }

    public void setChestLoc(List<Integer> coord, String type, String lvl){
        String chest_num=Integer.toString(getchestnum(type, lvl)+1);
        this.getConfig().set("chests."+ type + "." + lvl+ ".location"+chest_num,coord);
        incrementchestnum(type, lvl);
        this.saveConfig();
    }

    public List<Integer> getChestLocation(int index, String type, String lvl){
        String chest_num=Integer.toString(index);
        return this.getConfig().getIntegerList("chests."+ type + "." + lvl+ ".location"+chest_num);
    }

    void removeCoordsForIndex(int index, String type, String lvl){
        for (int i=index; i<getchestnum(type, lvl); i++){
            this.getConfig().set("chests."+ type + "." + lvl+ ".location"+i,getChestLocation(i+1,type, lvl));
        }
        this.getConfig().set("chests."+ type + "." + lvl+ ".location"+getchestnum(type, lvl),null);
        this.decrementchestnum(type, lvl);
        this.saveConfig();
    }

    public boolean  remChestLoc(List<Integer> coord, String type, String lvl){
        int searched_index=0;
    //first find the index of the coords
        if (getchestnum(type, lvl) == 0) return false;

        for(int i=1; i<getchestnum(type, lvl)+1; i++){
            //check if the coords of an index match the searched coords
            List<Integer> check_coords= this.getConfig().getIntegerList("chests."+ type + "." + lvl+ ".location"+i);
            if(check_coords.get(0)== coord.get(0) && check_coords.get(1)== coord.get(1) && check_coords.get(2)== coord.get(2)){
                searched_index=i;
                break;
            }
            else if( i == getchestnum(type, lvl)){
                //coords not found
                return false;
            }
    }

    removeCoordsForIndex(searched_index,type, lvl);
    return true;
    }

    //gives back all the coordinates as a list
    public List<List<Integer>> configLoctoList(String type, String lvl){
        List<ArrayList<Integer>> coord_list = new ArrayList<>();
        if (getchestnum(type, lvl)==0) return null;
        for(int i=1; i<=getchestnum(type, lvl); i++){
            coord_list.add( (ArrayList<Integer>) getConfig().getIntegerList("chests."+ type + "." + lvl+ ".location"+i));
        }
        return (List) coord_list;
    }


}
