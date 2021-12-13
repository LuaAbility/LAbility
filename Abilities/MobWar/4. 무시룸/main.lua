function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
		
	plugin.registerEvent(abilityData, "PlayerItemConsumeEvent", 0, function(a, e)
		if e:getItem():getType():toString() == "COOKED_BEEF" or e:getItem():getType():toString() == "BEEF" or e:getItem():getType():toString() == "MUSHROOM_STEW" then
			if game.checkCooldown(e:getPlayer(), a, 0) then
				e:getPlayer():addPotionEffect(newInstance("$.potion.PotionEffect", {effect.BLINDNESS, 100, 0}))
			end
		end
	end) 
	
	plugin.registerEvent(abilityData, "PlayerInteractEvent", 0, function(a, e)
		if e:getAction():toString() == "RIGHT_CLICK_AIR" or e:getAction():toString() == "RIGHT_CLICK_BLOCK" then
			if e:getItem() ~= nil then
				if game.isAbilityItem(e:getItem(), "SHEARS") then
					if game.checkCooldown(e:getPlayer(), a, 1) then
						game.changeAbility(e:getPlayer(), a, "LA-MW-003", false)
						e:getPlayer():getWorld():spawnParticle(import("$.Particle").ITEM_CRACK, e:getPlayer():getLocation():add(0,1,0), 100, 0.5, 1, 0.5, 0.05, newInstance("$.inventory.ItemStack", {import("$.Material").RED_MUSHROOM}))
						e:getPlayer():getWorld():playSound(e:getPlayer():getLocation(), import("$.Sound").ENTITY_MOOSHROOM_SHEAR, 0.25, 1)
						e:getPlayer():getWorld():playSound(e:getPlayer():getLocation(), import("$.Sound").ENTITY_COW_AMBIENT, 0.25, 1)
					end
				end
			end
		end
	end)
end