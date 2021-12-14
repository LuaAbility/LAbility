function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	
	plugin.registerEvent(abilityData, "EntityDamageByEntityEvent", 1200, function(a, e)
		local damagee = e:getEntity()
		local damager = e:getDamager()
		if e:getCause():toString() == "PROJECTILE" then damager = e:getDamager():getShooter() end
		
		if damager:getType():toString() == "PLAYER" and damagee:getType():toString() == "PLAYER" then
			if game.checkCooldown(e:getEntity(), a, 0) then
				damagee:addPotionEffect(newInstance("$.potion.PotionEffect", {effect.SPEED, 600, 1}))
				damagee:addPotionEffect(newInstance("$.potion.PotionEffect", {effect.INCREASE_DAMAGE, 600, 0}))
				damagee:getWorld():spawnParticle(import("$.Particle").VILLAGER_ANGRY, damagee:getLocation():add(0,1,0), 20, 0.5, 1, 0.5, 0.05)
				damagee:getWorld():playSound(damagee:getLocation(), import("$.Sound").ENTITY_ZOMBIFIED_PIGLIN_ANGRY, 1, 1)
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "EntityTargetLivingEntityEvent", 0, function(a, e)
		if e:getTarget() ~= nil and e:getEntity() ~= nil then
			if e:getTarget():getType():toString() == "PLAYER" and e:getEntity():getType():toString() == "ZOMBIFIED_PIGLIN" then
				if game.checkCooldown(e:getTarget(), a, 1) then
					e:setTarget(nil)
					e:setCancelled(true)
				end
			end
		end
	end)
	
	plugin.addPassiveScript(abilityData, 1, function(p)
		if p:getEquipment():getBoots() ~= nil and
		p:getEquipment():getHelmet() ~= nil and
		p:getEquipment():getLeggings() ~= nil and
		p:getEquipment():getChestplate() ~= nil then
			if string.find(p:getEquipment():getBoots():getType():toString(), "GOLDEN") and
			string.find(p:getEquipment():getHelmet():getType():toString(), "GOLDEN") and
			string.find(p:getEquipment():getLeggings():getType():toString(), "GOLDEN") and
			string.find(p:getEquipment():getChestplate():getType():toString(), "GOLDEN") then
				p:addPotionEffect(newInstance("$.potion.PotionEffect", {effect.DAMAGE_RESISTANCE, 20, 0}))
			end
		end
	end)
end