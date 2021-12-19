function main(abilityData)
	dragonBossBar = nil
	dragonKey = nil
	
	plugin.addPassiveScript(abilityData, 0, function(p)
		dragonKey = newInstance("$.NamespacedKey", {plugin.getPlugin(), p:getUniqueId():toString() .. "DRAGON"})
		dragonBossBar = plugin.getServer():createBossBar(dragonKey, p:getName() .. "(엔더 드래곤)", import("$.boss.BarColor").PURPLE, import("$.boss.BarStyle").SEGMENTED_20, { import("$.boss.BarFlag").CREATE_FOG} )
		local players = util.getTableFromList(game.getPlayers())
		for i = 1, #players do
			dragonBossBar:addPlayer(players[i]:getPlayer())
		end
	end)
	
	plugin.addPassiveScript(abilityData, 1, function(p)
		if dragonBossBar ~= nil then
			dragonBossBar:setProgress(p:getHealth() / p:getAttribute(attribute.GENERIC_MAX_HEALTH):getValue())
		end
	end)
	
	plugin.onPlayerEnd(abilityData, function(p)
		if dragonKey ~= nil and dragonBossBar ~= nil then
			local players = util.getTableFromList(dragonBossBar:getPlayers())
			for i = 1, #players do
				dragonBossBar:removePlayer(players[i]:getPlayer())
			end
			plugin.getServer():removeBossBar(dragonKey)
		end
	end)
	
	plugin.registerEvent(abilityData, "PlayerInteractEvent", 4000, function(a, e)
		if e:getAction():toString() == "RIGHT_CLICK_AIR" or e:getAction():toString() == "RIGHT_CLICK_BLOCK" then
			if e:getItem() ~= nil then
				if game.isAbilityItem(e:getItem(), "IRON_INGOT") then
					if game.checkCooldown(e:getPlayer(), a, 0) then
						local players = util.getTableFromList(game.getPlayers())
						for i = 1, #players do
							if players[i]:getPlayer() ~= e:getPlayer() then
								if (e:getPlayer():getLocation():distance(players[i]:getPlayer():getLocation()) <= 5) then
									players[i]:getPlayer():damage(7, e:getPlayer())
									local velocity = e:getPlayer():getLocation()
									local dPos = newInstance("$.util.Vector", { velocity:getX() - players[i]:getPlayer():getLocation():getX(), velocity:getY() - players[i]:getPlayer():getLocation():getY(), velocity:getZ() - players[i]:getPlayer():getLocation():getZ() })
									local pitch = (math.atan2(math.sqrt(dPos:getZ() * dPos:getZ() + dPos:getX() * dPos:getX()), dPos:getY()))
									local yaw = (math.atan2(dPos:getZ(), dPos:getX()))
									
									velocity = newInstance("$.util.Vector", { math.sin(pitch) * math.cos(yaw), math.cos(pitch), math.sin(pitch) * math.sin(yaw) })
									velocity:setX(velocity:getX() * -10)
									velocity:setY(1.5)
									velocity:setZ(velocity:getZ() * -10)
									
									players[i]:getPlayer():setVelocity(velocity)
								end
							end
						end
						e:getPlayer():getWorld():spawnParticle(import("$.Particle").SMOKE_NORMAL, e:getPlayer():getLocation():add(0,1,0), 500, 0.5, 1, 0.5)
						e:getPlayer():getWorld():playSound(e:getPlayer():getLocation(), import("$.Sound").ENTITY_ENDER_DRAGON_GROWL, 1, 1)
					end
				end
			end
		end
		
		if e:getAction():toString() == "LEFT_CLICK_AIR" or e:getAction():toString() == "LEFT_CLICK_BLOCK" then
			if e:getItem() ~= nil then
				if game.isAbilityItem(e:getItem(), "IRON_INGOT") then
					if game.checkCooldown(e:getPlayer(), a, 0) then
						local playerEye = e:getPlayer():getEyeLocation():getDirection()
						local pos = e:getPlayer():getLocation()
						pos:setX(pos:getX() + (playerEye:getX() * 1.5))
						pos:setY(pos:getY() + 1)
						pos:setZ(pos:getZ() + (playerEye:getZ() * 1.5))
						local fireball = e:getPlayer():getWorld():spawnEntity(pos, import("$.entity.EntityType").DRAGON_FIREBALL)
						fireball:setShooter(e:getPlayer())
						util.runLater(function()
							if fireball:isValid() then fireball:remove() end
						end, 100)
						e:getPlayer():getWorld():playSound(e:getPlayer():getLocation(), import("$.Sound").ENTITY_ENDER_DRAGON_SHOOT, 1, 1)
					end
				end
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "EntityDamageByEntityEvent", 0, function(a, e)
		if (e:getDamager():getType():toString() == "DRAGON_FIREBALL" or e:getDamager():getType():toString() == "AREA_EFFECT_CLOUD") and e:getEntity():getType():toString() == "PLAYER" then
			if game.checkCooldown(e:getEntity(), a, 1) then
				e:setCancelled(true)
			end
		end
	end)
end


