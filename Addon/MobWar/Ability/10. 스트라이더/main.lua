function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	
	plugin.addPassiveScript(abilityData, 20, function(p)
		p:addPotionEffect(newInstance("$.potion.PotionEffect", {effect.FIRE_RESISTANCE, 30, 0}))
		if p:getLocation():getBlock():getType():toString() == "WATER" or p:getLocation():getBlock():getType():toString() == "WATER_CAULDRON" then
			p:damage(1)
			p:getWorld():spawnParticle(import("$.Particle").SMOKE_NORMAL, p:getLocation():add(0,1,0), 50, 0.5, 1, 0.5, 0.05)
			p:getWorld():playSound(p:getLocation(), import("$.Sound").ENTITY_STRIDER_HURT, 0.25, 1)
		end
	end)
end