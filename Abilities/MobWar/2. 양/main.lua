function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	
	plugin.registerEvent(abilityData, "PlayerItemConsumeEvent", 0, function(a, e)
		if e:getItem():getType():toString() == "COOKED_MUTTON" or e:getItem():getType():toString() == "MUTTON" then
			if game.checkCooldown(e:getPlayer(), a, 0) then
				e:getPlayer():addPotionEffect(newInstance("$.potion.PotionEffect", {effect.BLINDNESS, 100, 0}))
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "PlayerInteractEvent", 400, function(a, e)
		if e:getAction():toString() == "RIGHT_CLICK_AIR" or e:getAction():toString() == "RIGHT_CLICK_BLOCK" then
			if e:getItem() ~= nil then
				if game.isAbilityItem(e:getItem(), "GRASS") then
					if game.checkCooldown(e:getPlayer(), a, 1) then
						e:setCancelled(true)
						e:getPlayer():setFoodLevel(e:getPlayer():getFoodLevel() + 4)
						if (e:getPlayer():getFoodLevel() >= 20) then 
							e:getPlayer():addPotionEffect(newInstance("$.potion.PotionEffect", {effect.REGENERATION, 100, 2}))
						else 
							e:getPlayer():addPotionEffect(newInstance("$.potion.PotionEffect", {effect.REGENERATION, 100, 1}))
						end
						local itemStack = { newInstance("$.inventory.ItemStack", {e:getMaterial(), 1}) }
						e:getPlayer():getInventory():removeItem(itemStack)
						e:getPlayer():getWorld():spawnParticle(import("$.Particle").ITEM_CRACK, e:getPlayer():getLocation():add(0,1,0), 100, 0.5, 1, 0.5, 0.05, newInstance("$.inventory.ItemStack", {import("$.Material").KELP}))
						e:getPlayer():getWorld():playSound(e:getPlayer():getLocation(), import("$.Sound").ENTITY_SHEEP_AMBIENT, 0.25, 1)
					end
				end
			end
		end
	end)
end