function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
		
	plugin.registerEvent(abilityData, "PlayerItemConsumeEvent", 0, function(a, e)
		if e:getItem():getType():toString() == "COOKED_BEEF" or e:getItem():getType():toString() == "BEEF" or e:getItem():getType():toString() == "MUSHROOM_STEW" then
			if game.checkCooldown(e:getPlayer(), a, 0) then
				e:getPlayer():addPotionEffect(newInstance("$.potion.PotionEffect", {effect.BLINDNESS, 100, 0}))
			end
		end
	end) 
	
	plugin.registerEvent(abilityData, "PlayerInteractEvent", 0, function(a, e)
		if e:getAction():toString() == "RIGHT_CLICK_AIR" or e:getAction():toString() == "RIGHT_CLICK_BLOCK" then
			if e:getItem() ~= nil then
				if e:getItem():getType():toString() == "SHEARS" then
					if game.checkCooldown(e:getPlayer(), a, 1) then
						game.changeAbility(e:getPlayer(), a, "LA-MW-003", false)
					end
				end
			end
		end
	end)
end