package com.LAbility.Manager;

import com.LAbility.*;
import com.LAbility.LuaUtility.List.AbilityList;
import com.LAbility.LuaUtility.LuaAbilityLoader;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandManager implements CommandExecutor {
	public final LAbilityMain main;

	public CommandManager(LAbilityMain main_) {
		main = main_;
	}
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("la")) {
			if (args.length == 0) {
				sender.sendMessage("\2476-------[\247eLAbility\2476]-------");
				sender.sendMessage("\2476/la \247eplayer \247f: \247a플레이어 용 명령어를 확인합니다."); // OK
				sender.sendMessage("\2476/la \247eadmin \247f: \247a관리자 용 명령어를 확인합니다."); // OK
				sender.sendMessage("\2476/la \247eutil \247f: \247a유틸 명령어를 확인합니다."); // OK
				sender.sendMessage("\2476/la \247edebug \247f: \247a디버그 명령어를 확인합니다."); // OK
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
					sender.sendMessage("\2476/la \247eruleset <RuleID> \247f: \247a게임의 룰을 해당 룰로 적용합니다."); // OK
					sender.sendMessage("\2476/la \247estart \247f: \247a게임을 시작합니다.");
					sender.sendMessage("\2476/la \247estop \247f: \247a게임을 중지합니다.");
					sender.sendMessage("\2476/la \247eskip \247f: \247a모든 플레이어의 능력을 모두 확정합니다."); // OK
					sender.sendMessage("\2476/la \247ereroll <Player>\247f: \247a플레이어의 능력을 재추첨합니다. 공란 시 모두 변경."); // OK
					sender.sendMessage("\2476/la \247eob <Player>\247f: \247a해당 플레이어를 게임에서 제외합니다.");  // OK
					sender.sendMessage("\2476/la \247esee <Player> \247f: \247a플레이어에게 할당된 능력들을 확인합니다.");  // OK
					sender.sendMessage("\2476/la \247eout <Player> \247f: \247a해당 플레이어를 탈락시킵니다.");  // OK
					sender.sendMessage("\2476/la \247eadd <Player> <AbilityID> \247f: \247a플레이어에게 해당 능력을 추가합니다."); // OK
					sender.sendMessage("\2476/la \247eremove <Player> <AbilityID> \247f: \247a플레이어에게서 해당 능력을 제거합니다. ID 공란 시 모두 제거."); // OK
					sender.sendMessage("\2476/la \247elist \247f: \247a모든 플레이어의 능력을 확인합니다."); // OK
					return true;
				}

				if (args[0].equalsIgnoreCase("util")) {
					sender.sendMessage("\2476-------[\247eUtil Command\2476]-------");
					sender.sendMessage("\2476/la \247eablist <Page> \247f: \247a현재 로드된 능력 리스트를 확인합니다.");  // OK
					sender.sendMessage("\2476/la \247eability <AbilityID> \247f: \247a해당 능력의 정보를 확인합니다.");  // OK
					sender.sendMessage("\2476/la \247erlist <Page> \247f: \247a현재 로드된 룰 리스트를 확인합니다.");  // OK
					sender.sendMessage("\2476/la \247erule <RuleID> \247f: \247a해당 룰의 정보를 확인합니다.");  // OK
					return true;
				}

				if (args[0].equalsIgnoreCase("debug")) {
					sender.sendMessage("\2476-------[\247eDebug Command\2476]-------");
					sender.sendMessage("\2476/la \247etest \247f: \247a테스트 모드에 진입합니다. 게임 시작을 하지 않아도 능력 사용이 가능합니다.\n테스트 모드를 종료하려면 /la stop을 입력하세요.");  // OK
					sender.sendMessage("\2476/la \247ecooldown \247f: \247a쿨타임을 모두 초기화합니다.");  // OK
					sender.sendMessage("\2476/la \247evariable \247f: \247a플레이어들의 변수를 확인합니다.");  // OK
					sender.sendMessage("\2476/la \247ereload \247f: \247a스크립트를 다시 로드합니다.");  // OK
					return true;
				}

				if (args[0].equalsIgnoreCase("check")) {
					if (!(sender instanceof Player)) {
						sender.sendMessage("\2474[\247cLAbility\2474] \247c플레이어만 명령어 사용이 가능합니다.");
						return true;
					}
					Player senderPlayer = (Player) sender;
					int index = main.gameManager.players.indexOf(senderPlayer.getName());
					if (index >= 0) {
						if (main.gameManager.canCheckAbility) {
							LAPlayer lap = main.gameManager.players.get(index);
							if ((args.length > 1)) {
								lap.CheckAbility(senderPlayer, Integer.parseInt(args[1]));
								return true;
							} else {
								lap.CheckAbility(senderPlayer, -1);
								return true;
							}
						}
						else {
							senderPlayer.sendMessage("\2474[\247cLAbility\2474] \247c현재 게임 설정에서는 자신의 능력을 확인할 수 없습니다.");
						}
					} else {
						senderPlayer.sendMessage("\2474[\247cLAbility\2474] \247c게임에 참여 중이 아닙니다.");
						return true;
					}
				}

				if (args[0].equalsIgnoreCase("ob") && sender.isOp()) {
					if (!main.gameManager.isGameReady) {
						if ((args.length > 1)) {
							for (Player p : main.getServer().getOnlinePlayers()) {
								if (p.getName().equals(args[1])) {
									if (main.gameManager.players.contains(p)) {
										main.gameManager.players.remove(p);
										if (sender instanceof Player && sender.equals(p)) {
											sender.sendMessage("\2478[\2477LAbility\2478] \2477" + "옵저버로 설정되었습니다.");
											sender.sendMessage("\2478[\2477LAbility\2478] \2477" + "재 접속 시, 게임에 다시 참여할 수 있습니다.");
										}
										sender.sendMessage("\2478[\2477LAbility\2478] \2477" + args[1] + " 플레이어가 옵저버로 설정되었습니다.");
										sender.sendMessage("\2478[\2477LAbility\2478] \2477" + "재 접속 시, 게임에 다시 참여할 수 있습니다.");
									} else {
										sender.sendMessage("\2474[\247cLAbility\2474] \247c해당 플레이어는 게임에 참가하고 있지 않습니다.");
									}
									return true;
								}
							}
							sender.sendMessage("\2474[\247cLAbility\2474] \247c플레이어가 존재하지 않습니다.");
						} else {
							sender.sendMessage("\2474[\247cLAbility\2474] \247c플레이어 이름을 입력해주세요.");
						}
						return true;
					} else {
						sender.sendMessage("\2474[\247cLAbility\2474] \247c게임이 진행 중입니다.");
					}
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
							main.abilities.get(index).ExplainAbility(sender);
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

				if (args[0].equalsIgnoreCase("rlist")) {
					int index = 1;
					int allData = main.rules.size();
					sender.sendMessage("\2476-------[\247eRules\2476]-------");
					for (int i = 0; i < main.rules.size(); i++) {
						sender.sendMessage("\2476[" + main.rules.get(i).ruleID + "] \247e" + main.rules.get(i).ruleName);
					}
					return true;
				}

				if (args[0].equalsIgnoreCase("rule")) {
					if ((args.length > 1)) {
						int index = main.rules.indexOf(args[1]);
						if (index >= 0) {
							main.rules.get(index).ExplainRule(sender);
							return true;
						}
						else {
							sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 룰 ID입니다.");
						}
					}
					else {
						sender.sendMessage("\2474[\247cLAbility\2474] \247c룰 ID을 입력해주세요.");
						return true;
					}
				}

				if (args[0].equalsIgnoreCase("ruleset")) {
					if (!main.gameManager.isGameReady) {
						if ((args.length > 1)) {
							int index = main.rules.indexOf(args[1]);
							if (index >= 0) {
								main.gameManager.currentRuleIndex = index;
								main.rules.get(index).InitScript();
								Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e룰 \2476[" + main.rules.get(index).ruleName + "]\247e이(가) 적용되었습니다.");
								return true;
							} else {
								sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 룰 ID 입니다.");
							}
						} else {
							sender.sendMessage("\2474[\247cLAbility\2474] \247c룰 ID을 입력해주세요.");
							return true;
						}
					}
					else {
						sender.sendMessage("\2474[\247cLAbility\2474] \247c게임이 진행 중입니다.");
					}
				}

				if (args[0].equalsIgnoreCase("see") && sender.isOp()) {
					if ((args.length > 1)) {
						int index = main.gameManager.players.indexOf(args[1]);
						if (index >= 0) {
							sender.sendMessage("\2476-------[\247e" + args[1] + "'s Ability\2476]-------");
							main.gameManager.players.get(index).CheckAbility(sender, -1);
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

				if (args[0].equalsIgnoreCase("out") && sender.isOp()) {
					if (main.gameManager.isGameStarted) {
						if ((args.length > 1)) {
							int index = main.gameManager.players.indexOf(args[1]);
							if (index >= 0) {
								Bukkit.broadcastMessage("\2474[\247cLAbility\2474] " + main.gameManager.players.get(index).getPlayer().getName() + "\247c님이 관리자에 의해 탈락하셨습니다.");
								main.gameManager.EliminatePlayer(main.gameManager.players.get(index));

								if (main.gameManager.getSurvivePlayer().size() == 1) {
									main.getServer().broadcastMessage("§6[§eLAbility§6] §e게임이 종료되었습니다.");
									main.getServer().broadcastMessage("§6[§eLAbility§6] §e" + main.gameManager.getSurvivePlayer().get(0).getPlayer().getName() + "님이 우승하셨습니다!");
									main.gameManager.OnGameEnd(true);
								} else if (LAbilityMain.instance.gameManager.getSurvivePlayer().size() < 1) {
									main.getServer().broadcastMessage("§6[§eLAbility§6] §e게임이 종료되었습니다.");
									main.getServer().broadcastMessage("§6[§eLAbility§6] §e우승자가 없습니다.");
									main.gameManager.OnGameEnd(true);
								}
								return true;
							} else {
								sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 플레이어 입니다.");
							}
						} else {
							sender.sendMessage("\2474[\247cLAbility\2474] \247c플레이어 이름을 입력해주세요.");
							return true;
						}
					}
					else {
						sender.sendMessage("\2474[\247cLAbility\2474] \247c게임 진행 중이 아닙니다.");
					}
				}

				if (args[0].equalsIgnoreCase("add") && sender.isOp()) {
					if ((args.length > 1)) {
						int index = main.gameManager.players.indexOf(args[1]);
						if (index >= 0) {
							LAPlayer p = main.gameManager.players.get(index);
							if (args.length > 2){
								int index2 = main.abilities.indexOf(args[2]);
								if (index2 >= 0) {
									Ability a = main.abilities.get(index2);
									Ability newA = new Ability(a);
									p.getAbility().add(newA);
									newA.InitScript();
									if (sender instanceof Player && !sender.equals(p.getPlayer())) {
										sender.sendMessage("\2478[\2477LAbility\2478] \2477" + args[1] + " 가 \2478" + a.abilityName + "\2477 능력을 얻었습니다.");
										sender.sendMessage("\2478[\2477LAbility\2478] \2477" + "해당 유저는 해당 능력을 사용할 수 있습니다.");
									}
									p.getPlayer().sendMessage("\2476[\247eLAbility\2476] \2476" + a.abilityName + "\247e 능력을 얻었습니다.");
									p.getPlayer().sendMessage("\2476[\247eLAbility\2476] \247a" + "/la check " + (p.getAbility().size() - 1) + "\247e로 확인가능합니다.");

									return true;
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

				if (args[0].equalsIgnoreCase("remove") && sender.isOp()) {
					if ((args.length > 1)) {
						int index = main.gameManager.players.indexOf(args[1]);
						if (index >= 0) {
							LAPlayer p = main.gameManager.players.get(index);
							if (args.length > 2){
								int index2 = main.abilities.indexOf(args[2]);
								if (index2 >= 0) {
									Ability a = main.abilities.get(index2);
									if (p.hasAbility(a)) {
										if (sender instanceof Player && !sender.equals(p.getPlayer())) {
											sender.sendMessage("\2478[\2477LAbility\2478] \2477" + args[1] + " 가 \2478" + a.abilityName + "\2477 능력을 잃었습니다.");
											sender.sendMessage("\2478[\2477LAbility\2478] \2477" + "해당 유저는 더이상 해당 능력 사용이 불가능합니다.");
										}
										p.getPlayer().sendMessage("\2474[\247cLAbility\2474] \2474" + a.abilityName + "\247c 능력을 잃었습니다.");
										p.getPlayer().sendMessage("\2474[\247cLAbility\2474] \247c" + "해당 능력은, 더 이상 사용하실 수 없습니다.");

										int abilityIndex = p.getAbility().indexOf(a.abilityID);
										if (main.gameManager.isGameStarted) {
											p.getAbility().get(abilityIndex).stopActive(p);
											p.getAbility().remove(abilityIndex);
										}
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
								if (p.getAbility().size() > 0) {
									if (sender instanceof Player && !sender.equals(p.getPlayer())) {
										sender.sendMessage("\2478[\2477LAbility\2478] \2477" + args[1] + " 가 \2478모든\2477 능력을 잃었습니다.");
										sender.sendMessage("\2478[\2477LAbility\2478] \2477" + "해당 유저는 더이상 능력 사용이 불가능합니다.");
									}
									p.getPlayer().sendMessage("\2474[\247cLAbility\2474] \2474모든\247c 능력을 잃었습니다.");
									p.getPlayer().sendMessage("\2474[\247cLAbility\2474] \247c능력을 더 이상 사용하실 수 없습니다.");

									if (main.gameManager.isGameReady) {
										for (Ability a : p.getAbility()) {
											a.stopActive(p);
										}
									}

									p.getAbility().clear();
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

				if (args[0].equalsIgnoreCase("list") && sender.isOp()) {
					sender.sendMessage("\2476-------[\247eAbility List\2476]-------");
					for (LAPlayer lap : main.gameManager.players) {
						String abilityString = "";
						abilityString += ("\247e" + lap.getPlayer().getName() + "\2477 : \247a");
						int index = 0;
						for (Ability a : lap.getAbility()) {
							abilityString += a.abilityName;
							if (index++ < (lap.getAbility().size() - 1)) {
								abilityString += ", ";
							}
						}
						if (index == 0) abilityString += "\247c없음";
						sender.sendMessage(abilityString);
					}
				}

				if (args[0].equalsIgnoreCase("reroll") && sender.isOp()) {
					if (main.gameManager.isGameReady) {
						if ((args.length > 1)) {
							int index = main.gameManager.players.indexOf(args[1]);
							if (index >= 0) {
								LAPlayer p = main.gameManager.players.get(index);

								AbilityList<Ability> alist = (AbilityList<Ability>) p.getAbility().clone();

								main.gameManager.AssignAbility(p);
								for (Ability a : alist){
									main.gameManager.ResignAbility(p, a);
								}
							} else {
								sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 플레이어 입니다.");
							}
						} else {
							for (LAPlayer p : main.gameManager.getSurvivePlayer()) {
								AbilityList<Ability> alist = (AbilityList<Ability>) p.getAbility().clone();

								main.gameManager.AssignAbility(p);
								for (Ability a : alist){
									main.gameManager.ResignAbility(p, a);
								}
							}
							Bukkit.broadcastMessage("\2474[\247cLAbility\2474] \247c관리자가 강제로 능력을 재추첨했습니다.");
						}
					} else {
						sender.sendMessage("\2474[\247cLAbility\2474] \247c게임 진행 중이 아닙니다.");
					}
				}

				if (args[0].equalsIgnoreCase("yes")) {
					if (!(sender instanceof Player)) {
						sender.sendMessage("\2474[\247cLAbility\2474] \247c플레이어만 명령어 사용이 가능합니다.");
						return true;
					}
					Player senderPlayer = (Player) sender;
					if (main.gameManager.isGameReady) {
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
					} else {
						senderPlayer.sendMessage("\2474[\247cLAbility\2474] \247c게임 진행 중이 아닙니다.");
					}
				}

				if (args[0].equalsIgnoreCase("no")) {
					if (!(sender instanceof Player)) {
						sender.sendMessage("\2474[\247cLAbility\2474] \247c플레이어만 명령어 사용이 가능합니다.");
						return true;
					}
					Player senderPlayer = (Player) sender;
					if (main.gameManager.isGameReady) {
						int index = main.gameManager.players.indexOf(senderPlayer.getName());
						if (index >= 0) {
							LAPlayer lap = main.gameManager.players.get(index);
							if (lap.isAssign) {
								senderPlayer.sendMessage("\2474[\247cLAbility\2474] \247c이미 능력이 확정되었습니다.");
								return true;
							}

							AbilityList<Ability> alist = (AbilityList<Ability>) lap.getAbility().clone();

							main.gameManager.AssignAbility(lap);
							for (Ability a : alist){
								main.gameManager.ResignAbility(lap, a);
							}
							lap.isAssign = true;
							sender.sendMessage("\2472[\247aLAbility\2472] \247a" + "현재 능력을 버리고 새로운 능력을 갖습니다.");
							return true;
						} else {
							senderPlayer.sendMessage("\2474[\247cLAbility\2474] \247c게임에 참여 중이 아닙니다.");
							return true;
						}
					} else {
						senderPlayer.sendMessage("\2474[\247cLAbility\2474] \247c게임 진행 중이 아닙니다.");
					}
				}

				if (args[0].equalsIgnoreCase("skip") && sender.isOp()) {
					if (main.gameManager.isGameReady) {
						for (LAPlayer lap : main.gameManager.players) lap.isAssign = true;
						main.getServer().broadcastMessage("\2474[\247cLAbility\2474] \247c관리자가 모든 능력을 강제로 할당시켰습니다.");
					} else {
						sender.sendMessage("\2474[\247cLAbility\2474] \247c게임 진행 중이 아닙니다.");
					}
				}

				if (args[0].equalsIgnoreCase("start") && sender.isOp()) {
					if (!main.gameManager.isGameReady) {
						if (!LAbilityMain.instance.gameManager.raffleAbility) LAbilityMain.instance.scheduleManager.PrepareTimer();
						else if ((!LAbilityMain.instance.gameManager.overlapAbility && (LAbilityMain.instance.gameManager.abilityAmount * LAbilityMain.instance.gameManager.players.size()) > LAbilityMain.instance.abilities.size()) ||
								(LAbilityMain.instance.gameManager.overlapAbility && LAbilityMain.instance.gameManager.abilityAmount > LAbilityMain.instance.abilities.size())) {
							if (!LAbilityMain.instance.gameManager.overlapAbility) {
								sender.sendMessage("\2474[\247cLAbility\2474] \247c인원 수가 너무 많아 게임 플레이가 불가능합니다.");
								sender.sendMessage("\2474[\247cLAbility\2474] \247c현재 로드된 능력들로 플레이 가능한 최대 인원은 " + LAbilityMain.instance.abilities.size() / LAbilityMain.instance.gameManager.abilityAmount + "명 입니다.");
							}
							if (LAbilityMain.instance.gameManager.abilityAmount > 1) sender.sendMessage("\2474[\247cLAbility\2474] \247c추첨하는 능력의 개수가 너무 많습니다. 추첨하는 능력의 개수를 줄여주세요.");
						} else if (LAbilityMain.instance.gameManager.players.size() < 2){
							sender.sendMessage("\2474[\247cLAbility\2474] \247c한 명일때는 게임 시작이 불가능합니다.");
						}
						else LAbilityMain.instance.scheduleManager.PrepareTimer();
					}
					else {
						sender.sendMessage("\2474[\247cLAbility\2474] \247c게임이 진행 중입니다.");
					}
				}

				if (args[0].equalsIgnoreCase("stop") && sender.isOp()) {
					if (main.gameManager.isGameReady) {
						LAbilityMain.instance.gameManager.OnGameEnd(false);
						main.getServer().broadcastMessage("\2474[\247cLAbility\2474] \247c게임이 중단되었습니다.");
					}
					else {
						sender.sendMessage("\2474[\247cLAbility\2474] \247c게임 진행 중이 아닙니다.");
					}
				}

				if (args[0].equalsIgnoreCase("test") && sender.isOp()) {
					if (!main.gameManager.isGameStarted && !main.gameManager.isGameReady) {
						sender.sendMessage("\2472[\247aLAbility\2472] \247a테스트 모드입니다. 게임 시작상태가 되며, 능력 사용이 가능합니다.");
						main.gameManager.isGameStarted = true;
						main.gameManager.isGameReady = true;
						main.gameManager.isTestMode = true;
						for (LAPlayer lap : LAbilityMain.instance.gameManager.players) {
							lap.isSurvive = true;
							lap.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(lap.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
							lap.getPlayer().setWalkSpeed(0.2f);
						}

						LAbilityMain.instance.gameManager.RunPassive();
					}
				}

				if (args[0].equalsIgnoreCase("cooldown") && sender.isOp()) {
					sender.sendMessage("\2472[\247aLAbility\2472] \247a쿨타임이 초기화되었습니다.");
					for (LAPlayer lap : LAbilityMain.instance.gameManager.players) {
						for (Ability a : lap.getAbility()) {
							a.resetCooldown();
						}
					}
				}

				if (args[0].equalsIgnoreCase("reload") && sender.isOp()) {
					LAbilityMain.instance.gameManager.OnGameEnd(false);
					LAbilityMain.instance.hasError = 0;
					LAbilityMain.instance.gameManager = new GameManager();
					LAbilityMain.instance.scheduleManager = new ScheduleManager();
					LAbilityMain.instance.dataPacks = new HashMap<>();

					LAbilityMain.instance.rules = LuaAbilityLoader.LoadLuaRules();
					LAbilityMain.instance.abilities = LuaAbilityLoader.LoadAllLuaAbilities();
					LAbilityMain.instance.gameManager.ResetAll();
					if (LAbilityMain.instance.dataPacks.size() > 0) {
						try {
							LAbilityMain.instance.appendResourcePacks();
							LAbilityMain.instance.webServer.start();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					if (LAbilityMain.instance.rules.size() > 0) {
						LAbilityMain.instance.rules.get(0).InitScript();
						Bukkit.getConsoleSender().sendMessage("\2476[\247eLAbility\2476] \247e룰 [" + LAbilityMain.instance.rules.get(0).ruleName + "]이(가) 적용되었습니다.");
					}
					else Bukkit.getConsoleSender().sendMessage("\2474[\247cLAbility\2474] \247c룰이 존재하지 않습니다. 게임이 정상적으로 진행되지 않을 수 있습니다.");

					if (LAbilityMain.instance.hasError > 0) Bukkit.getConsoleSender().sendMessage("\2474[\247cLAbility\2474] \247c" + LAbilityMain.instance.hasError + "개의 능력을 로드하는데 문제가 생겼습니다. 해당 능력들은 로드하지 않습니다.");
					Bukkit.getConsoleSender().sendMessage("\2476[\247eLAbility\2476] \2477v0.2 " + LAbilityMain.instance.abilities.size() + "개 능력 로드 완료!");
					Bukkit.getConsoleSender().sendMessage("Made by MINUTE.");
					sender.sendMessage("\2472[\247aLAbility\2472] \247aReload Complete.");
				}

				if (args[0].equalsIgnoreCase("variable") && sender.isOp()) {
					sender.sendMessage("\2476-------[\247eVariable\2476]-------");

					String serverVariables = "\247a서버 \2476: \247b";
					for (String key : main.gameManager.variable.keySet()) {
						serverVariables += key + "(" + main.gameManager.variable.get(key) + ")  ";
					}
					sender.sendMessage(serverVariables);

					for (LAPlayer lap : main.gameManager.players) {
						String variables = "\247a" + lap.getPlayer().getName() + " \2476: \247b";
						for (String key : lap.getVariableMap().keySet()) {
							variables += key + "(" + lap.getVariableMap().get(key) + ")  ";
						}
						sender.sendMessage(variables);
					}
				}
			}
		}
		return true;
	}
}
