package com.helloitsmeadm.itemshow;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Objects;

public class ChatListener implements Listener {
    final ItemShow itemShow;

    public ChatListener(ItemShow itemShow) {
        this.itemShow = itemShow;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        // Set the format just for testing
        event.setFormat("§d§lDeveloper§r §fHelloItsMeAdm §8» §d" + event.getMessage());

        // Get the placeholder from the config
        String placeholder = itemShow.getConfig().getString("placeholder");

        // Check if the message contains [item]
        if (!event.getMessage().contains(placeholder)) {
            return;
        }

        // Cancel the event
        event.setCancelled(true);

        // Create a new ItemStack from the item in the player's main hand
        ItemStack item = Objects.requireNonNull(event.getPlayer().getEquipment()).getItemInMainHand();

        // Check if player is holding something
        if (item.getType().equals(Material.AIR)) {
            event.getPlayer().sendMessage(itemShow.getConfig().getString("prefix") + itemShow.getConfig().getString("error_not_holding_item"));
            return;
            // Check if the message contains only one placeholder
        } else if (event.getMessage().indexOf(placeholder) != event.getMessage().lastIndexOf(placeholder)) {
            event.getPlayer().sendMessage(itemShow.getConfig().getString("prefix") + itemShow.getConfig().getString("error_more_than_one"));
            return;
        }

        // Get the item name
        String itemName;
        if (Objects.requireNonNull(item.getItemMeta()).hasDisplayName()) {
            itemName = item.getItemMeta().getDisplayName();
        } else {
            itemName = item.getType().name().replace("_", " ");
            itemName = itemName.substring(0, 1).toUpperCase() + itemName.substring(1).toLowerCase();
        }

        // Split message on placeholder "[item]"
        int splitIndex = event.getFormat().indexOf(placeholder);
        String[] message = new String[2];

        if (splitIndex != -1) {
            message[0] = event.getFormat().substring(0, splitIndex);
            message[1] = event.getFormat().substring(splitIndex + placeholder.length());
        }

        // Create a new TextComponent
        TextComponent mainMessage = new TextComponent(message[0]);
        String itemComponentText;
        if (item.getAmount() > 1) {
            itemComponentText = itemShow.getConfig().getString("formatted_placeholder_multiple").replace("item", itemName).replace("%count%", String.valueOf(item.getAmount()));
        } else {
            itemComponentText = itemShow.getConfig().getString("formatted_placeholder").replace("item", itemName);
        }
        TextComponent itemComponent = new TextComponent(itemComponentText);
        TextComponent endMessage = new TextComponent("§d" + message[1]);

        // Create text for hover message
        StringBuilder finalHoverText = new StringBuilder("§f" + itemName);

        // Check if the item has enchantments
        if (item.getItemMeta().hasEnchants()) {
            finalHoverText.append("\n");
            Map<Enchantment, Integer> enchants = item.getItemMeta().getEnchants();
            for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                String enchantName = entry.getKey().getKey().getKey().replace("_", " ");
                enchantName = enchantName.substring(0, 1).toUpperCase() + enchantName.substring(1).toLowerCase();
                finalHoverText.append("§7").append(enchantName).append(" ").append(RomanNumeralConverter.convertToRoman(entry.getValue())).append("\n");
            }
        }

        // Check if the item has lore
        if (item.getItemMeta().hasLore()) {
            for (String lore : Objects.requireNonNull(item.getItemMeta().getLore())) {
                finalHoverText.append("§7").append(lore).append("\n");
            }
            // Remove the last new line
            finalHoverText.deleteCharAt(finalHoverText.length() - 1);
        }

        // If the item has some damage
        if (item.getType().getMaxDurability() != 0) {
            finalHoverText.append("\n\n").append("§fDurability: ").append(item.getType().getMaxDurability() - item.getDurability()).append(" / ").append(item.getType().getMaxDurability());
        }

        // Add footer
        finalHoverText.append("\n").append("§8minecraft:").append(item.getType().name().toLowerCase());

        // Set the hover event including name, lore and enchanants
        itemComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(String.valueOf(finalHoverText)).create()));

        // Add the components to the main message
        mainMessage.addExtra(itemComponent);
        mainMessage.addExtra(endMessage);

        // Send the message to all players
        Bukkit.getOnlinePlayers().forEach(player -> player.spigot().sendMessage(mainMessage));

        //TODO: Two and more items
    }
}
