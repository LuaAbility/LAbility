function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	
	plugin.addPassiveScript(abilityData, 1, function(p)
		p:addPotionEffect(newInstance("$.potion.PotionEffect", {effect.JUMP, 20, 1}))
	end)
	
	plugin.registerEvent(abilityData, "EntityDamageByEntityEvent", 0, function(a, e)
		local damagee = e:getEntity()
		local damager = e:getDamager()
		if e:getCause():toString() == "PROJECTILE" then damager = e:getDamager():getShooter() end
		
		if damager:getType():toString() == "PLAYER" and damagee:getType():toString() == "PLAYER" then
			if math.random(100) <= 10 then
				if game.checkCooldown(damagee, a, 0) then
					game.changeAbility(damagee, a, "LA-MW-007-HIDDEN", false)
				end
			end
		end
	end)
end