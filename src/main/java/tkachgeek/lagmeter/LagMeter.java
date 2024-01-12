package tkachgeek.lagmeter;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.plugin.java.JavaPlugin;
import ru.cwcode.commands.Command;
import tkachgeek.tkachutils.numbers.NumbersUtils;
import tkachgeek.tkachutils.scheduler.Scheduler;

public final class LagMeter extends JavaPlugin {
  public static BossBar bar = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SEGMENTED_20);
  public static BossBar barMs = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SOLID);
  
  @Override
  public void onEnable() {
    new Command("lag", new LagMeterToggle(true)).register();
    
    Scheduler.create(this)
             .async()
             .perform(LagMeter::tick)
             .infinite()
             .register(this, 1);
  }
  
  @Override
  public void onDisable() {
    // Plugin shutdown logic
  }
  
  public void tick() {
    if (bar.getPlayers().isEmpty()) return;
    
    var tickTime = Bukkit.getAverageTickTime();
    var tps = 1000 / tickTime;
    var tickTimeToBar = 50 - tickTime;
    
    if (tps > 20) tps = 20;
    else if (tps < 0) tps = 0;
    
    if (tickTimeToBar < 0) tickTimeToBar = 0;
    
    bar.setTitle("TPS: " + NumbersUtils.round(tps, 1));
    bar.setProgress(tps / 20);
    
    barMs.setTitle("Avg " + NumbersUtils.round(tickTime, 1) + " ms/tick");
    barMs.setProgress(1 - tickTimeToBar / 50);
    
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
  }
}
