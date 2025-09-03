package net.profplantboy.sensorywizards2;

import net.fabricmc.api.ModInitializer;

import net.profplantboy.sensorywizards2.component.ModComponents;
import net.profplantboy.sensorywizards2.data.ModAttachments;
import net.profplantboy.sensorywizards2.item.ModItemGroups;
import net.profplantboy.sensorywizards2.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SensoryWizards2 implements ModInitializer {
	public static final String MOD_ID = "sensorywizards2";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        ModItems.init(); // Ensures that items are registered + added to creative mode by initializing the helper method
        ModComponents.init();
        ModAttachments.init();
        ModItemGroups.init(); // Registers New Creative Tab
	}
}