package tkachgeek.lagmeter;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.plugin.java.JavaPlugin;
import ru.cwcode.commands.Command;
import tkachgeek.tkachutils.datetime.Expireable;
import tkachgeek.tkachutils.numbers.NumbersUtils;
import tkachgeek.tkachutils.scheduler.Scheduler;

public final class LagMeter extends JavaPlugin {
  private static final String[] animationFrames =
     {
        "|...................",
        ".|..................",
        "..|.................",
        "...|................",
        "....|...............",
        ".....|..............",
        "......|.............",
        ".......|............",
        "........|...........",
        ".........|..........",
        "..........|.........",
        "...........|........",
        "............|.......",
        ".............|......",
        "..............|.....",
        "...............|....",
        "................|...",
        ".................|..",
        "..................|.",
        "...................|",
     };
  public static BossBar bar = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SEGMENTED_20);
  public static BossBar barMs = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SOLID);
  public static BossBar barCurrent = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SOLID);
  private static long maxTickTime = 0;
  private static long lastTickNanoTime = Long.MIN_VALUE;
  private static Expireable lastMaxTimeUpdate = new Expireable(10000);
  
  @Override
  public void onEnable() {
    new Command("lag", new LagMeterToggle(true)).register();
    
    Scheduler.create(this)
             .perform(LagMeter::tick)
             .infinite()
             .register(this, 1);
  }
  
  @Override
  public void onDisable() {
    // Plugin shutdown logic
  }
  
  public void tick() {
    if (bar.getPlayers().isEmpty()) {
      lastTickNanoTime = System.nanoTime();
      return;
    }
    
    if (lastMaxTimeUpdate.isExpiredAndReset()) maxTickTime = 0;
    
    long prevTickTime = System.nanoTime() - lastTickNanoTime;
    maxTickTime = Math.max(prevTickTime - 50_000_000, maxTickTime);
    
    if (maxTickTime == prevTickTime) lastMaxTimeUpdate.reset();
    lastTickNanoTime = System.nanoTime();
    
    var tickTime = Bukkit.getAverageTickTime();
    var tps = 1000 / tickTime;
    var tickTimeToBar = 50 - tickTime;
    
    if (tps > 20) tps = 20;
    else if (tps < 0) tps = 0;
    
    if (tickTimeToBar < 0) tickTimeToBar = 0;
    
    bar.setTitle("TPS: " + NumbersUtils.round(tps, 1) + " " + getAnimationFrame());
    bar.setProgress(tps / 20);
    
    barMs.setTitle("Avg " + NumbersUtils.round(tickTime, 1) + " ms/tick   Max " + NumbersUtils.round(maxTickTime / 1_000_000D, 1) + " ms/tick");
    barMs.setProgress(1 - tickTimeToBar / 50);
    
    barCurrent.setTitle("Cur " + NumbersUtils.round(prevTickTime / 1_000_000D, 1) + " ms/tick");
    barCurrent.setProgress(NumbersUtils.bound((prevTickTime - 25_000_000) / 50_000_000D, 0, 1));
    
    if (tps < 12) {
      bar.setColor(BarColor.RED);
    } else if (tps < 17) {
      bar.setColor(BarColor.YELLOW);
    } else bar.setColor(BarColor.GREEN);
    
    if (tickTimeToBar < 5) {
      barMs.setColor(BarColor.RED);
    } else if (tickTimeToBar < 25) {
      barMs.setColor(BarColor.YELLOW);
    } else barMs.setColor(BarColor.GREEN);
    
    var diff = Math.abs(prevTickTime - 50_000_000);
    
    if (diff < 10_000_000) {
      barCurrent.setColor(BarColor.GREEN);
    } else if (diff < 25_000_000) {
      barCurrent.setColor(BarColor.YELLOW);
    } else barCurrent.setColor(BarColor.RED);
  }
  
  private String getAnimationFrame() {
    return animationFrames[(Bukkit.getCurrentTick() % animationFrames.length)];
  }
}
