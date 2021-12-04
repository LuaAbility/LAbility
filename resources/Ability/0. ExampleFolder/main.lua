function main(abilityData)
	plugin.registerEvent("EntityDamageByEntityEvent", function(a, e)
		local effect = import("$.potion.PotionEffectType")
		
		if e:getCause():toString() == "ENTITY_ATTACK" then
			local damager = e:getDamager():getType():toString()
			local damagee = e:getEntity():getType():toString()
			
			if damager == "PLAYER" and damagee == "PLAYER" then
				local players = util.getTableFromList(game:getPlayers())
				print("a")
				for i, player in ipairs(players) do
					if player:getPlayer() == e:getDamager() and player:hasAbility(a) then
						print("b")
						e:getDamager():addPotionEffect(newInstance("$.potion.PotionEffect", {effect.NIGHT_VISION, 200, 1}))
					end
				end
			end
		end
	end, abilityData)
end