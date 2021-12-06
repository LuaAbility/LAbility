function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	
	plugin.registerEvent(abilityData, "EntityDamageByEntityEvent", 1200, function(a, e)
		if e:getDamager():getType():toString() == "PLAYER" and e:getEntity():getType():toString() == "PLAYER" then
			if game.checkCooldown(e:getEntity(), a, 0) then
				e:getEntity():addPotionEffect(newInstance("$.potion.PotionEffect", {effect.SPEED, 600, 1}))
				e:getEntity():addPotionEffect(newInstance("$.potion.PotionEffect", {effect.INCREASE_DAMAGE, 600, 0}))
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "EntityTargetLivingEntityEvent", 0, function(a, e)
		if e:getTarget():getType():toString() == "PLAYER" and e:getEntity():getType():toString() == "ZOMBIFIED_PIGLIN" then
			if game.checkCooldown(e:getTarget(), a, 1) then
				e:setTarget(nil);
				e:setCancelled(true);
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