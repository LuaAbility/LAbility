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
		if e:getCause():toString() == "FALL" then
			e:setCancelled(true);
		end
	end)
end