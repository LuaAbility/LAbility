function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	local redrum = false
	
	plugin.addPassiveScript(abilityData, 1, function(p)
		p:addPotionEffect(newInstance("$.potion.PotionEffect", {effect.JUMP, 20, 1}))
		if redrum then p:addPotionEffect(newInstance("$.potion.PotionEffect", {effect.INCREASE_DAMAGE, 20, 0})) end
	end)
	
	plugin.addPassiveScript(abilityData, 600, function(p)
		if redrum then p:addPotionEffect(newInstance("$.potion.PotionEffect", {effect.CONFUSION, 300, 0})) end
	end)
	
	plugin.registerEvent(abilityData, "EntityDamageByEntityEvent", 0, function(a, e)
		local damagee = e:getEntity()
		local damager = e:getDamager()
		if e:getCause():toString() == "PROJECTILE" then damager = e:getDamager():getShooter() end
		
		if damager:getType():toString() == "PLAYER" and damagee:getType():toString() == "PLAYER" then
			if math.random(100) <= 10 and not redrum then
				if game.checkCooldown(damagee, a, 0) then
					redrum = true
					game.sendMessage(damagee, "§c살인 ON")
				end
			end
		end
	end)
end