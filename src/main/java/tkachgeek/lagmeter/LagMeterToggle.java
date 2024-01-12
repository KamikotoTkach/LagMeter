package tkachgeek.lagmeter;

import ru.cwcode.commands.paperplatform.executor.LocalToggleExecutor;

public class LagMeterToggle extends LocalToggleExecutor {
  
  public LagMeterToggle(boolean initialState) {
    super(initialState);
  }
  
  @Override
  public void onEnable() {
    LagMeter.bar.addPlayer(player());
    LagMeter.barMs.addPlayer(player());
  }
  
  @Override
  public void onDisable() {
    LagMeter.bar.removePlayer(player());
    LagMeter.barMs.removePlayer(player());
  }
  
  @Override
  public void executeForPlayer()  {
  
  }
}
