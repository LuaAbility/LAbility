function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	local cause = import("$.event.entity.EntityDamageEvent")
	
	plugin.registerEvent(abilityData, "PlayerInteractEvent", 600, function(a, e)
		if e:getAction():toString() == "RIGHT_CLICK_AIR" or e:getAction():toString() == "RIGHT_CLICK_BLOCK" then
			if e:getItem() ~= nil then
				if game.isAbilityItem(e:getItem(), "IRON_INGOT") then
					if e:getPlayer():getTargetBlock(nil, 30):getType():toString() ~= "AIR" then
						if game.checkCooldown(e:getPlayer(), a, 0) then
							e:getPlayer():getWorld():spawnParticle(import("$.Particle").PORTAL, e:getPlayer():getLocation():add(0,1,0), 1000, 0.5, 1, 0.5, 0.5)
							e:getPlayer():getWorld():playSound(e:getPlayer():getLocation(), import("$.Sound").ENTITY_ENDERMAN_TELEPORT, 0.5, 1)
							teleport(e:getPlayer(), e:getPlayer():getTargetBlock(nil, 30))
							e:getPlayer():getWorld():spawnParticle(import("$.Particle").REVERSE_PORTAL, e:getPlayer():getLocation():add(0,1,0), 1000, 0.5, 1, 0.5, 0.5)
							e:getPlayer():getWorld():playSound(e:getPlayer():getLocation(), import("$.Sound").ENTITY_ENDERMAN_TELEPORT, 0.5, 1)
						end
					else game.sendMessage(e:getPlayer(), "§4[§c엔더맨§4] §c허공엔 사용이 불가능합니다.")
					end
				end
			end
		end
	end)
	
	
	plugin.registerEvent(abilityData, "EntityDamageByEntityEvent", 200, function(a, e)
		if e:getCause():toString() == "PROJECTILE" and e:getEntity():getType():toString() == "PLAYER" then
			if game.checkCooldown(e:getEntity(), a, 1) then
				e:setCancelled(true)
			end
		end
	end)
	
		
	plugin.registerEvent(abilityData, "EntityTargetLivingEntityEvent", 0, function(a, e)
		if e:getTarget() ~= nil and e:getEntity() ~= nil then
			if e:getTarget():getType():toString() == "PLAYER" and e:getEntity():getType():toString() == "ENDERMAN" then
				if game.checkCooldown(e:getTarget(), a, 2) then
					e:setTarget(nil)
					e:setCancelled(true)
				end
			end
		end
	end)
	
	plugin.addPassiveScript(abilityData, 20, function(p)
		if p:getLocation():getBlock():getType():toString() == "WATER" or p:getLocation():getBlock():getType():toString() == "WATER_CAULDRON" then
			p:damage(1)
			p:getWorld():spawnParticle(import("$.Particle").SMOKE_NORMAL, p:getLocation():add(0,1,0), 50, 0.5, 1, 0.5, 0.05)
			p:getWorld():playSound(p:getLocation(), import("$.Sound").ENTITY_ENDERMAN_HURT, 0.25, 1)
		end
	end)
end

function teleport(player, block)
	local playerLoc = player:getLocation()
	local blockLoc = block:getLocation()
	
	local targetVec = newInstance("$.util.Vector", {blockLoc:getX(), blockLoc:getY(), blockLoc:getZ()})
	if player:getWorld():getBlockAt(blockLoc:getX(), blockLoc:getY() + 1, blockLoc:getZ()):getType():toString() ~= "AIR" or
	player:getWorld():getBlockAt(blockLoc:getX(), blockLoc:getY() + 2, blockLoc:getZ()):getType():toString() ~= "AIR" then
		targetVec:setY(blockLoc:getWorld():getHighestBlockYAt(blockLoc:getX(), blockLoc:getZ()))
	end
	
	local playerVec = newInstance("$.util.Vector", {playerLoc:getX(), playerLoc:getY(), playerLoc:getZ()})
	if playerVec:distance(targetVec) > 30.0 and not (playerVec:getY() - targetVec:getY() > 30.0 or playerVec:getY() - targetVec:getY() < -30.0) then
		local moreDirection = playerVec:distance(targetVec) - 30
		local playerEye = player:getEyeLocation():getDirection()
		playerEye = newInstance("$.util.Vector", {playerEye:getX() * moreDirection * -1, 0, playerEye:getZ() * moreDirection * -1})
		
		targetVec:setX(targetVec:getX() + playerEye:getX())
		targetVec:setZ(targetVec:getZ() + playerEye:getZ())
	end
	
	blockLoc:setX(targetVec:getX() + 0.5)
	blockLoc:setY(targetVec:getY() + 1.0)
	blockLoc:setZ(targetVec:getZ() + 0.5)
	blockLoc:setPitch(playerLoc:getPitch())
	blockLoc:setYaw(playerLoc:getYaw())
	player:teleport(blockLoc)
end