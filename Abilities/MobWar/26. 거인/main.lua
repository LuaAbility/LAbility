function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	
	plugin.addPassiveScript(abilityData, 0, function(p)
		p:getAttribute(attribute.GENERIC_MAX_HEALTH):setBaseValue(40)
	end)
	
	plugin.onPlayerEnd(abilityData, function(p)
		p:getAttribute(attribute.GENERIC_MAX_HEALTH):setBaseValue(p:getAttribute(attribute.GENERIC_MAX_HEALTH):getDefaultValue())
	end)
end