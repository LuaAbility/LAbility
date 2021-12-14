function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	
	plugin.registerEvent(abilityData, "EntityDamageByEntityEvent", 900, function(a, e)
		if e:getDamager():getType():toString() == "PLAYER" and e:getEntity():getType():toString() == "PLAYER" then
			local item = e:getDamager():getInventory():getItemInMainHand()
			if game.isAbilityItem(item, "IRON_INGOT") then
				if game.checkCooldown(e:getDamager(), a, 0) then
					e:getDamager():addPotionEffect(newInstance("$.potion.PotionEffect", {effect.CONFUSION, 400, 0}))
					e:getDamager():addPotionEffect(newInstance("$.potion.PotionEffect", {effect.WEAKNESS, 400, 0}))
					e:getEntity():addPotionEffect(newInstance("$.potion.PotionEffect", {effect.CONFUSION, 400, 0}))
					e:getEntity():addPotionEffect(newInstance("$.potion.PotionEffect", {effect.POISON, 400, 0}))
					
					e:getEntity():getWorld():spawnParticle(import("$.Particle").ITEM_CRACK, e:getEntity():getLocation():add(0,1,0), 100, 0.5, 1, 0.5, 0.05, newInstance("$.inventory.ItemStack", {import("$.Material").HONEYCOMB_BLOCK}))
					e:getDamager():getWorld():playSound(e:getDamager():getLocation(), import("$.Sound").ENTITY_BEE_STING, 1, 1)
				end
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "EntityTargetLivingEntityEvent", 0, function(a, e)
		if e:getTarget() ~= nil and e:getEntity() ~= nil then
			if e:getTarget():getType():toString() == "PLAYER" and e:getEntity():getType():toString() == "BEE" then
				if game.checkCooldown(e:getTarget(), a, 1) then
					e:setTarget(nil)
					e:setCancelled(true)
				end
			end
		end
	end)
end