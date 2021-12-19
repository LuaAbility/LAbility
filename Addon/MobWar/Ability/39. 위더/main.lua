function main(abilityData)
	witherBossBar = nil
	witherKey = nil
	
	plugin.addPassiveScript(abilityData, 0, function(p)
		witherKey = newInstance("$.NamespacedKey", {plugin.getPlugin(), p:getUniqueId():toString() .. "WITHER"})
		witherBossBar = plugin.getServer():createBossBar(witherKey, p:getName() .. "(위더)", import("$.boss.BarColor").BLUE, import("$.boss.BarStyle").SEGMENTED_20, { import("$.boss.BarFlag").DARKEN_SKY} )
		local players = util.getTableFromList(game.getPlayers())
		for i = 1, #players do
			witherBossBar:addPlayer(players[i]:getPlayer())
		end
	end)
	
	plugin.addPassiveScript(abilityData, 1, function(p)
		if witherBossBar ~= nil then
			witherBossBar:setProgress(p:getHealth() / p:getAttribute(attribute.GENERIC_MAX_HEALTH):getValue())
		end
	end)
	
	plugin.onPlayerEnd(abilityData, function(p)
		if witherKey ~= nil and witherBossBar ~= nil then
			local players = util.getTableFromList(witherBossBar:getPlayers())
			for i = 1, #players do
				witherBossBar:removePlayer(players[i]:getPlayer())
			end
			print(plugin.getServer():removeBossBar(witherKey))
		end
	end)
	
	plugin.registerEvent(abilityData, "PlayerInteractEvent", 3000, function(a, e)
		if e:getAction():toString() == "RIGHT_CLICK_AIR" or e:getAction():toString() == "RIGHT_CLICK_BLOCK" then
			if e:getItem() ~= nil then
				if game.isAbilityItem(e:getItem(), "IRON_INGOT") then
					if game.checkCooldown(e:getPlayer(), a, 0) then
						for i = 1, 2 do
							local entity = e:getPlayer():getWorld():spawnEntity(e:getPlayer():getLocation(), import("$.entity.EntityType").WITHER_SKELETON)
							util.runLater(function()
								if entity:isValid() then entity:remove() end
							end, 600)
						end
						e:getPlayer():getWorld():spawnParticle(import("$.Particle").SMOKE_NORMAL, e:getPlayer():getLocation():add(0,1,0), 500, 0.5, 1, 0.5)
						e:getPlayer():getWorld():playSound(e:getPlayer():getLocation(), import("$.Sound").ENTITY_WITHER_SPAWN, 1, 1)
					end
				end
			end
		end
		
		if e:getAction():toString() == "LEFT_CLICK_AIR" or e:getAction():toString() == "LEFT_CLICK_BLOCK" then
			if e:getItem() ~= nil then
				if game.isAbilityItem(e:getItem(), "IRON_INGOT") then
					if game.checkCooldown(e:getPlayer(), a, 0) then
						local players = util.getTableFromList(game.getPlayers())
						for i = 1, #players do
							if players[i]:getPlayer() ~= e:getPlayer() then
								if (e:getPlayer():getLocation():distance(players[i]:getPlayer():getLocation()) <= 10) then
									local pos = e:getPlayer():getEyeLocation()
									pos:setY(pos:getY() + 2)
									
									local dPos = newInstance("$.util.Vector", { players[i]:getPlayer():getLocation():getX() - pos:getX(), players[i]:getPlayer():getLocation():getY() - pos:getY() + 1, players[i]:getPlayer():getLocation():getZ() - pos:getZ() })
									local pitch = (math.atan2(math.sqrt(dPos:getZ() * dPos:getZ() + dPos:getX() * dPos:getX()), dPos:getY()))
									local yaw = (math.atan2(dPos:getZ(), dPos:getX()))
									
									pos:setDirection(newInstance("$.util.Vector", { math.sin(pitch) * math.cos(yaw), math.cos(pitch), math.sin(pitch) * math.sin(yaw) }))
									
									local skull = e:getPlayer():getWorld():spawnEntity(pos, import("$.entity.EntityType").WITHER_SKULL)
									skull:setShooter(e:getPlayer())
									util.runLater(function()
										if skull:isValid() then skull:remove() end
									end, 100)
								end
							end
						end
						e:getPlayer():getWorld():spawnParticle(import("$.Particle").SMOKE_NORMAL, e:getPlayer():getLocation():add(0,1,0), 500, 0.5, 1, 0.5)
						e:getPlayer():getWorld():playSound(e:getPlayer():getLocation(), import("$.Sound").ENTITY_WITHER_SHOOT, 1, 1)
					end
				end
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "EntityDamageByEntityEvent", 0, function(a, e)
		if string.find(e:getDamager():getType():toString(), "WITHER") and e:getEntity():getType():toString() == "PLAYER" then
			if game.checkCooldown(e:getEntity(), a, 2) then
				e:setCancelled(true)
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "EntityTargetLivingEntityEvent", 0, function(a, e)
		if e:getTarget() ~= nil and e:getEntity() ~= nil then
			if e:getTarget():getType():toString() == "PLAYER" and string.find(e:getEntity():getType():toString(), "WITHER") then
				if game.checkCooldown(e:getTarget(), a, 1) then
					e:setTarget(nil)
					e:setCancelled(true)
				end
			end
		end
	end)
end


