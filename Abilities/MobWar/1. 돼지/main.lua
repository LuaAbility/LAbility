function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	
	plugin.registerEvent(abilityData, "PlayerItemConsumeEvent", 0, function(a, e)
		if e:getItem():getType():toString() == "COOKED_PORKCHOP" or e:getItem():getType():toString() == "PORKCHOP" then
			if game.checkCooldown(e:getPlayer(), a, 0) then
				e:getPlayer():addPotionEffect(newInstance("$.potion.PotionEffect", {effect.BLINDNESS, 100, 0}))
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "PlayerInteractEvent", 2000, function(a, e)
		if e:getAction():toString() == "RIGHT_CLICK_AIR" or e:getAction():toString() == "RIGHT_CLICK_BLOCK" then
			if e:getItem() ~= nil then
				if game.isAbilityItem(e:getItem(), "CARROT_ON_A_STICK") then
					if game.checkCooldown(e:getPlayer(), a, 1) then
						e:getPlayer():addPotionEffect(newInstance("$.potion.PotionEffect", {effect.SPEED, 800, 0}))
					end
				end
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "EntityDamageByEntityEvent", 0, function(a, e)
		if e:getDamager():getType():toString() == "LIGHTNING" and e:getEntity():getType():toString() == "PLAYER" then
			if game.checkCooldown(e:getEntity(), a, 2) then
				game.changeAbility(e:getEntity(), a, "LA-MW-014", false)
			end
		end
	end)

end