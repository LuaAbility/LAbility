function main(abilityData)
	local effect = import("$.potion.PotionEffectType")

	plugin.registerEvent(abilityData, "PlayerInteractEvent", 400, function(a, e)
		if e:getAction():toString() == "RIGHT_CLICK_AIR" or e:getAction():toString() == "RIGHT_CLICK_BLOCK" then
			if e:getItem() ~= nil then
				if game.isAbilityItem(e:getItem(), "IRON_INGOT") then
					if game.checkCooldown(e:getPlayer(), a, 0) then
						local playerEye = e:getPlayer():getEyeLocation():getDirection()
						local pos = e:getPlayer():getLocation()
						pos:setX(pos:getX() + (playerEye:getX() * 1.5))
						pos:setY(pos:getY() + 1)
						pos:setZ(pos:getZ() + (playerEye:getZ() * 1.5))
						local fireball = e:getPlayer():getWorld():spawnEntity(pos, import("$.entity.EntityType").FIREBALL)
						e:getPlayer():getWorld():playSound(e:getPlayer():getLocation(), import("$.Sound").ENTITY_GHAST_SHOOT, 0.25, 1)
						e:getPlayer():getWorld():playSound(e:getPlayer():getLocation(), import("$.Sound").ENTITY_GHAST_WARN, 0.5, 1)
						fireball:setShooter(e:getPlayer())
						util.runLater(function()
							if fireball:isValid() then fireball:remove() end
						end, 200)
					end
				end
			end
		end
	end)

	plugin.registerEvent(abilityData, "EntityTargetLivingEntityEvent", 0, function(a, e)
		if e:getTarget() ~= nil and e:getEntity() ~= nil then
			if e:getTarget():getType():toString() == "PLAYER" and e:getEntity():getType():toString() == "GHAST" then
				if game.checkCooldown(e:getTarget(), a, 1) then
					e:setTarget(nil)
					e:setCancelled(true)
				end
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "EntityDamageByEntityEvent", 0, function(a, e)
		if e:getDamager():getType():toString() == "FIREBALL" and e:getEntity():getType():toString() == "PLAYER" then
			if game.checkCooldown(e:getEntity(), a, 2) then
				e:setDamage(100000)
				e:getEntity():getWorld():playSound(e:getEntity():getLocation(), import("$.Sound").ENTITY_GHAST_DEATH, 1, 1)
				e:getEntity():getWorld():spawnParticle(import("$.Particle").SMOKE_NORMAL, e:getEntity():getLocation():add(0,1,0), 200, 0.5, 1, 0.5)
			end
		end
	end)
end