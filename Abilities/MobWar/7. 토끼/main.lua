function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	
	plugin.addPassiveScript(abilityData, 1, function(p)
		p:addPotionEffect(newInstance("$.potion.PotionEffect", {effect.JUMP, 20, 1}))
	end)
	
	plugin.registerEvent(abilityData, "EntityDamageByEntityEvent", 0, function(a, e)
		if e:getDamager():getType():toString() == "PLAYER" and e:getEntity():getType():toString() == "PLAYER" then
			if math.random(100) <= 3 then
				if game.checkCooldown(e:getEntity(), a, 0) then
					game.changeAbility(e:getEntity(), a, "LA-MW-007-HIDDEN", false)
				end
			end
		end
	end)
end