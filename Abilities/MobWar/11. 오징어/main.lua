function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	
	plugin.registerEvent(abilityData, "EntityDamageByEntityEvent", 100, function(a, e)
		if e:getDamager():getType():toString() == "PLAYER" and e:getEntity():getType():toString() == "PLAYER" then
			if game.checkCooldown(e:getEntity(), a, 0) then
				e:getDamager():addPotionEffect(newInstance("$.potion.PotionEffect", {effect.BLINDNESS, 200, 0}))
			end
		end
	end)
end