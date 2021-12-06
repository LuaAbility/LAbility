function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	local material = import("$.Material")
		
	plugin.registerEvent(abilityData, "PlayerInteractEvent", 400, function(a, e)
		if e:getAction():toString() == "RIGHT_CLICK_BLOCK" then
			if e:getClickedBlock() ~= nil then
				if string.find(e:getClickedBlock():toString(), "BED") and e:getClickedBlock():toString() ~= "BEDROCK" then
					if game.checkCooldown(e:getPlayer(), a, 0) then
						e:setCancelled(true)
						local randomNumber = math.random(100)
						if randomNumber <= 1 then
							local itemStack = newInstance("$.inventory.ItemStack", {material.ELYTRA, 1})
							plugin.getServer():getWorlds():get(0):dropItemNaturally(e:getPlayer():getLocation(), itemStack)
						elseif randomNumber <= 10 then
							local itemStack = newInstance("$.inventory.ItemStack", {material.ENDER_PEARL, math.random(10)})
							plugin.getServer():getWorlds():get(0):dropItemNaturally(e:getPlayer():getLocation(), itemStack)
						elseif randomNumber <= 20 then
							local itemStack = newInstance("$.inventory.ItemStack", {material.PHANTOM_MEMBRANE, math.random(5)})
							plugin.getServer():getWorlds():get(0):dropItemNaturally(e:getPlayer():getLocation(), itemStack)
						elseif randomNumber <= 30 then
							local itemStack = newInstance("$.inventory.ItemStack", {material.STRING, math.random(10)})
							plugin.getServer():getWorlds():get(0):dropItemNaturally(e:getPlayer():getLocation(), itemStack)
						elseif randomNumber <= 50 then
							local itemStack = newInstance("$.inventory.ItemStack", {material.WHITE_WOOL, math.random(5)})
							plugin.getServer():getWorlds():get(0):dropItemNaturally(e:getPlayer():getLocation(), itemStack)
						elseif randomNumber <= 70 then
							local itemStack = newInstance("$.inventory.ItemStack", {material.RABBIT, math.random(5)})
							plugin.getServer():getWorlds():get(0):dropItemNaturally(e:getPlayer():getLocation(), itemStack)
						else
							local itemStack = newInstance("$.inventory.ItemStack", {material.CHICKEN, math.random(3)})
							plugin.getServer():getWorlds():get(0):dropItemNaturally(e:getPlayer():getLocation(), itemStack)
						end
					end
				end
			end
		end
	end) 
	
	plugin.addPassiveScript(abilityData, 1, function(p)
		local players = util.getTableFromList(game.getPlayers())
		for i = 1, #players do
			if players[i]:getPlayer() ~= p then
				if (p:getLocation():distance(players[i]:getPlayer():getLocation()) <= 10) then
					p:addPotionEffect(newInstance("$.potion.PotionEffect", {effect.SPEED, 60, 1}))
				end
			end
		end
	end)
end