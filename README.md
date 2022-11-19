# myLoot
Instanced loot chests for Minecraft Fabric 

<p><img src="https://i.imgur.com/oGN5UNd.png" alt="https://i.imgur.com/oGN5UNd.png" width="600" height="386" /><img src="https://i.imgur.com/svaGO8D.png" alt="https://i.imgur.com/svaGO8D.png" width="600" height="422" /></p>
<p><img src="https://i.imgur.com/2OI03Hl.png" alt="https://i.imgur.com/2OI03Hl.png" width="300" height="195" /><img src="https://i.imgur.com/a3x6uOm.png" alt="https://i.imgur.com/a3x6uOm.png" width="300" height="209" /><img src="https://i.imgur.com/tsdJHfs.png" alt="https://i.imgur.com/tsdJHfs.png" width="490" height="280" /><img src="https://i.imgur.com/l5I8gAy.png" alt="https://i.imgur.com/l5I8gAy.png" width="600" height="511" /></p>
<p><img src="https://i.imgur.com/dmNMnPm.png" alt="https://i.imgur.com/dmNMnPm.png" width="400" height="243" /></p>
<p><img src="https://i.imgur.com/zSsH7X4.gif" alt="https://i.imgur.com/zSsH7X4.gif" /></p>
<p style="text-align: center;">&nbsp;</p>
<p style="text-align: center;"><a title="https://www.curseforge.com/members/spoorn/projects" href="https://www.curseforge.com/members/spoorn/projects"><img style="display: block; margin-left: auto; margin-right: auto;" src="https://img.shields.io/static/v1?label=%20&amp;message=other%20projects&amp;color=4b5966&amp;labelColor=658f79&amp;logo=curseforge&amp;logoColor=white&amp;style=for-the-badge" alt="https://img.shields.io/static/v1?label=%20&amp;message=other%20projects&amp;color=4b5966&amp;labelColor=658f79&amp;logo=curseforge&amp;logoColor=white&amp;style=for-the-badge" width="175" height="28" /></a>&nbsp;<a title="https://discord.gg/xfddZwG898" href="https://discord.gg/xfddZwG898"><img style="display: block; margin-left: auto; margin-right: auto;" src="https://img.shields.io/discord/990178919795785749?color=4b5966&amp;label=%20&amp;labelColor=658f79&amp;logo=discord&amp;logoColor=white&amp;style=for-the-badge" alt="https://img.shields.io/discord/990178919795785749?color=4b5966&amp;label=%20&amp;labelColor=658f79&amp;logo=discord&amp;logoColor=white&amp;style=for-the-badge" width="120" height="28" /></a>&nbsp;<a title="https://github.com/spoorn/myLoot" href="https://github.com/spoorn/myLoot"><img style="display: block; margin-left: auto; margin-right: auto;" src="https://img.shields.io/github/stars/spoorn/myLoot?color=4b5966&amp;labelColor=658f79&amp;label=github%20&amp;logo=github&amp;logoColor=white&amp;style=for-the-badge" alt="https://img.shields.io/github/stars/spoorn/myLoot?color=4b5966&amp;labelColor=658f79&amp;label=github%20&amp;logo=github&amp;logoColor=white&amp;style=for-the-badge" width="120" height="28" /></a>&nbsp;<a title="https://github.com/spoorn/myLoot/issues" href="https://github.com/spoorn/myLoot/issues"><img style="display: block; margin-left: auto; margin-right: auto;" src="https://img.shields.io/github/issues-raw/spoorn/myLoot?color=4b5966&amp;labelColor=658f79&amp;label=issues%20&amp;logo=github&amp;logoColor=white&amp;style=for-the-badge" alt="https://img.shields.io/github/issues-raw/spoorn/myLoot?color=4b5966&amp;labelColor=658f79&amp;label=issues%20&amp;logo=github&amp;logoColor=white&amp;style=for-the-badge" width="120" height="28" /></a></p>
<p style="text-align: center;">&nbsp;</p>
<h2><strong>Description</strong></h2>
<p><span style="font-size: 14px;">myLoot replaces world-generated loot containers such as loot chests and barrels with a special myLoot container variant that has instanced loot per player.&nbsp; This means each player can loot whatever they want out of any loot chest and other players will still have access to the original loot - providing a better looting experience!</span></p>
<p><span style="font-size: 14px;">No more logging onto a multiplayer server to find chunks within a 5000 block radius have already been looted by your friends.&nbsp; Loot for all</span></p>
<p><span style="font-size: 14px;">myLoot is useful in singleplayer servers as well - you can visually see which loot chests you have already opened so you don't end up getting lost and re-looting areas</span></p>
<p><span style="font-size: 1.2rem;">&nbsp;</span></p>
<h2><strong>Features</strong></h2>
<p><span style="font-size: 14px;">All replacement works in modded structures as well as vanilla (dungeons, villages, strongholds, dimensions, etc.)</span></p>
<ul>
<li><span style="font-size: 14px;">Replaces loot chests (works for double chests!)</span></li>
<li><span style="font-size: 14px;">Replaces loot barrels</span></li>
<li><span style="font-size: 14px;">Replaces chest Minecarts</span></li>
<li><span style="font-size: 14px;">Replaces Shulker Boxes</span></li>
<li><span style="font-size: 14px;">myLoot containers will change in appearance to the player if they have opened it.&nbsp; This is client-side so other players will still see the unopened textures if they have not opened it.</span></li>
<li><span style="font-size: 14px;">myLoot containers will warn the player that breaking it can affect other players.&nbsp; A player can still break myLoot containers by holding Sneak while breaking.&nbsp;&nbsp;<em>Note: breaking a myLoot container by default will only drop the instanced loot of the player who broke it (plus the loot container item).&nbsp; This can be changed via the "dropBehavior" config to instead drop ALL players' loot</em></span></li>
<li><span style="font-size: 14px;">This should work with&nbsp;<strong>modded items</strong> in the myLoot containers as well and with&nbsp;<strong>modded loot containers</strong></span></li>
<li><span style="font-size: 14px;">myLoot Chest is craftable with a regular Minecraft chest surrounded by enchanted apples (see images above)</span></li>
<li><span style="font-size: 14px;">myLoot Chest can be combined into a double chest</span></li>
<li><span style="font-size: 14px;">myLoot Chest Minecart is craftable with a myLoot chest and minecart</span></li>
<li><span style="font-size: 14px;">myLoot Shulker Box when broken will drop a special myLoot Shulker Box <strong>Item</strong> that persists instanced inventories, so players can still access their instanced loot from the shulker box when it is placed back down.&nbsp; Essentially, it's a portable instanced shulker box!</span></li>
</ul>
<p>&nbsp;</p>
<p>&nbsp;</p>
<h3><strong>Dependencies</strong></h3>
<p>This mod requires:</p>
<ul>
<li>SpoornPacks -&nbsp;<a href="https://www.curseforge.com/minecraft/mc-mods/spoornpacks">https://www.curseforge.com/minecraft/mc-mods/spoornpacks</a></li>
<li>Fabric API - <a style="background-color: #ffffff;" href="https://www.curseforge.com/minecraft/mc-mods/fabric-api">https://www.curseforge.com/minecraft/mc-mods/fabric-api</a>&nbsp;</li>
</ul>
<p>&nbsp;</p>
<p><img src="https://i.imgur.com/HabVZJR.png" alt="requires fabric" width="150" height="50" /></p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<h3><strong>FAQ</strong></h3>
<p><span style="font-size: 14px;"><b>1.&nbsp;</b>How does inserters/extractors such as hoppers/pipes affect the myLoot containers?</span></p>
<p><em>Modifying a myLoot container's inventory before player opening - such as using hoppers to add/remove items - should not affect the player's instanced inventory.&nbsp; The <strong>original rolled loot</strong> will persist so any player opening a myLoot container for the first time will still get the original loot.</em></p>
<p><em>Also, myLoot containers essentially have an invisible inventory that contains the original <strong>non-player instanced</strong> inventory.&nbsp; Modifying that inventory via inserters/extractors such as hoppers/pipes will work, but when a player breaks the myLoot container, only&nbsp;<strong>additions</strong> <strong>to the original rolled loot</strong> will drop (plus the player's instanced inventory who broke the myLoot container if applicable).&nbsp; This is to prevent mass duplication of loot (however duplication of the original loot one time is still possible... I haven't yet found a way to prevent this without being invasive i.e. I don't want to break any code, vanilla or modded, that has logic around a loot container's inventory so I can't just delete the original invisible inventory).</em></p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<h3><strong>Credits</strong></h3>
<p>The mechanics of myLoot containers changing appearance when opened by a player, and the stylish glowing texture design are heavily inspired by&nbsp;the Forge <a href="https://www.curseforge.com/minecraft/mc-mods/lootr">Lootr</a> mod - but everything else was originally made from scratch.&nbsp; myLoot is not a direct port of Lootr, and features will be different.&nbsp; An official port of Lootr to Fabric is in the works.&nbsp; Please see&nbsp;<a href="https://github.com/spoorn/myLoot/issues/1">https://github.com/spoorn/myLoot/issues/1</a></p>
<p>&nbsp;</p>
<p>&nbsp;&nbsp;</p>
<h2>Need a Server?</h2>
<p><a title="https://bisecthosting.com/spoorn" href="https://bisecthosting.com/spoorn"><img src="https://i.imgur.com/KPURPK0.png" alt="https://bisecthosting.com/spoorn" width="1000" height="158" /></a></p>
