function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	local material = import("$.Material")
	local color = import("$.Color")
	
	plugin.registerEvent(abilityData, "PlayerInteractEvent", 400, function(a, e)
		if e:getAction():toString() == "RIGHT_CLICK_AIR" or e:getAction():toString() == "RIGHT_CLICK_BLOCK" then
			if e:getItem() ~= nil then
				if game.isAbilityItem(e:getItem(), "GOLD_INGOT") then
					if game.checkCooldown(e:getPlayer(), a, 0) then
						e:setCancelled(true)
						local itemStack = { newInstance("$.inventory.ItemStack", {e:getMaterial(), 1}) }
						e:getPlayer():getInventory():removeItem(itemStack)
						
						local randomNumber = math.random(100)
						if randomNumber <= 1 then
							local itemStack = newInstance("$.inventory.ItemStack", {material.END_CRYSTAL, 1})
							e:getPlayer():getWorld():dropItemNaturally(e:getPlayer():getLocation(), itemStack)
						elseif randomNumber <= 10 then
							local itemStack = newInstance("$.inventory.ItemStack", {material.GOLDEN_SWORD, 1})
							local itemMeta = itemStack:getItemMeta()
							itemMeta:setUnbreakable(true)
							itemStack:setItemMeta(itemMeta)
							e:getPlayer():getWorld():dropItemNaturally(e:getPlayer():getLocation(), itemStack)
						elseif randomNumber <= 20 then
							local itemStack = newInstance("$.inventory.ItemStack", {material.DIAMOND, math.random(5)})
							e:getPlayer():getWorld():dropItemNaturally(e:getPlayer():getLocation(), itemStack)
						elseif randomNumber <= 30 then
							local itemStack = newInstance("$.inventory.ItemStack", {material.POTION, 1})
							local itemMeta = itemStack:getItemMeta()
							itemMeta:addCustomEffect(newInstance("$.potion.PotionEffect", {effect.FIRE_RESISTANCE, 6000, 0}, true))
							itemMeta:setDisplayName("§r§b화염 저항 포션")
							itemMeta:setColor(color.ORANGE)
							itemStack:setItemMeta(itemMeta)
							e:getPlayer():getWorld():dropItemNaturally(e:getPlayer():getLocation(), itemStack)
						elseif randomNumber <= 50 then
							local itemStack = newInstance("$.inventory.ItemStack", {material.ENDER_PEARL, math.random(5)})
							e:getPlayer():getWorld():dropItemNaturally(e:getPlayer():getLocation(), itemStack)
						elseif randomNumber <= 70 then
							local itemStack = newInstance("$.inventory.ItemStack", {material.ARROW, math.random(6, 12)})
							e:getPlayer():getWorld():dropItemNaturally(e:getPlayer():getLocation(), itemStack)
						else
							local itemStack = newInstance("$.inventory.ItemStack", {material.IRON_INGOT, math.random(3, 15)})
							e:getPlayer():getWorld():dropItemNaturally(e:getPlayer():getLocation(), itemStack)
						end
						e:getPlayer():getWorld():spawnParticle(import("$.Particle").SMOKE_NORMAL, e:getPlayer():getLocation():add(0,1,0), 150, 0.5, 1, 0.5, 0.05)
						e:getPlayer():getWorld():playSound(e:getPlayer():getLocation(), import("$.Sound").ENTITY_PIGLIN_CELEBRATE, 0.5, 1)
					end
				end
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "EntityTargetLivingEntityEvent", 0, function(a, e)
		if e:getTarget() ~= nil and e:getEntity() ~= nil then
			if e:getTarget():getType():toString() == "PLAYER" and e:getEntity():getType():toString() == "PIGLIN" then
				if game.checkCooldown(e:getTarget(), a, 1) then
					e:setTarget(nil)
					e:setCancelled(true)
				end
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "EntityDamageByEntityEvent", 0, function(a, e)
		if e:getDamager():getType():toString() == "PLAYER" then
			local item = e:getDamager():getInventory():getItemInMainHand()
			if string.find(item:getType():toString(), "GOLD") then
				if game.checkCooldown(e:getDamager(), a, 2) then
					e:setDamage(e:getDamage() * 1.5)
				end
			end
		end
	end)
end