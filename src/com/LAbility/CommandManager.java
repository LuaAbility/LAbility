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
				sender.sendMessage("\2476/la \247eplayer \247f: \247a플레이어 용 명령어를 확인합니다."); // OK
				sender.sendMessage("\2476/la \247eadmin \247f: \247a관리자 용 명령어를 확인합니다."); // OK
				sender.sendMessage("\2476/la \247eutil \247f: \247a유틸 명령어를 확인합니다."); // OK
				sender.sendMessage();
				return true;
			}
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("player")) {
					sender.sendMessage("\2476-------[\247ePlayer Command\2476]-------");
					sender.sendMessage("\2476/la \247echeck \247f: \247a자신의 능력을 확인합니다."); // OK
					sender.sendMessage("\2476/la \247eyes \247f: \247a현재 능력을 사용합니다."); // OK
					sender.sendMessage("\2476/la \247eno \247f: \247a현재 능력을 사용하지 않습니다. 이 때, 다른 능력으로 변경됩니다."); // OK
					return true;
				}
				if (args[0].equalsIgnoreCase("admin")) {
					sender.sendMessage("\2476-------[\247eAdmin Command\2476]-------");
					sender.sendMessage("\2476/la \247estart \247f: \247a게임을 시작합니다.");
					sender.sendMessage("\2476/la \247estop \247f: \247a게임을 중지합니다.");
					sender.sendMessage("\2476/la \247eskip \247f: \247a모든 플레이어의 능력을 모두 확정합니다."); // OK
					sender.sendMessage("\2476/la \247ereroll <Player>\247f: \247a플레이어의 능력을 재추첨합니다. 공란 시 모두 변경."); // OK
					sender.sendMessage("\2476/la \247eob <Player>\247f: \247a해당 플레이어를 게임에서 제외합니다.");  // OK
					sender.sendMessage("\2476/la \247esee <Player> \247f: \247a플레이어에게 할당된 능력들을 확인합니다.");  // OK
					sender.sendMessage("\2476/la \247eadd <Player> <AbilityID> \247f: \247a플레이어에게 해당 능력을 추가합니다."); // OK
					sender.sendMessage("\2476/la \247eremove <Player> <AbilityID> \247f: \247a플레이어에게서 해당 능력을 제거합니다. ID 공란 시 모두 제거."); // OK
					sender.sendMessage("\2476/la \247elist \247f: \247a모든 플레이어의 능력을 확인합니다."); // OK
					return true;
				}
				if (args[0].equalsIgnoreCase("util")) {
					sender.sendMessage("\2476-------[\247eAdmin Command\2476]-------");
					sender.sendMessage("\2476/la \247eablist <Page> \247f: \247a현재 로드된 능력 리스트를 확인합니다.");  // OK
					sender.sendMessage("\2476/la \247eability <AbilityID> \247f: \247a해당 능력의 정보를 확인합니다.");  // OK
					return true;
				}
				if (args[0].equalsIgnoreCase("check")) {
					int index = main.gameManager.players.indexOf(senderPlayer.getName());
					if (index >= 0){
						LAPlayer lap = main.gameManager.players.get(index);
						if ((args.length > 1)) {
							lap.CheckAbility(senderPlayer, Integer.parseInt(args[1]));
							return true;
						} else {
							lap.CheckAbility(senderPlayer, -1);
							return true;
						}
					} else {
						senderPlayer.sendMessage("\2474[\247cLAbility\2474] \247c게임에 참여 중이 아닙니다.");
						return true;
					}
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
								}
								else {
									sender.sendMessage("\2474[\247cLAbility\2474] \247c해당 플레이어는 게임에 참가하고 있지 않습니다.");
								}
								return true;
							}
						}
						sender.sendMessage("\2474[\247cLAbility\2474] \247c플레이어가 존재하지 않습니다.");
					}
					else {
						sender.sendMessage("\2474[\247cLAbility\2474] \247c플레이어 이름을 입력해주세요.");
					}
					return true;
				}

				if (args[0].equalsIgnoreCase("ablist")) {
					int index = 1;
					int allData = main.abilities.size();
					int maxIndex = (allData % 8 == 0) ? allData / 8 : allData / 8 + 1;
					if ((args.length > 1)) index = Integer.parseInt(args[1]);
					if (index < 1) index = 1;
					if (index > maxIndex) index = maxIndex;
					sender.sendMessage("\2476-------[\247ePage " + (index) + "/" + (maxIndex) +"\2476]-------");
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
						else {
							sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 ID 입니다.");
						}
					}
					else {
						sender.sendMessage("\2474[\247cLAbility\2474] \247cID를 입력해주세요.");
						return true;
					}
				}

				if (args[0].equalsIgnoreCase("see")) {
					if ((args.length > 1)) {
						int index = main.gameManager.players.indexOf(args[1]);
						if (index >= 0) {
							sender.sendMessage("\2476-------[\247e" + args[1] + "'s Ability\2476]-------");
							main.gameManager.players.get(index).CheckAbility(senderPlayer, -1);
							return true;
						}
						else{
							sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 플레이어 입니다.");
						}
					}
					else {
						sender.sendMessage("\2474[\247cLAbility\2474] \247c플레이어 이름을 입력해주세요.");
						return true;
					}
				}

				if (args[0].equalsIgnoreCase("add")) {
					if ((args.length > 1)) {
						int index = main.gameManager.players.indexOf(args[1]);
						if (index >= 0) {
							LAPlayer p = main.gameManager.players.get(index);
							if (args.length > 2){
								int index2 = main.abilities.indexOf(args[2]);
								if (index2 >= 0) {
									Ability a = main.abilities.get(index2);
									if (!p.hasAbility(a)) {
										p.ability.add(new Ability(a));
										if (!senderPlayer.equals(p.player)) {
											sender.sendMessage("\2478[\2477LAbility\2478] \2477" + args[1] + " 가 \2478" + a.abilityName + "\2477 능력을 얻었습니다.");
											sender.sendMessage("\2478[\2477LAbility\2478] \2477" + "해당 유저는 해당 능력을 사용할 수 있습니다.");
										}
										p.player.sendMessage("\2476[\247eLAbility\2476] \2476" + a.abilityName + "\247e 능력을 얻었습니다.");
										p.player.sendMessage("\2476[\247eLAbility\2476] \247a" + "/la check " + (p.ability.size() - 1) + "\247e로 확인가능합니다.");

										LAbilityMain.instance.gameManager.StopAllPassive();
										LAbilityMain.instance.gameManager.RunAllPassive();
										return true;
									}
									else {
										sender.sendMessage("\2474[\247cLAbility\2474] \247c해당 유저는 해당 능력을 이미 소지 중입니다.");
									}
								}
								else {
									sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 ID 입니다.");
								}
							}
							else {
								sender.sendMessage("\2474[\247cLAbility\2474] \247cID를 입력해주세요.");
							}
							return true;
						}
						else{
							sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 플레이어 입니다.");
						}
					}
					else {
						sender.sendMessage("\2474[\247cLAbility\2474] \247c플레이어 이름을 입력해주세요.");
						return true;
					}
				}

				if (args[0].equalsIgnoreCase("remove")) {
					if ((args.length > 1)) {
						int index = main.gameManager.players.indexOf(args[1]);
						if (index >= 0) {
							LAPlayer p = main.gameManager.players.get(index);
							if (args.length > 2){
								int index2 = main.abilities.indexOf(args[2]);
								if (index2 >= 0) {
									Ability a = main.abilities.get(index2);
									if (p.hasAbility(a)) {
										p.ability.remove(a);
										if (!senderPlayer.equals(p.player)) {
											sender.sendMessage("\2478[\2477LAbility\2478] \2477" + args[1] + " 가 \2478" + a.abilityName + "\2477 능력을 잃었습니다.");
											sender.sendMessage("\2478[\2477LAbility\2478] \2477" + "해당 유저는 더이상 해당 능력 사용이 불가능합니다.");
										}
										p.player.sendMessage("\2474[\247cLAbility\2474] \2474" + a.abilityName + "\247c 능력을 잃었습니다.");
										p.player.sendMessage("\2474[\247cLAbility\2474] \247c" + "해당 능력은, 더 이상 사용하실 수 없습니다.");

										LAbilityMain.instance.gameManager.StopAllPassive();
										LAbilityMain.instance.gameManager.RunAllPassive();
										return true;
									}
									else {
										sender.sendMessage("\2474[\247cLAbility\2474] \247c해당 유저는 해당 능력을 소지 중이 아닙니다.");
									}
								}
								else {
									sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 ID 입니다.");
								}
							}
							else {
								if (p.ability.size() > 0) {
									p.ability.clear();
									if (!senderPlayer.equals(p.player)) {
										sender.sendMessage("\2478[\2477LAbility\2478] \2477" + args[1] + " 가 \2478모든\2477 능력을 잃었습니다.");
										sender.sendMessage("\2478[\2477LAbility\2478] \2477" + "해당 유저는 더이상 능력 사용이 불가능합니다.");
									}
									p.player.sendMessage("\2474[\247cLAbility\2474] \2474모든\247c 능력을 잃었습니다.");
									p.player.sendMessage("\2474[\247cLAbility\2474] \247c능력을 더 이상 사용하실 수 없습니다.");

									LAbilityMain.instance.gameManager.StopAllPassive();
									LAbilityMain.instance.gameManager.RunAllPassive();
								}
								else {
									sender.sendMessage("\2474[\247cLAbility\2474] \247c해당 유저는 능력을 소지 중이 아닙니다.");
								}
							}
							return true;
						}
						else{
							sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 플레이어 입니다.");
						}
					}
					else {
						sender.sendMessage("\2474[\247cLAbility\2474] \247c플레이어 이름을 입력해주세요.");
						return true;
					}
				}

				if (args[0].equalsIgnoreCase("list")) {
					sender.sendMessage("\2476-------[\247eAbility List\2476]-------");
					for (LAPlayer lap : main.gameManager.players) {
						String abilityString = "";
						abilityString += ("\247e" + lap.player.getName() + "\2477 : \247a");
						int index = 0;
						for (Ability a : lap.ability) {
							abilityString += a.abilityName;
							if (index++ < (lap.ability.size() - 1)) {
								abilityString += ", ";
							}
						}
						if (index == 0) abilityString += "\247c없음";
						sender.sendMessage(abilityString);
					}
				}

				if (args[0].equalsIgnoreCase("reroll")) {
					if ((args.length > 1)) {
						int index = main.gameManager.players.indexOf(args[1]);
						if (index >= 0) {
							LAPlayer p = main.gameManager.players.get(index);
							main.gameManager.ResignAbility(p);
							main.gameManager.AssignAbility(p);

							LAbilityMain.instance.gameManager.StopAllPassive();
							LAbilityMain.instance.gameManager.RunAllPassive();
						} else {
							sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 플레이어 입니다.");
						}
					}
					else {
						main.gameManager.ResignAbility();
						main.gameManager.AssignAbility();

						LAbilityMain.instance.gameManager.StopAllPassive();
						LAbilityMain.instance.gameManager.RunAllPassive();
					}
				}

				if (args[0].equalsIgnoreCase("yes")) {
					int index = main.gameManager.players.indexOf(senderPlayer.getName());
					if (index >= 0) {
						LAPlayer lap = main.gameManager.players.get(index);
						if (lap.isAssign) {
							senderPlayer.sendMessage("\2474[\247cLAbility\2474] \247c이미 능력이 확정되었습니다.");
							return true;
						}
						lap.isAssign = true;
						sender.sendMessage("\2472[\247aLAbility\2472] \247a" + "능력을 결정했습니다. 게임 시작까지 기다려주세요.");
						return true;
					} else {
						senderPlayer.sendMessage("\2474[\247cLAbility\2474] \247c게임에 참여 중이 아닙니다.");
						return true;
					}
				}

				if (args[0].equalsIgnoreCase("no")) {
					int index = main.gameManager.players.indexOf(senderPlayer.getName());
					if (index >= 0) {
						LAPlayer lap = main.gameManager.players.get(index);
						if (lap.isAssign) {
							senderPlayer.sendMessage("\2474[\247cLAbility\2474] \247c이미 능력이 확정되었습니다.");
							return true;
						}
						lap.ResignAbility();
						sender.sendMessage("\2472[\247aLAbility\2472] \247a" + "현재 능력을 버리고 새로운 능력을 갖습니다.");
						return true;
					} else {
						senderPlayer.sendMessage("\2474[\247cLAbility\2474] \247c게임에 참여 중이 아닙니다.");
						return true;
					}
				}

				if (args[0].equalsIgnoreCase("skip")) {
					if (main.gameManager.isGameStarted) {
						for (LAPlayer lap : main.gameManager.players) lap.isAssign = true;
						main.getServer().broadcastMessage("\2474[\247cLAbility\2474] \247c관리자가 모든 능력을 강제로 할당시켰습니다.");
					} else {
						senderPlayer.sendMessage("\2474[\247cLAbility\2474] \247c게임 진행 중이 아닙니다.");
					}
				}

				if (args[0].equalsIgnoreCase("start")) {
					if (!main.gameManager.isGameStarted) {
						ScheduleManager.PrepareTimer();
					}
					else {
						senderPlayer.sendMessage("\2474[\247cLAbility\2474] \247c게임이 진행 중입니다.");
					}
				}

				if (args[0].equalsIgnoreCase("stop")) {
					if (main.gameManager.isGameStarted) {
						ScheduleManager.ClearTimer();
						main.gameManager.ResetAll();
						Bukkit.getScheduler().cancelTasks(LAbilityMain.plugin);
						main.getServer().broadcastMessage("\2474[\247cLAbility\2474] \247c게임이 중단되었습니다.");
					}
					else {
						senderPlayer.sendMessage("\2474[\247cLAbility\2474] \247c게임 진행 중이 아닙니다.");
					}
				}

				if (args[0].equalsIgnoreCase("test")) {
					main.gameManager.RunAllPassive();
					main.gameManager.isGameStarted = true;
				}

				if (args[0].equalsIgnoreCase("reload")) {
					main.gameManager.StopAllPassive();
					main.gameManager.StopAllActiveTimer();
					main.onEnable();
					sender.sendMessage("\2478[\2477LAbility\2478] \2477Reload Complete.");
					if (main.hasError > 0) sender.sendMessage("\2474[\247cLAbility\2474] \247c" + main.hasError + "개의 능력을 로드하는데 문제가 생겼습니다. 해당 능력들은 로드하지 않습니다.");
					sender.sendMessage("\2478[\2477LAbility\2478] \2477" + main.abilities.size() + "개 능력 로드 완료!");
				}
			}
		}
		return false;
	}
}
