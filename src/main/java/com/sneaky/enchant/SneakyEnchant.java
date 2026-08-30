package com.sneaky.enchant;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SneakyEnchant implements ModInitializer {
    private final Map<ServerPlayerEntity, Boolean> sneakStates = new HashMap<>();

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                boolean wasSneaking = sneakStates.getOrDefault(player, false);
                boolean isSneaking = player.isSneaking();

                if (isSneaking && !wasSneaking) {
                    enchantInventory(player);
                }
                sneakStates.put(player, isSneaking);
            }
        });
    }

    private void enchantInventory(ServerPlayerEntity player) {
        List<Enchantment> enchants = new ArrayList<>();
        for (Enchantment e : Registry.ENCHANTMENT) {
            enchants.add(e);
        }

        if (enchants.isEmpty()) return;

        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isEmpty()) continue;

            Map<Enchantment, Integer> map = new HashMap<>();
            int numEnchants = 1 + player.getRandom().nextInt(3);
            
            for (int j = 0; j < numEnchants; j++) {
                Enchantment ench = enchants.get(player.getRandom().nextInt(enchants.size()));
                int level = 1 + player.getRandom().nextInt(5);
                map.put(ench, level);
            }
            
            EnchantmentHelper.set(map, stack);
        }
        
        player.sendMessage(Text.of("§d§l[MAGIA] §r§fSeus itens foram encantados aleatoriamente!"), false);
    }
}