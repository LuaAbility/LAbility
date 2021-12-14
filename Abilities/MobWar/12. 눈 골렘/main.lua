function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	
	plugin.addPassiveScript(abilityData, 100, function(p)
		local players = util.getTableFromList(game.getPlayers())
		for i = 1, #players do
			if players[i]:getPlayer() ~= p then
				if (p:getLocation():distance(players[i]:getPlayer():getLocation()) <= 10) then
					players[i]:getPlayer():setFreezeTicks(300)
				end
			end
		end
		p:getWorld():spawnParticle(import("$.Particle").SNOWFLAKE, p:getLocation():add(0,1,0), 500, 5, 1, 5, 0.05)
		p:getWorld():playSound(p:getLocation(), import("$.Sound").BLOCK_POWDER_SNOW_FALL, 0.5, 1)
	end)
	
	plugin.registerEvent(abilityData, "EntityTargetLivingEntityEvent", 0, function(a, e)
		if e:getTarget() ~= nil and e:getEntity() ~= nil then
			if e:getTarget():getType():toString() == "PLAYER" and e:getEntity():getType():toString() == "SNOW_GOLEM" then
				if game.checkCooldown(e:getTarget(), a, 0) then
					e:setTarget(nil)
					e:setCancelled(true)
				end
			end
		end
	end)
end