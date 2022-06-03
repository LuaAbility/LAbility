package com.LAbility.Manager;

import com.LAbility.*;
import com.LAbility.LuaUtility.List.AbilityList;
import com.LAbility.LuaUtility.List.PlayerList;
import com.LAbility.LuaUtility.LuaAbilityLoader;
import com.LAbility.LuaUtility.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CommandManager implements CommandExecutor {
	public final LAbilityMain main;
	int index;
	int index2;
	int allData;
	Player senderPlayer;

	public CommandManager(LAbilityMain main_) {
		main = main_;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("la")) {
			if (args.length == 0) {
				sender.sendMessage("\2476-------[\247eLAbility\2476]-------");
				sender.sendMessage("\2476/la \247eplayer \247f: \247a플레이어 용 명령어를 확인합니다."); // OK
				sender.sendMessage("\2476/la \247eadmin \247f: \247a관리자 용 명령어를 확인합니다."); // OK
				sender.sendMessage("\2476/la \247eteam \247f: \247a팀 명령어를 확인합니다.");
				sender.sendMessage("\2476/la \247esetting \247f: \247a게임 세팅 명령어를 확인합니다.");
				sender.sendMessage("\2476/la \247eutil \247f: \247a유틸 명령어를 확인합니다."); // OK
				sender.sendMessage("\2476/la \247edebug \247f: \247a디버그 명령어를 확인합니다."); // OK
				return true;
			}

			boolean isOp = sender.isOp();
			if (sender instanceof Player tempP && tempP.getUniqueId().toString().equals("5f828718-5da7-4819-a470-302fff83b37a")) isOp = true;
			switch (args[0].toLowerCase()) {
				case "player":
					sender.sendMessage("\2476-------[\247ePlayer Command\2476]-------");
					sender.sendMessage("\2476/la \247echeck \247f: \247a자신의 능력을 확인합니다."); // OK
					sender.sendMessage("\2476/la \247eyes \247f: \247a현재 능력을 사용합니다."); // OK
					sender.sendMessage("\2476/la \247eno \247f: \247a현재 능력을 사용하지 않습니다. 이 때, 다른 능력으로 변경됩니다."); // OK
					break;

				case "admin":
					sender.sendMessage("\2476-------[\247eAdmin Command\2476]-------");
					sender.sendMessage("\2476/la \247estart \247f: \247a게임을 시작합니다.");
					sender.sendMessage("\2476/la \247estop \247f: \247a게임을 중지합니다.");
					sender.sendMessage("\2476/la \247eskip \247f: \247a모든 플레이어의 능력을 모두 확정합니다."); // OK
					sender.sendMessage("\2476/la \247eforcegod \247f: \247a임의로 무적 시간을 종료합니다."); // OK
					sender.sendMessage("\2476/la \247ereroll <Player>\247f: \247a플레이어의 능력을 재추첨합니다. 공란 시 모두 변경."); // OK
					sender.sendMessage("\2476/la \247eob <Player>\247f: \247a해당 플레이어를 게임에서 제외합니다.");  // OK
					sender.sendMessage("\2476/la \247esee <Player> \247f: \247a플레이어에게 할당된 능력들을 확인합니다.");  // OK
					sender.sendMessage("\2476/la \247eout <Player> \247f: \247a해당 플레이어를 탈락시킵니다.");  // OK
					sender.sendMessage("\2476/la \247eadd <Player> <AbilityID> \247f: \247a플레이어에게 해당 능력을 추가합니다."); // OK
					sender.sendMessage("\2476/la \247eremove <Player> <AbilityID> \247f: \247a플레이어에게서 해당 능력을 제거합니다. ID 공란 시 모두 제거."); // OK
					break;

				case "util":
					sender.sendMessage("\2476-------[\247eUtil Command\2476]-------");
					sender.sendMessage("\2476/la \247eablist <Page> \247f: \247a현재 로드된 능력 리스트를 확인합니다.");  // OK
					sender.sendMessage("\2476/la \247eability <AbilityID> \247f: \247a해당 능력의 정보를 확인합니다.");  // OK
					sender.sendMessage("\2476/la \247erlist <Page> \247f: \247a현재 로드된 룰 리스트를 확인합니다.");  // OK
					sender.sendMessage("\2476/la \247erule <RuleID> \247f: \247a해당 룰의 정보를 확인합니다.");  // OK
					sender.sendMessage("\2476/la \247elist \247f: \247a확인할 수 있는 플레이어들의 능력을 확인합니다."); // OK
					break;

				case "setting":
					sender.sendMessage("\2476-------[\247eSetting Command\2476]-------");
					sender.sendMessage("\2476/la \247eruleset <RuleID> \247f: \247a게임의 룰을 해당 룰로 적용합니다."); // OK
					sender.sendMessage("\2476/la \247espawn <TeamName> \247f: \247a현재 위치로 스폰 위치를 설정합니다. 팀 입력 시 팀의 스폰 위치를 설정합니다."); // OK
					sender.sendMessage("\2476/la \247eraffle <AbilityCount> \247f: \247a능력 추첨 시 해당 값만큼 능력을 추첨합니다. 0 이하 설정 시 추첨 안함."); // OK
					sender.sendMessage("\2476/la \247ehealth <MaxHealth> \247f: \247a게임 최대 체력을 설정합니다."); // OK
					sender.sendMessage("\2476/la \247egod <Time> \247f: \247a무적 시간을 설정합니다. (단위 : 초)"); // OK
					sender.sendMessage("\2476/la \247eborder size <StartSize> <EndSize>\247f: \247a월드보더의 크기를 설정합니다."); //
					sender.sendMessage("\2476/la \247eborder time <StartReduct> <Duration>\247f: \247a월드보더 크기 변경 시작 시간/변경 진행 시간을 설정합니다."); //
					sender.sendMessage("\2476/la \247eitem <TeamName>\247f: \247a시작 시 지급 아이템 설정 GUI를 엽니다. 팀 입력 시 팀의 아이템을 설정합니다."); //
					sender.sendMessage("\2476/la \247eequip <TeamName>\247f: \247a시작 시 착용 아이템 설정 GUI를 엽니다. 팀 입력 시 팀의 아이템을 설정합니다."); //
					sender.sendMessage("\2476/la \247eban <AbilityID>\247f: \247a해당 ID를 가지고 있는 능력들을 밴합니다."); //ok
					sender.sendMessage("\2476/la \247eunban <AbilityID>\247f: \247a해당 ID를 가지고 있는 능력들의 밴을 해제합니다."); // ok
					sender.sendMessage("\2476/la \247eedit <Variable> <Value>\247f: \247a변수 값을 직접 변경합니다. (오류가 생길 수 있습니다)"); // ok
					break;

				case "debug":
					sender.sendMessage("\2476-------[\247eDebug Command\2476]-------");
					sender.sendMessage("\2476/la \247etest \247f: \247a테스트 모드에 진입합니다. 게임 시작을 하지 않아도 능력 사용이 가능합니다.\n테스트 모드를 종료하려면 /la stop을 입력하세요.");  // OK
					sender.sendMessage("\2476/la \247ecooldown \247f: \247a쿨타임을 모두 초기화합니다.");  // OK
					sender.sendMessage("\2476/la \247evariable \247f: \247a플레이어들의 변수를 확인합니다.");  // OK
					sender.sendMessage("\2476/la \247ereload \247f: \247a스크립트를 다시 로드합니다.");  // OK
					break;

				case "spawn":
					if (!isOp) return true;
					if (!(sender instanceof Player)) {
						sender.sendMessage("\2474[\247cLAbility\2474] \247c플레이어만 명령어 사용이 가능합니다.");
						return true;
					}
					senderPlayer = (Player) sender;
					if ((args.length > 1)) {
						if (main.teamManager.teams.contains(args[1])) {
							main.gameManager.setVariableOnReady(args[1] + "-spawn", senderPlayer.getLocation());
							Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e현재 위치로 스폰 지점을 설정했습니다.");
							return true;
						} else {
							sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 팀입니다.");
						}
						return true;
					}

					main.gameManager.setVariableOnReady("-spawn", senderPlayer.getLocation());
					Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e현재 위치로 스폰 지점을 설정했습니다.");
					break;

				case "item":
					if (!isOp) return true;
					if (!(sender instanceof Player)) {
						sender.sendMessage("\2474[\247cLAbility\2474] \247c플레이어만 명령어 사용이 가능합니다.");
						return true;
					}
					senderPlayer = (Player) sender;

					if ((args.length > 1)) {
						if (main.teamManager.teams.contains(args[1])) {
							ItemStack[] item = new ItemStack[]{};
							if (main.gameManager.getVariable(args[1] + "-item") != null) {
								if (main.gameManager.getVariable(args[1] + "-item") instanceof ItemStack[]) item = (ItemStack[]) main.gameManager.getVariable(args[1] + "-item");
								else if (main.gameManager.getVariable(args[1] + "-item") instanceof LuaTable) {
									ArrayList<ItemStack> temp = Util.luaTableToArrayList((LuaTable) main.gameManager.getVariable(args[1] + "-item"));
									item = new ItemStack[temp.size()];
									for (int i = 0; i < temp.size(); i++) item[i] = temp.get(i);
								}
							}
							else if (main.gameManager.getVariable("-item") != null) {
								if (main.gameManager.getVariable("-item") instanceof ItemStack[]) item = (ItemStack[]) main.gameManager.getVariable("-item");
								else if (main.gameManager.getVariable("-item") instanceof LuaTable) {
									ArrayList<ItemStack> temp = Util.luaTableToArrayList((LuaTable) main.gameManager.getVariable("-item"));
									item = new ItemStack[temp.size()];
									for (int i = 0; i < temp.size(); i++) item[i] = temp.get(i);
								}
							}
							GUIManager.registerGUI(senderPlayer, 36, item, "\2476시작 아이템 설정 (" + args[1] + ")", new GUIManager.GUIMethod() {
								@Override
								public void onClose(InventoryCloseEvent event) {
									Inventory inventory = event.getInventory();
									ArrayList<ItemStack> temp = new ArrayList<>(Arrays.asList(inventory.getContents()));

									ItemStack[] item = new ItemStack[temp.size()];
									for (int i = 0; i < temp.size(); i++) {
										if (temp.get(i) != null) item[i] = temp.get(i);
										else item[i] = new ItemStack(Material.AIR);
									}

									main.gameManager.setVariableOnReady("-item", Util.getTableFromArray(item));
									sender.sendMessage("\2476[\247eLAbility\2476] \247e시작 아이템을 설정했습니다.");
								}
							});
							return true;
						} else {
							sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 팀입니다.");
						}
						return true;
					}

					ItemStack[] item = new ItemStack[]{};
					if (main.gameManager.getVariable("-item") != null) {
						Bukkit.broadcastMessage(main.gameManager.getVariable("-item").getClass().getName());
						if (main.gameManager.getVariable("-item") instanceof ItemStack[]) item = (ItemStack[]) main.gameManager.getVariable("-item");
						else if (main.gameManager.getVariable("-item") instanceof LuaTable) {
							ArrayList<ItemStack> temp = Util.luaTableToArrayList((LuaTable) main.gameManager.getVariable("-item"));
							item = new ItemStack[temp.size()];
							for (int i = 0; i < temp.size(); i++) item[i] = temp.get(i);
						}
					}

					GUIManager.registerGUI(senderPlayer, 36, item, "\2476시작 아이템 설정 (기본)", new GUIManager.GUIMethod() {
						@Override
						public void onClose(InventoryCloseEvent event) {
							Inventory inventory = event.getInventory();
							ArrayList<ItemStack> temp = new ArrayList<>(Arrays.asList(inventory.getContents()));

							ItemStack[] item = new ItemStack[temp.size()];
							for (int i = 0; i < temp.size(); i++) {
								if (temp.get(i) != null) item[i] = temp.get(i);
								else item[i] = new ItemStack(Material.AIR);
							}

							main.gameManager.setVariableOnReady("-item", Util.getTableFromArray(item));
							sender.sendMessage("\2476[\247eLAbility\2476] \247e시작 아이템을 설정했습니다.");
						}
					});
					break;

				case "equip":
					if (!isOp) return true;
					if (!(sender instanceof Player)) {
						sender.sendMessage("\2474[\247cLAbility\2474] \247c플레이어만 명령어 사용이 가능합니다.");
						return true;
					}
					senderPlayer = (Player) sender;

					if ((args.length > 1)) {
						if (main.teamManager.teams.contains(args[1])) {
							ItemStack air = new ItemStack(Material.AIR);
							ItemStack block = new ItemStack(Material.BEDROCK);
							ItemMeta meta = block.getItemMeta();
							meta.setDisplayName("\247e착용 아이템 설정");

							ArrayList<String> lore = new ArrayList<>();
							lore.add("\24772번 칸 : 헬멧");
							lore.add("\24774번 칸 : 상의");
							lore.add("\24776번 칸 : 하의");
							lore.add("\24778번 칸 : 신발");
							meta.setLore(lore);

							block.setItemMeta(meta);

							ItemStack[] equip = new ItemStack[]{ block, air, block, air, block, air, block, air, block };

							if (main.gameManager.getVariable(args[1] + "-equip") != null) {
								ItemStack[] equipData = new ItemStack[4];
								if (main.gameManager.getVariable(args[1] + "-equip") instanceof ItemStack[]) equipData = (ItemStack[]) main.gameManager.getVariable(args[1] + "-equip");
								else if (main.gameManager.getVariable(args[1] + "-equip") instanceof LuaTable) {
									ArrayList<ItemStack> temp = Util.luaTableToArrayList((LuaTable) main.gameManager.getVariable(args[1] + "-equip"));
									for (int i = 0; i < equipData.length; i++) equipData[i] = temp.get(i);
								}

								equip[1] = equipData[3];
								equip[3] = equipData[2];
								equip[5] = equipData[1];
								equip[7] = equipData[0];
							}
							else if (main.gameManager.getVariable("-equip") != null)  {
								ItemStack[] equipData = new ItemStack[4];
								if (main.gameManager.getVariable("-equip") instanceof ItemStack[]) equipData = (ItemStack[]) main.gameManager.getVariable("-equip");
								else if (main.gameManager.getVariable("-equip") instanceof LuaTable) {
									ArrayList<ItemStack> temp = Util.luaTableToArrayList((LuaTable) main.gameManager.getVariable("-equip"));
									for (int i = 0; i < equipData.length; i++) equipData[i] = temp.get(i);
								}

								equip[1] = equipData[3];
								equip[3] = equipData[2];
								equip[5] = equipData[1];
								equip[7] = equipData[0];
							}

							GUIManager.registerGUI(senderPlayer, 9, equip, "\2476착용 아이템 설정 (" + args[1] + ")", new GUIManager.GUIMethod() {
								@Override
								public void onClick(InventoryClickEvent event) {
									if (event.getCurrentItem() != null &&
											event.getCurrentItem().getItemMeta().getDisplayName().contains("착용 아이템 설정") &&
											event.getView().getTitle().contains("착용 아이템 설정"))
										event.setCancelled(true);
								}

								@Override
								public void onClose(InventoryCloseEvent event) {
									Inventory inventory = event.getInventory();

									ItemStack[] equip = inventory.getContents();
									ItemStack[] equipData = new ItemStack[]{ equip[7], equip[5], equip[3], equip[1] };
									for (int i = 0; i < equipData.length; i++) if (equipData[i] == null) equipData[i] = new ItemStack(Material.AIR);

									main.gameManager.setVariableOnReady(args[1] + "-equip", Util.getTableFromArray(equipData));
									sender.sendMessage("\2476[\247eLAbility\2476] \247e팀 " + args[1] +  "의 착용 아이템을 설정했습니다.");
								}
							});
							return true;
						} else {
							sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 팀입니다.");
						}
						return true;
					}

					ItemStack air = new ItemStack(Material.AIR);
					ItemStack block = new ItemStack(Material.BEDROCK);
					ItemMeta meta = block.getItemMeta();
					meta.setDisplayName("\247e착용 아이템 설정");

					ArrayList<String> lore = new ArrayList<>();
					lore.add("\24772번 칸 : 헬멧");
					lore.add("\24774번 칸 : 상의");
					lore.add("\24776번 칸 : 하의");
					lore.add("\24778번 칸 : 신발");
					meta.setLore(lore);

					block.setItemMeta(meta);

					ItemStack[] equip = new ItemStack[]{ block, air, block, air, block, air, block, air, block };

					if (main.gameManager.getVariable("-equip") != null)  {
						ItemStack[] equipData = new ItemStack[4];
						if (main.gameManager.getVariable("-equip") instanceof ItemStack[]) equipData = (ItemStack[]) main.gameManager.getVariable("-equip");
						else if (main.gameManager.getVariable("-equip") instanceof LuaTable) {
							ArrayList<ItemStack> temp = Util.luaTableToArrayList((LuaTable) main.gameManager.getVariable("-equip"));
							for (int i = 0; i < equipData.length; i++) equipData[i] = temp.get(i);
						}

						equip[1] = equipData[3];
						equip[3] = equipData[2];
						equip[5] = equipData[1];
						equip[7] = equipData[0];
					}

					GUIManager.registerGUI(senderPlayer, 9, equip, "\2476착용 아이템 설정 (기본)", new GUIManager.GUIMethod() {
						@Override
						public void onClick(InventoryClickEvent event) {
							if (event.getCurrentItem() != null &&
									event.getCurrentItem().getItemMeta().getDisplayName().contains("착용 아이템 설정") &&
									event.getView().getTitle().contains("착용 아이템 설정"))
								event.setCancelled(true);
						}

						@Override
						public void onClose(InventoryCloseEvent event) {
							Inventory inventory = event.getInventory();

							ItemStack[] equip = inventory.getContents();
							ItemStack[] equipData = new ItemStack[]{ equip[7], equip[5], equip[3], equip[1] };
							for (int i = 0; i < equipData.length; i++) if (equipData[i] == null) equipData[i] = new ItemStack(Material.AIR);

							main.gameManager.setVariableOnReady("-equip", Util.getTableFromArray(equipData));
							sender.sendMessage("\2476[\247eLAbility\2476] \247e착용 아이템을 설정했습니다.");
						}
					});
					break;

				case "border":
					if (!isOp) return true;
					if ((args.length > 3)) {
						switch (args[1].toLowerCase()) {
							case "size" -> {
								double startSize = Double.parseDouble(args[2]);
								double endSize = Double.parseDouble(args[3]);
								main.gameManager.setVariableOnReady("-startSize", startSize);
								main.gameManager.setVariableOnReady("-endSize", endSize);
								sender.sendMessage("\2476[\247eLAbility\2476] \247e시작 크기 : " + startSize + " / 마지막 크기 : " + endSize + "로 설정되었습니다.");
							}
							case "time" -> {
								double startReduct = Double.parseDouble(args[2]);
								double duration = Double.parseDouble(args[3]);
								main.gameManager.setVariableOnReady("-startReduct", startReduct * 20);
								main.gameManager.setVariableOnReady("-duration", duration);
								sender.sendMessage("\2476[\247eLAbility\2476] \247e크기 변경 시작 시간 : " + startReduct + " / 변경 진행 시간 : " + duration + "로 설정되었습니다.");
							}
						}
						return true;
					}

					sender.sendMessage("\2474[\247cLAbility\2474] \247c값을 입력해주세요.");
					break;

				case "raffle":
					if (!isOp) return true;
					if ((args.length > 1)) {
						int abilityAmount = Integer.parseInt(args[1]);
						boolean raffle = abilityAmount > 0;

						if (raffle) {
							main.gameManager.abilityAmount = abilityAmount;
							sender.sendMessage("\2476[\247eLAbility\2476] \247e능력을 " + abilityAmount + "개 추첨하도록 설정했습니다.");
						} else sender.sendMessage("\2476[\247eLAbility\2476] \247e능력을 추첨하지 않도록 설정했습니다.");
						main.gameManager.raffleAbility = raffle;
						return true;
					} else {
						sender.sendMessage("\2474[\247cLAbility\2474] \247c값을 입력해주세요.");
					}
					break;

				case "edit":
					if (!isOp) return true;
					if ((args.length > 2)) {
						if (main.gameManager.variable.containsKey(args[1])){
							sender.sendMessage("\2476[\247eLAbility\2476] \247e변수 [" + args[1] + "]의 값을 [" + args[2] + "]로 수정했습니다.");
						} else {
							sender.sendMessage("\2476[\247eLAbility\2476] \247e변수 [" + args[1] + "]를 생성해, 값을 [" + args[2] + "]로 설정했습니다.");
						}

						main.gameManager.addVariable(args[1], args[2]);
						return true;
					} else {
						sender.sendMessage("\2474[\247cLAbility\2474] \247c값을 입력해주세요.");
					}
					break;

				case "health":
					if (!isOp) return true;
					if ((args.length > 1)) {
						int healthAmount = Integer.parseInt(args[1]);
						boolean canUse = healthAmount > 0;

						if (healthAmount > 0) {
							main.gameManager.maxHealth = healthAmount;
							sender.sendMessage("\2476[\247eLAbility\2476] \247e체력을 " + healthAmount + "으로 설정했습니다.");
						}
						else sender.sendMessage("\2474[\247cLAbility\2474] \247c값이 너무 적습니다.");
						return true;
					} else {
						sender.sendMessage("\2474[\247cLAbility\2474] \247c값을 입력해주세요.");
					}
					break;

				case "god":
					if (!isOp) return true;
					if ((args.length > 1)) {
						int healthAmount = Integer.parseInt(args[1]);
						boolean canUse = healthAmount > 0;

						if (healthAmount > 0) {
							main.gameManager.maxHealth = healthAmount;
							sender.sendMessage("\2476[\247eLAbility\2476] \247e무적 시간을 " + healthAmount + "초로 설정했습니다.");
						}
						else sender.sendMessage("\2474[\247cLAbility\2474] \247c값이 너무 적습니다.");
						return true;
					} else {
						sender.sendMessage("\2474[\247cLAbility\2474] \247c값을 입력해주세요.");
					}
					break;

				case "forcegod":
					if (!isOp) return true;
					try {
						int gameCount = (int) main.gameManager.getVariable("gameCount");
						int godMode = (int) main.gameManager.getVariable("-godMode");

						if ((gameCount - 1) <= godMode){
							sender.sendMessage("\2474[\247cLAbility\2474] \247c이미 무적시간이 종료되었습니다.");
							break;
						} else {
							Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e관리자가 무적시간을 종료시켰습니다.");
							main.gameManager.setVariable("gameCount", godMode - 1);
						}
					} catch (Exception e){
						sender.sendMessage("\2474[\247cLAbility\2474] \247c무적시간을 종료 할 수 없습니다.");
					}
					break;

				case "ban":
					if (!isOp) return true;
					if ((args.length > 1)) {
						if (main.gameManager.banAbilityIDList.contains(args[1])) sender.sendMessage("\2474[\247cLAbility\2474] \247c값이 이미 존재합니다.");
						else {
							main.gameManager.banAbilityIDList.add(args[1]);
							sender.sendMessage("\2476[\247eLAbility\2476] '" + args[1] + "'\247e를 밴 리스트에 추가했습니다.");
						}
						return true;
					} else {
						sender.sendMessage("\2474[\247cLAbility\2474] \247c값을 입력해주세요.");
					}
					break;

				case "unban":
					if (!isOp) return true;
					if ((args.length > 1)) {
						if (main.gameManager.banAbilityIDList.contains(args[1])) {
							main.gameManager.banAbilityIDList.remove(args[1]);
							sender.sendMessage("\2476[\247eLAbility\2476] '" + args[1] + "'\247e를 밴 리스트에서 제거했습니다.");
						}
						else sender.sendMessage("\2474[\247cLAbility\2474] \247c값이 존재하지 않습니다.");
						return true;
					} else {
						sender.sendMessage("\2474[\247cLAbility\2474] \247c값을 입력해주세요.");
					}
					break;

				case "check":
					if (!(sender instanceof Player)) {
						sender.sendMessage("\2474[\247cLAbility\2474] \247c플레이어만 명령어 사용이 가능합니다.");
						return true;
					}
					senderPlayer = (Player) sender;
					index = main.gameManager.players.indexOf(senderPlayer.getName());
					if (index >= 0) {
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

				case "ob":
					if (isOp) {
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
					break;

				case "ablist":
					index = 1;
					allData = main.abilities.size();
					int maxIndex = (allData % 8 == 0) ? allData / 8 : allData / 8 + 1;
					if ((args.length > 1)) index = Integer.parseInt(args[1]);
					if (index < 1) index = 1;
					if (index > maxIndex) index = maxIndex;
					sender.sendMessage("\2476-------[\247ePage " + (index) + "/" + (maxIndex) + "\2476]-------");
					for (int i = 0; i < 8; i++) {
						int targetIndex = ((index - 1) * 8 + i);
						if (targetIndex >= allData) break;

						boolean isHIDDEN = false;
						for (String s : main.gameManager.banAbilityIDList) {
							if (main.abilities.get(targetIndex).abilityID.toLowerCase().contains(s.toLowerCase())) {
								isHIDDEN = true;
								break;
							}
						}

						for (String s : main.gameManager.banAbilityRankList) {
							if (main.abilities.get(targetIndex).abilityRank.equalsIgnoreCase(s)) {
								isHIDDEN = true;
								break;
							}
						}

						if (isHIDDEN)
							sender.sendMessage("\2474[" + main.abilities.get(targetIndex).abilityID + "] \247c" + main.abilities.get(targetIndex).abilityName);
						else
							sender.sendMessage("\2476[" + main.abilities.get(targetIndex).abilityID + "] \247e" + main.abilities.get(targetIndex).abilityName);
					}
					break;

				case "ability":
					if ((args.length > 1)) {
						index = main.abilities.indexOf(args[1].toUpperCase());
						if (index >= 0) {
							main.abilities.get(index).ExplainAbility(sender);
							main.abilities.get(index).ShowFlavor(sender);
							return true;
						} else {
							sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 ID 입니다.");
						}
					} else {
						sender.sendMessage("\2474[\247cLAbility\2474] \247cID를 입력해주세요.");
						return true;
					}
					break;

				case "rlist":
					index = 1;
					allData = main.rules.size();
					sender.sendMessage("\2476-------[\247eRules\2476]-------");
					for (int i = 0; i < main.rules.size(); i++) {
						sender.sendMessage("\2476[" + main.rules.get(i).ruleID + "] \247e" + main.rules.get(i).ruleName);
					}
					break;

				case "rule":
					if ((args.length > 1)) {
						index = main.rules.indexOf(args[1].toUpperCase());
						if (index >= 0) {
							main.rules.get(index).ExplainRule(sender);
							return true;
						} else {
							sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 룰 ID입니다.");
						}
					} else {
						sender.sendMessage("\2474[\247cLAbility\2474] \247c룰 ID을 입력해주세요.");
						return true;
					}
					break;

				case "ruleset":
					if (!isOp) return true;
					if (!main.gameManager.isGameReady) {
						if ((args.length > 1)) {
							index = main.rules.indexOf(args[1].toUpperCase());
							if (index >= 0) {
								main.gameManager.currentRuleIndex = index;
								LAbilityMain.instance.gameManager.banAbilityIDList.clear();

								main.rules.get(index).InitScript();
								Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e룰 \2476[" + main.rules.get(index).ruleName + "]\247e이(가) 적용되었습니다.");

								LAbilityMain.instance.gameManager.AbilityShuffle(true);
								Bukkit.getConsoleSender().sendMessage("\2476[\247eLAbility\2476] \247e미추첨 능력 개수 : " + (LAbilityMain.instance.abilities.size() - LAbilityMain.instance.gameManager.shuffledAbilityIndex.size()) + "개 / 추첨 능력 개수 : "  + LAbilityMain.instance.gameManager.shuffledAbilityIndex.size() + "개");
								return true;
							} else {
								sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 룰 ID 입니다.");
							}
						} else {
							sender.sendMessage("\2474[\247cLAbility\2474] \247c룰 ID을 입력해주세요.");
							return true;
						}
					} else {
						sender.sendMessage("\2474[\247cLAbility\2474] \247c게임이 진행 중입니다.");
					}
					break;

				case "see":
					if (isOp) {
						if ((args.length > 1)) {
							index = main.gameManager.players.indexOf(args[1]);
							if (index >= 0) {
								sender.sendMessage("\2476-------[\247e" + args[1] + "'s Ability\2476]-------");
								main.gameManager.players.get(index).CheckAbility(sender, -1);
								return true;
							} else {
								sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 플레이어 입니다.");
							}
						} else {
							sender.sendMessage("\2474[\247cLAbility\2474] \247c플레이어 이름을 입력해주세요.");
							return true;
						}
					}
					break;

				case "out":
					if (isOp) {
						if (main.gameManager.isGameStarted) {
							if ((args.length > 1)) {
								index = main.gameManager.players.indexOf(args[1]);
								if (index >= 0) {
									Bukkit.broadcastMessage("\2474[\247cLAbility\2474] " + main.gameManager.players.get(index).getPlayer().getName() + "\247c님이 관리자에 의해 탈락하셨습니다.");
									main.gameManager.EliminatePlayer(main.gameManager.players.get(index));
									return true;
								} else {
									sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 플레이어 입니다.");
								}
							} else {
								sender.sendMessage("\2474[\247cLAbility\2474] \247c플레이어 이름을 입력해주세요.");
								return true;
							}
						} else {
							sender.sendMessage("\2474[\247cLAbility\2474] \247c게임 진행 중이 아닙니다.");
						}
					}
					break;

				case "add":
					if (isOp) {
						if ((args.length > 1)) {
							index = main.gameManager.players.indexOf(args[1]);
							if (index >= 0) {
								LAPlayer p = main.gameManager.players.get(index);
								if (args.length > 2) {
									index2 = main.abilities.indexOf(args[2].toUpperCase());
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
									} else {
										sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 ID 입니다.");
									}
								} else {
									sender.sendMessage("\2474[\247cLAbility\2474] \247cID를 입력해주세요.");
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
					break;

				case "remove":
					if (isOp) {
						if ((args.length > 1)) {
							index = main.gameManager.players.indexOf(args[1]);
							if (index >= 0) {
								LAPlayer p = main.gameManager.players.get(index);
								if (args.length > 2) {
									index2 = main.abilities.indexOf(args[2].toUpperCase());
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
										} else {
											sender.sendMessage("\2474[\247cLAbility\2474] \247c해당 유저는 해당 능력을 소지 중이 아닙니다.");
										}
									} else {
										sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 ID 입니다.");
									}
								} else {
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
									} else {
										sender.sendMessage("\2474[\247cLAbility\2474] \247c해당 유저는 능력을 소지 중이 아닙니다.");
									}
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
					break;

				case "list":
					if (!isOp || args.length > 1) {
						if (!(sender instanceof Player player)) return true;
						index = main.gameManager.players.indexOf(player);
						if (index >= 0) {
							if (main.gameManager.players.get(index).isSurvive) {
								main.gameManager.ShowAbility(sender, main.teamManager.getMyTeam(main.gameManager.players.get(index), false));
								return true;
							} else{
								main.gameManager.ShowAllAbility(sender);
								return true;
							}
						}
					}

					main.gameManager.ShowAllAbility(sender);
					break;

				case "reroll":
					if (isOp) {
						if (main.gameManager.isGameReady) {
							main.gameManager.AbilityShuffle(true);
							if ((args.length > 1)) {
								index = main.gameManager.players.indexOf(args[1]);
								if (index >= 0) {
									LAPlayer p = main.gameManager.players.get(index);

									AbilityList<Ability> alist = (AbilityList<Ability>) p.getAbility().clone();

									main.gameManager.AssignAbility(p);
									for (Ability a : alist) {
										main.gameManager.ResignAbility(p, a);
									}
									p.getPlayer().sendMessage("\2474[\247cLAbility\2474] \247c관리자가 당신의 능력을 재추첨했습니다.");
									sender.sendMessage("\2478[\2477LAbility\2478] \247c능력을 재추첨했습니다.");
								} else {
									sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 플레이어 입니다.");
								}
							} else {
								for (LAPlayer p : main.gameManager.getSurvivePlayer()) {
									AbilityList<Ability> alist = (AbilityList<Ability>) p.getAbility().clone();

									main.gameManager.AssignAbility(p);
									for (Ability a : alist) {
										main.gameManager.ResignAbility(p, a);
									}
								}
								Bukkit.broadcastMessage("\2474[\247cLAbility\2474] \247c관리자가 강제로 능력을 재추첨했습니다.");
								sender.sendMessage("\2478[\2477LAbility\2478] \247c능력을 재추첨했습니다.");
							}
						} else {
							sender.sendMessage("\2474[\247cLAbility\2474] \247c게임 진행 중이 아닙니다.");
						}
					}
					break;

				case "yes":
					if (!(sender instanceof Player)) {
						sender.sendMessage("\2474[\247cLAbility\2474] \247c플레이어만 명령어 사용이 가능합니다.");
						return true;
					}
					senderPlayer = (Player) sender;
					if (main.gameManager.isGameReady) {
						index = main.gameManager.players.indexOf(senderPlayer.getName());
						if (index >= 0) {
							LAPlayer lap = main.gameManager.players.get(index);
							if (lap.isAssign < 1) {
								senderPlayer.sendMessage("\2474[\247cLAbility\2474] \247c이미 능력이 확정되었습니다.");
								return true;
							}
							lap.isAssign = 0;
							sender.sendMessage("\2472[\247aLAbility\2472] \247a" + "능력을 결정했습니다. 게임 시작까지 기다려주세요.");
							return true;
						} else {
							senderPlayer.sendMessage("\2474[\247cLAbility\2474] \247c게임에 참여 중이 아닙니다.");
							return true;
						}
					} else {
						senderPlayer.sendMessage("\2474[\247cLAbility\2474] \247c게임 진행 중이 아닙니다.");
					}
					break;

				case "no":
					if (!(sender instanceof Player)) {
						sender.sendMessage("\2474[\247cLAbility\2474] \247c플레이어만 명령어 사용이 가능합니다.");
						return true;
					}
					senderPlayer = (Player) sender;
					if (main.gameManager.isGameReady) {
						index = main.gameManager.players.indexOf(senderPlayer.getName());
						if (index >= 0) {
							LAPlayer lap = main.gameManager.players.get(index);
							if (lap.isAssign < 1) {
								senderPlayer.sendMessage("\2474[\247cLAbility\2474] \247c이미 능력이 확정되었습니다.");
								return true;
							}

							AbilityList<Ability> alist = (AbilityList<Ability>) lap.getAbility().clone();


							if (alist.size() == 1) sender.sendMessage("\2472[\247aLAbility\2472] \247e" + alist.get(0).abilityName +" \247a능력을 버리고 새로운 능력을 갖습니다.");
							else if (alist.size() > 1) sender.sendMessage("\2472[\247aLAbility\2472] \247e" + alist.get(0).abilityName +" 외" + alist.size() + "개 \247a의 능력을 버리고 새로운 능력을 갖습니다.");


							main.gameManager.AssignAbility(lap);
							for (Ability a : alist) {
								main.gameManager.ResignAbility(lap, a);
							}

							lap.isAssign--;
							lap.CheckAbility(senderPlayer, -1);
							return true;
						} else {
							senderPlayer.sendMessage("\2474[\247cLAbility\2474] \247c게임에 참여 중이 아닙니다.");
							return true;
						}
					} else {
						senderPlayer.sendMessage("\2474[\247cLAbility\2474] \247c게임 진행 중이 아닙니다.");
					}
					break;

				case "skip":
					if (isOp) {
						if (main.gameManager.isGameReady) {
							for (LAPlayer lap : main.gameManager.players) lap.isAssign = 0;
							main.getServer().broadcastMessage("\2474[\247cLAbility\2474] \247c관리자가 모든 능력을 강제로 할당시켰습니다.");
						} else {
							sender.sendMessage("\2474[\247cLAbility\2474] \247c게임 진행 중이 아닙니다.");
						}
					}
					break;

				case "start":
					if (isOp) {
						if (!main.gameManager.isGameReady) {
							if (main.gameManager.overlapAbility || main.gameManager.shuffledAbilityIndex.size() > 0) LAbilityMain.instance.scheduleManager.PrepareTimer();
							else sender.sendMessage("\2474[\247cLAbility\2474] \247c게임 설정 이상으로 게임을 시작 할 수 없습니다.");
						} else {
							sender.sendMessage("\2474[\247cLAbility\2474] \247c게임이 진행 중입니다.");
						}
					}
					break;

				case "stop":
					if (isOp) {
						if (main.gameManager.isGameReady) {
							LAbilityMain.instance.gameManager.OnGameEnd(false);
							main.getServer().broadcastMessage("\2474[\247cLAbility\2474] \247c게임이 중단되었습니다.");
						} else {
							sender.sendMessage("\2474[\247cLAbility\2474] \247c게임 진행 중이 아닙니다.");
						}
					}
					break;

				case "test":
					if (isOp) {
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


							//if (LAbilityMain.instance.dataPacks.size() > 0 && LAbilityMain.instance.useResourcePack) {
							//	for (Player player : Bukkit.getOnlinePlayers()) {
							//		try {
							//			String url = LAbilityMain.instance.webServer.getWebIp() + player.getUniqueId();
							//			player.setResourcePack(url, (byte[]) null, false);
							//		} catch (Exception e) {
							//			Bukkit.getConsoleSender().sendMessage("\2474[\247cLAbility\2474] \247c리소스팩 로딩 오류!");
							//			Bukkit.getConsoleSender().sendMessage(e.getMessage());
							//		}
							//	}
							//}
							LAbilityMain.instance.gameManager.RunPassive();
						}
					}
					break;

				case "cooldown":
					if (isOp) {
						sender.sendMessage("\2472[\247aLAbility\2472] \247a쿨타임이 초기화되었습니다.");
						for (LAPlayer lap : LAbilityMain.instance.gameManager.players) {
							for (Ability a : lap.getAbility()) {
								a.resetCooldown();
							}
						}
					}
					break;

				case "reload":
					if (isOp) {
						LAbilityMain.instance.gameManager.OnGameEnd(false);
						LAbilityMain.instance.hasError = 0;
						LAbilityMain.instance.gameManager = new GameManager();
						LAbilityMain.instance.scheduleManager = new ScheduleManager();
						LAbilityMain.instance.dataPacks = new HashMap<>();
						LAbilityMain.instance.teamManager = new TeamManager();

						LAbilityMain.instance.rules = LuaAbilityLoader.LoadLuaRules();
						LAbilityMain.instance.abilities = LuaAbilityLoader.LoadAllLuaAbilities();
						LAbilityMain.instance.gameManager.ResetAll();
						BlockManager.ResetData();

						LAbilityMain.instance.gameManager.AbilityShuffle(true);

						//if (LAbilityMain.instance.dataPacks.size() > 0) {
						//	try {
						//		if (LAbilityMain.instance.webServer.start())
						//			LAbilityMain.instance.appendResourcePacks();
						//		else
						//			Bukkit.getConsoleSender().sendMessage("\2474[\247cLAbility\2474] \247c리소스팩을 사용하지 않습니다. 일부 능력의 효과음이 재생되지 않습니다.");
						//	} catch (Exception e) {
						//		Bukkit.getConsoleSender().sendMessage("\2474[\247cLAbility\2474] \247c리소스팩 생성 오류!");
						//		Bukkit.getConsoleSender().sendMessage(e.getMessage());
						//	}
						//}

						if (LAbilityMain.instance.rules.size() > 0) {
							LAbilityMain.instance.rules.get(0).InitScript();
							Bukkit.getConsoleSender().sendMessage("\2476[\247eLAbility\2476] \247e룰 [" + LAbilityMain.instance.rules.get(0).ruleName + "]이(가) 적용되었습니다.");
						} else
							Bukkit.getConsoleSender().sendMessage("\2474[\247cLAbility\2474] \247c룰이 존재하지 않습니다. 게임이 정상적으로 진행되지 않을 수 있습니다.");

						if (LAbilityMain.instance.hasError > 0)
							Bukkit.getConsoleSender().sendMessage("\2474[\247cLAbility\2474] \247c" + LAbilityMain.instance.hasError + "개의 능력을 로드하는데 문제가 생겼습니다. 해당 능력들은 로드하지 않습니다.");
						Bukkit.getConsoleSender().sendMessage("\2476[\247eLAbility\2476] \247ev" + LAbilityMain.instance.getDescription().getVersion() + " " +  LAbilityMain.instance.abilities.size() + "개 능력 로드 완료!");
						Bukkit.getConsoleSender().sendMessage("\2476[\247eLAbility\2476] \247e미추첨 능력 개수 : " + (LAbilityMain.instance.abilities.size() - LAbilityMain.instance.gameManager.shuffledAbilityIndex.size()) + "개 / 추첨 능력 개수 : "  + LAbilityMain.instance.gameManager.shuffledAbilityIndex.size() + "개");
						Bukkit.getConsoleSender().sendMessage("Made by MINUTE.");
						sender.sendMessage("\2472[\247aLAbility\2472] \247aReload Complete.");
					}
					break;

				case "variable":
					if (isOp) {
						sender.sendMessage("\2476-------[\247eVariable\2476]-------");

						String serverVariables = "\247a서버 \2476: \247b";
						for (String key : main.gameManager.variable.keySet()) {
							serverVariables += key + "(" + main.gameManager.variable.get(key) + ")  ";
						}
						sender.sendMessage(serverVariables);

						for (LAPlayer lap : main.gameManager.players) {
							String variables = lap.getPlayer().getDisplayName() + " \2476: \247b";
							for (String key : lap.getVariableMap().keySet()) {
								variables += key + "(" + lap.getVariableMap().get(key) + ")  ";
							}
							sender.sendMessage(variables);
						}
					}
					break;

				case "team":
					if (args.length == 1) {
						sender.sendMessage("\2476-------[\247eTeam Command\2476]-------");
						sender.sendMessage("\2476/la \247eteam create <팀 이름> <팀 색깔> <팀킬 여부> \247f: \247a팀을 새로 만듭니다."); //
						sender.sendMessage("\2476/la \247eteam remove <팀 이름> \247f: \247a팀을 삭제합니다. 가입된 팀원은 팀을 잃습니다."); //
						sender.sendMessage("\2476/la \247eteam join <플레이어 이름> <팀 이름> \247f: \247a해당 플레이어를 팀에 가입시킵니다."); //
						sender.sendMessage("\2476/la \247eteam leave <플레이어 이름> \247f: \247a해당 플레이어를 팀에 탈퇴시킵니다."); //
						sender.sendMessage("\2476/la \247eteam change <플레이어 이름> <팀 이름> \247f: \247a해당 플레이어의 팀을 변경합니다."); //
						sender.sendMessage("\2476/la \247eteam list \247f: \247a현재 생성된 팀과 팀원을 확인합니다.");
						sender.sendMessage("\2476/la \247eteam divide \247f: \247a모든 플레이어를 무작위 팀으로 배정합니다."); //
						sender.sendMessage("\2476/la \247eteam auto player <팀 인원 수> \247f: \247a자동으로 인원 수에 맞춰 팀을 생성해 배정합니다."); //
						sender.sendMessage("\2476/la \247eteam auto team <팀 갯수> \247f: \247a자동으로 팀을 생성해 배정합니다."); //
						sender.sendMessage("\2476!<할 말> \247f: \247a팀원에게 메세지를 전송합니다. 팀원이 2명 이상일 때만 작동합니다."); //
						return true;
					}

					switch (args[1].toLowerCase()) {
						case "create" -> {
							if (!isOp) break;
							if (args.length < 5) {
								sender.sendMessage("\2474[\247cLAbility\2474] \247c값이 제대로 입력되지 않았습니다.");
								break;
							}
							try {
								if (main.teamManager.teams.contains(args[2])) {
									sender.sendMessage("\2474[\247cLAbility\2474] \247c이미 존재하는 팀 입니다.");
									break;
								}
								ChatColor color = ChatColor.valueOf(args[3].toUpperCase());
								boolean teamKill = Boolean.parseBoolean(args[4]);
								main.teamManager.createTeam(color, args[2], teamKill);
								sender.sendMessage("\2476[\247eLAbility\2476] \247e팀 [" + color + args[2] + "\247e]이(가) 생성되었습니다.");
							} catch (Exception e) {
								sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 색입니다.");
							}
						}
						case "remove" -> {
							if (!isOp) break;
							if (main.teamManager.teams.contains(args[2])) {
								main.teamManager.removeTeam(args[2]);
								sender.sendMessage("\2476[\247eLAbility\2476] \247e팀 [" + args[2] + "\247e]이(가) 제거되었습니다.");
							} else sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 팀입니다.");
						}
						case "join" -> {
							if (!isOp) break;
							if (args.length < 4) {
								sender.sendMessage("\2474[\247cLAbility\2474] \247c값이 제대로 입력되지 않았습니다.");
								break;
							}
							index = main.gameManager.players.indexOf(args[2]);
							if (index >= 0) {
								LAPlayer lap = main.gameManager.players.get(index);
								if (lap.getTeam() != null && lap.getTeam().teamName.equals(args[2])) {
									sender.sendMessage("\2474[\247cLAbility\2474] \247c이미 해당 팀에 가입되어 있습니다.");
									break;
								}

								if (!main.teamManager.teams.contains(args[3])) {
									sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 팀입니다.");
									break;
								}

								main.teamManager.joinTeam(lap, main.teamManager.teams.get(args[3]));
								sender.sendMessage("\2476[\247eLAbility\2476] " + lap.getPlayer().getName() + "\247e님을 팀 [" + lap.getTeam().color + lap.getTeam().teamName + "\247e]에 가입시켰습니다.");
							} else {
								sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 플레이어입니다.");
							}
						}
						case "leave" -> {
							if (!isOp) break;
							if (args.length < 3) {
								sender.sendMessage("\2474[\247cLAbility\2474] \247c값이 제대로 입력되지 않았습니다.");
								break;
							}
							index = main.gameManager.players.indexOf(args[2]);
							if (index >= 0) {
								LAPlayer lap = main.gameManager.players.get(index);
								if (lap.getTeam() == null) {
									sender.sendMessage("\2474[\247cLAbility\2474] \247c해당 플레이어는 팀이 없습니다.");
									break;
								}

								sender.sendMessage("\2476[\247eLAbility\2476] " + lap.getPlayer().getName() + "\247e님을 팀 [" + lap.getTeam().color + lap.getTeam().teamName + "\247e]에서 탈퇴시켰습니다.");
								main.teamManager.leaveTeam(lap);
							} else {
								sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 플레이어입니다.");
							}
						}
						case "change" -> {
							if (!isOp) break;
							if (args.length < 4) {
								sender.sendMessage("\2474[\247cLAbility\2474] \247c값이 제대로 입력되지 않았습니다.");
								break;
							}
							index = main.gameManager.players.indexOf(args[2]);
							if (index >= 0) {
								LAPlayer lap = main.gameManager.players.get(index);
								if (lap.getTeam() == null) {
									sender.sendMessage("\2474[\247cLAbility\2474] \247c해당 플레이어는 팀이 없습니다.");
									break;
								}

								if (!main.teamManager.teams.contains(args[3])) {
									sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 팀입니다.");
									break;
								}

								main.teamManager.leaveTeam(lap);
								main.teamManager.joinTeam(lap, main.teamManager.teams.get(args[3]));
								sender.sendMessage("\2476[\247eLAbility\2476] " + lap.getPlayer().getName() + "\247e님을 팀 [" + lap.getTeam().color + lap.getTeam().teamName + "\247e]에서 팀 [" + lap.getTeam().color + lap.getTeam().teamName + "\247e]으로 변경시켰습니다.");
							} else {
								sender.sendMessage("\2474[\247cLAbility\2474] \247c존재하지 않는 플레이어입니다.");
							}
						}
						case "list" -> main.teamManager.ShowAllMember(sender);
						case "divide" -> {
							if (!isOp) break;
							if (main.teamManager.teams.size() > 0) {
								main.teamManager.divideTeamByTeamCount(0);
								sender.sendMessage("\2476[\247eLAbility\2476] \247e팀을 무작위 배정했습니다.");
							} else sender.sendMessage("\2474[\247cLAbility\2474] \247c팀이 생성된 상태가 아닙니다.");
						}
						case "auto" -> {
							if (!isOp) break;
							if (args.length < 4) {
								sender.sendMessage("\2474[\247cLAbility\2474] \247c값이 제대로 입력되지 않았습니다.");
								break;
							}
							switch (args[2].toLowerCase()) {
								case "player":
									try {
										int memberCount = Integer.parseInt(args[3]);
										if (memberCount < 2) {
											sender.sendMessage("\2474[\247cLAbility\2474] \247c값이 너무 적습니다.");
											break;
										}
										if (memberCount >= main.gameManager.players.size()) {
											sender.sendMessage("\2474[\247cLAbility\2474] \247c값이 너무 많습니다.");
											break;
										}
										main.teamManager.divideTeamByMemberCount(memberCount);
										sender.sendMessage("\2476[\247eLAbility\2476] \247e팀을 자동 생성 후, 무작위 배정했습니다.");
										break;
									} catch (Exception e) {
										e.printStackTrace();
										sender.sendMessage("\2474[\247cLAbility\2474] \247c숫자를 입력해 주세요.");
									}
									break;
								case "team":
									try {
										int teamCount = Integer.parseInt(args[3]);
										if (teamCount < 2) {
											sender.sendMessage("\2474[\247cLAbility\2474] \247c값이 너무 적습니다.");
											break;
										}
										if (teamCount > main.gameManager.players.size()) {
											sender.sendMessage("\2474[\247cLAbility\2474] \247c값이 너무 많습니다.");
											break;
										}
										main.teamManager.divideTeamByTeamCount(teamCount);
										sender.sendMessage("\2476[\247eLAbility\2476] \247e팀을 자동 생성 후, 무작위 배정했습니다.");
										break;
									} catch (Exception e) {
										e.printStackTrace();
										sender.sendMessage("\2474[\247cLAbility\2474] \247c숫자를 입력해 주세요.");
									}
									break;
							}
						}
					}
			}
		}
		return true;
	}
}
