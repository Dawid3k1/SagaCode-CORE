package org.sagamc.sagamcCore;

import org.sagamc.sagamcCore.core.SagaCaseCommand;
import org.sagamc.sagamcCore.shop.*;
import org.sagamc.sagamcCore.codes.*;
import org.sagamc.sagamcCore.chat.*;
import org.sagamc.sagamcCore.afk.*;
import org.sagamc.sagamcCore.teleport.*;
import org.sagamc.sagamcCore.commands.*;
import org.sagamc.sagamcCore.vanish.*;
import org.sagamc.sagamcCore.listeners.*;
import org.sagamc.sagamcCore.tasks.*;
import org.sagamc.sagamcCore.util.*;
import org.sagamc.sagamcCore.home.*;
import org.sagamc.sagamcCore.warp.*;
import org.sagamc.sagamcCore.lottery.*;
import org.sagamc.sagamcCore.stats.*;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Random;

public class SagamcCore extends JavaPlugin {
    private Economy economy;
    private FileConfiguration messages;
    private ShopConfig shopConfig;
    private CodeManager codeManager;
    private ChatManager chatManager;
    private TeleportManager teleportManager;
    private AfkZoneManager afkZoneManager;
    private VanishManager vanishManager;
    private AutoMessageTask autoMessageTask;
    private HomeManager homeManager;
    private WarpManager warpManager;
    private LotteryManager lotteryManager;
    private StatsManager statsManager;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.saveResource("messages.yml", false);
        this.messages = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "messages.yml"));

        if (!this.setupEconomy()) {
            this.printNoVaultMessage();
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("FASTCODE-CASE") == null) {
            this.getServer().getPluginManager().disablePlugin(this);
            this.printNoCaseMessage();
            return;
        }

        initializePlugin();
        this.printStartupMessage();
    }

    @Override
    public void onDisable() {
        if (this.autoMessageTask != null) {
            this.autoMessageTask.cancel();
        }
    }

    private void initializePlugin() {
        this.shopConfig = new ShopConfig(this.getConfig());
        ShopGUI.setConfig(this.shopConfig);
        this.getCommand("kucharz").setExecutor(new ShopCommand());
        this.getServer().getPluginManager().registerEvents(new ShopListener(this.economy, this.shopConfig, this.messages), this);

        this.codeManager = new CodeManager(this);
        this.getCommand("kod").setExecutor(new CodeCommand(this.codeManager, this.messages));

        this.chatManager = new ChatManager(this.getConfig());
        this.getCommand("chat").setExecutor(new ChatCommand(this.chatManager, this.messages));
        this.getServer().getPluginManager().registerEvents(new ChatListener(this.chatManager, this.messages), this);

        this.teleportManager = new TeleportManager(this, this.getConfig(), this.messages);
        this.getCommand("tpa").setExecutor(new TpaCommand(this.teleportManager, this.messages));
        this.getCommand("tpaccept").setExecutor(new TpacceptCommand(this.teleportManager, this.messages));
        this.getCommand("tp").setExecutor(new TpCommand(this.teleportManager, this.messages));
        this.getCommand("tphere").setExecutor(new TphereCommand(this.teleportManager, this.messages));
        this.getCommand("spawn").setExecutor(new SpawnCommand(this.teleportManager, this.getConfig(), this.messages));
        this.getServer().getPluginManager().registerEvents(new TeleportListener(this.teleportManager), this);

        this.afkZoneManager = new AfkZoneManager(this, this.economy, this.getConfig(), this.messages);
        this.getServer().getPluginManager().registerEvents(new AfkZoneListener(this.afkZoneManager), this);

        this.vanishManager = new VanishManager(this);
        this.getCommand("vanish").setExecutor(new VanishCommand(this.vanishManager, this.messages));

        this.getServer().getPluginManager().registerEvents(new JoinQuitListener(this.getConfig(), this.messages, this.vanishManager), this);
        this.getServer().getPluginManager().registerEvents(new DeathListener(this.getConfig(), this.messages), this);

        this.getCommand("alert").setExecutor(new BroadcastCommand(this.messages));

        this.autoMessageTask = new AutoMessageTask(this.getConfig());
        this.autoMessageTask.start(this);

        this.getCommand("saga-case").setExecutor(new SagaCaseCommand());
        this.getCommand("fly").setExecutor(new FlyCommand(this.messages));
        this.getCommand("speed").setExecutor(new SpeedCommand(this.messages));
        this.getCommand("helpop").setExecutor(new HelpopCommand(this.messages));
        this.getCommand("pay").setExecutor(new PayCommand(this.economy, this.messages));
        this.getCommand("gamemode").setExecutor(new GamemodeCommand(this.getConfig()));
        this.getCommand("repair").setExecutor(new RepairCommand(this.getConfig()));
        this.getCommand("repairall").setExecutor(new RepairCommand(this.getConfig()));
        this.getCommand("msg").setExecutor(new MsgCommand(this.messages));
        this.getCommand("r").setExecutor(new ReplyCommand(this.messages));
        this.getCommand("invsee").setExecutor(new InvseeCommand(this.getConfig()));

        this.homeManager = new HomeManager(this, this.getConfig());
        this.getCommand("sethome").setExecutor(new SetHomeCommand(this.homeManager, this.messages));
        this.getCommand("home").setExecutor(new HomeCommand(this.homeManager, this.teleportManager, this.messages));
        this.getCommand("delhome").setExecutor(new DelHomeCommand(this.homeManager, this.messages));
        this.getCommand("homes").setExecutor(new HomesCommand(this.homeManager, this.messages));

        this.warpManager = new WarpManager(this);
        this.getCommand("setwarp").setExecutor(new SetWarpCommand(this.warpManager, this.messages));
        this.getCommand("warp").setExecutor(new WarpCommand(this.warpManager, this.teleportManager, this.messages));
        this.getCommand("delwarp").setExecutor(new DelWarpCommand(this.warpManager, this.messages));
        this.getServer().getPluginManager().registerEvents(new WarpGUIListener(this.warpManager, this.teleportManager, this.messages), this);

        this.getCommand("kowal").setExecutor(new KowalCommand(this.messages));
        this.getCommand("ksiegarz").setExecutor(new KsiegarzCommand(this.messages));
        this.getServer().getPluginManager().registerEvents(new CraftingGUIListener(this.messages), this);

        this.lotteryManager = new LotteryManager(this, this.economy, this.getConfig(), this.messages);
        this.getCommand("lotto").setExecutor(new LotteryCommand(this.lotteryManager, this.messages));
        this.getServer().getPluginManager().registerEvents(new LotteryGUIListener(this.lotteryManager, this.messages), this);


        this.statsManager = new StatsManager(this);
        this.getCommand("stats").setExecutor(new StatsCommand(this.statsManager, this.economy, this.messages));
        this.getServer().getPluginManager().registerEvents(new StatsListener(this.statsManager), this);
        this.getServer().getPluginManager().registerEvents(new StatsGUIListener(this.messages), this);

        this.getCommand("topmoney").setExecutor(new TopMoneyCommand(this.economy, this.messages));

        int drawInterval = getConfig().getInt("lottery.drawIntervalMinutes", 60);
        new LotteryDrawTask(lotteryManager).runTaskTimer(this, 20L * 60 * drawInterval, 20L * 60 * drawInterval);

    }

    private void reloadPlugin() {
        if (this.autoMessageTask != null) {
            this.autoMessageTask.cancel();
        }

        reloadConfig();
        this.messages = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "messages.yml"));

        this.shopConfig = new ShopConfig(this.getConfig());
        ShopGUI.setConfig(this.shopConfig);

        this.codeManager.reload();
        this.chatManager.loadConfig(this.getConfig());
        this.teleportManager.loadConfig(this.getConfig());
        this.afkZoneManager.loadConfig();

        this.autoMessageTask = new AutoMessageTask(this.getConfig());
        this.autoMessageTask.start(this);

        getLogger().info("Plugin został pomyślnie przeładowany!");
    }

    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        this.economy = rsp.getProvider();
        return this.economy != null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("sagacore") && args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("sagamc.reload")) {
                sender.sendMessage(color(messages.getString("brak-permisji")));
                return true;
            }

            reloadPlugin();
            sender.sendMessage(color(messages.getString("reload")));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(color(messages.getString("tylko-gracz")));
            return true;
        }

        if (command.getName().equalsIgnoreCase("swiat-pvp")) {
            if (!player.hasPermission("sagamc.pvp")) {
                player.sendMessage(color(messages.getString("brak-permisji")));
                return true;
            }

            double cost = getConfig().getDouble("cena.pvp");

            if (!economy.has(player, cost)) {
                player.sendMessage(color(messages.getString("brak-pieniedzy").replace("%cena%", String.valueOf(cost))));
                return true;
            }

            economy.withdrawPlayer(player, cost);
            teleportFromSky(player, "world_pvp");
            player.sendMessage(color(messages.getString("pvp").replace("%cena%", String.valueOf(cost))));
            return true;
        }

        if (command.getName().equalsIgnoreCase("swiat-nopvp")) {
            if (!player.hasPermission("sagamc.nopvp")) {
                player.sendMessage(color(messages.getString("brak-permisji")));
                return true;
            }

            double cost = getConfig().getDouble("cena.nopvp");

            if (!economy.has(player, cost)) {
                player.sendMessage(color(messages.getString("brak-pieniedzy").replace("%cena%", String.valueOf(cost))));
                return true;
            }

            economy.withdrawPlayer(player, cost);
            teleportFromSky(player, "world_nopvp");
            player.sendMessage(color(messages.getString("nopvp").replace("%cena%", String.valueOf(cost))));
            return true;
        }

        return false;
    }

    public void teleportFromSky(Player player, String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            player.sendMessage("§cŚwiat nie istnieje!");
            return;
        }

        Random random = new Random();
        int radius = getConfig().getInt("teleport1.radius", 5000);

        int x = random.nextInt(radius * 2) - radius;
        int z = random.nextInt(radius * 2) - radius;

        int surfaceY = world.getHighestBlockYAt(x, z);
        Block surfaceBlock = world.getBlockAt(x, surfaceY - 1, z);

        if (surfaceBlock.getType() == Material.LAVA || surfaceBlock.getType() == Material.WATER) {
            teleportFromSky(player, worldName);
            return;
        }

        Location skyLocation = new Location(world, x + 0.5, surfaceY + 150, z + 0.5);
        player.teleport(skyLocation);

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 700, 0, false, false));
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1f, 0.6f);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                if (player.isOnGround()) {
                    world.spawnParticle(Particle.EXPLOSION, player.getLocation(), 1);
                    world.spawnParticle(Particle.CLOUD, player.getLocation(), 30, 1, 0.5, 1, 0.1);
                    world.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);
                    cancel();
                    return;
                }

                world.spawnParticle(Particle.FLAME, player.getLocation(), 8, 0.3, 0.3, 0.3, 0.02);
                world.spawnParticle(Particle.SMOKE, player.getLocation(), 4, 0.2, 0.2, 0.2, 0.01);
            }
        }.runTaskTimer(this, 0L, 2L);
    }

    public static String color(String text) {
        return ColorUtil.legacyColor(text);
    }

    private void printStartupMessage() {
        getLogger().info("╔══════════════════════════════════════════════════════════════╗");
        getLogger().info("║                                                              ║");
        getLogger().info("╟ Plugin: SAGAMC-CORE                                          ║");
        getLogger().info("╟ Został uruchomiony!                                          ║");
        getLogger().info("║                                                              ║");
        getLogger().info("╟ Discord: https://discord.gg/sagacode                         ║");
        getLogger().info("║                                                              ║");
        getLogger().info("╚══════════════════════════════════════════════════════════════╝");
    }

    private void printNoVaultMessage() {
        getLogger().info("╔══════════════════════════════════════════════════════════════╗");
        getLogger().info("║                                                              ║");
        getLogger().info("╟ Plugin: SAGAMC-CORE                                          ║");
        getLogger().info("╟ Został nie uruchomiony!                                      ║");
        getLogger().info("╟ Aby plugin zadzialal musisz miec dodatkowy: VAULT            ║");
        getLogger().info("║                                                              ║");
        getLogger().info("╟ Discord: https://discord.gg/sagacode                         ║");
        getLogger().info("║                                                              ║");
        getLogger().info("╚══════════════════════════════════════════════════════════════╝");
    }

    private void printNoCaseMessage() {
        getLogger().info("╔══════════════════════════════════════════════════════════════╗");
        getLogger().info("║                                                              ║");
        getLogger().info("╟ Plugin: SAGAMC-CORE                                          ║");
        getLogger().info("╟ Został nie uruchomiony!                                      ║");
        getLogger().info("╟ Aby plugin zadzialal musisz miec dodatkowy: FASTCODE-CASE    ║");
        getLogger().info("║                                                              ║");
        getLogger().info("╟ Discord: https://discord.gg/sagacode                         ║");
        getLogger().info("║                                                              ║");
        getLogger().info("╚══════════════════════════════════════════════════════════════╝");
    }
}