function main(abilityData)
	local effect = import("$.potion.PotionEffectType")

	plugin.addPassiveScript(abilityData, 600, function(p)
		local players = util.getTableFromList(game.getPlayers())
		for i = 1, #players do
			if players[i]:getPlayer() ~= p then
				if (p:getLocation():distance(players[i]:getPlayer():getLocation()) <= 10) then
					local pos = p:getLocation()
					pos:setY(pos:getY() + 1)
					local bullet = p:getWorld():spawnEntity(pos, import("$.entity.EntityType").SHULKER_BULLET)
					bullet:setTarget(players[i]:getPlayer())
					bullet:setShooter(p)
					p:getWorld():spawnParticle(import("$.Particle").SMOKE_NORMAL, p:getLocation():add(0,1,0), 100, 0.5, 1, 0.5, 0.05)
					p:getWorld():playSound(p:getLocation(), import("$.Sound").ENTITY_SHULKER_SHOOT, 0.25, 1)
				end
			end
		end
	end)
	
	plugin.addPassiveScript(abilityData, 1, function(p)
		if p:isSneaking() then
			p:addPotionEffect(newInstance("$.potion.PotionEffect", {effect.DAMAGE_RESISTANCE, 2, 0}))
			p:setWalkSpeed(0)
		else 
			p:setWalkSpeed(0.2) 
		end
	end)
	
	plugin.onPlayerEnd(abilityData, function(p)
		p:getPlayer():setWalkSpeed(0.2)
	end)
	
	plugin.registerEvent(abilityData, "EntityTargetLivingEntityEvent", 0, function(a, e)
		if e:getTarget() ~= nil and e:getEntity() ~= nil then
			if e:getTarget():getType():toString() == "PLAYER" and (e:getEntity():getType():toString() == "SHULKER_BULLET" or e:getEntity():getType():toString() == "SHULKER") then
				if game.checkCooldown(e:getTarget(), a, 0) then
					e:setTarget(nil)
					e:setCancelled(true)
				end
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "EntityDamageByEntityEvent", 0, function(a, e)
		if e:getDamager():getType():toString() == "SHULKER_BULLET" and e:getEntity():getType():toString() == "PLAYER" then
			if game.checkCooldown(e:getEntity(), a, 1) then
				e:setCancelled(true)
			end
		end
	end)
end