package com.LAbility;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandManager implements CommandExecutor {
	public final LAbilityMain main;

	public CommandManager(LAbilityMain main_) {
		main = main_;
	}
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("la")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("\2474[\247cLAbility\2474] \247c플레이어만 명령어 사용이 가능합니다.");
				return true;
			}
			Player senderPlayer = (Player) sender;
			if (args.length == 0) {
				sender.sendMessage("\2476-------[\247eLAbility\2476]-------");
				sender.sendMessage("\2476/la \247eplayer \247f: \247a플레이어 용 명령어를 확인합니다.");
				sender.sendMessage("\2476/la \247eadmin \247f: \247a관리자 용 명령어를 확인합니다.");
				sender.sendMessage("\2476/la \247eutil \247f: \247a유틸 명령어를 확인합니다.");
				sender.sendMessage();
				return true;
			}
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("player")) {
					sender.sendMessage("\2476-------[\247ePlayer Command\2476]-------");
					sender.sendMessage("\2476/la \247echeck \247f: \247a자신의 능력을 확인합니다.");
					sender.sendMessage("\2476/la \247eyes \247f: \247a현재 능력을 사용합니다.");
					sender.sendMessage("\2476/la \247eno \247f: \247a현재 능력을 사용하지 않습니다. 이 때, 다른 능력으로 변경됩니다.");
					sender.sendMessage("\2476/la \247ea \247f: \247a일부 능력의 경우, 해당 명령어를 사용하여 능력을 사용합니다.");
					return true;
				}
				if (args[0].equalsIgnoreCase("admin")) {
					sender.sendMessage("\2476-------[\247eAdmin Command\2476]-------");
					sender.sendMessage("\2476/la \247estart \247f: \247a게임을 시작합니다.");
					sender.sendMessage("\2476/la \247estop \247f: \247a게임을 중지합니다.");
					sender.sendMessage("\2476/la \247eskip \247f: \247a모든 플레이어의 능력을 모두 확정합니다.");
					sender.sendMessage("\2476/la \247ereroll <Player>\247f: \247a플레이어의 능력을 재추첨합니다. 공란 시 모두 변경.");
					sender.sendMessage("\2476/la \247eob <Player>\247f: \247a해당 플레이어를 게임에서 제외합니다.");
					sender.sendMessage("\2476/la \247esee <Player> \247f: \247a플레이어에게 할당된 능력들을 확인합니다.");
					sender.sendMessage("\2476/la \247eadd <Player> <AbilityID> \247f: \247a플레이어에게 해당 능력을 추가합니다.");
					sender.sendMessage("\2476/la \247eremove <Player> <AbilityID> \247f: \247a플레이어에게서 해당 능력을 제거합니다. ID 공란 시 모두 제거.");
					sender.sendMessage("\2476/la \247elist \247f: \247a모든 플레이어의 능력을 확인합니다.");
					return true;
				}
				if (args[0].equalsIgnoreCase("util")) {
					sender.sendMessage("\2476-------[\247eAdmin Command\2476]-------");
					sender.sendMessage("\2476/la \247eablist <Page> \247f: \247a현재 로드된 능력 리스트를 확인합니다.");
					sender.sendMessage("\2476/la \247eability <AbilityID> \247f: \247a해당 능력의 정보를 확인합니다.");
					return true;
				}
				if (args[0].equalsIgnoreCase("check")) {
					for (LAPlayer lap : main.gameManager.players) {
						if (lap.player.equals(senderPlayer)) {
							if ((args.length > 1)) {
								lap.CheckAbility(senderPlayer, Integer.parseInt(args[1]));
								return true;
							} else {
								lap.CheckAbility(senderPlayer, -1);
								return true;
							}
						}
					}
					senderPlayer.sendMessage("\2474[\247cLAbility\2474] \247c게임에 참여 중이 아닙니다.");
					return true;
				}

				if (args[0].equalsIgnoreCase("ob")) {
					if ((args.length > 1)) {
						for (Player p : main.getServer().getOnlinePlayers()) {
							if (p.getName().equals(args[1])) {
								if (main.gameManager.players.contains(p)){
									main.gameManager.players.remove(p);
									if (!senderPlayer.equals(p)) {
										sender.sendMessage("\2478[\2477LAbility\2478] \2477" + args[1] + " 플레이어가 옵저버로 설정되었습니다.");
										sender.sendMessage("\2478[\2477LAbility\2478] \2477" + "재 접속 시, 게임에 다시 참여할 수 있습니다.");
									}
									p.sendMessage("\2478[\2477LAbility\2478] \2477" + "옵저버로 설정되었습니다.");
									p.sendMessage("\2478[\2477LAbility\2478] \2477" + "재 접속 시, 게임에 다시 참여할 수 있습니다.");
									return true;
								}
								else {
									sender.sendMessage("\2474[\247cLAbility\2474] \247c해당 플레이어는 게임에 참가하고 있지 않습니다.");
									return true;
								}
							}
						}
						sender.sendMessage("\2474[\247cLAbility\2474] \247c플레이어가 존재하지 않습니다.");
						return true;
					}
					sender.sendMessage("\2474[\247cLAbility\2474] \247c플레이어 이름을 입력해주세요.");
					return true;
				}

				if (args[0].equalsIgnoreCase("ablist")) {
					int index = 1;
					int allData = main.abilities.size();
					if ((args.length > 1)) index = Integer.parseInt(args[1]);
					sender.sendMessage("\2476-------[\247ePage " + (index) + "/" + (allData / 8 + 1) +"\2476]-------");
					for (int i = 0; i < 8; i++) {
						int targetIndex = ((index - 1) * 8 + i);
						if (targetIndex >= allData) break;
						sender.sendMessage("\2476[" + main.abilities.get(targetIndex).abilityID + "] \247e" + main.abilities.get(targetIndex).abilityName);
					}
					return true;
				}

				if (args[0].equalsIgnoreCase("ability")) {
					if ((args.length > 1)) {
						int index = main.abilities.indexOf(args[1]);
						if (index >= 0) {
							main.abilities.get(index).ExplainAbility(senderPlayer);
							return true;
						}
						else{
							sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 ID 입니다.");
						}
					}
					return true;
				}
			}
		}
		return false;
	}
}
