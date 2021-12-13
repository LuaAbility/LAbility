function main(abilityData)
	abilityTime = false
	plugin.registerEvent(abilityData, "PlayerInteractEvent", 400, function(a, e)
		if e:getAction():toString() == "RIGHT_CLICK_AIR" or e:getAction():toString() == "RIGHT_CLICK_BLOCK" then
			if e:getItem() ~= nil then
				if game.isAbilityItem(e:getItem(), "IRON_INGOT") and e:getPlayer():getLocation():getBlock():getRelative(import("$.block.BlockFace").DOWN):getType():toString() ~= "AIR" then
					if game.checkCooldown(e:getPlayer(), a, 0) then
						local vector = e:getPlayer():getEyeLocation():getDirection()
						vector:setX(vector:getX() * 6.0)
						vector:setY(0.15)
						vector:setZ(vector:getZ() * 6.0)
						e:getPlayer():setVelocity(vector)
						abilityTime = true
						util.runLater(function() abilityTime = false end, 12)
						e:getPlayer():getWorld():playSound(e:getPlayer():getLocation(), import("$.Sound").ENTITY_GOAT_SCREAMING_PREPARE_RAM, 1, 1)
					end
				end
			end
		end
	end)
	
	plugin.addPassiveScript(abilityData, 1, function(p)
		local players = util.getTableFromList(game.getPlayers())
		if abilityTime then
			for i = 1, #players do
				if players[i]:getPlayer() ~= p then
					if (p:getLocation():distance(players[i]:getPlayer():getLocation()) <= 3) then
						local vector = p:getEyeLocation():getDirection()
						vector:setX(vector:getX() * 6.0)
						vector:setY(0.3)
						vector:setZ(vector:getZ() * 6.0)
						players[i]:getPlayer():setVelocity(vector)
						players[i]:getPlayer():damage(4, p)
						
						local vector = p:getEyeLocation():getDirection()
						vector:setX(vector:getX() * 0.75)
						vector:setZ(vector:getZ() * 0.75)
						p:setVelocity(vector)
						abilityTime = false
						players[i]:getPlayer():getWorld():playSound(players[i]:getPlayer():getLocation(), import("$.Sound").ENTITY_GOAT_SCREAMING_RAM_IMPACT, 1, 1)
					end
				end
			end
			
			p:getWorld():spawnParticle(import("$.Particle").SMOKE_NORMAL, p:getLocation():add(0,1,0), 30, 0.5, 1, 0.5, 0.05)
		end
	end)

	
	plugin.registerEvent(abilityData, "EntityTargetLivingEntityEvent", 0, function(a, e)
		if e:getTarget() ~= nil and e:getEntity() ~= nil then
			if e:getTarget():getType():toString() == "PLAYER" and e:getEntity():getType():toString() == "GOAT" then
				if game.checkCooldown(e:getTarget(), a, 1) then
					e:setTarget(nil)
					e:setCancelled(true)
				end
			end
		end
	end)
end