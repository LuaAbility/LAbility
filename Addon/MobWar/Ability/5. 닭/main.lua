function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
		
	plugin.registerEvent(abilityData, "PlayerItemConsumeEvent", 0, function(a, e)
		if e:getItem():getType():toString() == "COOKED_CHICKEN" or e:getItem():getType():toString() == "CHICKEN" then
			if game.checkCooldown(e:getPlayer(), a, 0) then
				e:getPlayer():addPotionEffect(newInstance("$.potion.PotionEffect", {effect.BLINDNESS, 100, 0}))
			end
		end
	end) 
	
	plugin.registerEvent(abilityData, "EntityDamageEvent", 0, function(a, e)
		if e:getCause():toString() == "FALL" and e:getEntity():getType():toString() == "PLAYER" then
			if game.checkCooldown(e:getEntity(), a, 1) then
				e:setCancelled(true)
				e:getEntity():getWorld():spawnParticle(import("$.Particle").ITEM_CRACK, e:getEntity():getLocation():add(0,1,0), 50, 0.5, 1, 0.5, 0.05, newInstance("$.inventory.ItemStack", {import("$.Material").FEATHER}))
				e:getEntity():getWorld():playSound(e:getEntity():getLocation(), import("$.Sound").ENTITY_CHICKEN_AMBIENT, 0.25, 1)
			end
		end
	end)
end