package org.teacon.powertool;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.teacon.powertool.block.PowerToolBlocks;
import org.teacon.powertool.item.PowerToolItems;
import org.teacon.powertool.menu.PowerToolMenus;
import org.teacon.powertool.network.PowerToolNetwork;

@Mod(PowerTool.MODID)
public class PowerTool {

    public static final String MODID = "powertool";

    public PowerTool() {
        PowerToolBlocks.register();
        PowerToolItems.register();
        PowerToolNetwork.register();
        PowerToolMenus.register();
        PowerToolSoundEvents.register(FMLJavaModLoadingContext.get().getModEventBus());
        PowerToolConfig.init(ModLoadingContext.get());
    }
}
