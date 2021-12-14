function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	local types = import("$.entity.EntityType")
	
	plugin.registerEvent(abilityData, "EntityDamageByEntityEvent", 1200, function(a, e)
		if e:getDamager():getType():toString() == "PLAYER" and e:getEntity():getType():toString() == "PLAYER" then
			local item = e:getDamager():getInventory():getItemInMainHand()
			if game.isAbilityItem(item, "BONE") then
				if game.checkCooldown(e:getDamager(), a, 0) then
					for i = 1, 5 do
						local entity = e:getDamager():getWorld():spawnEntity(e:getEntity():getLocation(), types.WOLF)
						entity:setTarget(e:getEntity())
						util.runLater(function()
							if entity:isValid() then entity:remove() end
						end, 600)
					end
					
					e:getEntity():getWorld():spawnParticle(import("$.Particle").SMOKE_NORMAL, e:getEntity():getEyeLocation(), 150, 0.5, 1, 0.5, 0.05)
					e:getDamager():getWorld():playSound(e:getDamager():getLocation(), import("$.Sound").ENTITY_WOLF_HOWL, 1, 1)
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