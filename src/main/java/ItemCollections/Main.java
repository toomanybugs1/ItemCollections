package ItemCollections;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
	
	static {
		ConfigurationSerialization.registerClass(ItemCollection.class, "ItemCollection");
	}
	
	ArrayList<ItemCollection> itemCollections = new ArrayList<ItemCollection>();
	
	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		itemCollections = (ArrayList<ItemCollection>) this.getConfig().getList("collections");
		if (itemCollections == null) {
			itemCollections = new ArrayList<ItemCollection>();
			getLogger().info("Collection list is null.");
		}
	}
	
	@Override
	public void onDisable() {
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("ic")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("This can only be run by a player.");
				return true;
			}
			Player player = (Player) sender;
			
			if (args.length == 0) {
			
				player.sendMessage("§6[ItemCollections] §2Your commands:");
				if (player.hasPermission("ic.create"))
					player.sendMessage("§2/ic create <collection>");
				if (player.hasPermission("ic.delete"))
					player.sendMessage("§2/ic remove <collection>");
				if (player.hasPermission("ic.edit")) {
					player.sendMessage("§2/ic additem <collection>");
					player.sendMessage("§2/ic setreward <collection>");
					player.sendMessage("§2/ic removeitem <collection>");
				}
				if (player.hasPermission("ic.listitems")) 
					player.sendMessage("§2/ic list <collection>");
				if (player.hasPermission("ic.listcollections"))
					player.sendMessage("§2/ic collections");
				if (!player.hasPermission("ic.collection"))
					player.sendMessage("§2/ic collection <collection>");
				
				return true;
			}
			
			//get latest config
			itemCollections = (ArrayList<ItemCollection>) this.getConfig().getList("collections");
			
			//CREATE
			if (args[0].equals("create")) {
				if (args.length != 2) {
					sender.sendMessage("§cUsage: /ic create <name>");
					return true;
				}
				
				if (!player.hasPermission("ic.create")) {
					sender.sendMessage("§cYou do not have permission to do this.");
					return true;
				}
				
				if (checkNames(args[1])) {
					player.sendMessage("§cCollection with this name already exists.");
					return true;
				} else {
					//create empty collection and add it
					ItemCollection newCollection = new ItemCollection(args[1]);
					itemCollections.add(newCollection);
					this.getConfig().set("collections", itemCollections);
					this.saveConfig();
					player.sendMessage("§2Created collection §6" + args[1] +"§2!");
					return true;
				}
				
			} 
			//REMOVE
			else if (args[0].equals("remove")) {
				if (args.length != 2) {
					sender.sendMessage("§cUsage: /ic remove <name>");
					return true;
				}
				
				if (!player.hasPermission("ic.delete")) {
					sender.sendMessage("§cYou do not have permission to do this.");
					return true;
				}
				
				int colIndex = findByName(args[1]);
				if (colIndex == -1) {
					player.sendMessage("§cCouldn't find that collection.");
					return true;
				}
				
				itemCollections.remove(colIndex);
				this.getConfig().set("collections", itemCollections);
				this.saveConfig();
				player.sendMessage("§2Removed collection §6" + args[1] +"§2!");
				return true;
			}
			//ADDITEM
			else if (args[0].equals("additem")) {
				if (args.length != 2) {
					sender.sendMessage("§cUsage: /ic additem <name>");
					return true;
				}
				
				if (!player.hasPermission("ic.edit")) {
					sender.sendMessage("§cYou do not have permission to do this.");
					return true;
				}
				
				int colIndex = findByName(args[1]);
				if (colIndex == -1) {
					player.sendMessage("§cCouldn't find that collection.");
					return true;
				}
				
				ItemStack newItem = player.getInventory().getItemInMainHand();
				
				if (newItem == null || newItem.getType() == Material.AIR) {
					player.sendMessage("§cNo item in hand.");
					return true;
				}
				
				itemCollections.get(colIndex).addItem(newItem);
				
				this.getConfig().set("collections", itemCollections);
				this.saveConfig();
				player.sendMessage("§2Added §6" + newItem.getType().toString() +"§2 to collection §6" + args[1] + "§2!");
				return true;
				
			} 
			//SET REWARD
			else if (args[0].equals("setreward")) {
				if (args.length != 2) {
					sender.sendMessage("§cUsage: /ic setreward <name>");
					return true;
				}
				
				if (!player.hasPermission("ic.edit")) {
					sender.sendMessage("§cYou do not have permission to do this.");
					return true;
				}
				
				int colIndex = findByName(args[1]);
				if (colIndex == -1) {
					player.sendMessage("§cCouldn't find that collection.");
					return true;
				}
				
				ItemStack newItem = player.getInventory().getItemInMainHand();
				
				if (newItem == null || newItem.getType() == Material.AIR) {
					player.sendMessage("§cNo item in hand.");
					return true;
				}
				
				itemCollections.get(colIndex).setResult(newItem);
				
				this.getConfig().set("collections", itemCollections);
				this.saveConfig();
				player.sendMessage("§2Set §6" + newItem.getType().toString() +"§2 to collection §6" + args[1] + "§2 as the reward!");
				return true;
				
			}
			//LIST ITEMS
			else if (args[0].equals("list")) {
				if (args.length != 2) {
					sender.sendMessage("§cUsage: /ic list <name>");
					return true;
				}
				
				if (!player.hasPermission("ic.listitems")) {
					sender.sendMessage("§cYou do not have permission to do this.");
					return true;
				}
				
				int colIndex = findByName(args[1]);
				if (colIndex == -1) {
					player.sendMessage("§cCouldn't find that collection.");
					return true;
				}
				
				ItemCollection collection = itemCollections.get(colIndex);
				player.sendMessage("§2Items in collection §6" + collection.getName() + "§2:");
				for (int i = 0; i < collection.getRequiredItems().size(); i++) {
					player.sendMessage("§6" + i + ": §2" + collection.getRequiredItems().get(i));
				}
				return true;
				
			} 
			//REMOVE ITEM
			else if (args[0].equals("removeitem")) {
				
				if (args.length != 3) {
					sender.sendMessage("§cUsage: /ic list <name> <index>");
					return true;
				}
				
				if (!player.hasPermission("ic.edit")) {
					sender.sendMessage("§cYou do not have permission to do this.");
					return true;
				}
				
				int colIndex = findByName(args[1]);
				if (colIndex == -1) {
					player.sendMessage("§cCouldn't find that collection.");
					return true;
				}
				
				ItemCollection collection = itemCollections.get(colIndex);
				int removeIndex = 0;
				try {
					removeIndex = Integer.parseInt(args[2]);
				}
				catch (NumberFormatException e) {
					player.sendMessage("§cIndex must be a number. Use /ic list <name> to see the index of the item to remove.");
					return true;
				}
					
				if (removeIndex >= collection.getRequiredItems().size() || removeIndex < 0) {
					player.sendMessage("§cIndex not in list. Use /ic list <name> to see the index of the item to remove.");
					return true;
				}
				
				ItemStack removedItem = collection.getRequiredItems().remove(removeIndex);
				this.getConfig().set("collections", itemCollections);
				this.saveConfig();
				player.sendMessage("§2Removed §6" + removedItem.toString() +"§2 from collection " + args[1] + "!");
				
				return true;
				
			} 
			//LIST COLLECTIONS
			else if (args[0].equals("collections")) {
				if (args.length != 1) {
					sender.sendMessage("§cUsage: /ic collections");
					return true;
				}
				
				if (!player.hasPermission("ic.listcollections")) {
					sender.sendMessage("§cYou do not have permission to do this.");
					return true;
				}
				
				sender.sendMessage("§2Collections:");
				for (int i = 0; i < itemCollections.size(); i++) {
					sender.sendMessage("§6" + itemCollections.get(i).getName());
				}				
				return true;
			}
			//Cash in collection
			else if (args[0].equals("collection")) {
				if (args.length != 2) {
					sender.sendMessage("§cUsage: /ic collection <collection>");
					return true;
				}
				
				if (!player.hasPermission("ic.collection")) {
					sender.sendMessage("§cYou do not have permission to do this.");
					return true;
				}
				
				int colIndex = findByName(args[1]);
				if (colIndex == -1) {
					player.sendMessage("§cCouldn't find that collection.");
					return true;
				}
				ItemCollection collection = itemCollections.get(colIndex);
				ArrayList<ItemStack> colItems = collection.getRequiredItems();
				ArrayList<Integer> itemIndexes = new ArrayList<Integer>();
				
				for (int i = 0; i < colItems.size(); i++) {
					boolean foundItem = false;
					//check if all the items are there
					for(int j = 0; j < player.getInventory().getSize(); j++){
						  ItemStack itm = player.getInventory().getItem(j);
						  
						  if(itm != null && itm.equals(colItems.get(i))){
							  itemIndexes.add(j);
							  foundItem = true;
							  break;
						  }
					}
					if (!foundItem) {
						player.sendMessage("§cYou don't have the items to complete the collection.");
						return true;
					}
				}
				
				//remove the items
				for(int j = 0; j < itemIndexes.size(); j++){
				    player.getInventory().setItem(itemIndexes.get(j), null);
				    //update the player's inventory
				    player.updateInventory();
				}
				
				player.getInventory().addItem(collection.getResult());
				player.sendMessage("§2You've completed the collection!");
				return true;
			}
			else {
				
				return false;
			}
		}
		
		return false;
	}
	
	//returns true if theres an existing name
	public boolean checkNames(String name) {
		for (int i = 0; i < itemCollections.size(); i++)
			if (itemCollections.get(i).getName().equals(name))
				return true;
		
		return false;
	}
	
	public int findByName(String name) {
		for (int i = 0; i < itemCollections.size(); i++)
			if (itemCollections.get(i).getName().equals(name))
				return i;
		
		return -1;
	}
	
}
