function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	
	plugin.registerEvent(abilityData, "EntityDamageByEntityEvent", 0, function(a, e)
		if (e:getCause():toString() == "PROJECTILE" or e:getCause():toString() == "ENTITY_ATTACK") and e:getEntity():getType():toString() == "PLAYER" then
			local damager = e:getDamager()
			if e:getCause():toString() == "PROJECTILE" then damager = e:getDamager():getShooter() end
			
			if game.checkCooldown(e:getEntity(), a, 0) then
				e:getEntity():addPotionEffect(newInstance("$.potion.PotionEffect", {effect.SPEED, 600, 1}))
				
				for i = 0, 4 do 
					util.runLater(function() 
						local vibration = newInstance("$.Vibration", { e:getEntity():getLocation(), newInstance("$.Vibration$Destination$EntityDestination", {damager}), 20})
						e:getEntity():getWorld():spawnParticle(import("$.Particle").VIBRATION, e:getEntity():getEyeLocation(), 1, 0.5, 1, 0.5, 1, vibration)
						e:getEntity():getWorld():playSound(e:getEntity():getLocation(), import("$.Sound").BLOCK_SCULK_SENSOR_CLICKING, 1, 0.3)
					end, i * 20)
				end
			end
		end
	end)
	
	plugin.addPassiveScript(abilityData, 1, function(p)
		p:addPotionEffect(newInstance("$.potion.PotionEffect", {effect.BLINDNESS, 40, 0}))
		p:addPotionEffect(newInstance("$.potion.PotionEffect", {effect.INCREASE_DAMAGE, 20, 0}))
	end)
end