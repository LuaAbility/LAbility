function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	
	plugin.addPassiveScript(abilityData, 1, function(p)
		p:addPotionEffect(newInstance("$.potion.PotionEffect", {effect.DOLPHINS_GRACE, 20, 0}))
		p:addPotionEffect(newInstance("$.potion.PotionEffect", {effect.SLOW, 20, 2}))
		p:addPotionEffect(newInstance("$.potion.PotionEffect", {effect.WATER_BREATHING, 20, 0}))
	end)
end