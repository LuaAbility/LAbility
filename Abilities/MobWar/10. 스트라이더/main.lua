function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	
	plugin.addPassiveScript(abilityData, 20, function(p)
		p:addPotionEffect(newInstance("$.potion.PotionEffect", {effect.FIRE_RESISTANCE, 30, 0}))
		if p:getLocation():getBlock():getType():toString() == "WATER" or p:getLocation():getBlock():getType():toString() == "WATER_CAULDRON" then
			p:damage(1)
		end
	end)
end