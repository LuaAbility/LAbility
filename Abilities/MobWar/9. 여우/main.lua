function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	
	plugin.registerEvent(abilityData, "EntityDamageByEntityEvent", 2000, function(a, e)
		if e:getDamager():getType():toString() == "PLAYER" and e:getEntity():getType():toString() == "PLAYER" then
			local randomData = math.random(100)
			if randomData <= 15 then
				local item = { e:getEntity():getInventory():getItemInMainHand() }
				if item[1] ~= nil and item[1]:getType():toString() ~= "AIR" then
					if game.checkCooldown(e:getDamager(), a, 0) then
						e:getEntity():getInventory():removeItem(item)
						e:getDamager():getInventory():addItem(item)
						game.sendMessage(e:getEntity(), "§a쇽!")
						e:getEntity():getWorld():spawnParticle(import("$.Particle").SMOKE_NORMAL, e:getEntity():getLocation():add(0,1,0), 150, 0.5, 1, 0.5, 0.05)
						e:getDamager():getWorld():spawnParticle(import("$.Particle").COMPOSTER, e:getDamager():getLocation():add(0,1,0), 100, 0.5, 1, 0.5, 0.05)
						e:getDamager():getWorld():playSound(e:getDamager():getLocation(), import("$.Sound").ENTITY_FOX_BITE, 0.5, 1)
					end
				end
			end
		end
	end)
end