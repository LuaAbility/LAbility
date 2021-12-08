function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	
	plugin.registerEvent(abilityData, "PlayerItemConsumeEvent", 0, function(a, e)
		if e:getItem():getType():toString() == "ROTTEN_FLESH" then
			if game.checkCooldown(e:getPlayer(), a, 0) then
				e:getPlayer():setFoodLevel(e:getPlayer():getFoodLevel() + 4)
				local itemStack = { newInstance("$.inventory.ItemStack", {e:getItem():getType(), 1}) }
				e:getPlayer():getInventory():removeItem(itemStack)
				e:setCancelled(true)
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "EntityTargetLivingEntityEvent", 0, function(a, e)
		if e:getTarget() ~= nil and e:getEntity() ~= nil then
			if e:getTarget():getType():toString() == "PLAYER" and e:getEntity():getType():toString() == "ZOMBIE" then
				if game.checkCooldown(e:getTarget(), a, 1) then
					e:setTarget(nil)
					e:setCancelled(true)
				end
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "PlayerDeathEvent", 0, function(a, e)
		local damageEvent = e:getEntity():getLastDamageCause()
		
		if (damageEvent ~= nil and damageEvent:isCancelled() == false and damageEvent:getEventName() == "EntityDamageByEntityEvent") then
			local damagee = damageEvent:getEntity()
			local damager = damageEvent:getDamager()
			if damageEvent:getCause():toString() == "PROJECTILE" then damager = damageEvent:getDamager():getShooter() end
			
			if damager:getType():toString() == "PLAYER" and damagee:getType():toString() == "PLAYER" then
				if game.checkCooldown(e:getEntity(), a, 1) then
					game.changeAbility(damager, a, "LA-MW-015", true)
				end
			end
		end
	end)
end