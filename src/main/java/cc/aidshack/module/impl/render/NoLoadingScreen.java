package cc.aidshack.module.impl.render;

import cc.aidshack.module.Module;

public class NoLoadingScreen extends Module {

	public NoLoadingScreen() {
		super("NoLoadingScreen", "removes pre-world loading screen", false,Category.RENDER);
		this.setKey(-1);
	}



}
