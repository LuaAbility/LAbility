function main(abilityData)
	attribute = import("$.attribute.Attribute")
	plugin.registerEvent(abilityData, "EntityDamageEvent", 0, function(a, e)
		if e:getEntity():getType():toString() == "PLAYER" then
			if e:getEntity():getHealth() - e:getDamage() <= 0 then
				local maxHealth = e:getEntity():getAttribute(attribute.GENERIC_MAX_HEALTH)
				if maxHealth:getValue() < 2.0 then
					maxHealth:setBaseValue(20)
					game.sendMessage(e:getEntity(), "§2[§a슬라임§2] §a원래 체력으로 돌아갑니다.")
				else
					if game.checkCooldown(e:getEntity(), a, 0) then
						e:getEntity():setHealth(maxHealth:getValue() / 2.0)
						maxHealth:setBaseValue(maxHealth:getValue() / 2.0)
						game.sendMessage(e:getEntity(), "§2[§a슬라임§2] §a부활했습니다. 최대 체력이 " .. maxHealth:getValue() / 2.0 .. "칸이 됩니다.")
						e:setCancelled(true)
					end
				end
			end
			
		end
	end)
	
	plugin.onPlayerEnd(abilityData, function(p)
		p:getAttribute(attribute.GENERIC_MAX_HEALTH):setBaseValue(p:getAttribute(attribute.GENERIC_MAX_HEALTH):getDefaultValue())
	end)
	
	plugin.registerEvent(abilityData, "EntityTargetLivingEntityEvent", 0, function(a, e)
		if e:getTarget() ~= nil and e:getEntity() ~= nil then
			if e:getTarget():getType():toString() == "PLAYER" and e:getEntity():getType():toString() == "SLIME" then
				if game.checkCooldown(e:getTarget(), a, 1) then
					e:setTarget(nil)
					e:setCancelled(true)
				end
			end
		end
	end)
end