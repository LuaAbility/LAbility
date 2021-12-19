function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	firstFallDamage = false
	
	plugin.registerEvent(abilityData, "PlayerInteractEvent", 1600, function(a, e)
		if e:getAction():toString() == "RIGHT_CLICK_AIR" or e:getAction():toString() == "RIGHT_CLICK_BLOCK" then
			if e:getItem() ~= nil then
				if game.isAbilityItem(e:getItem(), "IRON_SWORD") then
					if game.checkCooldown(e:getPlayer(), a, 0) then
						e:getPlayer():setAllowFlight(true)
						e:getPlayer():getWorld():spawnParticle(import("$.Particle").SMOKE_NORMAL, e:getPlayer():getLocation():add(0,1,0), 150, 0.5, 1, 0.5, 0.05)
						e:getPlayer():getWorld():playSound(e:getPlayer():getLocation(), import("$.Sound").ENTITY_VEX_CHARGE, 0.25, 1)
						util.runLater(function()
							local down = e:getPlayer():getLocation():getBlock():getRelative(import("$.block.BlockFace").DOWN):getType()
							local moreDown = e:getPlayer():getLocation():getBlock():getRelative(import("$.block.BlockFace").DOWN):getRelative(import("$.block.BlockFace").DOWN):getType()
							e:getPlayer():setAllowFlight(false)
							if down:toString() == "AIR" and moreDown:toString() == "AIR" then firstFallDamage = true end
							e:getPlayer():setFlying(false)
							game.sendMessage(e:getPlayer(), "§2[§a벡스§2] §a능력 시전 시간이 종료되었습니다.")
							e:getPlayer():getWorld():spawnParticle(import("$.Particle").SMOKE_NORMAL, e:getPlayer():getLocation():add(0,1,0), 150, 0.5, 1, 0.5, 0.05)
							e:getPlayer():getWorld():playSound(e:getPlayer():getLocation(), import("$.Sound").ENTITY_VEX_AMBIENT, 0.25, 1)
						end, 600)
					end
				end
			end
		end
	end)
	
	plugin.onPlayerEnd(abilityData, function(p)
		p:getPlayer():setAllowFlight(false)
		p:getPlayer():setFlying(false)
	end)
	
	plugin.registerEvent(abilityData, "EntityDamageEvent", 0, function(a, e)
		if e:getCause():toString() == "FALL" and firstFallDamage then
			if game.checkCooldown(e:getEntity(), a, 1) then
				e:setCancelled(true)
				firstFallDamage = false
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "EntityTargetLivingEntityEvent", 0, function(a, e)
		if e:getTarget() ~= nil and e:getEntity() ~= nil then
			if e:getTarget():getType():toString() == "PLAYER" and e:getEntity():getType():toString() == "VEX" then
				if game.checkCooldown(e:getTarget(), a, 2) then
					e:setTarget(nil)
					e:setCancelled(true)
				end
			end
		end
	end)
end