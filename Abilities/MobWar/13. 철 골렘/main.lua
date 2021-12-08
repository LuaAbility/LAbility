function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	
	plugin.registerEvent(abilityData, "EntityDamageByEntityEvent", 300, function(a, e)
		if e:getDamager():getType():toString() == "PLAYER" and e:getEntity():getType():toString() == "PLAYER" then
			if math.random() <= 0.2 then
				if game.checkCooldown(e:getDamager(), a, 0) then
					local vector = e:getDamager():getEyeLocation():getDirection()
					vector:setX(vector:getX() / 4)
					vector:setY(1.3)
					vector:setZ(vector:getZ() / 4)
					e:getEntity():setVelocity(vector)
					e:getEntity():damage(e:getDamage())
					e:setCancelled(true)
				end
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "EntityTargetLivingEntityEvent", 0, function(a, e)
		if e:getTarget() ~= nil and e:getEntity() ~= nil then
			if e:getTarget():getType():toString() == "PLAYER" and e:getEntity():getType():toString() == "IRON_GOLEM" then
				if game.checkCooldown(e:getTarget(), a, 1) then
					e:setTarget(nil);
					e:setCancelled(true);
				end
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "PlayerInteractEvent", 600, function(a, e)
		if e:getAction():toString() == "RIGHT_CLICK_AIR" or e:getAction():toString() == "RIGHT_CLICK_BLOCK" then
			if e:getItem() ~= nil then
				if e:getItem():getType():toString() == "IRON_INGOT" then
					local maxHealth = e:getPlayer():getAttribute(attribute.GENERIC_MAX_HEALTH):getValue()
					if e:getPlayer():getHealth() < maxHealth then
						if game.checkCooldown(e:getPlayer(), a, 2) then
							e:setCancelled(true)
							local newHealth = e:getPlayer():getHealth() + 4
							if newHealth > maxHealth then newHealth = maxHealth end
							e:getPlayer():setHealth(newHealth)
							local itemStack = { newInstance("$.inventory.ItemStack", {e:getMaterial(), 1}) }
							e:getPlayer():getInventory():removeItem(itemStack)
						end
					end
				end
			end
		end
	end)
end