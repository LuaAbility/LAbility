function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	
	plugin.registerEvent(abilityData, "EntityDamageByEntityEvent", 0, function(a, e)
		if (e:getCause():toString() == "PROJECTILE" or e:getCause():toString() == "ENTITY_ATTACK") and e:getEntity():getType():toString() == "PLAYER" then
			if game.checkCooldown(e:getEntity(), a, 0) then
				e:getEntity():addPotionEffect(newInstance("$.potion.PotionEffect", {effect.SPEED, 600, 1}))
			end
		end
	end)
	
	plugin.addPassiveScript(abilityData, 1, function(p)
		p:addPotionEffect(newInstance("$.potion.PotionEffect", {effect.BLINDNESS, 40, 0}))
		p:addPotionEffect(newInstance("$.potion.PotionEffect", {effect.INCREASE_DAMAGE, 20, 0}))
	end)
end