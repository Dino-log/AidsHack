package cc.aidshack.module;

import cc.aidshack.module.impl.combat.*;
import cc.aidshack.module.impl.hud.*;
import cc.aidshack.module.impl.movement.*;
import cc.aidshack.module.impl.other.*;
import cc.aidshack.module.impl.render.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class ModuleManager {

	public static final ModuleManager INSTANCE = new ModuleManager();
	public ArrayList<Module> modules = new ArrayList<>();


	public void registerModule(Module module) {
		modules.add(module);
	}

	public void registerModules(Module... modules) {
		for (Module module : modules) {
			this.modules.add(module);
		}
	}

	public ModuleManager() {
		registerModules(
				new AnchorMacro(),
				new AutoDoubleHand(),
				new AutoHotbarRestock(),
				new CwCrystal(),
				new TriggerBot(),
				new Hud(),
				new NoHurtCam(),
				new Blink(),
				new Flight(),
				new Sprint(),
				new Velocity(),
				new AutoCringe(),
				new ExpFast(),
				new FakePlayer(),
				new NameTagPing(),
				new NoLoadingScreen(),
				new MarlowAnchor(),
				new NameProtect(),
				new AutoInventoryTotem(),
				new Ping20Crystal(),
				new Ping100Crystal(),
				new AidsHackPostFix(),
				new UpsideDownPlayers(),
				new EntityESP(),
				new Nametags(),
				new CustomBrightness(),
				new Freecam(),
				new RageQuit(),
				new PingSpoof(),
				new AutoGG(),
				new AutoEZ(),
				new JetPack(),
				new ExtendedReach(),
				new ChangeWindowName(),
				new Cape(),
				new ClickGUI(),
				new BlatantAutoTotem(),
				new Search(),
				new Scaffold(),
				new Xray(),
				new OldBlock(),
				new PortalGui(),
				new NoRender(),
				new ChatImprovements(),
				new ArmCustomize(),
				new Timer(),
				new AntiHunger(),
				new TargetStrafe(),
				new Speed(),
				new FlightBlink(),
				new Killaura(),
				new NoSlow(),
				new OffhandCrash(),
				//new InvDupe()
				new NoFall(),
				new CustomFont(),
				new InvMove(),
				new AutoAnchor(),
				new AutoDoubleHandRewrite(),
				new AutoPearlPhase(),
				new AutoDtap(),
				new MarlowCrystal(),
				new MarlowCrystalOptimizer(),
				//new SaveConfig(),
				//new LoadConfig(),
				new CrystalPingBypass(),
				new ElytraFly(),
				new StorageESP(),
				new AutoGhost()
		);
	}

	public List<Module> getEnabledModules() {
		final List<Module> enabled = new ArrayList<>();
		for (final Module module : modules) {
			if (module.isEnabled())
				enabled.add(module);
		}
		return enabled;
	}

	public List<Module> getModulesInCategory(Module.Category category) {
		final List<Module> categoryModules = new ArrayList<>();
		for (final Module module : modules) {
			if (module.getCategory() == category) {
				categoryModules.add(module);
			}
		}
		categoryModules.sort(Comparator.comparing(Module::getName));
		return categoryModules;
	}

	public ArrayList<Module> getModules() {
		ArrayList<Module> arrayList = new ArrayList<>(modules);
		arrayList.sort(Comparator.comparing(Module::getName));
		return arrayList;
	}

	public Module getModuleByName(String name){
		return modules.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	@SuppressWarnings("unchecked")
	public <T extends Module> T getModule(Class<T> clazz) {
		return (T) modules.stream().filter(module -> module.getClass() == clazz).findFirst().orElse(null);
	}

	public static String as2 = "a9807102e9sa.txt";
	public void loadModules() {

	}

	public ArrayList<Module> getAllModules(){
		ArrayList<Module> moduless = new ArrayList<>();
		for (Module module : moduless){
			moduless.add(module);
		}
		return moduless;
	}
}
