function main(abilityData)
	plugin.registerEvent(abilityData, "PlayerInteractEvent", 2000, function(a, e)
		if e:getAction():toString() == "RIGHT_CLICK_AIR" or e:getAction():toString() == "RIGHT_CLICK_BLOCK" then
			if e:getItem() ~= nil then
				if game.isAbilityItem(e:getItem(), "WHEAT") then
					if game.checkCooldown(e:getPlayer(), a, 0) then
						rollStat(e:getPlayer())
					end
				end
			end
		end
	end)
	
	plugin.addPassiveScript(abilityData, 0, function(p)
		rollStat(p)
	end)

	plugin.registerEvent(abilityData, "PlayerDeathEvent", 0, function(a, e)
		if game.checkCooldown(e:getEntity(), a, 1) then
			rollStat(e:getEntity())
		end
	end)
	
	plugin.onPlayerEnd(abilityData, function(p)
		p:getPlayer():getAttribute(attribute.GENERIC_MAX_HEALTH):setBaseValue(p:getPlayer():getAttribute(attribute.GENERIC_MAX_HEALTH):getDefaultValue())
		p:getPlayer():setWalkSpeed(0.2)
	end)
end

function rollStat(player)
	local healthStat = math.random(15, 30)
	local speedStat = math.random(2500, 5000)
	
	player:getAttribute(attribute.GENERIC_MAX_HEALTH):setBaseValue(healthStat)
	player:setWalkSpeed(speedStat / 10000.0)
	game.sendMessage(player, "§2[§a말§2] §a체력 : " .. healthStat .. " / 속도 : " .. speedStat / 10000.0 .. "로 재설정 되었습니다.")
	player:getWorld():playSound(player:getLocation(), import("$.Sound").ENTITY_HORSE_AMBIENT, 1, 1)
end