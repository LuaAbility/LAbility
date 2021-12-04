
plugin.registerEvent("EntityDamageByEntityEvent", function(e)
    e:getDamager():sendMessage("테스트 완료!")
    if util.getClass(e:getEntity()) == "org.bukkit.entity.Player"  and util.getClass(e:getDamager()) == "org.bukkit.entity.Player" then
        e:getDamager():sendMessage("테스트 완료!")
    end
end)