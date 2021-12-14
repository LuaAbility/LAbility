function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	
	plugin.registerEvent(abilityData, "PlayerInteractEvent", 1600, function(a, e)
		if e:getAction():toString() == "RIGHT_CLICK_AIR" or e:getAction():toString() == "RIGHT_CLICK_BLOCK" then
			local arrow = {newInstance("$.inventory.ItemStack", { import("$.Material").ARROW, 1 }) }
			if e:getItem() ~= nil then
				if game.isAbilityItem(e:getItem(), "GLASS_BOTTLE") then
					if game.checkCooldown(e:getPlayer(), a, 0) then
						local maxHealth = e:getPlayer():getAttribute(attribute.GENERIC_MAX_HEALTH):getValue()
						if (e:getPlayer():getHealth() + 8 >= maxHealth) then e:getPlayer():setHealth(maxHealth)
						else e:getPlayer():setHealth(e:getPlayer():getHealth() + 8) end
						e:getPlayer():getWorld():spawnParticle(import("$.Particle").HEART, e:getPlayer():getEyeLocation(), 10, 0.5, 1, 0.5, 0.05)
						e:getPlayer():getWorld():playSound(e:getPlayer():getLocation(), import("$.Sound").ENTITY_WITCH_DRINK, 0.25, 1)
					end
				end
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "EntityDamageByEntityEvent", 500, function(a, e)
		if e:getDamager():getType():toString() == "PLAYER" and e:getEntity():getType():toString() == "PLAYER" then
			local item = { e:getDamager():getInventory():getItemInMainHand() }
			if game.isAbilityItem(item[1], "GLASS_BOTTLE") then
				if game.checkCooldown(e:getDamager(), a, 1) then
					local randomData = math.random(3)
					if randomData == 1 then e:getEntity():addPotionEffect(newInstance("$.potion.PotionEffect", {effect.SLOW, 300, 0})) end
					if randomData == 2 then e:getEntity():addPotionEffect(newInstance("$.potion.PotionEffect", {effect.POISON, 300, 0})) end
					if randomData == 3 then e:getEntity():addPotionEffect(newInstance("$.potion.PotionEffect", {effect.WEAKNESS, 300, 0})) end
					e:getEntity():getWorld():spawnParticle(import("$.Particle").SPELL_WITCH, e:getEntity():getEyeLocation(), 150, 0.5, 1, 0.5, 0.05)
					e:getEntity():getWorld():playSound(e:getEntity():getLocation(), import("$.Sound").ENTITY_WITCH_CELEBRATE, 0.25, 1)
				end
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "EntityTargetLivingEntityEvent", 0, function(a, e)
		if e:getTarget() ~= nil and e:getEntity() ~= nil then
			if e:getTarget():getType():toString() == "PLAYER" and e:getEntity():getType():toString() == "WITCH" then
				if game.checkCooldown(e:getTarget(), a, 2) then
					e:setTarget(nil)
					e:setCancelled(true)
				end
			end
		end
	end)
end