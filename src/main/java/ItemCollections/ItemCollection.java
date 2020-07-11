package ItemCollections;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public class ItemCollection implements Cloneable, ConfigurationSerializable {
	
	private String name;
	private ArrayList<ItemStack> requiredItems;
	private ItemStack resultItem;
	
	public ItemCollection(String name) {
		this.name = name;
		this.requiredItems = new ArrayList<ItemStack>();
		this.resultItem = new ItemStack(Material.STONE, 1);
	}
	
	public String getName() {
		return name;
	}
	
	public void addItem(ItemStack item) {
		requiredItems.add(item);
	}
	
	//returns true if the item was there
	public boolean removeItem(ItemStack item) {
		return requiredItems.remove(item);		
	}
	
	public ArrayList<ItemStack> getRequiredItems() {
		return requiredItems;
	}
	
	public void setResult(ItemStack item) {
		resultItem = item;
	}
	
	public ItemStack getResult() {
		return resultItem;
	}

	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
		result.put("name", this.getName());
		result.put("result", this.getResult());
		result.put("required", this.getRequiredItems());
		
		return result;
	}
	
	public static ItemCollection deserialize(Map<String, Object> args) {
		String name = "unnamed";
		ArrayList<ItemStack> reqItems = new ArrayList<ItemStack>();
		ItemStack result = new ItemStack(Material.STONE, 1);
		
		if (args.containsKey("name"))
			name = (String) args.get("name");
		
		if (args.containsKey("result"))
			result = (ItemStack) args.get("result");
		
		if (args.containsKey("required"))
			reqItems = (ArrayList<ItemStack>) args.get("required");
		
		ItemCollection returnList = new ItemCollection(name);
		returnList.setResult(result);
		
		for (int i = 0; i < reqItems.size(); i++) 
			returnList.addItem(reqItems.get(i));
		
		return returnList;	
	}
	
}
