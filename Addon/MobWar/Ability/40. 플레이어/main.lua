function main(abilityData)
	difficult = 1
	plugin.registerEvent(abilityData, "PlayerInteractEvent", 6000, function(a, e)
		if e:getAction():toString() == "RIGHT_CLICK_AIR" or e:getAction():toString() == "RIGHT_CLICK_BLOCK" then
			if e:getItem() ~= nil then
				if game.isAbilityItem(e:getItem(), "IRON_INGOT") then
					if game.checkCooldown(e:getPlayer(), a, 0) then
						setDifficult(e:getPlayer())
					end
				end
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "EntityDamageByEntityEvent", 0, function(a, e)
		local damagee = e:getEntity()
		local damager = e:getDamager()
		if e:getCause():toString() == "PROJECTILE" then damager = e:getDamager():getShooter() end
		
		if damager:getType():toString() == "PLAYER" and damagee:getType():toString() == "PLAYER" then
			if game.checkCooldown(damagee, a, 1) then
				if difficult == 1 then e:setDamage(e:getDamage() * 0.5)
				elseif difficult == 2 then e:setDamage(e:getDamage() * 0.75)
				elseif difficult == 3 then e:setDamage(e:getDamage())
				elseif difficult == 4 then e:setDamage(e:getDamage() * 1.25)
				else e:setDamage(e:getDamage() * 1.5) end
			end
			
			if game.checkCooldown(damager, a, 1) then
				if difficult == 1 then e:setDamage(e:getDamage() * 0.5)
				elseif difficult == 2 then e:setDamage(e:getDamage() * 0.75)
				elseif difficult == 3 then e:setDamage(e:getDamage())
				elseif difficult == 4 then e:setDamage(e:getDamage() * 1.5)
				else e:setDamage(e:getDamage() * 2) end
			end
		end
	end)
	
	plugin.addPassiveScript(abilityData, 0, function(p)
		setDifficult(p)
	end)
end

function setDifficult(p)
	difficult = math.random(1, 5)
	if difficult == 1 then game.sendMessage(p, "§2[§a플레이어§2] §a난이도가 §b평화로움§a이 되었습니다.")
	elseif difficult == 2 then game.sendMessage(p, "§2[§a플레이어§2] §a난이도가 §e쉬움§a이 되었습니다.")
	elseif difficult == 3 then game.sendMessage(p, "§2[§a플레이어§2] §a난이도가 §6보통§a이 되었습니다.")
	elseif difficult == 4 then game.sendMessage(p, "§2[§a플레이어§2] §a난이도가 §c어려움§a이 되었습니다.")
	else game.sendMessage(p, "§2[§a플레이어§2] §a난이도가 §4하드코어§a가 되었습니다.") end
	p:getWorld():spawnParticle(import("$.Particle").COMPOSTER, p:getLocation():add(0,1,0), 100, 0.5, 1, 0.5, 0.05)
	p:getWorld():playSound(p:getLocation(), import("$.Sound").ENTITY_PLAYER_LEVELUP, 1, 1)
end