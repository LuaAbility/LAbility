attribute = import("$.attribute.Attribute")
function main(abilityData)
	plugin.registerEvent(abilityData, "EntityDamageEvent", 0, function(a, e)
		if e:getEntity():getType():toString() == "PLAYER" then
			if e:getEntity():getHealth() - e:getDamage() <= 0 then
				if e:getEntity():getAttribute(attribute.GENERIC_MAX_HEALTH):getValue() >= 2.0 then
					if game.checkCooldown(e:getEntity(), a, 0) then
						e:getEntity():getAttribute(attribute.GENERIC_MAX_HEALTH):setBaseValue(e:getEntity():getAttribute(attribute.GENERIC_MAX_HEALTH):getValue() / 2.0)
						e:getEntity():setHealth(e:getEntity():getAttribute(attribute.GENERIC_MAX_HEALTH):getValue())
						game.sendMessage(e:getEntity(), "§2[§a슬라임§2] §a부활했습니다. 최대 체력이 " .. e:getEntity():getAttribute(attribute.GENERIC_MAX_HEALTH):getValue() / 2.0 .. "칸이 됩니다.");
						e:setCancelled(true)
					end
				else
					e:getEntity():getAttribute(attribute.GENERIC_MAX_HEALTH):setBaseValue(20)
					game.sendMessage(e:getEntity(), "§2[§a슬라임§2] §a원래 체력으로 돌아갑니다.");
				end
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "EntityTargetLivingEntityEvent", 0, function(a, e)
		if e:getTarget():getType():toString() == "PLAYER" and e:getEntity():getType():toString() == "SLIME" then
			if game.checkCooldown(e:getTarget(), a, 1) then
				e:setTarget(nil)
				e:setCancelled(true)
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "EntityDamageByEntityEvent", 0, function(a, e)
		if e:getDamager():getType():toString() == "SLIME" and e:getEntity():getType():toString() == "PLAYER" then
			if game.checkCooldown(e:getEntity(), a, 2) then
				e:setCancelled(true)
			end
		end
	end)
end