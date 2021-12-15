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
						e:getPlayer():getWorld():spawnParticle(import("$.Particle").COMPOSTER, e:getPlayer():getLocation():add(0,1,0), 100, 0.5, 0.5, 0.5, 0.2)
						e:getPlayer():getWorld():playSound(e:getPlayer():getLocation(), import("$.Sound").ENTITY_PIG_AMBIENT, 0.25, 1)
					end
				end
			end
		end
	end)
	
	plugin.registerEvent(abilityData, "EntityDamageByEntityEvent", 0, function(a, e)
		if e:getDamager():getType():toString() == "LIGHTNING" and e:getEntity():getType():toString() == "PLAYER" then
			if game.checkCooldown(e:getEntity(), a, 2) then
				game.changeAbility(e:getEntity(), a, "LA-MW-014", false)
				e:getEntity():getWorld():spawnParticle(import("$.Particle").VILLAGER_ANGRY, e:getEntity():getLocation():add(0,1,0), 20, 0.5, 1, 0.5, 0.05)
				e:getEntity():getWorld():playSound(e:getEntity():getLocation(), import("$.Sound").ENTITY_ZOMBIFIED_PIGLIN_ANGRY, 1, 1)
			end
		end
	end)

end