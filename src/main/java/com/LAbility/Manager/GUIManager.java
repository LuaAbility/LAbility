package com.LAbility.Manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class GUIManager implements Listener {
    public abstract static class GUIMethod {
        public void onClick(InventoryClickEvent event) {

        }

        public void onClose(InventoryCloseEvent event) {

        }
    }

    private static HashMap<String, GUIMethod> registeredGUI = new HashMap<>();

    public static Inventory registerGUI(Player player, int size, ItemStack[] itemStacks, String inventoryName, GUIMethod method) {
        if (!registeredGUI.containsKey(inventoryName)) {
            registeredGUI.put(inventoryName, method);

            Inventory inv = Bukkit.createInventory(player, size, inventoryName);
            for (int i = 0; i < itemStacks.length && i < size; i++){
                inv.setItem(i, itemStacks[i]);
            }

            player.openInventory(inv);
            return inv;
        }

        return null;
    }

    @EventHandler
    public static void onInventoryClick(InventoryClickEvent event) {
        Player player = null;
        ItemStack clicked = null;
        InventoryView inventoryView = event.getView();
        Inventory inventory = event.getInventory();

        if (event.getWhoClicked() instanceof Player) player = (Player) event.getWhoClicked();
        if (event.getCurrentItem() != null) clicked = event.getCurrentItem();

        if (player != null && clicked != null) {
            if (registeredGUI.containsKey(inventoryView.getTitle())) {
                registeredGUI.get(inventoryView.getTitle()).onClick(event);
            }
        }
    }

    @EventHandler
    public static void onInventoryClose(InventoryCloseEvent event) {
        Player player = null;
        InventoryView inventoryView = event.getView();
        Inventory inventory = event.getInventory();

        if (event.getPlayer() instanceof Player) player = (Player) event.getPlayer();
        if (player != null) {
            if (registeredGUI.containsKey(inventoryView.getTitle())) {
                registeredGUI.get(inventoryView.getTitle()).onClose(event);
                registeredGUI.remove(inventoryView.getTitle());
            }
        }
    }
}
