![LAbility](https://user-images.githubusercontent.com/30228621/145702029-17ecfee6-6838-4405-91e3-2bdc88564e19.png)
### License
이 플러그인은 [**LAbility 라이센스**](https://github.com/MINUTE1084/LAbility/blob/master/LICENSE.md) 가 적용됩니다. 
### Contribute
LAbility는 Lua(루아)를 기반으로 하는 능력자 플러그인입니다.\
기존의 능력자 플러그인들에서 영감을 받아 제작되었지만, 모든 코드가 새롭게 작성되었습니다.\
당신만의 능력을 만들어 새로운 능력자 모드를 만들어보세요.

### How to make ability
LAbility에서 능력을 추가하기 위해서는 최소한 2가지의 파일이 필요합니다.
#### data.yml
```yaml
id: 'LA-001' #ID를 입력합니다. 플러그인 내에서 해당 ID로 능력을 구분하니 ID가 다른 능력과 겹치지 않도록 해주세요.
type: '테스트' #능력의 타입을 정합니다.
name: '테스트 데이터' #능력의 이름을 정합니다.
rank: 'A' #능력의 랭크를 정합니다.
description: | 
    테스트 데이터입니다.
    자신에게 재생 2 효과를 무한정으로 부여합니다.
#능력의 설명을 입력합니다.
#능력의 설명은 | 문자 다음 줄에서 입력할 수 있습니다.
#이 때, 반드시 개행이 이루어져야 합니다.
#작성이 어렵다면 YML 에디터를 사용해주세요.
```
data.yml 파일은 능력의 이름, 설명 등 기본적인 내용이 입력되는 파일입니다.\
만약 항목이 하나라도 입력되지 않으면, 능력이 로드되지 않습니다.

#### main.lua
 - 20 틱의 재생 2 효과 부여 (반복 딜레이: 20 틱)
```lua
function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	plugin.addPassiveScript(abilityData, 20, function(p)
		p:addPotionEffect(newInstance("$.potion.PotionEffect", {effect.REGENERATION, 20, 1}))
	end)
end
```

 - 플레이어 타격 시 자신에게 100 틱의 재생 2 효과 부여 (재발동 대기시간: 500 틱)
```lua
function main(abilityData)
	local effect = import("$.potion.PotionEffectType")
	plugin.registerEvent(abilityData, "EntityDamageByEntityEvent", 500, function(a, e)
		if e:getDamager():getType():toString() == "PLAYER" then
			if game.checkCooldown(e:getDamager(), a, 0) then
				e:getDamager():addPotionEffect(newInstance("$.potion.PotionEffect", {effect.REGENERATION, 100, 1}))
			end
		end
	end)
end
```
main.lua 파일은 능력을 구현하는 파일입니다.\
Lua(루아) 언어를 통해 구현이 가능하며, 파일에는 **main(abilityData)** 함수가 무조건 존재해야합니다.\
Lua의 추가 API는 Lukkit ( Docs : https://docs.lukkit.net/ )을 기반으로 사용하고 있습니다.\
다만, 해당 플러그인은 Lukkit의 API와 다른 점이 존재합니다.
 - Banner와 관련된 API는 삭제되었습니다.
 - ChatColor와 관련된 API는 삭제되었습니다.
 - Skull와 관련된 API는 삭제되었습니다.
 - Config와 관련된 API는 삭제되었습니다.
 - Plugin와 관련된 일부 API는 삭제되었습니다.
   - 삭제된 API : onLoad, onEnable, onDisable, addCommand, setNaggable, exportResource, getStorageObject, getPlugin
 - Plugin와 관련된 일부 API가 변경 및 추가 되었습니다.
   - plugin.registerEvent 는 해당 플러그인에서 4가지 Parameter를 요구합니다.
     - Parameter : main에서 전달받은 Ability, 이벤트의 이름, 해당 능력의 쿨타임(tick), 함수
       - 함수는 Paremeter로 해당 능력 데이터, 발생한 이벤트 데이터를 전달받습니다.
   - plugin.addPassiveScript가 추가됩니다. 해당 스크립트는 게임 시작 시 반복하여 실행됩니다.
     - Parameter : main에서 전달받은 Ability, 해당 능력의 반복 쿨타임(tick), 함수
       - 함수는 Paremeter로 해당 능력을 보유한 플레이어를 전달받습니다.
 - 게임과 관련된 API가 추가 되었습니다.
    - game.getPlayers가 추가됩니다. 현재 참여 중인 모든 플레이어를 불러옵니다.
    - game.checkCooldown 추가됩니다. 현재 쿨타임이 완료되었는지 확인합니다.
     - Parameter : 능력을 사용하는 플레이어, Ability, 기능의 ID

### Special Thanks
Lukkit - https://github.com/Lukkit/Lukkit (Lua 연동 코드 참조)
***
### Add-on
[![MobWar](https://user-images.githubusercontent.com/30228621/145704497-ee1ab8c1-725e-478f-bc11-7caa54d0a779.png)](https://github.com/LuaAbility/MobWar)
