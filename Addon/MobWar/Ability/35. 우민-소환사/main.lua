function main(abilityData)
	plugin.registerEvent(abilityData, "PlayerInteractEvent", 500, function(a, e)
		if e:getAction():toString() == "RIGHT_CLICK_AIR" or e:getAction():toString() == "RIGHT_CLICK_BLOCK" then
			if e:getItem() ~= nil then
				if game.isAbilityItem(e:getItem(), "IRON_INGOT") then
					if game.checkCooldown(e:getPlayer(), a, 0) then
						evoker(e:getPlayer())
						e:getPlayer():getWorld():playSound(e:getPlayer():getLocation(), import("$.Sound").ENTITY_EVOKER_CAST_SPELL, 1, 1)
					end
				end
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "EntityTargetLivingEntityEvent", 0, function(a, e)
		if e:getTarget() ~= nil and e:getEntity() ~= nil then
			if e:getTarget():getType():toString() == "PLAYER" and e:getEntity():getType():toString() == "EVOKER" then
				if game.checkCooldown(e:getTarget(), a, 1) then
					e:setTarget(nil)
					e:setCancelled(true)
				end
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "EntityDamageByEntityEvent", 0, function(a, e)
		if e:getDamager():getType():toString() == "EVOKER_FANGS" and e:getEntity():getType():toString() == "PLAYER" then
			if game.checkCooldown(e:getEntity(), a, 2) then
				e:setCancelled(true)
			end
		end
	end)
end


function evoker(player)
	local firstLoc = newInstance("org.bukkit.util.Vector", {player:getLocation():getX(), player:getLocation():getY(), player:getLocation():getZ()})
	local dir = player:getLocation():getDirection()
	
	for i = 1, 20 do
		util.runLater(function()
			local loc1 = newInstance("org.bukkit.util.Vector", {firstLoc:getX() + (dir:getX() * i), firstLoc:getY(), firstLoc:getZ() + (dir:getZ() * i)})
			local fangs1 = player:getWorld():spawnEntity(newInstance("org.bukkit.Location", {player:getWorld(), loc1:getX(), loc1:getY(), loc1:getZ()}), import("$.entity.EntityType").EVOKER_FANGS)
			fangs1:setOwner(player)
			
			local loc2 = newInstance("org.bukkit.util.Vector", {loc1:getX() + -dir:getZ(), loc1:getY(), loc1:getZ() + dir:getX()})
			local fangs2 = player:getWorld():spawnEntity(newInstance("org.bukkit.Location", {player:getWorld(), loc2:getX(), loc2:getY(), loc2:getZ()}), import("$.entity.EntityType").EVOKER_FANGS)
			fangs2:setOwner(player)
			
			local loc3 = newInstance("org.bukkit.util.Vector", {loc1:getX() + dir:getZ(), loc1:getY(), loc1:getZ() + -dir:getX()})
			local fangs3 = player:getWorld():spawnEntity(newInstance("org.bukkit.Location", {player:getWorld(), loc3:getX(), loc3:getY(), loc3:getZ()}), import("$.entity.EntityType").EVOKER_FANGS)
			fangs3:setOwner(player)
		end, (i - 1) * 1)
	end
end


