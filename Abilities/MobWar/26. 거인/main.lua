function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	
	plugin.addPassiveScript(abilityData, 2147483646, function(p)
		p:getAttribute(attribute.GENERIC_MAX_HEALTH):setBaseValue(40)
	end)
end