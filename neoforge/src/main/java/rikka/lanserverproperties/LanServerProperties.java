package rikka.lanserverproperties;

import java.util.function.Supplier;

import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ShareToLanScreen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.players.PlayerList;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

import rikka.lanserverproperties.mixin.PlayerListAccessor;

@Mod(LanServerProperties.MODID)
public class LanServerProperties {
	public static final String MODID = "lanserverproperties";

	public static LanServerProperties INSTANCE = null;

	public LanServerProperties() {
		if (INSTANCE == null)
			INSTANCE = this;
		else
			throw new RuntimeException("Duplicated Class Instantiation: LanServerProperties");

		// Register the GuiOpenEvent handler on client side only
		// If the mod is accidentally installed on a dedicated server then do nothing
		safeRunWhenOn(Dist.CLIENT, ()->ClientHandler::registerGuiEventHandler);
	}

	public static void safeRunWhenOn(Dist dist, Supplier<Runnable> toRun) {
		if (FMLLoader.getDist() == Dist.CLIENT) {
			toRun.get().run();
		}
	}

	private static class ClientHandler {
		public static void registerGuiEventHandler() {
			NeoForge.EVENT_BUS.addListener(ClientHandler::onGuiPostInit);
			NeoForge.EVENT_BUS.addListener(ClientHandler::onGuiDraw);
		}

		public static void onGuiPostInit(ScreenEvent.Init.Post event) {
			Screen gui = event.getScreen();
			if (gui instanceof ShareToLanScreen) {
				OpenToLanScreenEx hook = ((IShareToLanScreenParamAccessor) gui).getLSPData();

				hook.postInitShareToLanScreen(((IShareToLanScreenParamAccessor) gui).getFont(),
						event.getListenersList(), event::addListener, event::removeListener);
			} else if (gui instanceof PauseScreen) {
				OpenToLanScreenEx.initPauseScreen(gui, event.getListenersList(), event::addListener);
			}
		}

		public static void onGuiDraw(ScreenEvent.Render.Post event) {
			Screen gui = event.getScreen();
			if (gui instanceof ShareToLanScreen) {
				OpenToLanScreenEx.postDraw(gui, ((IShareToLanScreenParamAccessor) gui).getFont(),
						event.getGuiGraphics(),	event.getMouseX(), event.getMouseY(), event.getPartialTick());
			}
		}
	}

	/**
	 *  Called by the mod, should be consistent in all implementations.
	 */
	public static void setMaxPlayers(IntegratedServer server, int num) {
		PlayerList playerList = server.getPlayerList();
		((PlayerListAccessor)playerList).setMaxPlayers(num);
	}

	/**
	 * These are the lambda names used for Mixin injection, should be consistent in all implementations.
	 */
	// m_279789_(Lnet/minecraft/client/server/IntegratedServer;Lnet/minecraft/client/gui/components/Button;)V
	public static final String mixin_startButton_OnClick = "lambda$init$2";

	// m_257075_(Lnet/minecraft/client/gui/components/Button;Ljava/lang/String;)V
	public static final String mixin_portEditBox_OnChange = "lambda$init$3";
}
