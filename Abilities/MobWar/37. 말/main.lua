function main(abilityData)
	plugin.registerEvent(abilityData, "PlayerInteractEvent", 2000, function(a, e)
		if e:getAction():toString() == "RIGHT_CLICK_AIR" or e:getAction():toString() == "RIGHT_CLICK_BLOCK" then
			if e:getItem() ~= nil then
				if game.isAbilityItem(e:getItem(), "WHEAT") then
					if game.checkCooldown(e:getPlayer(), a, 1) then
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
		if game.checkCooldown(e:getPlayer(), a, 1) then
			rollStat(e:getPlayer())
		end
	end)
	
	plugin.onPlayerEnd(abilityData, function(p)
		player:getAttribute(attribute.GENERIC_MAX_HEALTH):setHealth(player:getAttribute(attribute.GENERIC_MAX_HEALTH):getDefaultValue())
		player:setWalkSpeed(speedStat)
	end)
end

function rollStat(player)
	local healthStat = math.random(15, 30)
	local speedStat = math.random()
	while (speedStat >= 0.25 and speedStat <= 0.5) do
		speedStat = math.random()
	end
	
	player:getAttribute(attribute.GENERIC_MAX_HEALTH):setHealth(healthStat)
	player:setWalkSpeed(speedStat)
	game.sendMessage(player, "§2[§a말§2] §a체력 : " .. healthStat .. " / 속도 : " .. speedStat "로 재설정 되었습니다.")
end