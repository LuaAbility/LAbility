function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	
	plugin.addPassiveScript(abilityData, 1, function(p)
		p:addPotionEffect(newInstance("$.potion.PotionEffect", {effect.JUMP, 20, 1}))
		p:addPotionEffect(newInstance("$.potion.PotionEffect", {effect.INCREASE_DAMAGE, 20, 0}))
	end)
end