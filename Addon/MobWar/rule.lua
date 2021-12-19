function rule()
	local godMode = false -- 건들면 안됨!
	local border = nil -- 건들면 안됨!
	local material = import("$.Material") -- 건들면 안됨!
	
	local godModeTick = 600 -- 무적 시간 (틱)
	local startX = 952.5 -- 시작 시 텔레포트 할 좌표 / 월드보더의 기준 좌표
	local startY = 33.0 -- 시작 시 텔레포트 할 좌표 / 월드보더의 기준 좌표
	local startZ = -155.5 -- 시작 시 텔레포트 할 좌표 / 월드보더의 기준 좌표
	
	local startBorderSize = 1000.0 -- 시작 시 월드 보더의 크기
	local endBorderSize = 15.0 -- 마지막 월드 보더의 크기
	local borderChangeSecond = 60 -- 월드보더의 크기가 변화하는 시간
	local endBorderTick = 1200 -- 월드보더 크기 축소 시작 시간 (틱)
	
	local startItem = {  -- 시작 시 지급 아이템
		newInstance("$.inventory.ItemStack", {material.IRON_SWORD, 1}), 
		newInstance("$.inventory.ItemStack", {material.IRON_INGOT, 64})
	}
	
	plugin.raffleAbilityOption(true) -- 시작 시 능력을 추첨할 지 결정합니다.
	plugin.abilityAmountOption(1, false) -- 능력의 추첨 옵션입니다. 숫자로 능력의 추첨 개수를 정하고, true/false로 다른 플레이어와 능력이 중복될 수 있는지를 정합니다. 같은 플레이어에게는 중복된 능력이 적용되지 않습니다.
	plugin.abilityItemOption(true, material.IRON_INGOT) -- 능력 발동 아이템 옵션입니다. true/false로 모든 능력의 발동 아이템을 통일 할 것인지 정하고, Material을 통해 통일할 아이템을 설정합니다.
	plugin.abilityCheckOption(true) -- 능력 확인 옵션입니다. 플레이어가 자신의 능력을 확인할 수 있는 지 정합니다.
	plugin.cooldownMultiplyOption(1.0) -- 능력 쿨타임 옵션입니다. 해당 값만큼 쿨타임 값에 곱해져 적용됩니다. (예: 0.5일 경우 쿨타임이 기본 쿨타임의 50%, 2.0일 경우 쿨타임이 기본 쿨타임의 200%)
	
	-- 시작 시 무적 설정 코드
	plugin.registerRuleTimer(0, function()
		godMode = true
		game.broadcastMessage("§6[§eLAbility§6] §e게임 시작 후 ".. (godModeTick / 20.0 / 60.0) .. "분 간 무적으로 진행됩니다.")
	end)
	
	-- 시작 시 텔레포트 코드
	plugin.registerRuleTimer(0, function()
		local players = util.getTableFromList(game.getPlayers())
		for i = 1, #players do
			players[i]:getPlayer():teleport(newInstance("$.Location", { players[i]:getPlayer():getWorld(), startX, startY, startZ }) )
		end
	end)
	
	-- 시작 시 힐 코드
	plugin.registerRuleTimer(0, function()
		local players = util.getTableFromList(game.getPlayers())
		for i = 1, #players do
			players[i]:getPlayer():setHealth(players[i]:getPlayer():getAttribute(import("$.attribute.Attribute").GENERIC_MAX_HEALTH):getBaseValue())
			players[i]:getPlayer():setFoodLevel(20)
		end
	end)
	
	-- 시작 시 게임모드 변경 코드
	plugin.registerRuleTimer(0, function()
		local players = util.getTableFromList(game.getPlayers())
		for i = 1, #players do
			players[i]:getPlayer():setGameMode(import("$.GameMode").SURVIVAL)
		end
	end)
	
	-- 시작 시 인벤토리 초기화 및 아이템 지급 코드
	plugin.registerRuleTimer(0, function()
		local players = util.getTableFromList(game.getPlayers())
		for i = 1, #players do
			players[i]:getPlayer():getInventory():clear() -- 인벤토리 초기화
			players[i]:getPlayer():getInventory():addItem(startItem) -- 아이템 지급
		end
	end)
	
	-- 시작 시 월드보더 설정 코드
	plugin.registerRuleTimer(0, function()
		local player = util.getTableFromList(game.getPlayers())[1]:getPlayer()
		border = player:getWorld():getWorldBorder()
		border:setCenter(startX, startZ)
		border:setSize(startBorderSize)
		
		game.broadcastMessage("§6[§eLAbility§6] §e게임 시작 후 ".. (endBorderTick / 20.0 / 60.0) .. "분 이후 월드의 크기가 작아집니다.")
	end)
	
	-- 보더 축소 코드
	plugin.registerRuleTimer(endBorderTick, function()
		if border ~= nil then
			border:setSize(endBorderSize, borderChangeSecond)
			game.broadcastMessage("§4[§cLAbility§4] §c지금부터 월드의 크기가 작아집니다!")
			game.broadcastMessage("§4[§cLAbility§4] §c크기는 ".. borderChangeSecond .. "초 동안 축소됩니다.")
			game.broadcastMessage("§4[§cLAbility§4] §c기준 좌표 : X - " .. startX .. " / Z - " .. startZ)
			game.broadcastMessage("§4[§cLAbility§4] §c기준 크기 : " .. endBorderSize .. "칸")
		end
	end)
	
	-- 무적 동작 코드
	plugin.registerRuleEvent("EntityDamageEvent", function(e)
		if e:getEntity():getType():toString() == "PLAYER" and godMode then
			e:setCancelled(true)
		end
	end)
	
	-- 무적 종료 코드
	plugin.registerRuleTimer(godModeTick, function()
		godMode = false
		game.broadcastMessage("§4[§cLAbility§4] §c무적시간이 종료되었습니다. 이제 데미지를 입습니다.")
	end)
	
	-- 사망 시 탈락
	plugin.registerRuleEvent("PlayerDeathEvent", function(e)
		if e:getEntity():getType():toString() == "PLAYER" then
			game.eliminatePlayer(e:getEntity())
			e:getEntity():getWorld():strikeLightningEffect(e:getEntity():getLocation())
			game.broadcastMessage("§4[§cLAbility§4] §c" .. e:getEntity():getName() .. "님이 탈락하셨습니다.")
			game.sendMessage(e:getEntity(), "§4[§cLAbility§4] §c사망으로 인해 탈락하셨습니다.")
			
			local players = util.getTableFromList(game.getPlayers())
			if #players == 1 then
				game.broadcastMessage("§6[§eLAbility§6] §e게임이 종료되었습니다.")
				game.broadcastMessage("§6[§eLAbility§6] §e" .. players[1]:getPlayer():getName() .. "님이 우승하셨습니다!")
				game.endGame()
			elseif #players < 1 then
				game.broadcastMessage("§6[§eLAbility§6] §e게임이 종료되었습니다.")
				game.broadcastMessage("§6[§eLAbility§6] §e우승자가 없습니다.")
				game.endGame()
			end
		end
	end)
end