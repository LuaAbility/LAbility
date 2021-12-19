function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	local targetPlayer = nil
	
	plugin.addPassiveScript(abilityData, 0, function(p)
		abilityPlayer = p
	end)
	
	plugin.addPassiveScript(abilityData, 200, function(p)
		phantom(p)
		if targetPlayer ~= nil then phantom(targetPlayer) end
	end)
	
	plugin.registerEvent(abilityData, "EntityDamageByEntityEvent", 24001, function(a, e)
		if e:getDamager():getType():toString() == "PLAYER" and e:getEntity():getType():toString() == "PLAYER" then
			local item = { e:getDamager():getInventory():getItemInMainHand() }
			if game.isAbilityItem(item[1], "IRON_INGOT") then
				if game.checkCooldown(e:getDamager(), a, 0) then
					targetPlayer = e:getEntity()
					game.sendMessage(e:getEntity(), "§c팬텀에게 능력이 공유되었습니다. 게임시간 기준 24시간이 지나면 능력 공유가 해제됩니다.")
					e:getEntity():getWorld():spawnParticle(import("$.Particle").REDSTONE, e:getEntity():getLocation():add(0,1,0), 150, 0.5, 1, 0.5, 0.05, newInstance("$.Particle$DustOptions", {import("$.Color").RED, 1}))
					e:getEntity():getWorld():playSound(e:getEntity():getLocation(), import("$.Sound").ENTITY_PHANTOM_BITE, 0.25, 1)
					
					util.runLater(function() 
						game.sendMessage(e:getEntity(), "§7팬텀의 능력 공유가 해제되었습니다.")
						targetPlayer = nil
					end, 24000)
				end
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "EntityTargetLivingEntityEvent", 0, function(a, e)
		if e:getTarget() ~= nil and e:getEntity() ~= nil then
			if e:getTarget():getType():toString() == "PLAYER" and e:getEntity():getType():toString() == "PHANTOM" then
				if game.checkCooldown(e:getTarget(), a, 1) then
					e:setTarget(nil)
					e:setCancelled(true)
				end
			end
		end
	end)
end

function phantom(p)
	if math.random(5) == 1 then
		local currentTime = p:getWorld():getTime() % 24000
		local newHealth = p:getHealth() + 4
		if newHealth > p:getAttribute(attribute.GENERIC_MAX_HEALTH):getValue() then newHealth = p:getAttribute(attribute.GENERIC_MAX_HEALTH):getValue() end
		
		if (currentTime < 13500 or currentTime > 23500) then 
			if abilityPlayer ~= nil then p:damage(4, abilityPlayer) 
			else p:damage(4, p) end
			game.sendMessage(p, "§4[§c팬텀§4] §c낮의 영향으로 데미지를 입습니다.")
			p:getWorld():playSound(p:getLocation(), import("$.Sound").ENTITY_PHANTOM_HURT, 0.25, 1)
		else 
			p:setHealth(newHealth) 
			game.sendMessage(p, "§1[§b팬텀§1] §b밤의 영향으로 체력을 회복합니다.")
		end
	end
end


