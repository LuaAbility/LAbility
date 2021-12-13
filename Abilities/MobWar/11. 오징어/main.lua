function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	
	plugin.registerEvent(abilityData, "EntityDamageByEntityEvent", 100, function(a, e)
		if e:getDamager():getType():toString() == "PLAYER" and e:getEntity():getType():toString() == "PLAYER" then
			if game.checkCooldown(e:getEntity(), a, 0) then
				e:getDamager():addPotionEffect(newInstance("$.potion.PotionEffect", {effect.BLINDNESS, 200, 0}))
				e:getDamager():getWorld():spawnParticle(import("$.Particle").ITEM_CRACK, e:getDamager():getLocation():add(0,1,0), 150, 0.5, 1, 0.5, 0.05, newInstance("$.inventory.ItemStack", {import("$.Material").BLACK_WOOL}))
				e:getDamager():getWorld():playSound(e:getDamager():getLocation(), import("$.Sound").ENTITY_SQUID_SQUIRT, 0.25, 1)
			end
		end
	end)
end