function main(abilityData)
	useSpiderAbility = true
	local blockFace = import("$.block.BlockFace")
	
	plugin.registerEvent(abilityData, "PlayerMoveEvent", 0, function(a, e)
		local north = e:getPlayer():getLocation():getBlock():getRelative(blockFace.NORTH):getType()
		local east = e:getPlayer():getLocation():getBlock():getRelative(blockFace.EAST):getType()
		local west = e:getPlayer():getLocation():getBlock():getRelative(blockFace.WEST):getType()
		local south = e:getPlayer():getLocation():getBlock():getRelative(blockFace.SOUTH):getType()
		local up = e:getPlayer():getLocation():getBlock():getRelative(blockFace.UP):getRelative(blockFace.UP):getType()
		
		if (north:toString() ~= "AIR" or east:toString() ~= "AIR" or west:toString() ~= "AIR" or south:toString() ~= "AIR") and up:toString() == "AIR" and useSpiderAbility then
			local velocity = e:getPlayer():getVelocity()
			if game.checkCooldown(e:getPlayer(), a, 0) then
				if e:getPlayer():isSneaking() then
					velocity:setY(-0.25)
					e:getPlayer():setVelocity(velocity)
					e:getPlayer():setFallDistance(0)
				else
					velocity:setY(0.25)
					e:getPlayer():setVelocity(velocity)
				end
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "PlayerInteractEvent", 0, function(a, e)
		if e:getAction():toString() == "RIGHT_CLICK_AIR" or e:getAction():toString() == "RIGHT_CLICK_BLOCK" then
			if e:getItem() ~= nil then
				if game.isAbilityItem(e:getItem(), "IRON_INGOT") then
					if game.checkCooldown(e:getPlayer(), a, 1) then
						if useSpiderAbility then
							useSpiderAbility = false
							game.sendMessage(e:getPlayer(), "§2[§a거미§2] §a능력을 비활성화했습니다.")
							e:getPlayer():getWorld():spawnParticle(import("$.Particle").SMOKE_NORMAL, e:getPlayer():getLocation():add(0,1,0), 100, 0.5, 1, 0.5, 0.05)
							e:getPlayer():getWorld():playSound(e:getPlayer():getLocation(), import("$.Sound").ENTITY_SPIDER_DEATH, 0.25, 1)
						else
							useSpiderAbility = true
							game.sendMessage(e:getPlayer(), "§2[§a거미§2] §a능력을 활성화했습니다.")
							e:getPlayer():getWorld():spawnParticle(import("$.Particle").SMOKE_NORMAL, e:getPlayer():getLocation():add(0,1,0), 100, 0.5, 1, 0.5, 0.05)
							e:getPlayer():getWorld():playSound(e:getPlayer():getLocation(), import("$.Sound").ENTITY_SPIDER_AMBIENT, 0.25, 1)
						end
					end
				end
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "EntityTargetLivingEntityEvent", 0, function(a, e)
		if e:getTarget() ~= nil and e:getEntity() ~= nil then
			if e:getTarget():getType():toString() == "PLAYER" and e:getEntity():getType():toString() == "SPIDER" then
				if game.checkCooldown(e:getTarget(), a, 2) then
					e:setTarget(nil)
					e:setCancelled(true)
				end
			end
		end
	end)
end