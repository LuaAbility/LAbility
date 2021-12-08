function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	local types = import("$.entity.EntityType")
	
	plugin.registerEvent(abilityData, "EntityDamageByEntityEvent", 1200, function(a, e)
		if e:getDamager():getType():toString() == "PLAYER" and e:getEntity():getType():toString() == "PLAYER" then
			local item = e:getDamager():getInventory():getItemInMainHand()
			if game.isAbilityItem(item:getType(), "BONE") then
				if game.checkCooldown(e:getDamager(), a, 0) then
					for i = 1, 5 do
						local entity = e:getDamager():getWorld():spawnEntity(e:getEntity():getLocation(), types.WOLF)
						entity:setTarget(e:getEntity())
						util.runLater(function()
							entity:remove()
						end, 600)
					end
				end
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "EntityTargetLivingEntityEvent", 0, function(a, e)
		if e:getTarget() ~= nil and e:getEntity() ~= nil then
			if e:getTarget():getType():toString() == "PLAYER" and e:getEntity():getType():toString() == "WOLF" then
				if game.checkCooldown(e:getTarget(), a, 1) then
					e:setTarget(nil)
					e:setCancelled(true)
				end
			end
		end
	end)
end