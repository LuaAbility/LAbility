function main(abilityData)
	local abilityUse = 0
	plugin.registerEvent(abilityData, "PlayerInteractEvent", 200, function(a, e)
		if e:getAction():toString() == "RIGHT_CLICK_AIR" or e:getAction():toString() == "RIGHT_CLICK_BLOCK" then
			if e:getItem() ~= nil then
				if game.isAbilityItem(e:getItem(), "EMERALD") then
					if game.checkCooldown(e:getPlayer(), a, 0) then
						e:setCancelled(true)
						local itemStack = { newInstance("$.inventory.ItemStack", {e:getMaterial(), 1}) }
						e:getPlayer():getInventory():removeItem(itemStack)
						
						if abilityUse < 2 then levelOne(e:getPlayer())
						elseif abilityUse < 4 then levelTwo(e:getPlayer())
						elseif abilityUse < 6 then levelThree(e:getPlayer())
						elseif abilityUse < 8 then levelFour(e:getPlayer())
						else levelFive(e:getPlayer()) end
						
						abilityUse = abilityUse + 1
						
						if abilityUse == 2 then game.sendMessage(e:getPlayer(), "§6[§e주민§6] §7수습생§e이 되었습니다. 다음 능력 발동부터 적용됩니다.") end
						if abilityUse == 4 then game.sendMessage(e:getPlayer(), "§6[§e주민§6] §6기능공§e이 되었습니다. 다음 능력 발동부터 적용됩니다.") end
						if abilityUse == 6 then game.sendMessage(e:getPlayer(), "§6[§e주민§6] §a전문가§e가 되었습니다. 다음 능력 발동부터 적용됩니다.") end
						if abilityUse == 8 then game.sendMessage(e:getPlayer(), "§6[§e주민§6] §b달인§e이 되었습니다. 다음 능력 발동부터 적용됩니다.") end
						
						e:getPlayer():getWorld():spawnParticle(import("$.Particle").VILLAGER_HAPPY, e:getPlayer():getLocation():add(0,1,0), 100, 0.5, 1, 0.5, 0.05)
						e:getPlayer():getWorld():playSound(e:getPlayer():getLocation(), import("$.Sound").ENTITY_VILLAGER_YES, 0.25, 1)
					end
				end
			end
		end
	end)
end


function levelOne(player)
	local randomNumber = math.random(3)
	if randomNumber == 1 then
		local itemStack = newInstance("$.inventory.ItemStack", {import("$.Material").BREAD, math.random(3, 6)})
		player:getWorld():dropItemNaturally(player:getLocation(), itemStack)
	elseif randomNumber == 2 then
		local itemStack = newInstance("$.inventory.ItemStack", {import("$.Material").ARROW, math.random(3, 16)})
		player:getWorld():dropItemNaturally(player:getLocation(), itemStack)
	else
		local itemStack = newInstance("$.inventory.ItemStack", {import("$.Material").IRON_INGOT, math.random(3, 10)})
		player:getWorld():dropItemNaturally(player:getLocation(), itemStack)
	end
end

function levelTwo(player)
	local randomNumber = math.random(3)
	if randomNumber == 1 then
		local itemStack = newInstance("$.inventory.ItemStack", {import("$.Material").BOW, 1})
		player:getWorld():dropItemNaturally(player:getLocation(), itemStack)
	elseif randomNumber == 2 then
		local itemStack = newInstance("$.inventory.ItemStack", {import("$.Material").IRON_SWORD, 1})
		local itemMeta = itemStack:getItemMeta()
		itemMeta:setUnbreakable(true)
		itemStack:setItemMeta(itemMeta)
		player:getWorld():dropItemNaturally(player:getLocation(), itemStack)
	else
		local itemStack = newInstance("$.inventory.ItemStack", {import("$.Material").LAPIS_LAZULI, math.random(5, 10)})
		player:getWorld():dropItemNaturally(player:getLocation(), itemStack)
	end
end


function levelThree(player)
	local randomNumber = math.random(3)
	if randomNumber == 1 then
		local itemStack = newInstance("$.inventory.ItemStack", {import("$.Material").CROSSBOW, 1})
		player:getWorld():dropItemNaturally(player:getLocation(), itemStack)
	elseif randomNumber == 2 then
		local itemStack = newInstance("$.inventory.ItemStack", {import("$.Material").ENDER_PEARL, math.random(3, 6)})
		player:getWorld():dropItemNaturally(player:getLocation(), itemStack)
	else
		local itemStack = newInstance("$.inventory.ItemStack", {import("$.Material").BOOKSHELF, math.random(6, 12)})
		player:getWorld():dropItemNaturally(player:getLocation(), itemStack)
	end
end


function levelFour(player)
	local randomNumber = math.random(3)
	if randomNumber == 1 then
		local itemStack = newInstance("$.inventory.ItemStack", {import("$.Material").ENCHANTING_TABLE, 1})
		player:getWorld():dropItemNaturally(player:getLocation(), itemStack)
	elseif randomNumber == 2 then
		local itemStack = newInstance("$.inventory.ItemStack", {import("$.Material").EXPERIENCE_BOTTLE, math.random(10, 20)})
		player:getWorld():dropItemNaturally(player:getLocation(), itemStack)
	else
		local itemStack = newInstance("$.inventory.ItemStack", {import("$.Material").GOLDEN_APPLE, math.random(1, 3)})
		player:getWorld():dropItemNaturally(player:getLocation(), itemStack)
	end
end


function levelFive(player)
	local randomNumber = math.random(3)
	if randomNumber == 1 then
		local itemStack = newInstance("$.inventory.ItemStack", {import("$.Material").DIAMOND, math.random(6, 15)})
		player:getWorld():dropItemNaturally(player:getLocation(), itemStack)
	elseif randomNumber == 2 then
		local itemStack = newInstance("$.inventory.ItemStack", {import("$.Material").ENCHANTED_GOLDEN_APPLE, 1})
		player:getWorld():dropItemNaturally(player:getLocation(), itemStack)
	else
		local itemStack = newInstance("$.inventory.ItemStack", {import("$.Material").EMERALD, 2})
		player:getWorld():dropItemNaturally(player:getLocation(), itemStack)
	end
end